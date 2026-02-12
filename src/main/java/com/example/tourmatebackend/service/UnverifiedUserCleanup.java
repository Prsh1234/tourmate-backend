package com.example.tourmatebackend.service;

import com.example.tourmatebackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDate;

@Component
public class UnverifiedUserCleanup {

    @Autowired
    private UserRepository uRepo;

    // Run every day at midnight
    @Scheduled(cron = "0 0 0 * * ?")
    public void deleteUnverifiedUsers() {
        LocalDate cutoff = LocalDate.now().minusDays(1);
        uRepo.findAll().stream()
                .filter(u -> !u.isEnabled() && u.getJoined().isBefore(cutoff))
                .forEach(uRepo::delete);
    }
}

