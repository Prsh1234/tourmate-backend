package com.example.tourmatebackend.dto.booking.tour;

public class TourBookingRequestDTO {
    private int tourId;
    private int userId;
    private int guideId;   // NEW
    private int travellers;

    public int getTourId() { return tourId; }
    public void setTourId(int tourId) { this.tourId = tourId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getGuideId() { return guideId; }
    public void setGuideId(int guideId) { this.guideId = guideId; }

    public int getTravellers() { return travellers; }
    public void setTravellers(int travellers) { this.travellers = travellers; }
}
