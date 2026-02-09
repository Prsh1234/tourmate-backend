package com.example.tourmatebackend.controller.traveller;

import com.example.tourmatebackend.dto.guide.GuideDashboardDTO;
import com.example.tourmatebackend.dto.traveller.TravellerDashboardDTO;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.UserRepository;
import com.example.tourmatebackend.service.TravellerService;
import com.example.tourmatebackend.service.UserService;
import com.example.tourmatebackend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/traveller")
public class TravellerController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TravellerService travellerService;

    @GetMapping("/dashboard")
    public ResponseEntity<?> getGuideDashboard(@RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email).orElseThrow();

        if (user == null || user.getGuide() == null) {
            return ResponseEntity.status(403).body("Not a guide account");
        }

        TravellerDashboardDTO dto = travellerService.getTravellerDashboard(user.getId());
        return ResponseEntity.ok(dto);
    }
}
