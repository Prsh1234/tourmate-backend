package com.example.tourmatebackend.repository;

import com.example.tourmatebackend.model.GuideReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GuideReviewRepository extends JpaRepository<GuideReview, Integer> {
    List<GuideReview> findByGuideId(int guideId);
    boolean existsByUserIdAndGuideId(int userId, int guideId);

    @Query("""
           SELECT AVG(r.rating)
           FROM GuideReview r
           WHERE r.guide.id = :guideId
           """)
    Double getAverageRating(@Param("guideId") int guideId);

    @Query("""
           SELECT COUNT(r.id)
           FROM GuideReview r
           WHERE r.guide.id = :guideId
           """)
    Integer getTotalReviews(@Param("guideId") int guideId);
}

