package com.example.tourmatebackend.controller.admin;

import com.example.tourmatebackend.dto.admin.GuideDecisionResponseDTO;
import com.example.tourmatebackend.dto.admin.GuideRequestDTO;
import com.example.tourmatebackend.model.Guide;
import com.example.tourmatebackend.service.NotificationService;
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
    @Autowired
    private NotificationService notificationService;

    // -------------------------
    // Verify Admin From Token
    // -------------------------
    private User extractUserFromToken(String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        String email = jwtUtil.extractEmail(token);
        return userRepository.findByEmail(email).orElse(null);
    }

    private boolean isAdmin(User user) {
        return user != null && user.getRole() == Role.ADMIN;
    }

    private ResponseEntity<?> unauthorized() {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(Map.of("status", "error", "message", "You are not authorized"));
    }

    // -------------------------
    // Get All Pending Guides
    // -------------------------
    @GetMapping("/pending")
    public ResponseEntity<?> getPendingGuides(@RequestHeader("Authorization") String authHeader) {

        User requester = extractUserFromToken(authHeader);
        if (!isAdmin(requester)) return unauthorized();

        List<Guide> pendingGuides = guideRepository.findAll()
                .stream()
                .filter(g -> g.getStatus() == GuideStatus.PENDING)
                .collect(Collectors.toList());

        List<GuideRequestDTO> dtoList = pendingGuides.stream().map(guide -> {
            GuideRequestDTO dto = new GuideRequestDTO();
            dto.setFullName(guide.getFullName());
            dto.setEmail(guide.getEmail());
            dto.setPhoneNumber(guide.getPhoneNumber());
            dto.setExperience(guide.getExperience());
            dto.setLanguages(guide.getLanguages());
            dto.setCategories(guide.getCategories());
            dto.setBio(guide.getBio());
            dto.setPrice(guide.getPrice());
            dto.setProfilePic(guide.getProfilePic());
            dto.setGovernmentPic(guide.getGovernmentPic());
            dto.setGovernmentNumber(guide.getGovernmentNumber());
            dto.setDob(guide.getDob()); // ✅ LocalDate
            dto.setStatus(GuideStatus.PENDING);
            dto.setUserId(guide.getUser().getId());


            return dto;
        }).collect(Collectors.toList());

        return ResponseEntity.ok(
                Map.of(
                        "status", "success",
                        "message", "Pending guide requests",
                        "data", dtoList
                )
        );
    }

    // -------------------------
    // Approve or Reject Guide
    // -------------------------
    @PostMapping("/{guideId}/decision")
    public ResponseEntity<?> decideGuide(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int guideId,
            @RequestParam("action") String action
    ) {

        User requester = extractUserFromToken(authHeader);
        if (!isAdmin(requester)) return unauthorized();

        Guide guide = guideRepository.findById(guideId).orElse(null);
        if (guide == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "Guide not found"));
        }

        if (action.equalsIgnoreCase("approve")) {
            guide.setStatus(GuideStatus.APPROVED);
            guide.getUser().setRole(Role.GUIDE);

        } else if (action.equalsIgnoreCase("reject")) {
            guide.setStatus(GuideStatus.REJECTED);

            if (guide.getUser().getRole() == Role.GUIDE) {
                guide.getUser().setRole(Role.TRAVELLER);
            }

        } else {
            return ResponseEntity.badRequest()
                    .body(Map.of("status", "error", "message", "Invalid action"));
        }

        guideRepository.save(guide);

        GuideDecisionResponseDTO dto = new GuideDecisionResponseDTO();
        dto.setGuideId(guide.getId());
        dto.setStatus(guide.getStatus().name());
        notificationService.createNotification(
                guide.getUser().getId(),
                "Registration Success",
                "You Registration has been " +action +" ."
        );

        return ResponseEntity.ok(
                Map.of(
                        "status", "success",
                        "message", "Guide request " + action + "d successfully",
                        "data", dto
                )
        );
    }
}
