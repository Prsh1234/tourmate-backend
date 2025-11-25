package com.example.tourmatebackend.dto.traveller;

import com.example.tourmatebackend.states.Category;
import com.example.tourmatebackend.states.Language;

import java.time.LocalDate;
import java.util.List;

public class TourResponseDTO {

    private int id;
    private String title;
    private String description;
    private String location;
    private Double price;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<Category> categories;
    private List<Language> languages;

    // Guide info
    private int guideId;
    private String guideName;
    private String guideExpertise;

    // Getters & Setters

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getLocation() {
        return location;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public Double getPrice() {
        return price;
    }
    public void setPrice(Double price) {
        this.price = price;
    }
    public LocalDate getStartDate() {
        return startDate;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    public LocalDate getEndDate() {
        return endDate;
    }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    public List<Category> getCategories() {
        return categories;
    }
    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }
    public List<Language> getLanguages() {
        return languages;
    }
    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }
    public int getGuideId() {
        return guideId;
    }
    public void setGuideId(int guideId) {
        this.guideId = guideId;
    }
    public String getGuideName() {
        return guideName;
    }
    public void setGuideName(String guideName) {
        this.guideName = guideName;
    }
    public String getGuideExpertise() {
        return guideExpertise;
    }
    public void setGuideExpertise(String guideExpertise) {
        this.guideExpertise = guideExpertise;
    }
}
