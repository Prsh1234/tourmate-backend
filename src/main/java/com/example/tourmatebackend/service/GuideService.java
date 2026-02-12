package com.example.tourmatebackend.service;

import com.example.tourmatebackend.dto.guide.GuideDashboardDTO;
import com.example.tourmatebackend.dto.guideRegistration.GuideRegisterRequestDTO;
import com.example.tourmatebackend.model.Guide;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.*;
import com.example.tourmatebackend.states.GuideStatus;
import com.example.tourmatebackend.states.TourStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class GuideService {

    @Autowired
    private GuideRepository guideRepository;

    @Autowired
    private TourRepository tourRepository;

    @Autowired
    private TourBookingRepository tourBookingRepository;

    @Autowired
    private GuideReviewRepository reviewRepository;

    public GuideDashboardDTO getGuideDashboard(int guideId) {

        GuideDashboardDTO dto = new GuideDashboardDTO();


        // Earnings from tours created by the guide
        Double tourCurrent = tourBookingRepository.getTourEarningsThisMonth(guideId);
        Double tourLast = tourBookingRepository.getTourEarningsLastMonth(guideId);

        double totalCurrent =(tourCurrent != null ? tourCurrent : 0);

        double totalLast =(tourLast != null ? tourLast : 0);

        // Set earnings
        dto.setMonthlyEarnings(totalCurrent);
        dto.setTotalLast(totalLast);

        // Calculate % change
        double changePercent;

        if (totalLast == 0 && totalCurrent > 0) {
            // From 0 to something → full growth
            changePercent = 100;
        } else if (totalLast == 0 && totalCurrent == 0) {
            // No earnings both months
            changePercent = 0;
        } else {
            changePercent = ((totalCurrent - totalLast) / totalLast) * 100;
        }

        dto.setEarningsChangePercent(changePercent);

        // Travelers
        Integer travelersTour = tourBookingRepository.getTotalTravelers(guideId);


        Integer travelers =(travelersTour == null ? 0 : travelersTour);
        dto.setTotalTravelers(travelers != null ? travelers : 0);



        // Reviews
        Double rating = reviewRepository.getAverageRating(guideId);
        Integer reviews = reviewRepository.getTotalReviews(guideId);

        dto.setRating(rating != null ? rating : 0.0);
        dto.setTotalReviews(reviews != null ? reviews : 0);

        Double totalTour = tourBookingRepository.getTourTotalEarnings(guideId);

        double totalEarnings =(totalTour == null ? 0 : totalTour);
        int activeTours = tourRepository.countByGuide_IdAndStatus(
                guideId,
                TourStatus.POSTED
        );
        dto.setActiveTours(activeTours);
        dto.setTotalEarnings(totalEarnings);
        return dto;

    }

    public Guide findByUserId(int userId) {
        return guideRepository.findByUserId(userId).orElse(null);
    }
    public Guide registerGuide(User user, GuideRegisterRequestDTO dto, MultipartFile profilePic, MultipartFile governmentPic) throws IOException, IOException {
        Guide guide = new Guide();

        guide.setFullName(dto.getFullName());
        guide.setEmail(dto.getEmail());
        guide.setPhoneNumber(dto.getPhoneNumber());
        guide.setExperience(dto.getExperience());
        guide.setLanguages(dto.getLanguages());
        guide.setCategories(dto.getCategories());
        guide.setBio(dto.getBio());
        guide.setPrice(dto.getPrice());
        guide.setGovernmentNumber(dto.getGovernmentNumber());
        guide.setDob(dto.getDob());
        guide.setUser(user);
        guide.setLocation(dto.getLocation());
        guide.setBankName(dto.getBankName());
        guide.setAccountNumber(dto.getAccountNumber());
        guide.setAccountHolderName(dto.getAccountHolderName());

        if (profilePic != null && !profilePic.isEmpty()) {
            guide.setProfilePic(profilePic.getBytes());
        }

        if (governmentPic != null && !governmentPic.isEmpty()) {
            guide.setGovernmentPic(governmentPic.getBytes());
        }

        return guideRepository.save(guide);
    }



}
