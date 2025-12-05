package com.example.tourmatebackend.repository;

import com.example.tourmatebackend.model.TourReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TourReviewRepository extends JpaRepository<TourReview, Integer> {
    List<TourReview> findByTourId(int tourId);
    boolean existsByUserIdAndTourId(int userId, int tourId);
}
