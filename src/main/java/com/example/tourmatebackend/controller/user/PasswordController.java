package com.example.tourmatebackend.controller.user;

import com.example.tourmatebackend.dto.user.ChangePasswordRequest;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.UserRepository;
import com.example.tourmatebackend.utils.JwtUtil;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class PasswordController {
    @Autowired private UserRepository userRepository;
    @Autowired private JwtUtil jwtUtil;
    private boolean isStrongPassword(String password) {
        return password != null &&
                password.length() >= 8 &&
                password.matches(".*[A-Z].*") &&      // at least 1 uppercase
                password.matches(".*[a-z].*") &&      // at least 1 lowercase
                password.matches(".*\\d.*") &&        // at least 1 digit
                password.matches(".*[@$!%*?&].*");    // at least 1 special character
    }
    // --------------------------------------
    // Change Password API
    // --------------------------------------
    @PutMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody ChangePasswordRequest request
    ) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Missing or invalid token"));
        }

        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);

        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("success", false, "message", "Invalid user"));
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // If user has a password (normal account)
        if (user.getPassword() != null) {
            if (request.getOldPassword() == null || request.getNewPassword() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Password fields cannot be empty"));
            }

            if (!encoder.matches(request.getOldPassword(), user.getPassword())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "Old password is incorrect"));
            }

            if (encoder.matches(request.getNewPassword(), user.getPassword())) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "New password must be different from old password"));
            }

        } else {
            // Google login / no password account
            if (request.getNewPassword() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("success", false, "message", "New password cannot be empty"));
            }
            // skip old password check
        }

        // Check new password strength
        if (!isStrongPassword(request.getNewPassword())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("success", false,
                            "message", "Password must be at least 8 characters long and include uppercase, lowercase, number, and special character."));
        }

        user.setPassword(encoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Password changed successfully"
        ));
    }


}
