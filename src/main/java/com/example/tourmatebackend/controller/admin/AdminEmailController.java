package com.example.tourmatebackend.controller.admin;

import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.UserRepository;
import com.example.tourmatebackend.service.EmailService;
import com.example.tourmatebackend.states.Role;
import com.example.tourmatebackend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/email")
public class AdminEmailController {

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @Autowired
    private UserRepository userRepository;

    // Send email endpoint
    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestHeader("Authorization") String authHeader,
                                       @RequestBody EmailRequest emailRequest) {

        User requester = extractUserFromToken(authHeader);
        if (!isAdmin(requester)) return unauthorized();

        if (emailRequest.getTo() == null || emailRequest.getTo().isEmpty() ||
                emailRequest.getSubject() == null || emailRequest.getSubject().isEmpty() ||
                emailRequest.getMessage() == null || emailRequest.getMessage().isEmpty()) {

            return ResponseEntity.badRequest()
                    .body(Map.of("success", false, "message", "To, subject and message are required"));
        }

        try {
            emailService.sendEmail(emailRequest.getTo(), emailRequest.getSubject(), emailRequest.getMessage());
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Email sent successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Failed to send email: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    // Helper methods

    private User extractUserFromToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) return null;
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        return userRepository.findByEmail(email).orElse(null);
    }

    private boolean isAdmin(User user) {
        return user != null && Role.ADMIN.equals(user.getRole());
    }

    private ResponseEntity<?> unauthorized() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("success", false, "message", "You are not authorized"));
    }

    // Request DTO (no Lombok)
    public static class EmailRequest {
        private String to;
        private String subject;
        private String message;

        // Getters and setters
        public String getTo() { return to; }
        public void setTo(String to) { this.to = to; }

        public String getSubject() { return subject; }
        public void setSubject(String subject) { this.subject = subject; }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }
}
