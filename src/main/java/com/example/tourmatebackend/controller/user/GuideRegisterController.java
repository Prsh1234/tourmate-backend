package com.example.tourmatebackend.controller.user;

import com.example.tourmatebackend.dto.guideRegistration.GuideRegisterRequestDTO;
import com.example.tourmatebackend.dto.guideRegistration.GuideRegisterResponseDTO;
import com.example.tourmatebackend.model.Guide;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.service.GuideService;
import com.example.tourmatebackend.repository.UserRepository;
import com.example.tourmatebackend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user/guides")
public class GuideRegisterController {

    @Autowired
    private GuideService guideService;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // -------------------------------
    // REGISTER GUIDE (only by current user)
    // -------------------------------
    @PostMapping("/register/{userId}")
    public ResponseEntity<?> registerGuide(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int userId,
            @RequestBody GuideRegisterRequestDTO guideRequest
    ) {

        String token = authHeader.replace("Bearer ", "");
        String emailFromToken = jwtUtil.extractEmail(token);
        User tokenUser = userRepository.findByEmail(emailFromToken).orElse(null);

        if (tokenUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "error", "message", "Invalid token"));
        }

        if (tokenUser.getId() != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "status", "error",
                            "message", "Access denied! You can only register yourself as a guide."
                    ));
        }

        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "User not found"));
        }

        try {
            Guide guide = guideService.registerGuide(user, guideRequest);

            GuideRegisterResponseDTO response = new GuideRegisterResponseDTO();
            response.setGuideId(guide.getId());
            response.setExpertise(guide.getExpertise());
            response.setBio(guide.getBio());
            response.setCategories(guide.getCategories());
            response.setLanguages(guide.getLanguages());
            response.setStatus(guide.getStatus());
            response.setProfilePic(guide.getProfilePic());
            response.setPrice(guide.getPrice());
            response.setLocation(guide.getLocation());
            response.setUserId(user.getId());
            response.setUserName(user.getFirstName() + " " + user.getLastName());
            response.setUserEmail(user.getEmail());

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Guide registration request submitted",
                    "data", response
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }


}
