package com.example.tourmatebackend.service;

import com.example.tourmatebackend.dto.guide.GuideDashboardDTO;
import com.example.tourmatebackend.dto.guideRegistration.GuideRegisterRequestDTO;
import com.example.tourmatebackend.model.Guide;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.GuideBookingRepository;
import com.example.tourmatebackend.repository.GuideRepository;
import com.example.tourmatebackend.repository.GuideReviewRepository;
import com.example.tourmatebackend.repository.TourBookingRepository;
import com.example.tourmatebackend.states.GuideStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class GuideService {

    @Autowired
    private GuideRepository guideRepository;

    @Autowired
    private GuideBookingRepository guideBookingRepository;

    @Autowired
    private TourBookingRepository tourBookingRepository;

    @Autowired
    private GuideReviewRepository reviewRepository;

    public GuideDashboardDTO getGuideDashboard(int guideId) {

        GuideDashboardDTO dto = new GuideDashboardDTO();

        // Earnings from guide bookings
        Double guideCurrent = guideBookingRepository.getGuideEarningsThisMonth(guideId);
        Double guideLast = guideBookingRepository.getGuideEarningsLastMonth(guideId);

        // Earnings from tours created by the guide
        Double tourCurrent = tourBookingRepository.getTourEarningsThisMonth(guideId);
        Double tourLast = tourBookingRepository.getTourEarningsLastMonth(guideId);

        double totalCurrent =
                (guideCurrent != null ? guideCurrent : 0) +
                        (tourCurrent != null ? tourCurrent : 0);

        double totalLast =
                (guideLast != null ? guideLast : 0) +
                        (tourLast != null ? tourLast : 0);

        // Set earnings
        dto.setMonthlyEarnings(totalCurrent);
        dto.setTotalLast(totalLast);

        // Calculate % change
        if (totalLast > 0) {
            dto.setEarningsChangePercent(((totalCurrent - totalLast) / totalLast) * 100);
        } else {
            dto.setEarningsChangePercent(0);
        }

        // Travelers
        Integer travelersGuide = guideBookingRepository.getTotalTravelers(guideId);
        Integer travelersTour = tourBookingRepository.getTotalTravelers(guideId);
        System.out.println(travelersGuide);
        System.out.println(travelersTour);

        Integer travelers =
                (travelersGuide == null ? 0 : travelersGuide) +
                        (travelersTour == null ? 0 : travelersTour);
        dto.setTotalTravelers(travelers != null ? travelers : 0);



        // Reviews
        Double rating = reviewRepository.getAverageRating(guideId);
        Integer reviews = reviewRepository.getTotalReviews(guideId);

        dto.setRating(rating != null ? rating : 0.0);
        dto.setTotalReviews(reviews != null ? reviews : 0);

        Double totalGuide = guideBookingRepository.getGuideTotalEarnings(guideId);
        Double totalTour = tourBookingRepository.getTourTotalEarnings(guideId);

        double totalEarnings =
                (totalGuide == null ? 0 : totalGuide) +
                        (totalTour == null ? 0 : totalTour);

        dto.setTotalEarnings(totalEarnings);
        return dto;

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

        if (profilePic != null && !profilePic.isEmpty()) {
            guide.setProfilePic(profilePic.getBytes());
        }

        if (governmentPic != null && !governmentPic.isEmpty()) {
            guide.setGovernmentPic(governmentPic.getBytes());
        }

        return guideRepository.save(guide);
    }



}
