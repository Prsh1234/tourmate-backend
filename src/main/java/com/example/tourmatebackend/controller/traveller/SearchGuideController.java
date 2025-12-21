package com.example.tourmatebackend.controller.traveller;

import com.example.tourmatebackend.dto.traveller.GuideResponseDTO;
import com.example.tourmatebackend.model.Guide;
import com.example.tourmatebackend.model.GuideReview;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.FavouriteRepository;
import com.example.tourmatebackend.repository.GuideRepository;
import com.example.tourmatebackend.repository.GuideReviewRepository;
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
    private GuideReviewRepository guideReviewRepository;
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
            @RequestParam(defaultValue = "") String search,
            @RequestParam(defaultValue = "0") Double minPrice,
            @RequestParam(defaultValue = "99999999999") Double maxPrice,
            @RequestParam(defaultValue = "price") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) List<String> category,
            @RequestParam(required = false) List<String> language,
            @RequestParam(defaultValue = "0") int rating,
            @RequestHeader("Authorization") String authHeader
    ) {

        // Only sort by DB column (price), ignore rating here
        Sort sort = Sort.by(sortBy.equalsIgnoreCase("price") ? "price" : "id"); // fallback to id if not price
        sort = sortDir.equalsIgnoreCase("desc") ? sort.descending() : sort.ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        // Fetch guides from DB by price and status
        Page<Guide> dbPage = guideRepository.findByStatusAndPriceBetween(
                GuideStatus.APPROVED, minPrice, maxPrice, pageable
        );

        // Map to DTO and filter by rating in-memory
        List<GuideResponseDTO> filtered = dbPage.getContent().stream()
                .filter(g -> category == null || category.isEmpty()
                        || g.getCategories().stream().map(Enum::name).anyMatch(category::contains))
                .filter(g -> language == null || language.isEmpty()
                        || g.getLanguages().stream().map(Enum::name).anyMatch(language::contains))
                .map(g -> mapToDTO(g, authHeader))
                .filter(dto -> dto.getAverageRating() >= rating) // in-memory rating filter
                .filter(dto -> search.isEmpty() ||
                        dto.getFullName().toLowerCase().contains(search.toLowerCase()) ||
                        dto.getLocation().toLowerCase().contains(search.toLowerCase()))
                .sorted((a, b) -> {
                    if ("rating".equalsIgnoreCase(sortBy)) {
                        return "desc".equalsIgnoreCase(sortDir)
                                ? Double.compare(b.getAverageRating(), a.getAverageRating())
                                : Double.compare(a.getAverageRating(), b.getAverageRating());
                    }
                    return 0; // price sorting handled by DB
                })
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

        GuideResponseDTO dto = new GuideResponseDTO();

        dto.setGuideId(g.getId());
        dto.setBio(g.getBio());
        dto.setPrice(g.getPrice());
        dto.setCategories(g.getCategories());
        dto.setLanguages(g.getLanguages());
        dto.setEmail(g.getEmail());
        dto.setFullName(g.getFullName());
        dto.setUserId(g.getUser().getId());
        dto.setPhoneNumber(g.getPhoneNumber());
        dto.setProfilePic(g.getProfilePic());
        dto.setLocation(g.getLocation());
        List<GuideReview> reviews = guideReviewRepository.findByGuideId(g.getId());
        double avgRating = reviews.stream().mapToInt(GuideReview::getRating).average().orElse(0.0);
        dto.setAverageRating(avgRating);
        dto.setTotalReviews(reviews.size());

        int currentUserId = extractUserId(authHeader);

        dto.setFavorited(
                favouriteRepository.existsByUserIdAndGuideId(currentUserId, g.getId())
        );


        return dto;
    }

}
