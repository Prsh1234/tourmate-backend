package com.example.tourmatebackend.controller.traveller;

import com.example.tourmatebackend.dto.traveller.GuideResponseDTO;
import com.example.tourmatebackend.model.Guide;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.FavouriteRepository;
import com.example.tourmatebackend.repository.GuideRepository;
import com.example.tourmatebackend.repository.UserRepository;
import com.example.tourmatebackend.states.GuideStatus;
import com.example.tourmatebackend.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.Base64;

@RestController
@RequestMapping("/api/traveller/guides")
public class SearchGuideController {

    @Autowired
    private FavouriteRepository favouriteRepository;
    @Autowired
    private GuideRepository guideRepository;
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

    private String encodeImage(byte[] img) {
        return (img != null && img.length > 0) ? Base64.getEncoder().encodeToString(img) : null;
    }

    // ========================
    // GET ALL APPROVED GUIDES (Paginated)
    // ========================
    @GetMapping("/approvedGuides")
    public ResponseEntity<?> getApprovedGuides(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestHeader("Authorization") String authHeader


    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        Page<Guide> guidePage = guideRepository.findByStatus(GuideStatus.APPROVED, pageable);

        List<GuideResponseDTO> data = guidePage.getContent().stream()
                .map(g -> mapToDTO(g, authHeader))
                .toList();

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Approved guides fetched",
                "data", data,
                "currentPage", guidePage.getNumber(),
                "pageSize", guidePage.getSize(),
                "totalGuides", guidePage.getTotalElements(),
                "totalPages", guidePage.getTotalPages()
        ));
    }

    // ========================
    // FILTER GUIDES
    // ========================
    @GetMapping("/filter")
    public ResponseEntity<?> filterGuides(
            @RequestParam(defaultValue = "") String location,
            @RequestParam(defaultValue = "0") Double minPrice,
            @RequestParam(defaultValue = "99999999") Double maxPrice,
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) List<String> category,
            @RequestParam(required = false) List<String> language,
            @RequestHeader("Authorization") String authHeader

    ) {

        Sort sort = sortDir.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Guide> dbPage = guideRepository
                .findByStatusAndLocationContainingIgnoreCaseAndPriceBetween(
                        GuideStatus.APPROVED, location, minPrice, maxPrice, pageable
                );

        List<GuideResponseDTO> filtered = dbPage.getContent().stream()
                .filter(g -> category == null || category.isEmpty()
                        || g.getCategories().stream().map(Enum::name).anyMatch(category::contains))
                .filter(g -> language == null || language.isEmpty()
                        || g.getLanguages().stream().map(Enum::name).anyMatch(language::contains))
                .map(g -> mapToDTO(g, authHeader))
                .toList();

        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Guides fetched",
                "data", filtered,
                "currentPage", dbPage.getNumber(),
                "pageSize", dbPage.getSize(),
                "totalGuides", dbPage.getTotalElements(),
                "totalPages", dbPage.getTotalPages()
        ));
    }

    // ========================
    // GET SINGLE GUIDE
    // ========================
    @GetMapping("/{guideId}")
    public ResponseEntity<?> getGuideById(@PathVariable int guideId,
                                          @RequestHeader("Authorization") String authHeader
    ) {
        Optional<Guide> guideOpt = guideRepository.findById(guideId);
        if (guideOpt.isEmpty() || guideOpt.get().getStatus() != GuideStatus.APPROVED) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status", "error", "message", "Guide not found or not approved"));
        }
        return ResponseEntity.ok(Map.of(
                "status", "success",
                "message", "Guide details",
                "data", mapToDTO(guideOpt.get(),authHeader)
        ));
    }

    // ========================
    // Convert Guide → GuideResponseDTO
    // ========================
    private GuideResponseDTO mapToDTO(Guide g, String authHeader) {
        User u = g.getUser();
        GuideResponseDTO dto = new GuideResponseDTO();
        dto.setGuideId(g.getId());
        dto.setExpertise(g.getExpertise());
        dto.setBio(g.getBio());
        dto.setLocation(g.getLocation());
        dto.setPrice(g.getPrice());
        dto.setCategories(g.getCategories());
        dto.setLanguages(g.getLanguages());
        int currentUserId = extractUserId(authHeader);

        dto.setFavorited(
                favouriteRepository.existsByUserIdAndGuideId(currentUserId, g.getId())
        );
        dto.setUserId(u.getId());
        dto.setUserName(u.getFirstName() + " " + u.getLastName());
        dto.setUserEmail(u.getEmail());
        dto.setProfilePic(encodeImage(u.getProfilePic()).getBytes());

        return dto;
    }
}
