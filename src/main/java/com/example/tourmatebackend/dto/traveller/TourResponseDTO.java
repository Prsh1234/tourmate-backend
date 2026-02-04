package com.example.tourmatebackend.dto.traveller;

import com.example.tourmatebackend.dto.guide.TourItineraryDTO;
import com.example.tourmatebackend.states.Category;
import com.example.tourmatebackend.states.Language;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TourResponseDTO {

    private int id;
    private String name;
    private String description;
    private String location;
    private Double price;

    private List<Category> categories;
    private List<Language> languages;

    private String duration;

    private int maxGuests;

    // Guide info
    private int guideId;
    private String guideName;
    private String guideExpertise;
    private List<TourItineraryDTO> itineraries;



    private List<String> included;
    private List<String> notIncluded;
    private List<String> importantInformation;
    private boolean isFavorited;

    private byte[] tourPic;
    private Double averageRating;
    private int totalReviews;

    // Getters & Setters

    public Double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(Double averageRating) {
        this.averageRating = averageRating;
    }

    public int getTotalReviews() {
        return totalReviews;
    }

    public void setTotalReviews(int totalReviews) {
        this.totalReviews = totalReviews;
    }
    public TourResponseDTO(){
        // You can optionally initialize collections here to avoid NullPointerException
        this.categories = new ArrayList<>();
        this.languages = new ArrayList<>();
        this.itineraries = new ArrayList<>();
    }


    // Getters & Setters


    public byte[] getTourPic() {
        return tourPic;
    }

    public void setTourPic(byte[] tourPic) {
        this.tourPic = tourPic;
    }

    public void setItineraries(List<TourItineraryDTO> itineraries) {
        this.itineraries = itineraries;
    }

    public List<TourItineraryDTO> getItineraries() {
        return itineraries;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public int getMaxGuests() {
        return maxGuests;
    }

    public void setMaxGuests(int maxGuests) {
        this.maxGuests = maxGuests;
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

    public List<String> getIncluded() {
        return included;
    }

    public void setIncluded(List<String> included) {
        this.included = included;
    }

    public List<String> getNotIncluded() {
        return notIncluded;
    }

    public void setNotIncluded(List<String> notIncluded) {
        this.notIncluded = notIncluded;
    }

    public List<String> getImportantInformation() {
        return importantInformation;
    }

    public void setImportantInformation(List<String> importantInformation) {
        this.importantInformation = importantInformation;
    }
    public boolean isFavorited() {
        return isFavorited;
    }

    public void setFavorited(boolean favorited) {
        isFavorited = favorited;
    }
}
