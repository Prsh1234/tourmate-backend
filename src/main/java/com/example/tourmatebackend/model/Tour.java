package com.example.tourmatebackend.model;

import com.example.tourmatebackend.states.Category;
import com.example.tourmatebackend.states.Language;
import com.example.tourmatebackend.states.TourStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String title;

    @Column(length = 2000)
    private String description;

    private String location;

    private double price;

    private LocalDate startDate;
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    private TourStatus status = TourStatus.DRAFTED; // DRAFTED, POSTED, CANCELLED, COMPLETED

    @ManyToOne
    @JoinColumn(name = "guide_id", nullable = false)
    @JsonBackReference("guide-tour")
    private Guide guide; // The guide who created this tour

    @ManyToOne
    @JoinColumn(name = "traveller_id")
    @JsonBackReference("user-tour")
    private User traveller; // The user who booked this tour (optional)

    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("tour-itinerary")
    private List<TourItinerary> itineraries;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "tour_categories", joinColumns = @JoinColumn(name = "tour_id"))
    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private List<Category> categories;


    @ElementCollection(targetClass = Language.class)
    @Enumerated(EnumType.STRING)
    private List<Language> languages;


    public List<TourItinerary> getItineraries() {
        return itineraries;
    }

    public void setItineraries(List<TourItinerary> itineraries) {
        this.itineraries = itineraries;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
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

    public TourStatus getStatus() {
        return status;
    }

    public void setStatus(TourStatus status) {
        this.status = status;
    }

    public Guide getGuide() {
        return guide;
    }

    public void setGuide(Guide guide) {
        this.guide = guide;
    }

    public User getTraveller() {
        return traveller;
    }

    public void setTraveller(User traveller) {
        this.traveller = traveller;
    }
}
