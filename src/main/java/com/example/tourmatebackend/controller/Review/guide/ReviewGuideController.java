package com.example.tourmatebackend.controller.Review.guide;

import com.example.tourmatebackend.dto.review.ReviewResponseDTO;
import com.example.tourmatebackend.dto.review.comment.ReviewCommentRequestDTO;
import com.example.tourmatebackend.model.Guide;
import com.example.tourmatebackend.model.GuideReview;
import com.example.tourmatebackend.model.TourReview;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.GuideRepository;
import com.example.tourmatebackend.repository.GuideReviewRepository;
import com.example.tourmatebackend.repository.TourReviewRepository;
import com.example.tourmatebackend.repository.UserRepository;
import com.example.tourmatebackend.service.ReviewService;
import com.example.tourmatebackend.states.Role;
import com.example.tourmatebackend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
@RestController
@RequestMapping("/api/guides/reviews")
public class ReviewGuideController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ReviewService reviewService;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GuideRepository guideRepository;
    @Autowired
    private GuideReviewRepository guideReviewRepository;
    @Autowired
    private TourReviewRepository tourReviewRepository;

    @PostMapping("/guide/{reviewId}/comment")
    public ResponseEntity<?> commentOnGuideReview(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int reviewId,
            @RequestBody ReviewCommentRequestDTO request
    ) {
        // Extract guide ID from token
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email).orElseThrow();

        if (!user.getRole().equals(Role.GUIDE)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Only guides can comment on reviews.");
        }

        Guide guide = guideRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Guide profile not found"));

        GuideReview updated = reviewService.addGuideComment(
                guide.getId(), reviewId, request.getComment());

        return ResponseEntity.ok(Map.of("status","success",
                "message","Comment added successfully."));
    }
    @GetMapping("/guide")
    public ResponseEntity<?> getGuideReviews(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email).orElseThrow();
        Guide guide = guideRepository.findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException("Guide profile not found"));

        List<GuideReview> reviews = guideReviewRepository.findByGuideId(guide.getId());
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

}
