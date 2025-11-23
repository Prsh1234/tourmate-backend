package com.example.tourmatebackend.controller.guide;

import com.example.tourmatebackend.model.Guide;
import com.example.tourmatebackend.model.Tour;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.GuideRepository;
import com.example.tourmatebackend.repository.TourRepository;
import com.example.tourmatebackend.repository.UserRepository;
import com.example.tourmatebackend.states.TourStatus;
import com.example.tourmatebackend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/guide/tour")
public class TourController {

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GuideRepository guideRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // ✅ Utility: extract user from JWT token
    private User getUserFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        String email = jwtUtil.extractEmail(authHeader.substring(7));
        return userRepository.findByEmail(email).orElse(null);
    }

    // ✅ Utility: verify user is guide
    private boolean isGuide(User user) {
        return user != null && user.getGuide() != null;
    }

    // ✅ 1. Create a new draft tour
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
        tour.setCategories(tourRequest.getCategories());
        Tour savedTour = tourRepository.save(tour);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Tour saved as draft successfully.",
                "data", Map.of(
                        "tourId", savedTour.getId(),
                        "categories", savedTour.getCategories(),
                        "title", savedTour.getTitle(),
                        "status", savedTour.getStatus().name()
                )
        ));
    }

    // ✅ 2. Post a drafted tour (make it public)
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
                "data", Map.of(
                        "tourId", tour.getId(),
                        "title", tour.getTitle(),
                        "status", tour.getStatus().name()
                )
        ));
    }

    // ✅ 3. Get all tours of the logged-in guide
    @GetMapping("/mytours")
    public ResponseEntity<?> getMyTours(@RequestHeader("Authorization") String authHeader) {
        User user = getUserFromToken(authHeader);
        if (!isGuide(user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "Only guides can view their tours."));
        }

        List<Tour> tours = tourRepository.findByGuideId(user.getGuide().getId());
        List<Map<String, Object>> data = new ArrayList<>();

        for (Tour t : tours) {
            Map<String, Object> tourData = new HashMap<>();
            tourData.put("id", t.getId());
            tourData.put("title", t.getTitle());
            tourData.put("location", t.getLocation());
            tourData.put("price", t.getPrice());
            tourData.put("status", t.getStatus().name());

            data.add(tourData);
        }

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "tours", data
        ));
    }
    // ✅ 4. Edit drafted tour
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

        if (tour.getStatus() != TourStatus.DRAFTED)
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "Only drafted tours can be edited."));

        tour.setTitle(updatedTour.getTitle());
        tour.setDescription(updatedTour.getDescription());
        tour.setLocation(updatedTour.getLocation());
        tour.setPrice(updatedTour.getPrice());
        tour.setStartDate(updatedTour.getStartDate());
        tour.setEndDate(updatedTour.getEndDate());
        tour.setCategories(updatedTour.getCategories());
        Tour saved = tourRepository.save(tour);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Tour updated successfully.",
                "data", saved
        ));
    }

    // ✅ 5. Delete drafted tour
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

        if (tour.getStatus() != TourStatus.DRAFTED)
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "Only drafted tours can be deleted."));

        tourRepository.delete(tour);
        return ResponseEntity.ok(Map.of("status", "success", "message", "Tour deleted successfully."));
    }

}
