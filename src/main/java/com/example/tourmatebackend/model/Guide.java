package com.example.tourmatebackend.model;

import com.example.tourmatebackend.states.Category;
import com.example.tourmatebackend.states.GuideStatus;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;

@Entity
public class Guide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String expertise;
    private String bio;

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public void setProfilePic(byte[] profilePic) {
        this.profilePic = profilePic;
    }

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "guide_categories", joinColumns = @JoinColumn(name = "guide_id"))
    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private List<Category> categories;
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
}
