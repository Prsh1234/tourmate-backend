package com.example.tourmatebackend.controller.booking;

import com.example.tourmatebackend.dto.booking.guide.GuideBookingResponseDTO;
import com.example.tourmatebackend.dto.booking.tour.TourBookingRequestDTO;
import com.example.tourmatebackend.dto.booking.tour.TourBookingResponseDTO;
import com.example.tourmatebackend.model.*;
import com.example.tourmatebackend.repository.*;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/traveller/tour")
public class TourBookingController {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private TourRepository tourRepository;
    @Autowired
    private GuideRepository guideRepository;

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TourBookingRepository bookingRepository;

    // --------------------------------------
    // CREATE BOOKING
    // --------------------------------------
    @PostMapping("/book-request")
    public ResponseEntity<?> createBooking(@RequestHeader("Authorization") String authHeader,
                                           @RequestBody TourBookingRequestDTO req) {

        Tour tour = tourRepository.findById(req.getTourId()).orElse(null);
        User user = getUserFromToken(authHeader);
        Guide guide = guideRepository.findById(req.getGuideId()).orElse(null);
        LocalDate now = LocalDate.now();

        if (tour == null || user == null || guide == null) {
            return ResponseEntity.badRequest().body("Invalid user, tour or guide ID");
        }

        TourBooking booking = new TourBooking();
        booking.setTour(tour);
        booking.setUser(user);
        booking.setGuide(guide);   // SAVE GUIDE
        if (req.getTravellers() <= 0) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "Traveller numbers not provided."));
        }

        if (req.getTravellers() > tour.getMaxGuests()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "Traveller numbers exceed maximum allowed travellers."));
        }

        booking.setTravellers(req.getTravellers());



        if (req.getStartDate() == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "Start date not provided."));
        }

        if (req.getStartDate().isBefore(now)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "Start date cannot be in the past."));
        }

        if (req.getStartDate().isEqual(now)) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "Start date cannot be today."));
        }

        booking.setStartDate(req.getStartDate());

        double price = tour.getPrice() * req.getTravellers();
        booking.setTotalPrice(price);

        bookingRepository.save(booking);


        notificationService.createNotification(
                guide.getUser().getId(),
                "Booking Request Received",
                "You have received a request to book you guide."
        );
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Booking request submitted",
                "data", new TourBookingResponseDTO(booking)
        ));

    }


    // --------------------------------------
    // GET ALL BOOKINGS OF A USER
    // --------------------------------------
    @GetMapping("/mytourbookings")
    public ResponseEntity<?> getTravellerTourBookings(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(required = false) String status, // comma-separated status filter
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        // Extract authenticated user from JWT
        User user = getUserFromToken(authHeader);

        Pageable pageable = PageRequest.of(page, size);

        Page<TourBooking> bookingPage;

        if (status == null || status.isEmpty()) {
            // No filter: get all bookings for the user
            bookingPage = bookingRepository.findByUserId(user.getId(), pageable);
        } else {
            // Convert comma-separated status to BookingStatus enums
            List<BookingStatus> statusEnums = Arrays.stream(status.split(","))
                    .map(String::toUpperCase)
                    .map(BookingStatus::valueOf)
                    .toList();

            bookingPage = bookingRepository.findByUserIdAndStatusIn(user.getId(), statusEnums, pageable);
        }

        List<TourBookingResponseDTO> bookings = bookingPage.getContent().stream()
                .map(TourBookingResponseDTO::new)
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
    @PutMapping("/{bookingId}/cancel")
    public ResponseEntity<?> cancelTourBooking(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int bookingId
    ) {
        // Extract authenticated user
        User user = getUserFromToken(authHeader);

        // Find the tour booking
        TourBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Tour booking not found"));

        // Ensure the user owns this booking
        if (booking.getUser().getId() != user.getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "You can only cancel your own bookings."));
        }

        // Only allow cancelling if status is PENDING
        if (booking.getStatus() != BookingStatus.PENDING) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", "Only pending bookings can be cancelled."));
        }

        // Cancel the booking
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
                "message", "Tour booking cancelled successfully",
                "data", new TourBookingResponseDTO(booking)
        ));
    }
    @GetMapping("/upcoming-trips")
    public ResponseEntity<?> getUpcomingTrips(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        User user = getUserFromToken(authHeader);

        Pageable pageable = PageRequest.of(page, size);

        // Upcoming = APPROVED bookings starting today or later
        Page<TourBooking> bookingPage =
                bookingRepository.findByUserIdAndStatusAndStartDateAfter(
                        user.getId(),
                        BookingStatus.APPROVED,
                        LocalDate.now(),
                        pageable
                );

        List<TourBookingResponseDTO> bookings = bookingPage.getContent().stream()
                .map(TourBookingResponseDTO::new)
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

    // Helper method to extract User from JWT
    private User getUserFromToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}
