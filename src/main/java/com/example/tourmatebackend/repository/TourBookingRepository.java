package com.example.tourmatebackend.repository;

import com.example.tourmatebackend.model.GuideBooking;
import com.example.tourmatebackend.model.TourBooking;
import com.example.tourmatebackend.states.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TourBookingRepository extends JpaRepository<TourBooking, Integer> {
    Page<TourBooking> findByUserId(int userId, Pageable pageable);
    Page<TourBooking> findByGuideId(int guideId, Pageable pageable); // guide's bookings

    Page<TourBooking> findByGuideIdAndStatusIn(int guideId, List<BookingStatus> statuses, Pageable pageable);
    Page<TourBooking> findByUserIdAndStatusIn(int userId, List<BookingStatus> statuses, Pageable pageable);
    boolean existsByUserIdAndTourIdAndStatus(int userId, int tourId, BookingStatus status);
    @Query("""
           SELECT SUM(b.totalPrice)
           FROM TourBooking b
           WHERE b.tour.guide.id = :guideId
             AND MONTH(b.bookingDate) = MONTH(CURRENT_DATE)
             AND YEAR(b.bookingDate) = YEAR(CURRENT_DATE)
             AND b.paymentStatus = "PAID"
             
           """)
    Double getTourEarningsThisMonth(@Param("guideId") int guideId);


    @Query("""
           SELECT SUM(b.totalPrice)
           FROM TourBooking b
           WHERE b.tour.guide.id = :guideId
             AND MONTH(b.paymentDate) = MONTH(CURRENT_DATE) - 1
             AND YEAR(b.paymentDate) = YEAR(CURRENT_DATE)
             AND b.paymentStatus = "PAID"
             
           """)
    Double getTourEarningsLastMonth(@Param("guideId") int guideId);

    @Query("""
           SELECT SUM(b.totalPrice)
           FROM TourBooking b
           WHERE b.tour.guide.id = :guideId
           AND b.paymentStatus = "PAID"
           """)
    Double getTourTotalEarnings(@Param("guideId") int guideId);
    @Query("""
           SELECT SUM(b.travellers)
           FROM TourBooking b
           WHERE b.tour.guide.id = :guideId
           AND b.status IN (com.example.tourmatebackend.states.BookingStatus.COMPLETED, com.example.tourmatebackend.states.BookingStatus.APPROVED)
           """)
    Integer getTotalTravelers(@Param("guideId") int guideId);



}
