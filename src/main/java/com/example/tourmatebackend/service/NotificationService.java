package com.example.tourmatebackend.service;

import com.example.tourmatebackend.model.Notification;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.NotificationRepository;
import com.example.tourmatebackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
