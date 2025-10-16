package com.example.tourmatebackend.repository;

import com.example.tourmatebackend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class RegisterController {
    @Autowired
    private UserRepository uRepo;

    @PostMapping("/register")
    public Object postSignup(@RequestBody User u) throws IOException {
        // Check if email already exists
        System.out.println("reaching");

        Optional<User> existingUser = uRepo.findByEmail(u.getEmail());
        if (existingUser.isPresent()) {
            return new ApiResponse(false, "Email already signed up");
        }
        // Hash password
//        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
//        u.setPassword(encoder.encode(u.getPassword()));

        // Save user
        User savedUser = uRepo.save(u);
        if (savedUser != null) {
            return new ApiResponse(true, "User registered successfully");
        } else {
            return new ApiResponse(false, "User registration failed");
        }
    }
    static class ApiResponse {
        public boolean success;
        public String message;

        public ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
}
