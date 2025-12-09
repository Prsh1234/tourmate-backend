package com.example.tourmatebackend.controller.payment;

import com.example.tourmatebackend.dto.booking.tour.TourBookingResponseDTO;
import com.example.tourmatebackend.model.TourBooking;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.TourBookingRepository;
import com.example.tourmatebackend.repository.UserRepository;
import com.example.tourmatebackend.service.NotificationService;
import com.example.tourmatebackend.states.BookingStatus;
import com.example.tourmatebackend.states.PaymentStatus;
import com.example.tourmatebackend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    // -----------------------------
    // Helper: extract user from token
    // -----------------------------
    private User getUserFromToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
