package com.example.tourmatebackend.dto.admin.dashboard;


public class TopGuideDTO {

    private String name;
    private String location;
    private double earnings;
    private Double avgRating;
    // optional (future)

    // getters & setters

    public Double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getEarnings() {
        return earnings;
    }

    public void setEarnings(double earnings) {
        this.earnings = earnings;
    }


}
