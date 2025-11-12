package com.example.tourmatebackend.controller.admin;

import com.example.tourmatebackend.model.Guide;
import com.example.tourmatebackend.states.GuideStatus;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.GuideRepository;
import com.example.tourmatebackend.repository.UserRepository;
import com.example.tourmatebackend.states.Role;
import com.example.tourmatebackend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/guides")
public class AdminGuideController {

    @Autowired
    private GuideRepository guideRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // List all pending guide requests
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingGuides(@RequestHeader("Authorization") String authHeader) {

        List<Guide> pendingGuides = guideRepository.findAll()
                .stream()
                .filter(g -> g.getStatus() == GuideStatus.PENDING)
                .collect(Collectors.toList());

        List<Map<String, Object>> data = pendingGuides.stream().map(g -> {
            Map<String, Object> map = new HashMap<>();
            map.put("guideId", g.getId());
            map.put("expertise", g.getExpertise());
            map.put("bio", g.getBio());
            map.put("status", g.getStatus().name());
            map.put("userId", g.getUser().getId());
            map.put("userEmail", g.getUser().getEmail());
            map.put("userName", g.getUser().getFirstName() + " " + g.getUser().getLastName());
            return map;
        }).collect(Collectors.toList());


        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Pending guide requests",
                "data", data
        ));
    }

    // Approve or reject a guide request
    @PostMapping("/{guideId}/decision")
    public ResponseEntity<?> decideGuide(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int guideId,
            @RequestParam("action") String action // "approve" or "reject"
    ) {


        Guide guide = guideRepository.findById(guideId).orElse(null);
        if (guide == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "Guide request not found"));
        }

        if (action.equalsIgnoreCase("approve")) {
            guide.setStatus(GuideStatus.APPROVED);
            guide.getUser().setRole(Role.GUIDE); // <-- Update user role
        } else if (action.equalsIgnoreCase("reject")) {
            guide.setStatus(GuideStatus.REJECTED);
            // Optionally reset role to TRAVELLER if previously changed
            if (guide.getUser().getRole() == Role.GUIDE) {
                guide.getUser().setRole(Role.TRAVELLER);
            }
        } else {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "Invalid action. Use 'approve' or 'reject'."));
        }

        guideRepository.save(guide);

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Guide request " + action + "d successfully",
                "data", Map.of(
                        "guideId", guide.getId(),
                        "status", guide.getStatus().name()
                )
        ));
    }

    private boolean isAdmin(User user) {
        return user != null && user.getRole() == Role.ADMIN;
    }

    private ResponseEntity<Map<String, Object>> forbiddenResponse() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("status", "error", "message", "You are not authorized to perform this action"));
    }
}
