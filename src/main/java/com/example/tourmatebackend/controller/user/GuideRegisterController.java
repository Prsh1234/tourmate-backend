package com.example.tourmatebackend.controller.user;

import com.example.tourmatebackend.model.Guide;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.GuideRepository;
import com.example.tourmatebackend.service.GuideService;
import com.example.tourmatebackend.repository.UserRepository;
import com.example.tourmatebackend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user/guides")
public class GuideRegisterController {

    @Autowired
    private GuideService guideService;

    @Autowired
    private GuideRepository guideRepository;

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
            @RequestBody Guide guideRequest
    ) {

        // Extract user from JWT token
        String token = authHeader.replace("Bearer ", "");
        String emailFromToken = jwtUtil.extractEmail(token);
        User tokenUser = userRepository.findByEmail(emailFromToken).orElse(null);

        if (tokenUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "error", "message", "Invalid token"));
        }

        // 🔥 Allow registration ONLY if token userId == URL userId
        if (tokenUser.getId() != userId) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of(
                            "status", "error",
                            "message", "Access denied! You can only register yourself as a guide."
                    ));
        }

        // Validate user exists
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "User not found"));
        }

        try {
            Guide guide = guideService.registerGuide(user, guideRequest);

            Map<String, Object> data = new HashMap<>();
            data.put("guideId", guide.getId());
            data.put("expertise", guide.getExpertise());
            data.put("bio", guide.getBio());
            data.put("categories", guide.getCategories());
            data.put("languages",guide.getLanguages());
            data.put("status", guide.getStatus().name());
            data.put("profilePic", guide.getProfilePic());
            data.put("userId", user.getId());
            data.put("userName", user.getFirstName() + " " + user.getLastName());
            data.put("userEmail", user.getEmail());

            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Guide registration request submitted",
                    "data", data
            ));

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

}
