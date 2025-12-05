package com.example.tourmatebackend.dto.guide;

import java.time.LocalTime;

public class TourItineraryDTO {

    private int id;
    private int stepNumber;
    private LocalTime time;
    private String title;
    private String description;

    public TourItineraryDTO() {}

    public TourItineraryDTO(
            int id,
            int stepNumber,
            LocalTime time,
            String title,
            String description
    ) {
        this.id = id;
        this.stepNumber = stepNumber;
        this.time = time;
        this.title = title;
        this.description = description;
    }

    // Getters
    public int getId() { return id; }
    public int getStepNumber() { return stepNumber; }
    public LocalTime getTime() { return time; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setStepNumber(int stepNumber) { this.stepNumber = stepNumber; }
    public void setTime(LocalTime time) { this.time = time; }
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
}
