package com.example.tourmatebackend.controller.user;

import com.example.tourmatebackend.dto.user.UpdateUserRequest;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.UserRepository;
import com.example.tourmatebackend.service.UserService;
import com.example.tourmatebackend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/getOAuth")
    public Map<String, Object> getUserInfo(@RequestHeader("Authorization") String authHeader) {
        String token = null;
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        if (token == null || !jwtUtil.validateToken(token)) {
            throw new RuntimeException("Invalid or missing token");
        }

        String email = jwtUtil.extractEmail(token);
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty()) {
            throw new RuntimeException("User not found");
        }

        User user = userOpt.get();

        Map<String, Object> response = new HashMap<>();
        response.put("email", user.getEmail());
        response.put("firstName", user.getFirstName());
        response.put("lastName", user.getLastName());
        response.put("token", token);
        response.put("userId", user.getId());
        response.put("role", user.getRole().name());

        // Convert profilePic to Base64 string if exists
        if (user.getProfilePic() != null) {
            String base64Pic = Base64.getEncoder().encodeToString(user.getProfilePic());
            response.put("profilePic", base64Pic);
        }

        return response;
    }
    @PutMapping("/update/{userId}")
    public ResponseEntity<?> updateUserDetails(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int userId,
            @RequestBody UpdateUserRequest request
    ) {
        try {
            // Extract user from token
            String token = authHeader.replace("Bearer ", "");
            String emailFromToken = jwtUtil.extractEmail(token);
            User tokenUser = userRepository.findByEmail(emailFromToken).orElseThrow();

            if (tokenUser == null || tokenUser.getId() != userId) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("You are not allowed to edit this user.");
            }

            User user = userRepository.findById(userId).orElse(null);
            if (user == null) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("User not found.");
            }

            if (request.getFirstName() != null)
                user.setFirstName(request.getFirstName());

            if (request.getLastName() != null)
                user.setLastName(request.getLastName());

            if (request.getPhoneNumber() != null)
                user.setPhoneNumber(request.getPhoneNumber());

            userRepository.save(user);

            return ResponseEntity.ok("User details updated successfully!");

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error updating user: " + e.getMessage());
        }
    }
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUser(@PathVariable int userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }
    @GetMapping("/getDetails")
    public ResponseEntity<?> getMyDetails(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        return ResponseEntity.ok(userService.getLoggedInUser(token));
    }
}
