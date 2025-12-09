package com.example.tourmatebackend.dto.guide;


public class GuideDashboardDTO {

    private double monthlyEarnings;
    private double earningsChangePercent;
    private double totalEarnings;
    private double totalLast;
    private int totalTravelers;

    private double rating;
    private int totalReviews;

    // Getters and Setters


    public double getTotalLast() {
        return totalLast;
    }

    public void setTotalLast(double totalLast) {
        this.totalLast = totalLast;
    }

    public double getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(double totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public double getMonthlyEarnings() { return monthlyEarnings; }
    public void setMonthlyEarnings(double monthlyEarnings) { this.monthlyEarnings = monthlyEarnings; }

    public double getEarningsChangePercent() { return earningsChangePercent; }
    public void setEarningsChangePercent(double earningsChangePercent) { this.earningsChangePercent = earningsChangePercent; }

    public int getTotalTravelers() { return totalTravelers; }
    public void setTotalTravelers(int totalTravelers) { this.totalTravelers = totalTravelers; }

    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }

    public int getTotalReviews() { return totalReviews; }
    public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }
}
