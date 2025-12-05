package com.example.tourmatebackend.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import java.time.LocalTime;

@Entity
public class TourItinerary {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int stepNumber;         // 1, 2, 3, 4 ...
    private LocalTime time;         // 09:00 AM
    private String title;           // Patan Durbar Square

    @Column(length = 2000)
    private String description;     // "Meet at main entrance..."

    @ManyToOne
    @JoinColumn(name = "tour_id")
    @JsonBackReference("tour-itinerary")
    private Tour tour;

    // ---------- Constructor ----------
    public TourItinerary() {}

    public TourItinerary(int stepNumber, LocalTime time, String title, String description, Tour tour) {
        this.stepNumber = stepNumber;
        this.time = time;
        this.title = title;
        this.description = description;
        this.tour = tour;
    }

    // ---------- Getters & Setters ----------
    public int getId() { return id; }

    public int getStepNumber() { return stepNumber; }
    public void setStepNumber(int stepNumber) { this.stepNumber = stepNumber; }

    public LocalTime getTime() { return time; }
    public void setTime(LocalTime time) { this.time = time; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Tour getTour() { return tour; }
    public void setTour(Tour tour) { this.tour = tour; }
}
