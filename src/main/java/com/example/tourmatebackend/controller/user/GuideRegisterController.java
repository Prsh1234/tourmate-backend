package com.example.tourmatebackend.controller.user;

import com.example.tourmatebackend.model.Guide;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.GuideRepository;
import com.example.tourmatebackend.service.GuideService;
import com.example.tourmatebackend.repository.UserRepository;
import com.example.tourmatebackend.states.GuideStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/user/guides")
public class GuideRegisterController {

    @Autowired
    private GuideService guideService;

    @Autowired
    private GuideRepository guideRepository;

    @Autowired
    private UserRepository userRepository;

    // Register a new guide
    @PostMapping("/register/{userId}")
    public ResponseEntity<?> registerGuide(@PathVariable int userId, @RequestBody Guide guideRequest) {
        User user = userRepository.findById(userId)
                .orElse(null);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of(
                            "status", "error",
                            "message", "User not found"
                    ));
        }
        try {
            Guide guide = guideService.registerGuide(user, guideRequest);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Guide registration request submitted",
                    "data", Map.of(
                            "guideId", guide.getId(),
                            "expertise", guide.getExpertise(),
                            "bio", guide.getBio(),
                            "status", guide.getStatus().name(),
                            "userId", user.getId(),
                            "userEmail", user.getEmail()
                    )
            ));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of(
                            "status", "error",
                            "message", e.getMessage()
                    ));
        }
    }
    // Get all approved guides
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
            map.put("userId", g.getUser().getId());
            map.put("userEmail", g.getUser().getEmail());
            map.put("userName", g.getUser().getFirstName() + " " + g.getUser().getLastName());
            return map;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(Map.of("status", "success", "message", "All approved guides", "data", data));
    }

    // Get selected guide by ID
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
        data.put("userId", g.getUser().getId());
        data.put("userEmail", g.getUser().getEmail());
        data.put("userName", g.getUser().getFirstName() + " " + g.getUser().getLastName());

        return ResponseEntity.ok(Map.of("status", "success", "message", "Guide details", "data", data));
    }

}
