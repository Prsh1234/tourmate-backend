package com.example.tourmatebackend.controller.favourite;

import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.UserRepository;
import com.example.tourmatebackend.service.FavouriteService;
import com.example.tourmatebackend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/traveller/favourites")
public class FavouriteController {

    @Autowired
    private FavouriteService favoriteService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserRepository userRepository;

    private int extractUserId(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }

    @PostMapping("/guide/{guideId}")
    public ResponseEntity<?> favoriteGuide(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int guideId) {

        int userId = extractUserId(authHeader);
        return ResponseEntity.ok(favoriteService.toggleGuideFavorite(userId, guideId));
    }

    @PostMapping("/tour/{tourId}")
    public ResponseEntity<?> favoriteTour(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int tourId) {

        int userId = extractUserId(authHeader);
        return ResponseEntity.ok(favoriteService.toggleTourFavorite(userId, tourId));
    }

    @GetMapping("/guides")
    public ResponseEntity<?> getFavouriteGuides(@RequestHeader("Authorization") String authHeader) {
        int userId = extractUserId(authHeader);
        return ResponseEntity.ok(favoriteService.getFavouriteGuides(userId));
    }

    @GetMapping("/tours")
    public ResponseEntity<?> getFavouriteTours(@RequestHeader("Authorization") String authHeader) {
        int userId = extractUserId(authHeader);
        return ResponseEntity.ok(favoriteService.getFavouriteTours(userId));
    }
    @GetMapping("/my")
    public ResponseEntity<?> getMyFavorites(@RequestHeader("Authorization") String authHeader) {
        int userId = extractUserId(authHeader);
        return ResponseEntity.ok(favoriteService.getUserFavorites(userId));
    }
}
