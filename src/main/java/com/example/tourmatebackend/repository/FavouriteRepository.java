package com.example.tourmatebackend.repository;

import com.example.tourmatebackend.model.Favourite;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FavouriteRepository extends JpaRepository<Favourite, Long> {

    boolean existsByUserIdAndGuideId(int userId, int guideId);
    boolean existsByUserIdAndTourId(int userId, int tourId);

    void deleteByUserIdAndGuideId(int userId, int guideId);
    void deleteByUserIdAndTourId(int userId, int tourId);

    List<Favourite> findByUserId(int userId);
    List<Favourite> findByUserIdAndType(int userId, String type);
    int countByUser_IdAndType(int userId, String type);

    @Query("""
    SELECT COUNT(DISTINCT f.guide.id)
    FROM Favourite f
    WHERE f.user.id = :userId
      AND f.type = 'GUIDE'
      AND f.guide IS NOT NULL
      AND f.createdAt >= :fromDate
""")
    int countFavouriteGuidesFrom(
            @Param("userId") int userId,
            @Param("fromDate") LocalDateTime fromDate
    );
}
