package com.example.tourmatebackend.repository;

import com.example.tourmatebackend.model.Guide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GuideRepository extends JpaRepository<Guide, Integer> {
    boolean existsByUserId(int userId);
}
