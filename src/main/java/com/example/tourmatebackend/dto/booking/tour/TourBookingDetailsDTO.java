package com.example.tourmatebackend.dto.booking.tour;


import com.example.tourmatebackend.model.TourBooking;

public class TourBookingDetailsDTO extends TourBookingResponseDTO {

    private double averageRating;
    private long reviewCount;

    public TourBookingDetailsDTO(TourBooking booking, double averageRating, long reviewCount) {
        super(booking); // populate existing fields
        this.averageRating = averageRating;
        this.reviewCount = reviewCount;
    }

    // getters
    public double getAverageRating() {
        return averageRating;
    }

    public long getReviewCount() {
        return reviewCount;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public void setReviewCount(long reviewCount) {
        this.reviewCount = reviewCount;
    }
}
