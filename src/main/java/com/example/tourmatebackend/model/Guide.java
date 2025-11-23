package com.example.tourmatebackend.model;

import com.example.tourmatebackend.states.Category;
import com.example.tourmatebackend.states.GuideStatus;
import com.example.tourmatebackend.states.Language;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
public class Guide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String expertise;
    private String bio;

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

    private String location;

    private double price;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "guide_categories", joinColumns = @JoinColumn(name = "guide_id"))
    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private Set<Category> categories;

    @ElementCollection(targetClass = Language.class)
    @Enumerated(EnumType.STRING)
    private Set<Language> languages;

    @Enumerated(EnumType.STRING)
    private GuideStatus status = GuideStatus.PENDING; // PENDING, APPROVED, REJECTED

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    @JsonBackReference("user-guide")
    private User user;

    @OneToMany(mappedBy = "guide", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference("guide-tour")
    private List<Tour> tours; // ✅ Added — list of tours created by this guide
    @Transient
    private byte[] profilePic;

    public byte[] getProfilePic() {
        // fetch from User entity automatically
        if (user != null) {
            return user.getProfilePic();
        }
        return null;
    }


    // Getters & Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

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

    public GuideStatus getStatus() {
        return status;
    }
    public void setStatus(GuideStatus status) {
        this.status = status;
    }

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }

    public List<Tour> getTours() {
        return tours;
    }
    public void setTours(List<Tour> tours) {
        this.tours = tours;
    }
    public Set<Category> getCategories() {
        return categories;
    }

    public void setCategories(Set<Category> categories) {
        this.categories = categories;
    }

    public void setProfilePic(byte[] profilePic) {
        this.profilePic = profilePic;
    }
    public Set<Language> getLanguages() {
        return languages;
    }

    public void setLanguages(Set<Language> languages) {
        this.languages = languages;
    }
}
