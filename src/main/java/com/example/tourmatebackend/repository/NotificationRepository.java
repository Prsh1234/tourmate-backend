package com.example.tourmatebackend.repository;

import com.example.tourmatebackend.model.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Get all notifications for the user
    List<Notification> findByUserIdOrderByCreatedAtDesc(int userId);

    // Get only unread notifications (for "live" updates)
    List<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(int userId);

    long countByUserIdAndIsReadFalse(int userId);

    void deleteByUserId(int userId);
}
