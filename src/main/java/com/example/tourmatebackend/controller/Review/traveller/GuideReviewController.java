package com.example.tourmatebackend.controller.Review.traveller;

import com.example.tourmatebackend.dto.review.CreateReviewDTO;
import com.example.tourmatebackend.dto.review.ReviewResponseDTO;
import com.example.tourmatebackend.model.Guide;
import com.example.tourmatebackend.model.GuideReview;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.GuideBookingRepository;
import com.example.tourmatebackend.repository.GuideRepository;
import com.example.tourmatebackend.repository.GuideReviewRepository;
import com.example.tourmatebackend.repository.UserRepository;
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
@RequestMapping("/api/traveller/guide/review")
public class GuideReviewController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private GuideBookingRepository guideBookingRepository;

    @Autowired
    private GuideReviewRepository guideReviewRepository;

    @Autowired
    private GuideRepository guideRepository;


    @PostMapping("/{guideId}")
    public ResponseEntity<?> addGuideReview(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int guideId,
            @RequestBody CreateReviewDTO dto
    ) {
        User user = getUserFromToken(authHeader);

//        // check if user had a completed booking with this guide
//        boolean hasCompletedBooking = guideBookingRepository
//                .existsByUserIdAndGuideIdAndStatus(user.getId(), guideId, BookingStatus.COMPLETED);
//
//        if (!hasCompletedBooking) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(Map.of("status", "error", "message", "You can review only after completing a booking."));
//        }
//
//        // prevent duplicate review
//        if (guideReviewRepository.existsByUserIdAndGuideId(user.getId(), guideId)) {
//            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                    .body(Map.of("status", "error", "message", "You have already reviewed this guide."));
//        }

        Guide guide = guideRepository.findById(guideId)
                .orElseThrow(() -> new RuntimeException("Guide not found"));

        GuideReview review = new GuideReview();
        review.setUser(user);
        review.setGuide(guide);
        review.setRating(dto.getRating());
        review.setReview(dto.getReview());

        guideReviewRepository.save(review);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Review added successfully"
        ));
    }
    @GetMapping("/{guideId}/reviews")
    public ResponseEntity<?> getGuideReviews(@PathVariable int guideId) {
        List<GuideReview> reviews = guideReviewRepository.findByGuideId(guideId);
        double avgRating = reviewService.calculateGuideAverageRating(reviews);
        List<ReviewResponseDTO> reviewDTOs = convertGuideReviews(reviews);
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "averageRating", avgRating,
                "totalReviews", reviews.size(),
                "reviews", reviewDTOs
        ));
    }
    public List<ReviewResponseDTO> convertGuideReviews(List<GuideReview> reviews) {

        return reviews.stream().map(r -> new ReviewResponseDTO(
                r.getId(),
                r.getRating(),
                r.getReview(),
                r.getUser().getId(),
                r.getUser().getFirstName(),
                r.getUser().getProfilePic(),
                "GUIDE",
                r.getGuide().getId(),
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
