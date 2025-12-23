package com.example.tourmatebackend.repository;

import com.example.tourmatebackend.model.GuideBooking;
import com.example.tourmatebackend.states.BookingStatus;
import com.example.tourmatebackend.states.GuideStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GuideBookingRepository extends JpaRepository<GuideBooking, Integer> {

    Page<GuideBooking> findByGuideId(int guideId, Pageable pageable);
    Page<GuideBooking> findByUserId(int userId, Pageable pageable);
    // Get bookings for a guide filtered by a list of statuses
    Page<GuideBooking> findByGuideIdAndStatusIn(int guideId, List<BookingStatus> statuses, Pageable pageable);
    Page<GuideBooking> findByGuideIdAndStatus(int guideId, BookingStatus status, Pageable pageable);
    Page<GuideBooking> findByUserIdAndStatusIn(int userId, List<BookingStatus> statuses, Pageable pageable);
    boolean existsByUserIdAndGuideIdAndStatus(int userId, int guideId, BookingStatus status);


    @Query("""
           SELECT SUM(b.totalPrice)
           FROM GuideBooking b
           WHERE b.guide.id = :guideId
             AND MONTH(b.bookingDate) = MONTH(CURRENT_DATE)
             AND YEAR(b.bookingDate) = YEAR(CURRENT_DATE)
             AND b.paymentStatus = "PAID"

           """)
    Double getGuideEarningsThisMonth(@Param("guideId") int guideId);

    @Query("""
           SELECT SUM(b.totalPrice)
           FROM GuideBooking b
           WHERE b.guide.id = :guideId
             AND MONTH(b.bookingDate) = MONTH(CURRENT_DATE) - 1
             AND YEAR(b.bookingDate) = YEAR(CURRENT_DATE)
             AND b.paymentStatus = "PAID"
           """)
    Double getGuideEarningsLastMonth(@Param("guideId") int guideId);

    @Query("""
           SELECT SUM(b.groupSize)
           FROM GuideBooking b
           WHERE b.guide.id = :guideId
           AND b.status IN (com.example.tourmatebackend.states.BookingStatus.COMPLETED, com.example.tourmatebackend.states.BookingStatus.APPROVED)
           """)
    Integer getTotalTravelers(@Param("guideId") int guideId);

    @Query("""
           SELECT SUM(b.totalPrice)
           FROM GuideBooking b
           WHERE b.guide.id = :guideId
                      AND b.paymentStatus = "PAID"

           """)
    Double getGuideTotalEarnings(@Param("guideId") int guideId);

    @Query("""
           SELECT SUM(b.totalPrice)
           FROM GuideBooking b
           WHERE b.guide.id = :guideId
                      AND b.paymentStatus = "PENDING"
                      AND b.status IN (com.example.tourmatebackend.states.BookingStatus.COMPLETED, com.example.tourmatebackend.states.BookingStatus.APPROVED)

           """)
    Double getGuidePendingPayments(@Param("guideId") int guideId);

    List<GuideBooking> findTop10ByGuideIdAndStatusInOrderByBookingDateDesc(
            int guideId, List<BookingStatus> statuses
    );

    @Query("""
        SELECT COALESCE(SUM(g.totalPrice), 0)
        FROM GuideBooking g
        WHERE g.user.id = :userId
          AND g.status IN :statuses
    """)
    double getTotalSpentByUser(
            @Param("userId") int userId,
            @Param("statuses") List<BookingStatus> statuses
    );

    long countByUserIdAndStatusIn(int userId, List<BookingStatus> statuses);
}

