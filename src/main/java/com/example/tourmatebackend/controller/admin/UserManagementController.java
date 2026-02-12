package com.example.tourmatebackend.controller.admin;

import com.example.tourmatebackend.dto.admin.UserManagement.GuideManagementResponseDTO;
import com.example.tourmatebackend.dto.admin.UserManagement.UserManagementResponseDTO;
import com.example.tourmatebackend.model.Guide;
import com.example.tourmatebackend.model.GuideReview;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.*;
import com.example.tourmatebackend.service.NotificationService;
import com.example.tourmatebackend.service.ReviewService;
import com.example.tourmatebackend.states.BookingStatus;
import com.example.tourmatebackend.states.GuideStatus;
import com.example.tourmatebackend.states.Role;
import com.example.tourmatebackend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;


@RestController
@RequestMapping("/api/admin/management")
public class UserManagementController {
    @Autowired
    private GuideRepository guideRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private TourBookingRepository tourBookingRepository;
    @Autowired
    private GuideReviewRepository guideReviewRepository;
    @Autowired
    private ReviewService reviewService;
    @Autowired
    private TourRepository tourRepository;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private NotificationService notificationService;



    private User extractUserFromToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        return userRepository.findByEmail(email).orElse(null);
    }
    private ResponseEntity<?> unauthorized() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("status", "error", "message", "You are not authorized"));
    }
    private boolean isAdmin(User user) {
        return user != null && user.getRole() == Role.ADMIN;
    }

    @GetMapping("/travellers")
    public ResponseEntity<?> getUsers(@RequestHeader("Authorization") String authHeader) {

        User requester = extractUserFromToken(authHeader);
        if (!isAdmin(requester)) return unauthorized();

        List<User> userList = userRepository.findAll()
                .stream()
                .filter(u -> u.getRole() == Role.TRAVELLER)
                .collect(Collectors.toList());
        List<BookingStatus> validStatuses =
                List.of(BookingStatus.APPROVED, BookingStatus.COMPLETED);

        List<UserManagementResponseDTO> dtoList = userList.stream().map(user -> {


            double tourSpent = tourBookingRepository
                    .getTotalSpentByUser(user.getId(), validStatuses);



            long tourBookings = tourBookingRepository
                    .countByUserIdAndStatusIn(user.getId(), validStatuses);

            UserManagementResponseDTO dto = new UserManagementResponseDTO();
            dto.setEmail(user.getEmail());
            dto.setPhoneNumber(user.getPhoneNumber());
            dto.setProfilePic(user.getProfilePic());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setSpent(tourSpent);
            dto.setBookings((int) (tourBookings));
            dto.setUserId(user.getId());
            dto.setRole(user.getRole());
            dto.setSuspended(user.isSuspended());

            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(
                Map.of(
                        "status", "success",
                        "message", "UserList",
                        "data", dtoList
                )
        );
    }

    @PutMapping("/travellers/suspend/{userId}")
    public ResponseEntity<?> toggleUser(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int userId
    ) {

        User requester = extractUserFromToken(authHeader);
        if (!isAdmin(requester)) return unauthorized();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST, "User not found"
                ));

        // Prevent suspension if guide request is pending
        if (user.getGuide() != null &&
                user.getGuide().getStatus() == GuideStatus.PENDING) {
            return ResponseEntity.badRequest().body("Guide Request Pending");
        }

        // ✅ Toggle suspension
        user.setSuspended(!user.isSuspended());

        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "suspended", user.isSuspended(),
                "role", user.getRole(),
                "message", user.isSuspended()
                        ? "User suspended successfully"
                        : "User unsuspended successfully"
        ));
    }


    @GetMapping("/guides")
    public ResponseEntity<?> getGuides(@RequestHeader("Authorization") String authHeader) {

        User requester = extractUserFromToken(authHeader);
        if (!isAdmin(requester)) return unauthorized();

        List<Guide> guideList = guideRepository.findByStatus(GuideStatus.APPROVED)
                .stream()
                .collect(Collectors.toList());

        List<GuideManagementResponseDTO> dtoList = guideList.stream().map(guide -> {


            GuideManagementResponseDTO dto = new GuideManagementResponseDTO();
            dto.setEmail(guide.getEmail());
            dto.setProfilePic(guide.getProfilePic());
            dto.setFullName(guide.getFullName());
            dto.setStatus(guide.getStatus());
            List<GuideReview> reviews = guideReviewRepository.findByGuideId(guide.getId());
            double avgRating = reviewService.calculateGuideAverageRating(reviews);
            dto.setRating(avgRating);

            dto.setTours(tourRepository.countByGuideId(guide.getId()));
            dto.setGuideId(guide.getId());


            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(
                Map.of(
                        "status", "success",
                        "message", "GuideList",
                        "data", dtoList
                )
        );
    }

    @PutMapping("/guides/suspend/{guideId}")
    public ResponseEntity<?> toggleGuide(@RequestHeader("Authorization") String authHeader,
                                         @PathVariable int guideId) {

        User requester = extractUserFromToken(authHeader);
        if (!isAdmin(requester)) return unauthorized();
        Optional<Guide> optionalGuide = guideRepository.findById(guideId);
        if (optionalGuide.isEmpty()) {
            return ResponseEntity.badRequest().body("Guide not found");
        }
        Guide guide = optionalGuide.get();
        if (guide.getStatus() == GuideStatus.PENDING){
            return ResponseEntity.badRequest().body("Cannot suspend, Guide Request Pending");
        }
        else if(guide.getStatus() == GuideStatus.SUSPENDED) {
            guide.setStatus(GuideStatus.APPROVED);
            notificationService.createNotification(
                    guide.getUser().getId(),
                    "Suspension",
                    "You have been unsuspended."
            );
        }
        else{
            guide.setStatus(GuideStatus.SUSPENDED);
            notificationService.createNotification(
                    guide.getUser().getId(),
                    "Suspension",
                    "You have been suspended."
            );
        }
        guideRepository.save(guide);
        notificationService.createNotification(
                guide.getUser().getId(),
                "Suspension",
                "You have been suspended."
        );
        return ResponseEntity.ok(
                Map.of(
                        "status", "success",
                        "newStatus", guide.getStatus(),
                        "message",
                        guide.getStatus().equals("SUSPENDED")
                                ? "User suspended successfully"
                                : "User unsuspended successfully"
                )

        );
    }

}
