package com.example.tourmatebackend.controller.guide;

import com.example.tourmatebackend.dto.guide.TourItineraryDTO;
import com.example.tourmatebackend.dto.guide.TourResponseDTO;
import com.example.tourmatebackend.model.Guide;
import com.example.tourmatebackend.model.Tour;
import com.example.tourmatebackend.model.TourItinerary;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.TourRepository;
import com.example.tourmatebackend.repository.UserRepository;
import com.example.tourmatebackend.states.TourStatus;
import com.example.tourmatebackend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/guide/tour")
public class TourController {

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // ==========================
    // Helper methods
    // ==========================
    private User getUserFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        String email = jwtUtil.extractEmail(authHeader.substring(7));
        return userRepository.findByEmail(email).orElse(null);
    }

    private boolean isGuide(User user) {
        return user != null && user.getGuide() != null;
    }

    private TourResponseDTO mapTourToDTO(Tour tour) {
        TourResponseDTO dto = new TourResponseDTO();
        dto.setId(tour.getId());
        dto.setTitle(tour.getTitle());
        dto.setDescription(tour.getDescription());
        dto.setLocation(tour.getLocation());
        dto.setPrice(tour.getPrice());
        dto.setStartDate(tour.getStartDate());
        dto.setEndDate(tour.getEndDate());
        dto.setCategories(tour.getCategories());
        dto.setLanguages(tour.getLanguages());
        dto.setStatus(tour.getStatus());

        dto.setGuideId(tour.getGuide().getId());
        dto.setGuideName(tour.getGuide().getUser().getFirstName() + " " + tour.getGuide().getUser().getLastName());
        dto.setGuideExpertise(tour.getGuide().getExpertise());
        dto.setItineraries(
                tour.getItineraries()
                        .stream()
                        .map(it -> new TourItineraryDTO(
                                it.getId(),
                                it.getStepNumber(),
                                it.getTime(),
                                it.getTitle(),
                                it.getDescription()
                        ))
                        .toList()
        );


        return dto;
    }

    // ==========================
    // 1. Create a new draft tour
    // ==========================
    @PostMapping("/create")
    public ResponseEntity<?> createTour(@RequestHeader("Authorization") String authHeader,
                                        @RequestBody Tour tourRequest) {
        User user = getUserFromToken(authHeader);
        if (!isGuide(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "Only guides can create tours."));
        }

        Guide guide = user.getGuide();
        Tour tour = new Tour();
        tour.setTitle(tourRequest.getTitle());
        tour.setDescription(tourRequest.getDescription());
        tour.setLocation(tourRequest.getLocation());
        tour.setPrice(tourRequest.getPrice());
        tour.setStartDate(tourRequest.getStartDate());
        tour.setEndDate(tourRequest.getEndDate());
        tour.setGuide(guide);
        tour.setStatus(TourStatus.DRAFTED);
        if (tourRequest.getCategories() != null) tour.setCategories(tourRequest.getCategories());
        if (tourRequest.getLanguages() != null) tour.setLanguages(tourRequest.getLanguages());
        if (tourRequest.getItineraries() != null) {
            for (TourItinerary itinerary : tourRequest.getItineraries()) {
                itinerary.setTour(tour);
            }
        }

        tour.setItineraries(tourRequest.getItineraries());

        Tour savedTour = tourRepository.save(tour);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Tour saved as draft successfully.",
                "data", mapTourToDTO(savedTour)
        ));
    }

    // ==========================
    // 2. Post a drafted tour
    // ==========================
    @PostMapping("/{tourId}/post")
    public ResponseEntity<?> postTour(@RequestHeader("Authorization") String authHeader,
                                      @PathVariable int tourId) {
        User user = getUserFromToken(authHeader);
        if (!isGuide(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "Only guides can post tours."));
        }

        Optional<Tour> optionalTour = tourRepository.findById(tourId);
        if (optionalTour.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "Tour not found."));
        }

        Tour tour = optionalTour.get();
        if (tour.getGuide().getId() != user.getGuide().getId()) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "You can only manage your own tours."));
        }

        if (tour.getStatus() != TourStatus.DRAFTED) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", "Only drafted tours can be posted."));
        }

        tour.setStatus(TourStatus.POSTED);
        tourRepository.save(tour);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Tour posted successfully.",
                "data", mapTourToDTO(tour)
        ));
    }

    // ==========================
    // 3. Get all tours of logged-in guide
    // ==========================
    @GetMapping("/mytours")
    public ResponseEntity<?> getMyTours(@RequestHeader("Authorization") String authHeader) {
        User user = getUserFromToken(authHeader);
        if (!isGuide(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "Only guides can view their tours."));
        }

        List<Tour> tours = tourRepository.findByGuideId(user.getGuide().getId());
        List<TourResponseDTO> data = tours.stream()
                .map(this::mapTourToDTO)
                .collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("status", "success", "data", data));
    }

    // ==========================
    // 4. Edit drafted tour
    // ==========================
    @PutMapping("/{tourId}/edit")
    public ResponseEntity<?> editTour(@RequestHeader("Authorization") String authHeader,
                                      @PathVariable int tourId,
                                      @RequestBody Tour updatedTour) {
        User user = getUserFromToken(authHeader);
        if (!isGuide(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "Only guides can edit tours."));
        }

        Optional<Tour> opt = tourRepository.findById(tourId);
        if (opt.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "Tour not found."));

        Tour tour = opt.get();
        if (tour.getGuide().getId() != user.getGuide().getId())
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "You can only edit your own tours."));

        if (updatedTour.getTitle() != null) tour.setTitle(updatedTour.getTitle());
        if (updatedTour.getDescription() != null)tour.setDescription(updatedTour.getDescription());
        if (updatedTour.getLocation() != null)tour.setLocation(updatedTour.getLocation());
        if (updatedTour.getPrice() != 0.0)tour.setPrice(updatedTour.getPrice());
        if (updatedTour.getStartDate() != null)tour.setStartDate(updatedTour.getStartDate());
        if (updatedTour.getEndDate() != null)tour.setEndDate(updatedTour.getEndDate());
        if (updatedTour.getCategories() != null) tour.setCategories(updatedTour.getCategories());
        if (updatedTour.getLanguages() != null) tour.setLanguages(updatedTour.getLanguages());
        tour.getItineraries().clear(); // thanks to orphanRemoval = true

        if (updatedTour.getItineraries() != null) {
            for (TourItinerary it : updatedTour.getItineraries()) {
                it.setTour(tour);
                tour.getItineraries().add(it);
            }
        }
        Tour saved = tourRepository.save(tour);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Tour updated successfully.",
                "data", mapTourToDTO(saved)
        ));
    }

    // ==========================
    // 5. Delete drafted tour
    // ==========================
    @DeleteMapping("/{tourId}/delete")
    public ResponseEntity<?> deleteTour(@RequestHeader("Authorization") String authHeader,
                                        @PathVariable int tourId) {
        User user = getUserFromToken(authHeader);
        if (!isGuide(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "Only guides can delete tours."));
        }

        Optional<Tour> opt = tourRepository.findById(tourId);
        if (opt.isEmpty())
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "Tour not found."));

        Tour tour = opt.get();
        if (tour.getGuide().getId() != user.getGuide().getId())
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "You can only delete your own tours."));

        tourRepository.delete(tour);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Tour deleted successfully."));
    }
}
