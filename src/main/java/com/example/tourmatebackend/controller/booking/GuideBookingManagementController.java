package com.example.tourmatebackend.controller.booking;

import com.example.tourmatebackend.DTO.GuideBookingResponseDTO;
import com.example.tourmatebackend.model.GuideBooking;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.GuideBookingRepository;
import com.example.tourmatebackend.repository.UserRepository;
import com.example.tourmatebackend.states.BookingStatus;
import com.example.tourmatebackend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/guides/bookings")
public class GuideBookingManagementController {

    @Autowired
    private GuideBookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // ----------------------------
    // Approve a booking request
    // ----------------------------
    @PutMapping("/{bookingId}/accept")
    public ResponseEntity<?> approveBooking(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int bookingId
    ) {
        User user = getUserFromToken(authHeader);

        if (!isGuide(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "Only guides can manage bookings."));
        }

        GuideBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getGuide().getId() != user.getGuide().getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "You can only manage bookings for your own guide profile."));
        }

        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Booking approved successfully",
                "data", new GuideBookingResponseDTO(booking)
        ));
    }

    // ----------------------------
    // Deny a booking request
    // ----------------------------
    @PutMapping("/{bookingId}/reject")
    public ResponseEntity<?> denyBooking(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int bookingId
    ) {
        User user = getUserFromToken(authHeader);

        if (!isGuide(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "Only guides can manage bookings."));
        }

        GuideBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        if (booking.getGuide().getId() != user.getGuide().getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "You can only manage bookings for your own guide profile."));
        }

        booking.setStatus(BookingStatus.DENIED);
        bookingRepository.save(booking);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Booking denied successfully",
                "data", new GuideBookingResponseDTO(booking)
        ));
    }

    // ----------------------------
    // Get all pending booking requests for this guide
    // ----------------------------
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingBookings(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        User user = getUserFromToken(authHeader);

        if (!isGuide(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "Only guides can view their bookings."));
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<GuideBooking> bookingPage = bookingRepository
                .findByGuideIdAndStatus(user.getGuide().getId(), BookingStatus.PENDING, pageable);

        List<GuideBookingResponseDTO> pendingBookings = bookingPage.getContent()
                .stream()
                .map(GuideBookingResponseDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Pending bookings fetched successfully",
                "data", pendingBookings,
                "currentPage", bookingPage.getNumber(),
                "totalPages", bookingPage.getTotalPages(),
                "totalItems", bookingPage.getTotalElements()
        ));
    }

    // --- Helper methods ---
    private User getUserFromToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private boolean isGuide(User user) {
        return user.getGuide() != null;
    }
}

