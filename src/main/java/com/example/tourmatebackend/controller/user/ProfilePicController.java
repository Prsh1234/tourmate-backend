package com.example.tourmatebackend.controller.user;

import com.example.tourmatebackend.dto.user.ChangePasswordRequest;
import com.example.tourmatebackend.dto.user.ChangeProfilePicRequest;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.UserRepository;
import com.example.tourmatebackend.utils.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class ProfilePicController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;


    // --------------------------
    // CHANGE PROFILE PIC
    // --------------------------
    @PutMapping(value = "/change-profile-pic", consumes = "multipart/form-data")
    public ResponseEntity<?> changeProfilePic(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("file") MultipartFile file) {

        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("status", "error", "message", "Invalid token"));
        }

        try {
            user.setProfilePic(file.getBytes());
            userRepository.save(user);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", "Failed to save image"));
        }

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Profile picture updated"
        ));
    }
}
