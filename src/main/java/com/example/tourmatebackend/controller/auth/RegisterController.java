package com.example.tourmatebackend.controller.auth;

import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.UserRepository;
import com.example.tourmatebackend.service.EmailService;
import com.example.tourmatebackend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class RegisterController {

    @Autowired
    private UserRepository uRepo;
    @Autowired
    private EmailService emailService;
    @Autowired
    private JwtUtil jwtUtil;
    private boolean isValidPhone(String phone) {
        return phone != null && phone.matches("^\\+?\\d{7,15}$");
    }
    @PostMapping("/register")
    public ResponseEntity<?> postSignup(@RequestBody User u) throws IOException {
        System.out.println("caliningin");
        if (!isValidPhone(u.getPhoneNumber())) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", "Invalid phone number"
            ));
        }
        Optional<User> existingUserByPhone = uRepo.findByPhoneNumber(u.getPhoneNumber());
        if (existingUserByPhone.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Phone Number already signed up"));
        }


        Optional<User> existingUser = uRepo.findByEmail(u.getEmail());
        if (existingUser.isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already signed up"));
        }

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        u.setPassword(encoder.encode(u.getPassword()));
        String verificationToken = UUID.randomUUID().toString();
        u.setVerificationToken(verificationToken);
        u.setEnabled(false);
        User savedUser = uRepo.save(u);

        if (savedUser != null) {
            String token = jwtUtil.generateAccessToken(savedUser.getEmail());
            String refreshToken = jwtUtil.generateRefreshToken(savedUser.getEmail());

            String verificationLink = "http://localhost:5173/verify?token=" + verificationToken;
            emailService.sendEmail(savedUser.getEmail(), "Verify your email",
                    "Click to verify your account: " + verificationLink);
            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "token", token,
                    "refreshToken", refreshToken,
                    "email", savedUser.getEmail(),
                    "firstName", savedUser.getFirstName(),
                    "lastName", savedUser.getLastName()
            ));

        } else {
            return ResponseEntity.badRequest().body(Map.of("error", "User registration failed"));
        }
    }
    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(@RequestParam("token") String token) {

        Optional<User> userOptional = uRepo.findByVerificationToken(token);

        if (userOptional.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Invalid verification token"
            ));
        }

        User user = userOptional.get();
        user.setEnabled(true);
        user.setVerificationToken(null);
        uRepo.save(user);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Email verified successfully. You can now login."
        ));
    }

}
