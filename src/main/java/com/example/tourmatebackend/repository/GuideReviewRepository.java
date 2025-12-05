package com.example.tourmatebackend.repository;

import com.example.tourmatebackend.model.GuideReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuideReviewRepository extends JpaRepository<GuideReview, Integer> {
    List<GuideReview> findByGuideId(int guideId);
    boolean existsByUserIdAndGuideId(int userId, int guideId);
}

