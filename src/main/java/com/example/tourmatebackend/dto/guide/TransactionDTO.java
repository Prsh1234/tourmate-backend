package com.example.tourmatebackend.dto.guide;



import java.time.LocalDate;

public class TransactionDTO {

    private String tourName;
    private LocalDate date;
    private double amount;
    private String status; // "completed", "pending"

    // Getters & Setters


    public String getTourName() {
        return tourName;
    }

    public void setTourName(String tourName) {
        this.tourName = tourName;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
