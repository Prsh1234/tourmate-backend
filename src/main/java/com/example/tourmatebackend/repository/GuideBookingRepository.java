package com.example.tourmatebackend.repository;

import com.example.tourmatebackend.model.GuideBooking;
import com.example.tourmatebackend.states.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GuideBookingRepository extends JpaRepository<GuideBooking, Integer> {
    Page<GuideBooking> findByGuideId(int guideId, Pageable pageable);
    Page<GuideBooking> findByUserId(int userId, Pageable pageable);
    // Get bookings for a guide filtered by a list of statuses
    Page<GuideBooking> findByGuideIdAndStatusIn(int guideId, List<BookingStatus> statuses, Pageable pageable);
    Page<GuideBooking> findByGuideIdAndStatus(int guideId, BookingStatus status, Pageable pageable);
    Page<GuideBooking> findByUserIdAndStatusIn(int userId, List<BookingStatus> statuses, Pageable pageable);
}
