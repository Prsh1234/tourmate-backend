package com.example.tourmatebackend.dto.traveller;

public class TravellerDashboardDTO {
    private int upcomingTrips;
    private int favouriteGuides;
    private int totalBookings;
    private Integer nextTripInDays;
    private int favouriteGuidesThisWeek;


    public int getFavouriteGuidesThisWeek() {
        return favouriteGuidesThisWeek;
    }

    public void setFavouriteGuidesThisWeek(int favouriteGuidesThisWeek) {
        this.favouriteGuidesThisWeek = favouriteGuidesThisWeek;
    }

    public int getUpcomingTrips() {
        return upcomingTrips;
    }

    public void setUpcomingTrips(int upcomingTrips) {
        this.upcomingTrips = upcomingTrips;
    }

    public int getFavouriteGuides() {
        return favouriteGuides;
    }

    public void setFavouriteGuides(int favouriteGuides) {
        this.favouriteGuides = favouriteGuides;
    }

    public int getTotalBookings() {
        return totalBookings;
    }

    public void setTotalBookings(int totalBookings) {
        this.totalBookings = totalBookings;
    }

    public Integer getNextTripInDays() {
        return nextTripInDays;
    }

    public void setNextTripInDays(Integer nextTripInDays) {
        this.nextTripInDays = nextTripInDays;
    }
}
