package com.example.tourmatebackend.controller.Notification;

import com.example.tourmatebackend.model.Notification;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.NotificationRepository;
import com.example.tourmatebackend.repository.UserRepository;
import com.example.tourmatebackend.service.NotificationService;
import com.example.tourmatebackend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    @Autowired
    NotificationService notificationService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    NotificationRepository notificationRepository;

    @GetMapping("/all")
    public List<Notification> getAllNotifications(@RequestHeader("Authorization") String authHeader) {
        int userId = extractUserId(authHeader);
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    @GetMapping("/unread/{userId}")
    public List<Notification> getUnreadNotifications(@RequestHeader("Authorization") String authHeader) {
        int userId = extractUserId(authHeader);
        return notificationRepository.findByUserIdAndIsReadFalseOrderByCreatedAtDesc(userId);
    }

    @PutMapping("/mark-read/{id}")
    public String markAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return "Marked as read";
    }
    private int extractUserId(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    //Implementation
//    useEffect(() => {
//    const interval = setInterval(() => {
//                fetch(`/api/notifications/unread/${userId}`)
//            .then(res => res.json())
//            .then(data => {
//        if (data.length > 0) {
//            // Show pop-up or badge
//            console.log("New notifications:", data);
//        }
//            });
//    }, 10000); // 10 seconds
//
//        return () => clearInterval(interval);
//    }, []);

}
