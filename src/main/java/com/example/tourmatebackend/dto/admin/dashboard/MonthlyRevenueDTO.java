package com.example.tourmatebackend.dto.admin.dashboard;

public class MonthlyRevenueDTO {
    private String label;   // e.g. "Dec 2024", "Jan 2025"
    private double revenue;

    public MonthlyRevenueDTO(String label, double revenue) {
        this.label = label;
        this.revenue = revenue;
    }

    public String getLabel() { return label; }
    public double getRevenue() { return revenue; }
}
