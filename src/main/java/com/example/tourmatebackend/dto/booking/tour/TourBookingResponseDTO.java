package com.example.tourmatebackend.dto.booking.tour;

import com.example.tourmatebackend.model.TourBooking;
public class TourBookingResponseDTO {

    private int bookingId;


    private int tourId;
    private String tourTitle;
    private String guideName;
    private double totalPrice;
    private int travellers;
    private String status;

    // Payment fields
    private String paymentStatus;


    private String paymentTransactionId;


    public TourBookingResponseDTO(TourBooking booking) {
        this.bookingId = booking.getId();
        this.tourId = booking.getTour().getId();
        this.tourTitle = booking.getTour().getTitle();

        this.guideName = booking.getGuide().getUser().getFirstName()
                + " "
                + booking.getGuide().getUser().getLastName();

        this.totalPrice = booking.getTotalPrice();
        this.travellers = booking.getTravellers();
        this.status = booking.getStatus().name();
        this.paymentStatus = booking.getPaymentStatus().name();
        this.paymentTransactionId = booking.getPaymentTransactionId();
    }

    // getters...
    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentTransactionId() {
        return paymentTransactionId;
    }

    public void setPaymentTransactionId(String paymentTransactionId) {
        this.paymentTransactionId = paymentTransactionId;
    }
    public int getBookingId() {
        return bookingId;
    }

    public void setBookingId(int bookingId) {
        this.bookingId = bookingId;
    }

    public int getTourId() {
        return tourId;
    }

    public void setTourId(int tourId) {
        this.tourId = tourId;
    }

    public String getTourTitle() {
        return tourTitle;
    }

    public void setTourTitle(String tourTitle) {
        this.tourTitle = tourTitle;
    }

    public String getGuideName() {
        return guideName;
    }

    public void setGuideName(String guideName) {
        this.guideName = guideName;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public int getTravellers() {
        return travellers;
    }

    public void setTravellers(int travellers) {
        this.travellers = travellers;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
