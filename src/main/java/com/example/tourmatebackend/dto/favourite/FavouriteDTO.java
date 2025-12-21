package com.example.tourmatebackend.dto.favourite;

import com.example.tourmatebackend.dto.traveller.GuideResponseDTO;
import com.example.tourmatebackend.dto.traveller.TourResponseDTO;
import java.time.LocalDateTime;

public class FavouriteDTO {

    private Long id;
    private String type;
    private GuideResponseDTO guide;
    private TourResponseDTO tour;

    private double averageRating;
    private double totalReviews;
    private LocalDateTime createdAt;

    public FavouriteDTO(Long id, String type, GuideResponseDTO guide, TourResponseDTO tour, LocalDateTime createdAt, double averageRating, double totalReviews) {
        this.id = id;
        this.type = type;
        this.guide = guide;
        this.tour = tour;
        this.createdAt = createdAt;
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
    }


// getters + setters


    public GuideResponseDTO getGuide() {
        return guide;
    }

    public void setGuide(GuideResponseDTO guide) {
        this.guide = guide;
    }

    public TourResponseDTO getTour() {
        return tour;
    }

    public void setTour(TourResponseDTO tour) {
        this.tour = tour;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public double getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(double totalReviews) {
        this.totalReviews = totalReviews;
    }
}
