package com.example.tourmatebackend.repository;

import com.example.tourmatebackend.model.Guide;
import com.example.tourmatebackend.model.Tour;
import com.example.tourmatebackend.states.GuideStatus;
import com.example.tourmatebackend.states.TourStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TourRepository extends JpaRepository<Tour, Integer> {
    List<Tour> findByGuideId(int guideId);
    List<Tour> findByGuideIdAndStatus(int guideId, TourStatus status);

    List<Tour> findByStatus(TourStatus status);
    // Fetch POSTED tours with filters using Spring Data paging
    Page<Tour> findByStatusAndLocationContainingIgnoreCaseAndPriceBetweenAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
            TourStatus status,
            String location,
            Double minPrice,
            Double maxPrice,
            java.time.LocalDate startDate,
            java.time.LocalDate endDate,
            Pageable pageable
    );
}