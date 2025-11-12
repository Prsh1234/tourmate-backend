package com.example.tourmatebackend.service;

import com.example.tourmatebackend.model.Guide;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.GuideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GuideService {

    @Autowired
    private GuideRepository guideRepository;

    public Guide registerGuide(User user, Guide guide) {
        if (guideRepository.existsByUserId(user.getId())) {
            throw new RuntimeException("User has already submitted a guide request");
        }
        guide.setUser(user);
        return guideRepository.save(guide);
    }
}
