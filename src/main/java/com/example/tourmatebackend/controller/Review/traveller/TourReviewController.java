package com.example.tourmatebackend.controller.Review.traveller;

import com.example.tourmatebackend.dto.review.CreateReviewDTO;
import com.example.tourmatebackend.dto.review.ReviewResponseDTO;
import com.example.tourmatebackend.model.*;
import com.example.tourmatebackend.repository.*;
import com.example.tourmatebackend.service.ReviewService;
import com.example.tourmatebackend.states.BookingStatus;
import com.example.tourmatebackend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/traveller/tour/review")
public class TourReviewController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewService reviewService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private TourBookingRepository tourBookingRepository;

    @Autowired
    private TourReviewRepository tourReviewRepository;

    @Autowired
    private TourRepository tourRepository;

    @PostMapping("/{tourId}")
    public ResponseEntity<?> addTourReview(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int tourId,
            @RequestBody CreateReviewDTO dto
    ) {
        User user = getUserFromToken(authHeader);

        boolean hasCompletedBooking = tourBookingRepository
                .existsByUserIdAndTourIdAndStatus(user.getId(), tourId, BookingStatus.COMPLETED);

//        if (!hasCompletedBooking) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(Map.of("status", "error", "message", "You can review only after completing this tour."));
//        }

//        if (tourReviewRepository.existsByUserIdAndTourId(user.getId(), tourId)) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(Map.of("status", "error", "message", "You already reviewed this tour."));
//        }

        Tour tour = tourRepository.findById(tourId)
                .orElseThrow(() -> new RuntimeException("Tour not found"));

        TourReview review = new TourReview();
        review.setUser(user);
        review.setTour(tour);
        review.setRating(dto.getRating());
        review.setReview(dto.getReview());

        tourReviewRepository.save(review);

        ReviewResponseDTO responseDTO = new ReviewResponseDTO(
                review.getId(),
                review.getRating(),
                review.getReview(),
                user.getId(),
                user.getFirstName() + " " + user.getLastName(),
                user.getProfilePic(),
                "TOUR",
                tour.getId(),
                review.getCreatedAt().toString(),
                review.getGuideComment(),
                review.getCommentAt() != null ? review.getCommentAt().toString() : null
        );

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "review", responseDTO
        ));
    }
    @GetMapping("/{tourId}/reviews")
    public ResponseEntity<?> getTourReviews(@PathVariable int tourId) {
        List<TourReview> reviews = tourReviewRepository.findByTourId(tourId);


        double avgRating = reviewService.calculateTourAverageRating(reviews);
        List<ReviewResponseDTO> reviewDTOs = convertTourReviews(reviews);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "averageRating", avgRating,
                "totalReviews", reviews.size(),
                "reviews", reviewDTOs
        ));
    }
    public List<ReviewResponseDTO> convertTourReviews(List<TourReview> reviews) {

        return reviews.stream().map(r -> new ReviewResponseDTO(
                r.getId(),
                r.getRating(),
                r.getReview(),
                r.getUser().getId(),
                r.getUser().getFirstName() + " "+ r.getUser().getLastName(),
                r.getUser().getProfilePic(),
                "TOUR",
                r.getTour().getId(),
                r.getCreatedAt().toString(),
                r.getGuideComment(),
                r.getCommentAt() != null ? r.getCommentAt().toString() : null
        )).toList();
    }

    private User getUserFromToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
}
