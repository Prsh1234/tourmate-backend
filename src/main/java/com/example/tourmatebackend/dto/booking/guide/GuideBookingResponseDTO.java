package com.example.tourmatebackend.dto.booking.guide;

import com.example.tourmatebackend.model.GuideBooking;

public class GuideBookingResponseDTO {
    private int bookingId;
    private int guideId;
    private String guideName;
    private double totalPrice;
    private int hours;
    private int groupSize;
    private String status;

    public GuideBookingResponseDTO(GuideBooking booking) {
        this.bookingId = booking.getId();
        this.guideId = booking.getGuide().getId();
        this.guideName = booking.getGuide().getUser().getFirstName()
                + " "
                + booking.getGuide().getUser().getLastName();
        this.totalPrice = booking.getTotalPrice();
        this.hours = booking.getHours();
        this.groupSize = booking.getGroupSize();
        this.status = booking.getStatus().name(); // PENDING, APPROVED, REJECTED
    }

    // Getters only (Spring can serialize)
    public int getBookingId() { return bookingId; }
    public int getGuideId() { return guideId; }
    public String getGuideName() { return guideName; }
    public double getTotalPrice() { return totalPrice; }
    public int getHours() { return hours; }
    public int getGroupSize() { return groupSize; }
    public String getStatus() { return status; }
}
