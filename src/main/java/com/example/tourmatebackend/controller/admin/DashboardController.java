package com.example.tourmatebackend.controller.admin;


import com.example.tourmatebackend.dto.admin.dashboard.AdminDashboardDTO;
import com.example.tourmatebackend.dto.admin.dashboard.MonthlyRevenueDTO;
import com.example.tourmatebackend.dto.admin.dashboard.RecentBookingDTO;
import com.example.tourmatebackend.dto.admin.dashboard.TopGuideDTO;
import com.example.tourmatebackend.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class DashboardController {

    @Autowired
    private AdminService adminService;

    @GetMapping("/dashboard")
    public AdminDashboardDTO getDashboard() {

        return adminService.getDashboardStats();
    }

    @GetMapping("/recent-bookings")
    public List<RecentBookingDTO> getRecentBookings() {
        return adminService.getRecentBookings();
    }

    @GetMapping("/top-guides")
    public List<TopGuideDTO> getTopGuides() {
        return adminService.getTopGuides();
    }


    @GetMapping("/monthly-revenue")
    public List<MonthlyRevenueDTO> getMonthlyRevenue() {
        return adminService.getMonthlyRevenue();
    }
}
