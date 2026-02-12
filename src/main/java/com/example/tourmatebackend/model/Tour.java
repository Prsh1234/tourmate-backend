package com.example.tourmatebackend.model;

import com.example.tourmatebackend.states.Category;
import com.example.tourmatebackend.states.Language;
import com.example.tourmatebackend.states.TourStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.List;

@Entity
public class Tour {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String name;

    @Column(length = 2000)
    private String description;

    private String location;

    private double price;

    private String duration;

    private int maxGuests;
    @Lob
    @Column(name = "tourPic", columnDefinition = "LONGBLOB")
    private byte[] tourPic;

    @Enumerated(EnumType.STRING)
    private TourStatus status = TourStatus.DRAFTED; // DRAFTED, POSTED, CANCELLED, COMPLETED

    @ManyToOne
    @JoinColumn(name = "guide_id", nullable = false)
    @JsonBackReference("guide-tour")
    private Guide guide; // The guide who created this tour
//
//    @ManyToOne
//    @JoinColumn(name = "traveller_id")
//    @JsonBackReference("user-tour")
//    private User traveller; // The user who booked this tour (optional)

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
    @ElementCollection
    @CollectionTable(name = "tour_included", joinColumns = @JoinColumn(name = "tour_id"))
    @Column(name = "included_item")
    private List<String> included;

    @ElementCollection
    @CollectionTable(name = "tour_not_included", joinColumns = @JoinColumn(name = "tour_id"))
    @Column(name = "not_included_item")
    private List<String> notIncluded;

    @ElementCollection
    @CollectionTable(name = "tour_important_info", joinColumns = @JoinColumn(name = "tour_id"))
    @Column(name = "info_item")
    private List<String> importantInformation;
    @OneToMany(mappedBy = "tour", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<TourBooking> bookings;


    public List<TourBooking> getBookings() {
        return bookings;
    }

    public void setBookings(List<TourBooking> bookings) {
        this.bookings = bookings;
    }

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

    public byte[] getTourPic() {
        return tourPic;
    }

    public void setTourPic(byte[] tourPic) {
        this.tourPic = tourPic;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public List<String> getIncluded() { return included; }
    public void setIncluded(List<String> included) { this.included = included; }

    public List<String> getNotIncluded() { return notIncluded; }
    public void setNotIncluded(List<String> notIncluded) { this.notIncluded = notIncluded; }

    public List<String> getImportantInformation() { return importantInformation; }
    public void setImportantInformation(List<String> importantInformation) {
        this.importantInformation = importantInformation;
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


}
