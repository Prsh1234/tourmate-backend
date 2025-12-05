package com.example.tourmatebackend.dto.review;


public class CreateReviewDTO {
    private int rating;   // 1–5

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

    private String review;
}
