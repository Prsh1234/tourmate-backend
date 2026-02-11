package com.example.tourmatebackend.controller.booking;

import com.example.tourmatebackend.dto.booking.tour.TourBookingResponseDTO;
import com.example.tourmatebackend.model.TourBooking;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.TourBookingRepository;
import com.example.tourmatebackend.repository.UserRepository;
import com.example.tourmatebackend.service.NotificationService;
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
@RequestMapping("/api/guides/tour/bookings")
public class TourBookingManagementController {

    @Autowired
    private TourBookingRepository bookingRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private NotificationService notificationService;
    @Autowired
    private JwtUtil jwtUtil;

    // ----------------------------
    // Accept a tour booking request
    // ----------------------------
    @PutMapping("/{bookingId}/accept")
    public ResponseEntity<?> acceptTourBooking(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int bookingId
    ) {
        User user = getUserFromToken(authHeader);

        if (!isGuide(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "Only guides can manage tour bookings."));
        }

        TourBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Tour booking not found"));


        if (booking.getGuide() == null || booking.getGuide().getId() != user.getGuide().getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "You can only manage your own tour bookings."));
        }

        booking.setStatus(BookingStatus.APPROVED);
        bookingRepository.save(booking);
        notificationService.createNotification(
                booking.getUser().getId(),
                "Booking Request Accepted",
                booking.getGuide().getUser().getFirstName() + " has accepted the booking request for " + booking.getTour().getName()+"."
        );
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Tour booking approved successfully",
                "data", new TourBookingResponseDTO(booking)
        ));
    }

    @PutMapping("/{bookingId}/complete")
    public ResponseEntity<?> completeTourBooking(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int bookingId
    ) {
        User user = getUserFromToken(authHeader);

        if (!isGuide(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "Only guides can manage tour bookings."));
        }

        TourBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Tour booking not found"));


        if (booking.getGuide() == null || booking.getGuide().getId() != user.getGuide().getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "You can only manage your own tour bookings."));
        }

        booking.setStatus(BookingStatus.COMPLETED);
        bookingRepository.save(booking);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Tour completed approved successfully",
                "data", new TourBookingResponseDTO(booking)
        ));
    }
    // ----------------------------
    // Reject a tour booking request
    // ----------------------------
    @PutMapping("/{bookingId}/reject")
    public ResponseEntity<?> rejectTourBooking(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int bookingId
    ) {
        User user = getUserFromToken(authHeader);

        if (!isGuide(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "Only guides can manage tour bookings."));
        }

        TourBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Tour booking not found"));

        if (booking.getGuide() == null || booking.getGuide().getId() != user.getGuide().getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "You can only manage your own tour bookings."));
        }

        booking.setStatus(BookingStatus.DENIED);
        bookingRepository.save(booking);
        notificationService.createNotification(
                booking.getUser().getId(),
                "Booking Request Rejected",
                booking.getGuide().getUser().getFirstName() + " has rejected the booking request  for " + booking.getTour().getName()+"."
        );
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Tour booking rejected successfully",
                "data", new TourBookingResponseDTO(booking)
        ));
    }
    // ----------------------------
    // Cancel a tour booking
    // ----------------------------
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<?> cancelTourBooking(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int bookingId
    ) {
        User user = getUserFromToken(authHeader);

        if (!isGuide(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "Only guides can manage tour bookings."));
        }

        TourBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Tour booking not found"));

        if (booking.getGuide() == null || booking.getGuide().getId() != user.getGuide().getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "You can only manage your own tour bookings."));
        }

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
        notificationService.createNotification(
                booking.getUser().getId(),
                "Booking Cancelled",
                booking.getGuide().getUser().getFirstName() + " has cancelled the booking for " + booking.getTour().getName()+"."
        );
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Tour booking cancelled successfully",
                "data", new TourBookingResponseDTO(booking)
        ));
    }
    // ----------------------------
    // View tour booking requests
    // ----------------------------
    @GetMapping("/view")
    public ResponseEntity<?> viewTourBookings(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String status, // comma-separated status filters
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        User user = getUserFromToken(authHeader);

        if (!isGuide(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "Only guides can view tour bookings."));
        }

        Pageable pageable = PageRequest.of(page, size);
        Page<TourBooking> bookingPage;

        if (status == null || status.isEmpty()) {
            // Get all
            bookingPage = bookingRepository.findByGuideId(user.getGuide().getId(), pageable);
        } else {
            // Filter by multiple statuses
            List<BookingStatus> statusEnums = Arrays.stream(status.split(","))
                    .map(String::toUpperCase)
                    .map(BookingStatus::valueOf)
                    .toList();

            bookingPage = bookingRepository.findByGuideIdAndStatusIn(
                    user.getGuide().getId(),
                    statusEnums,
                    pageable
            );
        }

        List<TourBookingResponseDTO> bookings = bookingPage.getContent()
                .stream()
                .map(TourBookingResponseDTO::new)
                .toList();

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Tour bookings fetched successfully",
                "data", bookings,
                "currentPage", bookingPage.getNumber(),
                "pageSize", bookingPage.getSize(),
                "totalItems", bookingPage.getTotalElements(),
                "totalPages", bookingPage.getTotalPages()
        ));
    }

    @GetMapping("/count")
    public ResponseEntity<?> getBookingCounts(
            @RequestHeader("Authorization") String authHeader
    ) {
        User user = getUserFromToken(authHeader);

        if (!isGuide(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "Only guides allowed"));
        }

        List<Object[]> results =
                bookingRepository.countBookingsByStatus(user.getGuide().getId());

        int upcoming = 0, requested = 0, past = 0, cancelled = 0;

        for (Object[] row : results) {
            BookingStatus status = (BookingStatus) row[0];
            long count = (long) row[1];

            switch (status) {
                case APPROVED -> upcoming += count;
                case PENDING -> requested += count;
                case COMPLETED -> past += count;
                case CANCELLED, DENIED -> cancelled += count;
            }
        }

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "data", Map.of(
                        "upcoming", upcoming,
                        "requested", requested,
                        "past", past,
                        "cancelled", cancelled
                )
        ));
    }
    // ----------------------------
    // Helper Methods
    // ----------------------------
    private User getUserFromToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    private boolean isGuide(User user) {
        return user.getGuide() != null; // ensures only guide accounts can access
    }
}
