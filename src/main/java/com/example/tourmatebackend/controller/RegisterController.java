package com.example.tourmatebackend.controller;

import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.UserRepository;
import com.example.tourmatebackend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class RegisterController {

    @Autowired
    private UserRepository uRepo;

    @Autowired
    private JwtUtil jwtUtil;

    @PostMapping("/register")
    public ResponseEntity<?> postSignup(@RequestBody User u) throws IOException {

        Optional<User> existingUser = uRepo.findByEmail(u.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already signed up"));
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        u.setPassword(encoder.encode(u.getPassword()));
        User savedUser = uRepo.save(u);

        if (savedUser != null) {
            String token = jwtUtil.generateToken(savedUser.getEmail());

            return ResponseEntity.ok(Map.of(
                    "token", token,
                    "email", savedUser.getEmail(),
                    "firstName", savedUser.getFirstName(),
                    "lastName", savedUser.getLastName()
            ));
        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "User registration failed"));
        }
    }
}
