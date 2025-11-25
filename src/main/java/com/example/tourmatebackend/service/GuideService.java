package com.example.tourmatebackend.service;

import com.example.tourmatebackend.dto.guideRegistration.GuideRegisterRequestDTO;
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

    public Guide registerGuide(User user, GuideRegisterRequestDTO dto) {
        Guide guide = new Guide();

        guide.setUser(user);
        guide.setExpertise(dto.getExpertise());
        guide.setBio(dto.getBio());
        guide.setCategories(dto.getCategories());
        guide.setLanguages(dto.getLanguages());
        guide.setPrice(dto.getPrice());
        guide.setLocation(dto.getLocation());
        guide.setStatus(GuideStatus.PENDING);

        return guideRepository.save(guide);
    }


}
