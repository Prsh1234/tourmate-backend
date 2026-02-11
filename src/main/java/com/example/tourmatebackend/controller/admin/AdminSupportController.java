package com.example.tourmatebackend.controller.admin;


import com.example.tourmatebackend.dto.support.SupportDTO;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.UserRepository;
import com.example.tourmatebackend.service.SupportService;
import com.example.tourmatebackend.states.Role;
import com.example.tourmatebackend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/support")
public class AdminSupportController {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private SupportService supportService;

    @GetMapping
    public ResponseEntity<?> getAllSupport(@RequestHeader("Authorization") String authHeader) {
        User requester = extractUserFromToken(authHeader);
        if (!isAdmin(requester)) return unauthorized();
        List<SupportDTO> messages = supportService.getAllSupportMessages();
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/role/{role}")
    public ResponseEntity<?> getSupportByRole(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable String role) {

        User requester = extractUserFromToken(authHeader);
        if (!isAdmin(requester)) return unauthorized();

        if (role.equalsIgnoreCase("ALL")) {
            return ResponseEntity.ok(supportService.getAllSupportMessages());
        }

        Role r;
        try {
            r = Role.valueOf(role.toUpperCase());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Invalid role");
        }

        return ResponseEntity.ok(supportService.getSupportMessagesByRole(r));
    }


    @PutMapping("/mark-seen/{id}")
    public ResponseEntity<?> markAsSeen(@RequestHeader("Authorization") String authHeader,
                                                 @PathVariable Long id) {
        User requester = extractUserFromToken(authHeader);
        if (!isAdmin(requester)) return unauthorized();

        SupportDTO updatedMessage = supportService.markAsSeen(id);
        return ResponseEntity.ok(updatedMessage);
    }




    private User extractUserFromToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        return userRepository.findByEmail(email).orElse(null);
    }

    private boolean isAdmin(User user) {
        return user != null && Role.ADMIN.equals(user.getRole());
    }
    private ResponseEntity<?> unauthorized() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("status", "error", "message", "You are not authorized"));
    }
}
