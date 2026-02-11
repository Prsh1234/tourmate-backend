package com.example.tourmatebackend.controller;


import com.example.tourmatebackend.dto.support.SupportDTO;
import com.example.tourmatebackend.dto.support.SupportRequestDTO;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.UserRepository;
import com.example.tourmatebackend.service.SupportService;
import com.example.tourmatebackend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/support")
public class SupportController {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private SupportService supportService;

    // Send a support message
    @PostMapping("/send")
    public SupportDTO sendMessage(
            @RequestBody SupportRequestDTO supportRequest,
            @RequestHeader("Authorization") String authHeader
    ) {
        User user = getUserFromToken(authHeader);
        return supportService.sendSupportMessage(user, supportRequest);
    }

    private User getUserFromToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

}
