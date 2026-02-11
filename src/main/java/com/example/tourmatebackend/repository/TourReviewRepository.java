package com.example.tourmatebackend.repository;

import com.example.tourmatebackend.model.GuideReview;
import com.example.tourmatebackend.model.TourReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface TourReviewRepository extends JpaRepository<TourReview, Integer> {
    List<TourReview> findByTourId(int tourId);
    List<TourReview> findByTour_Guide_Id(int guideId);
    boolean existsByUserIdAndTourId(int userId, int tourId);


    long countByTourId(int tourId);

    @Query("SELECT AVG(r.rating) FROM TourReview r WHERE r.tour.id = :tourId")
    Double findAverageRatingByTourId(int tourId);
}
