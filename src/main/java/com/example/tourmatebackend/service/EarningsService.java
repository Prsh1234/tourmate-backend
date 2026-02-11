package com.example.tourmatebackend.service;

import com.example.tourmatebackend.dto.guide.EarningsSummaryDTO;
import com.example.tourmatebackend.dto.guide.TransactionDTO;
import com.example.tourmatebackend.model.TourBooking;
import com.example.tourmatebackend.repository.TourBookingRepository;
import com.example.tourmatebackend.states.BookingStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class EarningsService {

    @Autowired
    private TourBookingRepository tourBookingRepository;



    private TransactionDTO mapTourBooking(TourBooking b) {
        TransactionDTO dto = new TransactionDTO();
        dto.setTourName(b.getTour().getName());
        dto.setDate(b.getBookingDate().toLocalDate());
        dto.setAmount(b.getTotalPrice());
        dto.setStatus(b.getStatus().name().toLowerCase());
        return dto;
    }
    public EarningsSummaryDTO getGuideEarnings(int guideId) {


        // Earnings from tours created by the guide
        Double tourCurrent = tourBookingRepository.getTourEarningsThisMonth(guideId);
        Double tourLast = tourBookingRepository.getTourEarningsLastMonth(guideId);

        double currentMonth =(tourCurrent != null ? tourCurrent : 0);

        double lastMonth =(tourLast != null ? tourLast : 0);

        double growth = 0;
        if (lastMonth > 0) {
            growth = ((currentMonth - lastMonth) / lastMonth) * 100;
        }
        Double totalTour = tourBookingRepository.getTourTotalEarnings(guideId);

        double totalEarnings = (totalTour == null ? 0 : totalTour);


        Double pendingTour = tourBookingRepository.getTourPendingPayments(guideId);

        double pendingPayout = (pendingTour == null ? 0 : pendingTour);

        List<BookingStatus> validStatuses = List.of(BookingStatus.COMPLETED, BookingStatus.APPROVED);

        List<TourBooking> tourBookings = tourBookingRepository
                .findTop10ByGuideIdAndStatusInOrderByBookingDateDesc(guideId, validStatuses);
        List<TransactionDTO> transactionDTOs = tourBookings.stream()
                .map(this::mapTourBooking)
                .sorted(Comparator.comparing(TransactionDTO::getDate).reversed())
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
