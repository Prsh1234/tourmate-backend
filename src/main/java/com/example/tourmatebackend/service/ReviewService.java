package com.example.tourmatebackend.service;

import com.example.tourmatebackend.model.GuideReview;
import com.example.tourmatebackend.model.TourReview;
import com.example.tourmatebackend.repository.GuideReviewRepository;
import com.example.tourmatebackend.repository.TourReviewRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewService {
    @Autowired
    private GuideReviewRepository guideReviewRepository;
    @Autowired
    private TourReviewRepository tourReviewRepository;
    public double calculateGuideAverageRating(List<GuideReview> reviews) {
        double avg = reviews.stream()
                .mapToInt(GuideReview::getRating)
                .average()
                .orElse(0);

        return Math.round(avg * 100.0) / 100.0;
    }

    public double calculateTourAverageRating(List<TourReview> reviews) {
        double avg = reviews.stream()
                .mapToInt(TourReview::getRating)
                .average()
                .orElse(0);

        return Math.round(avg * 100.0) / 100.0;
    }
    public GuideReview addGuideComment(int guideId, int reviewId, String comment) {

        GuideReview review = guideReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // Ensure the guide owns this review
        if (review.getGuide().getId() != guideId) {
            throw new RuntimeException("You are not authorized to comment on this review.");
        }

        // Allow only ONE comment
        if (review.getGuideComment() != null) {
            throw new RuntimeException("Comment already added.");
        }

        review.setGuideComment(comment);
        review.setCommentAt(LocalDateTime.now());

        return guideReviewRepository.save(review);
    }


    public TourReview addTourComment(int guideId, int reviewId, String comment) {

        TourReview review = tourReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        // Ensure the guide owns this review
        if (review.getTour().getGuide().getId() != guideId) {

            throw new RuntimeException("You are not authorized to comment on this review.");
        }

        // Allow only ONE comment
        if (review.getGuideComment() != null) {
            throw new RuntimeException("Comment already added.");
        }

        review.setGuideComment(comment);
        review.setCommentAt(LocalDateTime.now());

        return tourReviewRepository.save(review);
    }

    public long getTourReviewCount(int tourId) {
        return tourReviewRepository.countByTourId(tourId);
    }

    public double getTourAverageRating(int tourId) {
        Double avg = tourReviewRepository.findAverageRatingByTourId(tourId);
        return avg == null ? 0.0 : Math.round(avg * 100.0) / 100.0;
    }

}
