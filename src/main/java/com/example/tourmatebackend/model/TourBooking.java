package com.example.tourmatebackend.model;

import com.example.tourmatebackend.states.BookingStatus;
import com.example.tourmatebackend.states.PaymentStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class TourBooking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private Tour tour;

    @ManyToOne
    private User user;

    @ManyToOne
    @JoinColumn(name = "guide_id")
    private Guide guide;

    public Guide getGuide() { return guide; }
    public void setGuide(Guide guide) { this.guide = guide; }
    private int travellers;
    private double totalPrice;

    private LocalDateTime bookingDate = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private BookingStatus status = BookingStatus.PENDING;


    // Payment simulation
    // Payment fields
    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.PENDING;
    private String paymentTransactionId; // mock transaction ID
    private LocalDateTime paymentDate;
    // Getters & Setters
    public PaymentStatus getPaymentStatus() { return paymentStatus; }
    public void setPaymentStatus(PaymentStatus paymentStatus) { this.paymentStatus = paymentStatus; }


    public String getPaymentTransactionId() {
        return paymentTransactionId;
    }

    public void setPaymentTransactionId(String paymentTransactionId) {
        this.paymentTransactionId = paymentTransactionId;
    }

    public LocalDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(LocalDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Tour getTour() { return tour; }
    public void setTour(Tour tour) { this.tour = tour; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public int getTravellers() { return travellers; }
    public void setTravellers(int travellers) { this.travellers = travellers; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    public LocalDateTime getBookingDate() { return bookingDate; }
    public void setBookingDate(LocalDateTime bookingDate) { this.bookingDate = bookingDate; }

    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
}
