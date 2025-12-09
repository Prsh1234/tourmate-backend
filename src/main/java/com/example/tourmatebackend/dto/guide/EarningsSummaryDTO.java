package com.example.tourmatebackend.dto.guide;


import java.util.List;

public class EarningsSummaryDTO {

    private double monthlyEarnings;
    private double monthlyGrowthPercent;

    private double totalEarnings;
    private double pendingPayout;

    private List<TransactionDTO> recentTransactions;

    // Getters & Setters


    public double getMonthlyEarnings() {
        return monthlyEarnings;
    }

    public void setMonthlyEarnings(double monthlyEarnings) {
        this.monthlyEarnings = monthlyEarnings;
    }

    public double getMonthlyGrowthPercent() {
        return monthlyGrowthPercent;
    }

    public void setMonthlyGrowthPercent(double monthlyGrowthPercent) {
        this.monthlyGrowthPercent = monthlyGrowthPercent;
    }

    public double getTotalEarnings() {
        return totalEarnings;
    }

    public void setTotalEarnings(double totalEarnings) {
        this.totalEarnings = totalEarnings;
    }

    public double getPendingPayout() {
        return pendingPayout;
    }

    public void setPendingPayout(double pendingPayout) {
        this.pendingPayout = pendingPayout;
    }

    public List<TransactionDTO> getRecentTransactions() {
        return recentTransactions;
    }

    public void setRecentTransactions(List<TransactionDTO> recentTransactions) {
        this.recentTransactions = recentTransactions;
    }
}
