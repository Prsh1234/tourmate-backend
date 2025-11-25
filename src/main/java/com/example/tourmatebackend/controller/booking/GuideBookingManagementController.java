package com.example.tourmatebackend.controller.booking;

import com.example.tourmatebackend.dto.booking.guide.GuideBookingResponseDTO;
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


import java.util.Arrays;
import java.util.List;
import java.util.Map;

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
    @GetMapping("/view")
    public ResponseEntity<?> getGuideBookings(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String status, // comma-separated statuses
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        User user = getUserFromToken(authHeader);

        if (!isGuide(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "Only guides can view their bookings."));
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<GuideBooking> bookingPage;

        if (status == null || status.isEmpty()) {
            // No filter: get all bookings for this guide
            bookingPage = bookingRepository.findByGuideId(user.getGuide().getId(), pageable);
        } else {
            // Filter by one or more statuses
            List<BookingStatus> statusEnums = Arrays.stream(status.split(","))
                    .map(String::toUpperCase)
                    .map(BookingStatus::valueOf)
                    .toList();

            bookingPage = bookingRepository.findByGuideIdAndStatusIn(user.getGuide().getId(), statusEnums, pageable);
        }

        List<GuideBookingResponseDTO> bookings = bookingPage.getContent()
                .stream()
                .map(GuideBookingResponseDTO::new)
                .toList();

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Bookings fetched successfully",
                "data", bookings,
                "currentPage", bookingPage.getNumber(),
                "pageSize", bookingPage.getSize(),
                "totalItems", bookingPage.getTotalElements(),
                "totalPages", bookingPage.getTotalPages()
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

