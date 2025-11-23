package com.example.tourmatebackend.controller.guide;

import com.example.tourmatebackend.model.Guide;
import com.example.tourmatebackend.repository.GuideRepository;
import com.example.tourmatebackend.states.GuideStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.Base64;

@RestController
@RequestMapping("/api/user/guides")
public class GuidesController {

    @Autowired
    private GuideRepository guideRepository;

    // Convert profile pic byte[] → Base64 string
    private String encodeImage(byte[] img) {
        return (img != null && img.length > 0) ? Base64.getEncoder().encodeToString(img) : null;
    }

    // ============================
    // GET ALL APPROVED GUIDES
    // ============================
    @GetMapping("/all")
    public ResponseEntity<?> getAllApprovedGuides() {

        List<Guide> approvedGuides = guideRepository.findAll()
                .stream()
                .filter(g -> g.getStatus() == GuideStatus.APPROVED)
                .collect(Collectors.toList());

        List<Map<String, Object>> data = approvedGuides.stream().map(g -> {
            Map<String, Object> map = new HashMap<>();
            map.put("guideId", g.getId());
            map.put("expertise", g.getExpertise());
            map.put("bio", g.getBio());
            map.put("categories", g.getCategories());
            // User info
            map.put("userId", g.getUser().getId());
            map.put("userEmail", g.getUser().getEmail());
            map.put("userName", g.getUser().getFirstName() + " " + g.getUser().getLastName());

            // Profile picture (from User)
            map.put("profilePic", encodeImage(g.getUser().getProfilePic()));

            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(
                Map.of("status", "success", "message", "All approved guides", "data", data)
        );
    }

    // ============================
    // GET SINGLE GUIDE BY ID
    // ============================
    @GetMapping("/{guideId}")
    public ResponseEntity<?> getGuideById(@PathVariable int guideId) {

        Optional<Guide> guideOpt = guideRepository.findById(guideId);

        if (guideOpt.isEmpty() || guideOpt.get().getStatus() != GuideStatus.APPROVED) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "Guide not found or not approved"));
        }

        Guide g = guideOpt.get();

        Map<String, Object> data = new HashMap<>();
        data.put("guideId", g.getId());
        data.put("expertise", g.getExpertise());
        data.put("bio", g.getBio());
        data.put("categories", g.getCategories());
        // User info
        data.put("userId", g.getUser().getId());
        data.put("userEmail", g.getUser().getEmail());
        data.put("userName", g.getUser().getFirstName() + " " + g.getUser().getLastName());

        // Profile picture
        data.put("profilePic", encodeImage(g.getUser().getProfilePic()));

        return ResponseEntity.ok(
                Map.of("status", "success", "message", "Guide details", "data", data)
        );
    }
}
