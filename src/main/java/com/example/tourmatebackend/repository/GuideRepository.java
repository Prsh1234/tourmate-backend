package com.example.tourmatebackend.repository;

import com.example.tourmatebackend.model.Guide;
import com.example.tourmatebackend.states.GuideStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuideRepository extends JpaRepository<Guide, Integer> {
    boolean existsByUserId(int userId);
    Page<Guide> findByStatusAndLocationContainingIgnoreCaseAndPriceBetween(
            GuideStatus status,
            String location,
            Double minPrice,
            Double maxPrice,
            Pageable pageable
    );
}
