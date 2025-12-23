package com.example.tourmatebackend.controller.guide;

import com.example.tourmatebackend.dto.guide.TourItineraryDTO;
import com.example.tourmatebackend.dto.guide.TourResponseDTO;
import com.example.tourmatebackend.dto.guideRegistration.GuideRegisterRequestDTO;
import com.example.tourmatebackend.model.Guide;
import com.example.tourmatebackend.model.Tour;
import com.example.tourmatebackend.model.TourItinerary;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.TourRepository;
import com.example.tourmatebackend.repository.UserRepository;
import com.example.tourmatebackend.states.TourStatus;
import com.example.tourmatebackend.utils.JwtUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
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
    private ObjectMapper objectMapper;
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
        if (tour == null) {
            return null; // Return null if the tour object is null
        }

        TourResponseDTO dto = new TourResponseDTO();

        // Check for null values before accessing properties
        dto.setId(tour.getId());
        dto.setDescription(tour.getDescription() != null ? tour.getDescription() : "");
        dto.setLocation(tour.getLocation() != null ? tour.getLocation() : "");
        dto.setPrice(tour.getPrice() != 0.0 ? tour.getPrice() : 0.0);  // Default to 0 if null
        dto.setDuration(tour.getDuration() != null ? tour.getDuration() : ""); // Default to empty string if null
        dto.setName(tour.getName() != null ? tour.getName() : "");
        dto.setMaxGuests(tour.getMaxGuests() != 0 ? tour.getMaxGuests() : 0); // Default to 0 if null
        dto.setCategories(tour.getCategories() != null ? tour.getCategories() : Collections.emptyList());
        dto.setLanguages(tour.getLanguages() != null ? tour.getLanguages() : Collections.emptyList());
        dto.setStatus(tour.getStatus() != null ? tour.getStatus() : TourStatus.DRAFTED);
        dto.setIncluded(tour.getIncluded() != null ? tour.getIncluded() : Collections.emptyList());
        dto.setNotIncluded(tour.getNotIncluded() != null ? tour.getNotIncluded() : Collections.emptyList());
        dto.setImportantInformation(tour.getImportantInformation() != null ? tour.getImportantInformation() : Collections.emptyList());

        // Handle null guide
        if (tour.getGuide() != null) {
            dto.setGuideId(tour.getGuide().getId());
            dto.setGuideName(tour.getGuide().getFullName() != null ? tour.getGuide().getFullName() : "");
        } else {
            dto.setGuideId(0); // If guide is null, set it as null
            dto.setGuideName("");  // Set guide name as empty string if guide is null
        }

        // Handle null for tourPic
        dto.setTourPic(tour.getTourPic() != null ? tour.getTourPic() : null);

        // Handle itineraries null or empty case
        if (tour.getItineraries() != null) {
            dto.setItineraries(
                    tour.getItineraries().stream()
                            .map(it -> new TourItineraryDTO(
                                    it.getId(),
                                    it.getStepNumber(),
                                    it.getTime(),
                                    it.getTitle() != null ? it.getTitle() : "",
                                    it.getDescription() != null ? it.getDescription() : ""
                            ))
                            .toList()
            );
        } else {
            dto.setItineraries(Collections.emptyList());  // Default to empty list if itineraries are null
        }

        return dto;
    }

    // ==========================
    // 1. Create a new draft tour
    // ==========================
    @PostMapping(value="/create",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createTour(@RequestHeader("Authorization") String authHeader,
                                        @RequestPart("tour") String tourJson, // receive as String
                                        @RequestPart(value = "tourPic", required = false) MultipartFile tourPic) throws IOException {
        Tour tourRequest = objectMapper.readValue(tourJson, Tour.class);

        User user = getUserFromToken(authHeader);
        if (!isGuide(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "Only guides can create tours."));
        }

        Guide guide = user.getGuide();
        Tour tour = new Tour();
        // Handle the status conversion (String -> Enum)


        // If status is not provided, set it to DRAFTED by default
        if (tourRequest.getStatus() == null || tourRequest.getStatus() == TourStatus.DRAFTED) {
            tour.setStatus(TourStatus.DRAFTED);
        } else {
            tour.setStatus(TourStatus.POSTED); // Set the status if it's valid
        }

        tour.setDescription(tourRequest.getDescription());
        tour.setLocation(tourRequest.getLocation());
        tour.setPrice(tourRequest.getPrice());
        tour.setDuration(tourRequest.getDuration());
        tour.setName(tourRequest.getName());
        tour.setMaxGuests(tourRequest.getMaxGuests());
        tour.setGuide(guide);
        if (tourPic != null) {
            tour.setTourPic(tourPic.getBytes());
        }
        tour.setStatus(TourStatus.DRAFTED);

        if(tourRequest.getStatus() == TourStatus.DRAFTED){
            tour.setStatus(TourStatus.DRAFTED);
        }
        else if(tourRequest.getStatus() == TourStatus.POSTED){
            tour.setStatus(TourStatus.POSTED);
        }
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
                "message", "Tour saved successfully.",
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

        if (updatedTour.getName() != null) tour.setName(updatedTour.getName());
        if (updatedTour.getDescription() != null)tour.setDescription(updatedTour.getDescription());
        if (updatedTour.getLocation() != null)tour.setLocation(updatedTour.getLocation());
        if (updatedTour.getPrice() != 0.0)tour.setPrice(updatedTour.getPrice());
        if (updatedTour.getDuration() != null)tour.setDuration(updatedTour.getDuration());
        if (updatedTour.getMaxGuests() != 0)tour.setMaxGuests(updatedTour.getMaxGuests());
        if (updatedTour.getCategories() != null) tour.setCategories(updatedTour.getCategories());
        if (updatedTour.getLanguages() != null) tour.setLanguages(updatedTour.getLanguages());
        if(updatedTour.getIncluded() != null) tour.setIncluded(updatedTour.getIncluded());
        if(updatedTour.getNotIncluded() != null) tour.setNotIncluded(updatedTour.getNotIncluded());
        if(updatedTour.getImportantInformation() != null) tour.setImportantInformation(updatedTour.getImportantInformation());



        if (updatedTour.getItineraries() != null) {
            tour.getItineraries().clear(); // thanks to orphanRemoval = true

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
    @PutMapping("/tourPic/{tourId}/edit")
    public ResponseEntity<?> editTour(@RequestHeader("Authorization") String authHeader,
                                      @PathVariable int tourId,
                                      @RequestParam("file") MultipartFile file) throws IOException {
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
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "No file uploaded"));
        }
        tour.setTourPic(file.getBytes());

        Tour saved = tourRepository.save(tour);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Tour image updated successfully."

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
