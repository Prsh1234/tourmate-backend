package com.example.tourmatebackend.model;



import com.example.tourmatebackend.states.GuideStatus;
import jakarta.persistence.*;

@Entity
public class Guide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String expertise;
    private String bio;

    @Enumerated(EnumType.STRING)
    private GuideStatus status = GuideStatus.PENDING; // PENDING, APPROVED, REJECTED

    @OneToOne
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

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
}
