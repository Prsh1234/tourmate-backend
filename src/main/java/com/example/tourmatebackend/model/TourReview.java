package com.example.tourmatebackend.model;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class TourReview {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Tour getTour() {
        return tour;
    }

    public void setTour(Tour tour) {
        this.tour = tour;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getReview() {
        return review;
    }

    public void setReview(String review) {
        this.review = review;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private User user;

    @ManyToOne
    private Tour tour;

    private int rating;
    private String review;

    private LocalDateTime createdAt = LocalDateTime.now();

    // ---- NEW FIELDS ----
    private String guideComment;        // Only ONE comment allowed

    public String getGuideComment() {
        return guideComment;
    }

    public void setGuideComment(String guideComment) {
        this.guideComment = guideComment;
    }

    public LocalDateTime getCommentAt() {
        return commentAt;
    }

    public void setCommentAt(LocalDateTime commentAt) {
        this.commentAt = commentAt;
    }

    private LocalDateTime commentAt;
}
