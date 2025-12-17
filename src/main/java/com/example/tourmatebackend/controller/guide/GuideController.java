package com.example.tourmatebackend.controller.guide;

import com.example.tourmatebackend.dto.guide.EarningsSummaryDTO;
import com.example.tourmatebackend.dto.guide.GuideDashboardDTO;
import com.example.tourmatebackend.dto.guide.GuideUpdateResponseDTO;
import com.example.tourmatebackend.dto.guideRegistration.GuideRegisterRequestDTO;
import com.example.tourmatebackend.model.Guide;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.GuideRepository;
import com.example.tourmatebackend.repository.UserRepository;
import com.example.tourmatebackend.service.EarningsService;
import com.example.tourmatebackend.service.GuideService;
import com.example.tourmatebackend.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/guides")
public class GuideController {

    @Autowired
    private GuideRepository guideRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private GuideService guideService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private EarningsService earningsService;

    @PutMapping(value="/edit/{userId}", consumes = "multipart/form-data")
    public ResponseEntity<?> updateGuide(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int userId,
            @RequestPart(value="guide", required = false) String guideJson,
            @RequestPart(value = "file", required = false) MultipartFile file

    ) throws IOException {
        Guide guideRequest = objectMapper.readValue(guideJson, Guide.class);
        // Extract user from token
        String token = authHeader.replace("Bearer ", "");
        String emailFromToken = jwtUtil.extractEmail(token);
        User tokenUser = userRepository.findByEmail(emailFromToken).orElse(null);

        if (tokenUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "error", "message", "Invalid token"));
        }

        // Only allow user to edit their own guide profile
        if (tokenUser.getId() != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("status", "error", "message", "You can edit ONLY your own guide profile"));
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "User not found"));
        }

        Guide existingGuide = guideRepository.findByUserId(userId).orElse(null);
        if (existingGuide == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", "User is not registered as a guide"));
        }

        // Update guide fields


        if (guideRequest != null) {

            if (guideRequest.getBio() != null)
                existingGuide.setBio(guideRequest.getBio());

            if (guideRequest.getCategories() != null)
                existingGuide.setCategories(guideRequest.getCategories());

            if (guideRequest.getLanguages() != null)
                existingGuide.setLanguages(guideRequest.getLanguages());

            if (guideRequest.getPrice() != null)
                existingGuide.setPrice(guideRequest.getPrice());
        }

        if (file != null && !file.isEmpty()) {
            existingGuide.setProfilePic(file.getBytes());
        }


        Guide updatedGuide = guideRepository.save(existingGuide);

        GuideUpdateResponseDTO dto = new GuideUpdateResponseDTO();

        dto.setGuideId(updatedGuide.getId());
        dto.setBio(updatedGuide.getBio());
        dto.setCategories(updatedGuide.getCategories());
        dto.setLanguages(updatedGuide.getLanguages());
        dto.setPrice(updatedGuide.getPrice());
        dto.setStatus(updatedGuide.getStatus());
        dto.setProfilePic(updatedGuide.getProfilePic());

        dto.setUserId(user.getId());
        dto.setUserName(user.getFirstName() + " " + user.getLastName());
        dto.setUserEmail(user.getEmail());

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Guide profile updated successfully",
                "data", dto
        ));

    }
    @GetMapping("/dashboard")
    public ResponseEntity<?> getGuideDashboard(@RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email).orElseThrow();

        if (user == null || user.getGuide() == null) {
            return ResponseEntity.status(403).body("Not a guide account");
        }

        GuideDashboardDTO dto = guideService.getGuideDashboard(user.getGuide().getId());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/earnings")
    public ResponseEntity<?> getEarnings(@RequestHeader("Authorization") String authHeader) {

        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email).orElseThrow();

        if (user == null || user.getGuide() == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not a guide user");
        }

        int guideId = user.getGuide().getId();

        EarningsSummaryDTO earnings = earningsService.getGuideEarnings(guideId);

        return ResponseEntity.ok(earnings);
    }

}
