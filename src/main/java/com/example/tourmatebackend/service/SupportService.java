package com.example.tourmatebackend.service;

import com.example.tourmatebackend.dto.support.SupportDTO;
import com.example.tourmatebackend.dto.support.SupportRequestDTO;
import com.example.tourmatebackend.model.Support;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.SupportRepository;
import com.example.tourmatebackend.states.Role;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class SupportService {

    private final SupportRepository supportRepository;

    public SupportService(SupportRepository supportRepository) {
        this.supportRepository = supportRepository;
    }

    // Send a support message from a specific user
    public SupportDTO sendSupportMessage(User user, SupportRequestDTO supportRequestDTO) {
        Support support = new Support();
        support.setSubject(supportRequestDTO.getSubject());
        support.setMessage(supportRequestDTO.getMessage());
        if ("TRAVELLER".equalsIgnoreCase(supportRequestDTO.getRole())) {
            support.setRole(Role.TRAVELLER);
        } else if ("GUIDE".equalsIgnoreCase(supportRequestDTO.getRole())) {
            support.setRole(Role.GUIDE);
        } else {
            throw new IllegalArgumentException("Invalid role: " + supportRequestDTO.getRole());
        }
        support.setUser(user);
        support.setCreated_At(LocalDateTime.now());
        support.setView(false);
        supportRepository.save(support);

        return new SupportDTO(
                support.getSubject(),
                support.getMessage(),
                support.getRole(),
                support.getCreated_At()
        );
    }

    // Get all support messages
    public List<SupportDTO> getAllSupportMessages() {
        return supportRepository.findAll()
                .stream()
                .map(support -> new SupportDTO(
                        support.getId(),
                        support.getSubject(),
                        support.getMessage(),
                        support.getUser().getRole(),
                        support.getCreated_At(),
                        support.isView(),
                        support.getUser()
                ))
                .collect(Collectors.toList());
    }

    // Get messages by role
    public List<SupportDTO> getSupportMessagesByRole(Role role) {
        return supportRepository.findByRole(role)
                .stream()
                .map(support -> new SupportDTO(
                        support.getId(),
                        support.getSubject(),
                        support.getMessage(),
                        support.getUser().getRole(),
                        support.getCreated_At(),
                        support.isView(),
                        support.getUser()
                ))
                .collect(Collectors.toList());
    }

    public SupportDTO markAsSeen(Long id) {
        Support support = supportRepository.findById(id).orElseThrow();
        support.setView(true);
        support = supportRepository.save(support);
        return new SupportDTO(
                support.getId(),
                support.getSubject(),
                support.getMessage(),
                support.getUser().getRole(),
                support.getCreated_At(),
                support.isView(),
                support.getUser()
        );
    }


}
