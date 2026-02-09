package com.example.tourmatebackend.service;

import com.example.tourmatebackend.dto.guide.GuideDashboardDTO;
import com.example.tourmatebackend.dto.traveller.TravellerDashboardDTO;
import com.example.tourmatebackend.repository.FavouriteRepository;
import com.example.tourmatebackend.repository.TourBookingRepository;
import com.example.tourmatebackend.states.BookingStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
public class TravellerService {

    private final TourBookingRepository tourBookingRepository;
    private final FavouriteRepository favouriteRepository;

    public TravellerService(TourBookingRepository tourBookingRepository,
                            FavouriteRepository favouriteRepository) {
        this.tourBookingRepository = tourBookingRepository;
        this.favouriteRepository = favouriteRepository;
    }

    public TravellerDashboardDTO getTravellerDashboard(int userId) {

        TravellerDashboardDTO dto = new TravellerDashboardDTO();

        // Total bookings
        int totalBookings =
                tourBookingRepository.countByUser_Id(userId);

        // Upcoming trips (future + confirmed)
        int upcomingTrips =
                tourBookingRepository.countByUser_IdAndStartDateAfterAndStatus(
                        userId,
                        LocalDate.now(),
                        BookingStatus.APPROVED
                );

        // Favourite guides
        int favouriteGuides =
                favouriteRepository.countByUser_IdAndType(userId, "GUIDE");
        LocalDate nextTripDate =
                tourBookingRepository.findNextUpcomingTripDate(userId);

        Integer nextTripInDays = null;
        if (nextTripDate != null) {
            nextTripInDays =
                    (int) (nextTripDate.toEpochDay() - LocalDate.now().toEpochDay());
        }

        LocalDateTime fromDate = LocalDate.now().minusDays(7).atStartOfDay();
        int favouriteGuidesThisWeek = favouriteRepository.countFavouriteGuidesFrom(userId, fromDate);
        dto.setFavouriteGuidesThisWeek(favouriteGuidesThisWeek);
        dto.setNextTripInDays(nextTripInDays);

        dto.setTotalBookings(totalBookings);
        dto.setUpcomingTrips(upcomingTrips);
        dto.setFavouriteGuides(favouriteGuides);

        return dto;
    }
}

