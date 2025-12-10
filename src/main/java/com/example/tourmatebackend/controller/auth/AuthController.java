package com.example.tourmatebackend.controller.auth;

import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.UserRepository;
import com.example.tourmatebackend.states.Role;
import com.example.tourmatebackend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")

public class AuthController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        boolean isValid = jwtUtil.validateToken(token);

        if (isValid) {
            return ResponseEntity.ok().body("Token is valid");
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }
    }

    @GetMapping("/validate-admin")
    public ResponseEntity<?> validateAdmin(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");

        if (!jwtUtil.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid or expired token");
        }

        String email = jwtUtil.extractEmail(token);
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty() || userOpt.get().getRole() != Role.ADMIN) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Not an admin");
        }

        return ResponseEntity.ok().body("Admin validated");
    }


}
