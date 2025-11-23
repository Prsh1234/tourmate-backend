package com.example.tourmatebackend.service;

import com.example.tourmatebackend.model.Guide;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.GuideRepository;
import com.example.tourmatebackend.states.GuideStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GuideService {

    @Autowired
    private GuideRepository guideRepository;

    public Guide registerGuide(User user, Guide guideRequest) {

        if (guideRepository.existsByUserId(user.getId())) {
            throw new RuntimeException("User is already registered as a guide");
        }

        Guide guide = new Guide();
        guide.setUser(user);
        guide.setExpertise(guideRequest.getExpertise());
        guide.setBio(guideRequest.getBio());
        guide.setCategories(guideRequest.getCategories());  // MULTIPLE CATEGORIES
        guide.setStatus(GuideStatus.PENDING);
        guide.setProfilePic(user.getProfilePic());

        return guideRepository.save(guide);
    }


}
