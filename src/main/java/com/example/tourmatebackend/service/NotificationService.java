package com.example.tourmatebackend.service;

import com.example.tourmatebackend.model.Notification;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.NotificationRepository;
import com.example.tourmatebackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificationService {

    @Autowired
    NotificationRepository notificationRepository;

    @Autowired
    UserRepository userRepository;

    public void createNotification(int userId, String title, String message) {

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);

        notificationRepository.save(notification);
    }
    public void markAsRead( Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public List<Notification> getUserNotifications(int userId) {
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    public void markAllAsRead(int userId) {
        List<Notification> notifications =
                notificationRepository.findByUserIdOrderByCreatedAtDesc(userId);

        notifications.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(notifications);
    }

    public void clearAll(int userId) {
        notificationRepository.deleteByUserId(userId);
    }

}
