package com.example.tourmatebackend.service;


import com.example.tourmatebackend.dto.admin.dashboard.AdminDashboardDTO;
import com.example.tourmatebackend.dto.admin.dashboard.RecentBookingDTO;
import com.example.tourmatebackend.dto.admin.dashboard.TopGuideDTO;
import com.example.tourmatebackend.repository.*;
import com.example.tourmatebackend.states.Role;
import com.example.tourmatebackend.states.GuideStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AdminService {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private GuideRepository guideRepo;
    @Autowired
    private TourBookingRepository bookingRepo;

    public AdminDashboardDTO getDashboardStats() {
        AdminDashboardDTO stats = new AdminDashboardDTO();

        // total counts
        long totalTravelers = userRepo.countByRole(Role.TRAVELLER);
        long totalGuides = guideRepo.countByStatus(GuideStatus.APPROVED);
        long totalBookings = bookingRepo.count();
        double totalRevenue = bookingRepo.sumRevenueSince(LocalDateTime.of(2000,1,1,0,0)); // all time

        // counts last week for percentage
        LocalDateTime lastWeek = LocalDateTime.now().minusWeeks(1);
        long travelersLastWeek = userRepo.countByJoinedAfter(lastWeek.toLocalDate());
        long guidesLastWeek = guideRepo.countByJoinedAfter(lastWeek.toLocalDate());
        long bookingsLastWeek = bookingRepo.countByBookingDateAfter(lastWeek);
        Double revenueLastWeek = bookingRepo.sumRevenueSince(lastWeek);
        if (revenueLastWeek == null) revenueLastWeek = 0.0;

        stats.setTotalTravelers(totalTravelers);
        stats.setActiveGuides(totalGuides);
        stats.setTotalBookings(totalBookings);
        stats.setRevenue(totalRevenue);

        // percentage change calculation
        stats.setTravelersChange(calcPercentageChange(totalTravelers - travelersLastWeek, travelersLastWeek));
        stats.setGuidesChange(calcPercentageChange(totalGuides - guidesLastWeek, guidesLastWeek));
        stats.setBookingsChange(calcPercentageChange(totalBookings - bookingsLastWeek, bookingsLastWeek));
        stats.setRevenueChange(calcPercentageChange(totalRevenue - revenueLastWeek, revenueLastWeek));

        return stats;
    }
    public List<RecentBookingDTO> getRecentBookings() {

        return bookingRepo
                .findRecentBookings(PageRequest.of(0, 5))
                .stream()
                .map(b -> {
                    RecentBookingDTO dto = new RecentBookingDTO();
                    dto.setTourName(b.getTour().getName());
                    dto.setTravelerName(
                            b.getUser().getFirstName() + " " + b.getUser().getLastName()
                    );
                    dto.setBookingDate(b.getBookingDate());
                    dto.setTotalPrice(b.getTotalPrice());
                    dto.setStatus(b.getStatus().name());
                    return dto;
                })
                .toList();
    }

    public List<TopGuideDTO> getTopGuides() {

        return bookingRepo
                .findTopGuidesThisMonth(PageRequest.of(0, 5))
                .stream()
                .map(row -> {
                    TopGuideDTO dto = new TopGuideDTO();
                    dto.setName((String) row[1]);
                    dto.setLocation((String) row[2]);
                    dto.setEarnings((Double) row[3]);
                    dto.setAvgRating(
                            Math.round(((Double) row[4]) * 10.0) / 10.0
                    );
                    return dto;
                })
                .toList();
    }
    private double calcPercentageChange(double change, double oldValue) {
        if (oldValue == 0) return 0.0;
        return (change / oldValue) * 100.0;
    }
}
