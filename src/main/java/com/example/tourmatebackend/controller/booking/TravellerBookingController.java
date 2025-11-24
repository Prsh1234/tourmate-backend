package com.example.tourmatebackend.controller.booking;

import com.example.tourmatebackend.DTO.GuideBookingRequestDTO;
import com.example.tourmatebackend.DTO.GuideBookingResponseDTO;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.model.GuideBooking;
import com.example.tourmatebackend.service.GuideBookingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class TravellerBookingController {

    @Autowired
    private GuideBookingService bookingService;

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

}
