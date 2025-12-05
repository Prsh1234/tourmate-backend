package com.example.tourmatebackend.repository;

import com.example.tourmatebackend.model.GuideBooking;
import com.example.tourmatebackend.model.TourBooking;
import com.example.tourmatebackend.states.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TourBookingRepository extends JpaRepository<TourBooking, Integer> {
    Page<TourBooking> findByUserId(int userId, Pageable pageable);
    Page<TourBooking> findByGuideId(int guideId, Pageable pageable); // guide's bookings

    Page<TourBooking> findByGuideIdAndStatusIn(int guideId, List<BookingStatus> statuses, Pageable pageable);
    Page<TourBooking> findByUserIdAndStatusIn(int userId, List<BookingStatus> statuses, Pageable pageable);
    boolean existsByUserIdAndTourIdAndStatus(int userId, int tourId, BookingStatus status);

}
