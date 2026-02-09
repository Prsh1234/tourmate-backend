package com.example.tourmatebackend.dto.admin.dashboard;

public class AdminDashboardDTO {
    private long totalTravelers;
    private long activeGuides;
    private long totalBookings;
    private double revenue;

    private double travelersChange;
    private double guidesChange;
    private double bookingsChange;
    private double revenueChange;

    // Getters & Setters
    public long getTotalTravelers() { return totalTravelers; }
    public void setTotalTravelers(long totalTravelers) { this.totalTravelers = totalTravelers; }

    public long getActiveGuides() { return activeGuides; }
    public void setActiveGuides(long activeGuides) { this.activeGuides = activeGuides; }

    public long getTotalBookings() { return totalBookings; }
    public void setTotalBookings(long totalBookings) { this.totalBookings = totalBookings; }

    public double getRevenue() { return revenue; }
    public void setRevenue(double revenue) { this.revenue = revenue; }

    public double getTravelersChange() { return travelersChange; }
    public void setTravelersChange(double travelersChange) { this.travelersChange = travelersChange; }

    public double getGuidesChange() { return guidesChange; }
    public void setGuidesChange(double guidesChange) { this.guidesChange = guidesChange; }

    public double getBookingsChange() { return bookingsChange; }
    public void setBookingsChange(double bookingsChange) { this.bookingsChange = bookingsChange; }

    public double getRevenueChange() { return revenueChange; }
    public void setRevenueChange(double revenueChange) { this.revenueChange = revenueChange; }
}
