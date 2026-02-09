package com.example.tourmatebackend.repository;

import com.example.tourmatebackend.model.GuideBooking;
import com.example.tourmatebackend.model.TourBooking;
import com.example.tourmatebackend.states.BookingStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface TourBookingRepository extends JpaRepository<TourBooking, Integer> {

    Page<TourBooking> findByUserId(int userId, Pageable pageable);
    Page<TourBooking> findByGuideId(int guideId, Pageable pageable); // guide's bookings

    Page<TourBooking> findByGuideIdAndStatusIn(int guideId, List<BookingStatus> statuses, Pageable pageable);
    Page<TourBooking> findByUserIdAndStatusIn(int userId, List<BookingStatus> statuses, Pageable pageable);
    boolean existsByUserIdAndTourIdAndStatus(int userId, int tourId, BookingStatus status);
    Page<TourBooking> findByUserIdAndStatusAndStartDateAfter(int userId, BookingStatus status, LocalDate startDate, Pageable pageable);

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
    @Query("""
           SELECT SUM(b.totalPrice)
           FROM TourBooking b
           WHERE b.tour.guide.id = :guideId
                      AND b.paymentStatus = "PENDING"
                      AND b.status IN (com.example.tourmatebackend.states.BookingStatus.COMPLETED, com.example.tourmatebackend.states.BookingStatus.APPROVED)

           """)
    Double getTourPendingPayments(@Param("guideId") int guideId);


    List<TourBooking> findTop10ByGuideIdAndStatusInOrderByBookingDateDesc(
            int guideId, List<BookingStatus> statuses
    );

    @Query("""
        SELECT COALESCE(SUM(t.totalPrice), 0)
        FROM TourBooking t
        WHERE t.user.id = :userId
          AND t.status IN :statuses
    """)
    double getTotalSpentByUser(
            @Param("userId") int userId,
            @Param("statuses") List<BookingStatus> statuses
    );

    long countByUserIdAndStatusIn(int userId, List<BookingStatus> statuses);

    @Query("""
SELECT b.status, COUNT(b)
FROM TourBooking b
WHERE b.guide.id = :guideId
GROUP BY b.status
""")
    List<Object[]> countBookingsByStatus(@Param("guideId") int guideId);





    int countByUser_Id(int userId);

    int countByUser_IdAndStartDateAfterAndStatus(
            int userId,
            LocalDate date,
            BookingStatus status
    );
    @Query("""
        SELECT MIN(tb.startDate)
        FROM TourBooking tb
        WHERE tb.user.id = :userId
          AND tb.startDate > CURRENT_DATE
          AND tb.status = com.example.tourmatebackend.states.BookingStatus.APPROVED
    """)
    LocalDate findNextUpcomingTripDate(@Param("userId") int userId);

//admin
    long count();
    long countByBookingDateAfter(LocalDateTime date);

    @Query("""
            SELECT SUM(tb.totalPrice) FROM TourBooking tb 
            WHERE tb.bookingDate >= :date 
            AND tb.status IN (com.example.tourmatebackend.states.BookingStatus.COMPLETED, com.example.tourmatebackend.states.BookingStatus.APPROVED)

            """)
    Double sumRevenueSince(@Param("date") LocalDateTime date);

    @Query("""
    SELECT b
    FROM TourBooking b
    ORDER BY b.bookingDate DESC
""")
    List<TourBooking> findRecentBookings(Pageable pageable);

    @Query("""
SELECT 
    g.id,
    g.fullName,
    g.location,
    COALESCE(SUM(b.totalPrice), 0),
    COALESCE(AVG(gr.rating), 0),
    COUNT(gr.id)
FROM TourBooking b
JOIN b.guide g
LEFT JOIN GuideReview gr ON gr.guide.id = g.id
WHERE b.paymentStatus = com.example.tourmatebackend.states.PaymentStatus.PAID
  AND MONTH(b.bookingDate) = MONTH(CURRENT_DATE)
  AND YEAR(b.bookingDate) = YEAR(CURRENT_DATE)
GROUP BY g.id, g.fullName, g.location
ORDER BY SUM(b.totalPrice) DESC
""")
    List<Object[]> findTopGuidesThisMonth(Pageable pageable);
}
