package com.example.tourmatebackend.service;

import com.example.tourmatebackend.dto.guide.EarningsSummaryDTO;
import com.example.tourmatebackend.dto.guide.TransactionDTO;
import com.example.tourmatebackend.model.GuideBooking;
import com.example.tourmatebackend.model.TourBooking;
import com.example.tourmatebackend.repository.GuideBookingRepository;
import com.example.tourmatebackend.repository.TourBookingRepository;
import com.example.tourmatebackend.states.BookingStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

@Service
public class EarningsService {

    @Autowired
    private GuideBookingRepository guideBookingRepository;
    @Autowired
    private TourBookingRepository tourBookingRepository;

    private TransactionDTO mapGuideBooking(GuideBooking b) {
        TransactionDTO dto = new TransactionDTO();
        dto.setTourName(b.getGuide().getUser().getFirstName()); // adjust field name
        dto.setDate(b.getBookingDate().toLocalDate());
        dto.setAmount(b.getTotalPrice());
        dto.setStatus(b.getStatus().name().toLowerCase());
        return dto;
    }

    private TransactionDTO mapTourBooking(TourBooking b) {
        TransactionDTO dto = new TransactionDTO();
        dto.setTourName(b.getTour().getName());
        dto.setDate(b.getBookingDate().toLocalDate());
        dto.setAmount(b.getTotalPrice());
        dto.setStatus(b.getStatus().name().toLowerCase());
        return dto;
    }
    public EarningsSummaryDTO getGuideEarnings(int guideId) {
        Double guideCurrent = guideBookingRepository.getGuideEarningsThisMonth(guideId);
        Double guideLast = guideBookingRepository.getGuideEarningsLastMonth(guideId);

        // Earnings from tours created by the guide
        Double tourCurrent = tourBookingRepository.getTourEarningsThisMonth(guideId);
        Double tourLast = tourBookingRepository.getTourEarningsLastMonth(guideId);

        double currentMonth =
                (guideCurrent != null ? guideCurrent : 0) +
                        (tourCurrent != null ? tourCurrent : 0);

        double lastMonth =
                (guideLast != null ? guideLast : 0) +
                        (tourLast != null ? tourLast : 0);

        double growth = 0;
        if (lastMonth > 0) {
            growth = ((currentMonth - lastMonth) / lastMonth) * 100;
        }
        Double totalGuide = guideBookingRepository.getGuideTotalEarnings(guideId);
        Double totalTour = tourBookingRepository.getTourTotalEarnings(guideId);

        double totalEarnings =
                (totalGuide == null ? 0 : totalGuide) +
                        (totalTour == null ? 0 : totalTour);


        Double pendingGuide = guideBookingRepository.getGuidePendingPayments(guideId);
        Double pendingTour = tourBookingRepository.getTourPendingPayments(guideId);

        double pendingPayout =
                (pendingGuide == null ? 0 : pendingGuide) +
                        (pendingTour == null ? 0 : pendingTour);

        List<BookingStatus> validStatuses = List.of(BookingStatus.COMPLETED, BookingStatus.APPROVED);

        List<GuideBooking> guideBookings = guideBookingRepository
                .findTop10ByGuideIdAndStatusInOrderByBookingDateDesc(guideId, validStatuses);

        List<TourBooking> tourBookings = tourBookingRepository
                .findTop10ByGuideIdAndStatusInOrderByBookingDateDesc(guideId, validStatuses);
        List<TransactionDTO> transactionDTOs = Stream.concat(
                        guideBookings.stream().map(this::mapGuideBooking),
                        tourBookings.stream().map(this::mapTourBooking)
                )
                .sorted(Comparator.comparing(TransactionDTO::getDate).reversed())
                .limit(10)
                .toList();
        EarningsSummaryDTO summary = new EarningsSummaryDTO();
        summary.setMonthlyEarnings(currentMonth);
        summary.setMonthlyGrowthPercent(growth);
        summary.setTotalEarnings(totalEarnings);
        summary.setPendingPayout(pendingPayout);
        summary.setRecentTransactions(transactionDTOs);

        return summary;
    }
}
