package com.example.tourmatebackend.repository;

import com.example.tourmatebackend.model.Guide;
import com.example.tourmatebackend.states.GuideStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface GuideRepository extends JpaRepository<Guide, Integer> {
    Optional<Guide> findByUserId(int userId);

    Page<Guide> findByStatus(GuideStatus status, Pageable pageable);

    Page<Guide> findByStatusContainingIgnoreCaseAndPriceBetween(
            GuideStatus status,
            Double minPrice,
            Double maxPrice,
            Pageable pageable
    );
}
