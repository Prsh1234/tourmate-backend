package com.example.tourmatebackend.controller.booking;

import com.example.tourmatebackend.dto.booking.guide.GuideBookingRequestDTO;
import com.example.tourmatebackend.dto.booking.guide.GuideBookingResponseDTO;
import com.example.tourmatebackend.model.Guide;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.model.GuideBooking;
import com.example.tourmatebackend.repository.GuideBookingRepository;
import com.example.tourmatebackend.repository.GuideRepository;
import com.example.tourmatebackend.repository.UserRepository;
import com.example.tourmatebackend.service.GuideBookingService;
import com.example.tourmatebackend.service.NotificationService;
import com.example.tourmatebackend.states.BookingStatus;
import com.example.tourmatebackend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/traveller/guide")
public class GuideBookingController {

    @Autowired
    private GuideBookingService bookingService;
    @Autowired
    private GuideBookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GuideRepository guideRepository;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private NotificationService notificationService;
    // ----------------------------
    // Book a guide (request)
    // ----------------------------
    @PostMapping("/book-request")
    public ResponseEntity<?> requestBooking(@RequestBody GuideBookingRequestDTO request) {

        // Get authenticated user from security context
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof User user)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "error", "message", "Unauthorized"));
        }

        try {
            // Pass authenticated user to service
            GuideBooking booking = bookingService.createBookingRequest(request, user);
            Guide guide = guideRepository.findById(request.getGuideId()).orElseThrow();

            notificationService.createNotification(
                    guide.getUser().getId(),
                    "Booking Request Received",
                    "You have received a request to book you guide."
            );

            // Return proper response DTO
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Booking request submitted",
                    "data", new GuideBookingResponseDTO(booking)
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
    // --- Get traveller bookings with pagination ---
    @GetMapping("/mybookings")
    public ResponseEntity<?> getTravellerBookings(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String status, // comma-separated status filter
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        User user = getUserFromToken(authHeader);
        Pageable pageable = PageRequest.of(page, size);

        Page<GuideBooking> bookingPage;

        if (status == null || status.isEmpty()) {
            // No filter: get all bookings
            bookingPage = bookingRepository.findByUserId(user.getId(), pageable);
        } else {
            // Split comma-separated statuses and convert to enums
            List<BookingStatus> statusEnums = Arrays.stream(status.split(","))
                    .map(String::toUpperCase)
                    .map(BookingStatus::valueOf)
                    .toList();

            bookingPage = bookingRepository.findByUserIdAndStatusIn(user.getId(), statusEnums, pageable);
        }

        List<GuideBookingResponseDTO> bookings = bookingPage.getContent().stream()
                .map(GuideBookingResponseDTO::new)
                .toList();

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", bookings,
                "currentPage", bookingPage.getNumber(),
                "pageSize", bookingPage.getSize(),
                "totalBookings", bookingPage.getTotalElements(),
                "totalPages", bookingPage.getTotalPages()
        ));
    }


    // --- Cancel a booking ---
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<?> cancelBooking(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int bookingId
    ) {
        User user = getUserFromToken(authHeader);

        GuideBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getUser().getId() != user.getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "You can only cancel your own bookings."));
        }

        if (booking.getStatus() != BookingStatus.PENDING) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", "Only pending bookings can be cancelled."));
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        int receiver = booking.getGuide().getUser().getId();
        notificationService.createNotification(
                receiver,
                "Booking Request Cancelled",
                booking.getUser().getFirstName() + " has cancelled their request"
        );
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Booking cancelled successfully",
                "data", new GuideBookingResponseDTO(booking)
        ));
    }

    // --- Helper method to extract user from JWT ---
    private User getUserFromToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}
