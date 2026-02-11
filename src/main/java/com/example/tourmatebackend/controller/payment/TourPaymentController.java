package com.example.tourmatebackend.controller.payment;

import com.example.tourmatebackend.dto.booking.tour.TourBookingResponseDTO;
import com.example.tourmatebackend.model.TourBooking;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.TourBookingRepository;
import com.example.tourmatebackend.repository.UserRepository;
import com.example.tourmatebackend.service.NotificationService;
import com.example.tourmatebackend.states.BookingStatus;
import com.example.tourmatebackend.states.PaymentStatus;
import com.example.tourmatebackend.utils.HmacHelper;
import com.example.tourmatebackend.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Map;

@RestController
@RequestMapping("/api/traveller/tour/payments")
public class TourPaymentController {

    @Autowired
    private TourBookingRepository bookingRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // -----------------------------
    // Simulate payment request
    // -----------------------------
    @PostMapping("/{bookingId}/simulate")
    public ResponseEntity<?> simulatePayment(
            @PathVariable int bookingId,
            @RequestHeader("Authorization") String authHeader
    ) {
        User user = getUserFromToken(authHeader);

        TourBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getUser().getId() != user.getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "You can only pay for your own bookings."));
        }

        if (booking.getPaymentStatus() == PaymentStatus.PAID) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "Booking is already paid."));
        }
        if (booking.getStatus()== BookingStatus.PENDING|| booking.getStatus()== BookingStatus.DENIED || booking.getStatus()== BookingStatus.CANCELLED) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "You have not booked this tour."));
        }

        // Simulate generating a mock transaction
        String mockTransactionId = "TXN-" + System.currentTimeMillis();

        booking.setPaymentTransactionId(mockTransactionId);
        booking.setPaymentDate(LocalDateTime.now());
        booking.setPaymentStatus(PaymentStatus.PAID);

        bookingRepository.save(booking);
        notificationService.createNotification(
                booking.getGuide().getUser().getId(),
                "Payment Received",
                booking.getUser().getFirstName() + " has completed the payment of amount "+ booking.getTotalPrice()+ " ."
        );
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Payment simulated successfully",
                "transactionId", mockTransactionId,
                "booking", new TourBookingResponseDTO(booking)
        ));
    }


    @GetMapping("/esewa/success")
    public ResponseEntity<?> esewaSuccess(
            @RequestParam String meta,
            @RequestParam String returnUrl
    ) {
        try {
            if(meta.contains("?data=")) {
                meta = meta.split("\\?data=")[0];
            }

            // 1️⃣ URL-decode first
            String urlDecoded = URLDecoder.decode(meta, StandardCharsets.UTF_8);

            // 2️⃣ Base64-decode
            String decoded = new String(Base64.getDecoder().decode(urlDecoded));
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> payload = mapper.readValue(decoded, Map.class);

            String transactionUuid = (String) payload.get("transaction_uuid");
            double totalAmount = Double.parseDouble(payload.get("total_amount").toString());
            int bookingId = (int) payload.get("bookingId");

            // 3️⃣ Verify with eSewa RC API
            boolean verified = true; // skip RC check
            if (!verified) return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Payment verification failed");

            // 4️⃣ Update booking
            TourBooking booking = bookingRepository.findById(bookingId)
                    .orElseThrow(() -> new RuntimeException("Booking not found"));

            if (booking.getPaymentStatus() == PaymentStatus.PAID) return ResponseEntity.ok("Already paid");

            booking.setPaymentStatus(PaymentStatus.PAID);
            booking.setPaymentTransactionId(transactionUuid);
            booking.setPaymentDate(LocalDateTime.now());
            bookingRepository.save(booking);

            // 5️⃣ Notify guide
            notificationService.createNotification(
                    booking.getGuide().getUser().getId(),
                    "Payment Received",
                    booking.getUser().getFirstName() + " has completed the payment of amount " + booking.getTotalPrice() +  " for " + booking.getTour().getName()+"."
            );
            notificationService.createNotification(
                    booking.getUser().getId(),
                    "Payment Complete",
                    "You have completed the payment of amount " + booking.getTotalPrice() +  " for " + booking.getTour().getName()+"."
            );
            return ResponseEntity.status(HttpStatus.FOUND)
                    .header("Location", returnUrl)
                    .build();


        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Invalid Base64 data (possibly due to eSewa appending ?data=...)");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid payment data");
        }
    }






    @GetMapping("/esewa/failure")
    public ResponseEntity<?> esewaFailure(@RequestParam String returnUrl) {
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", returnUrl)
                .build();
//        return ResponseEntity.ok("Payment failed or cancelled");
    }



    // -----------------------------
    // Helper: extract user from token
    // -----------------------------
    private boolean verifyEsewaRC(String transactionUuid, double amount) {
        String url = String.format(
                "https://rc.esewa.com.np/api/epay/transaction/status/?product_code=EPAYTEST&total_amount=%s&transaction_uuid=%s",
                amount, transactionUuid
        );

        RestTemplate restTemplate = new RestTemplate();
        try {
            Map<String, Object> response = restTemplate.getForObject(url, Map.class);
            // RC API returns JSON, usually {"status":"SUCCESS"} for successful payments
            return response != null && "SUCCESS".equals(response.get("status"));
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }


    private User getUserFromToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
