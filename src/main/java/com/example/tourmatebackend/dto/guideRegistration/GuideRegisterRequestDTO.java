package com.example.tourmatebackend.dto.guideRegistration;


import com.example.tourmatebackend.states.Category;
import com.example.tourmatebackend.states.Language;

import java.util.List;

public class GuideRegisterRequestDTO {

    private String expertise;
    private String bio;
    private String location;
    private Double price;
    private List<Category> categories;
    private List<Language> languages;

    public GuideRegisterRequestDTO() {}

    public String getExpertise() {
        return expertise;
    }

    public void setExpertise(String expertise) {
        this.expertise = expertise;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
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
}
