package com.example.tourmatebackend.repository;

import com.example.tourmatebackend.model.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FavouriteRepository extends JpaRepository<Favourite, Long> {

    boolean existsByUserIdAndGuideId(int userId, int guideId);
    boolean existsByUserIdAndTourId(int userId, int tourId);

    void deleteByUserIdAndGuideId(int userId, int guideId);
    void deleteByUserIdAndTourId(int userId, int tourId);

    List<Favourite> findByUserId(int userId);
    List<Favourite> findByUserIdAndType(int userId, String type);
}
