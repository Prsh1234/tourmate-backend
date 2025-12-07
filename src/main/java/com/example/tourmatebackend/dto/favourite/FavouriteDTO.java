package com.example.tourmatebackend.dto.favourite;

import com.example.tourmatebackend.model.Favourite;
import com.example.tourmatebackend.model.Guide;
import com.example.tourmatebackend.model.Tour;
import com.example.tourmatebackend.states.FavouriteType;

import java.time.LocalDateTime;

public class FavouriteDTO {

    private Long id;
    private String type;

    private Guide guide;
    private Tour tour;
    private String guideName;

    private String tourTitle;

    private LocalDateTime createdAt;

    public FavouriteDTO(Long id, String type, Integer guideId, String guideName, Integer tourId, String tourTitle, LocalDateTime createdAt) {
        this.id = id;
        this.type = type;
        this.guideId = guideId;
        this.guideName = guideName;
        this.tourId = tourId;
        this.tourTitle = tourTitle;
        this.createdAt = createdAt;
    }

    // getters + setters


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

    public Integer getGuideId() {
        return guideId;
    }

    public void setGuideId(Integer guideId) {
        this.guideId = guideId;
    }

    public String getGuideName() {
        return guideName;
    }

    public void setGuideName(String guideName) {
        this.guideName = guideName;
    }

    public Integer getTourId() {
        return tourId;
    }

    public void setTourId(Integer tourId) {
        this.tourId = tourId;
    }

    public String getTourTitle() {
        return tourTitle;
    }

    public void setTourTitle(String tourTitle) {
        this.tourTitle = tourTitle;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
