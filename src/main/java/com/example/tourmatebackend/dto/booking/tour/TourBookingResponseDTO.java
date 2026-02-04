package com.example.tourmatebackend.dto.booking.tour;

import com.example.tourmatebackend.model.Tour;
import com.example.tourmatebackend.model.TourBooking;
import jakarta.persistence.Column;
import jakarta.persistence.Lob;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TourBookingResponseDTO {

    private int bookingId;


    private int tourId;
    private String tourName;
    private String guideName;
    private double totalPrice;
    private int travellers;
    private String status;

    // Payment fields
    private String paymentStatus;


    private String paymentTransactionId;
    private Tour tour;
    private LocalDateTime bookingDate = LocalDateTime.now();
    private LocalDate startDate;

    public TourBookingResponseDTO(TourBooking booking) {
        this.bookingId = booking.getId();
        this.tourId = booking.getTour().getId();
        this.tourName = booking.getTour().getName();

        this.guideName = booking.getGuide().getUser().getFirstName()
                + " "
                + booking.getGuide().getUser().getLastName();

        this.totalPrice = booking.getTotalPrice();
        this.travellers = booking.getTravellers();
        this.status = booking.getStatus().name();
        this.paymentStatus = booking.getPaymentStatus().name();
        this.paymentTransactionId = booking.getPaymentTransactionId();
        this.tour = booking.getTour();
        this.bookingDate = booking.getBookingDate();
        this.startDate = booking.getStartDate();
    }

    // getters...


    public LocalDateTime getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(LocalDateTime bookingDate) {
        this.bookingDate = bookingDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public Tour getTour() {
        return tour;
    }

    public void setTour(Tour tour) {
        this.tour = tour;
    }



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

    public String getTourName() {
        return tourName;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
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
