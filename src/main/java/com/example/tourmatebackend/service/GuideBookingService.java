package com.example.tourmatebackend.service;

import com.example.tourmatebackend.DTO.GuideBookingRequestDTO;
import com.example.tourmatebackend.model.Guide;
import com.example.tourmatebackend.model.GuideBooking;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.GuideBookingRepository;
import com.example.tourmatebackend.repository.GuideRepository;
import com.example.tourmatebackend.states.BookingStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GuideBookingService {

    @Autowired
    private GuideRepository guideRepository;

    @Autowired
    private GuideBookingRepository bookingRepository;

    // Create booking request
    public GuideBooking createBookingRequest(GuideBookingRequestDTO req, User user) {

        // Fetch guide
        Guide guide = guideRepository.findById(req.getGuideId())
                .orElseThrow(() -> new RuntimeException("Guide not found"));

        // Calculate total price: price per hour × hours × group size
        double total = guide.getPrice() * req.getHours() * req.getGroupSize();

        // Create booking
        GuideBooking booking = new GuideBooking();
        booking.setGuide(guide);
        booking.setUser(user); // authenticated user
        booking.setHours(req.getHours());
        booking.setGroupSize(req.getGroupSize());
        booking.setTotalPrice(total);
        booking.setStatus(BookingStatus.PENDING); // booking request

        return bookingRepository.save(booking);
    }

    // Update booking status (for guides)
    public GuideBooking updateStatus(int bookingId, BookingStatus status, User guideUser) {
        GuideBooking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Booking not found"));

        // Only allow the guide of this booking to update status
        if (booking.getGuide().getUser().getId() != (guideUser.getId())) {
            throw new RuntimeException("You can only manage your own bookings.");
        }

        booking.setStatus(status);
        return bookingRepository.save(booking);
    }
}
