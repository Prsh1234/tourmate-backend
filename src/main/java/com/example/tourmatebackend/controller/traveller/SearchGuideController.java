package com.example.tourmatebackend.controller.traveller;

import com.example.tourmatebackend.model.Guide;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.GuideRepository;
import com.example.tourmatebackend.states.GuideStatus;
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
    private GuideRepository guideRepository;

    // Convert profile pic byte[] → Base64 string
    private String encodeImage(byte[] img) {
        return (img != null && img.length > 0) ? Base64.getEncoder().encodeToString(img) : null;
    }

    // ============================
    // GET ALL APPROVED GUIDES
    // ============================
    @GetMapping("/approvedGuides")
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
            map.put("languages", g.getLanguages());
            map.put("price", g.getPrice());
            map.put("location", g.getLocation());
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


    @GetMapping("/filter")
    public ResponseEntity<?> getGuides(
            @RequestParam(required = false, defaultValue = "") String location,
            @RequestParam(required = false, defaultValue = "0") Double minPrice,
            @RequestParam(required = false, defaultValue = "10000000000") Double maxPrice,
            @RequestParam(required = false, defaultValue = "price") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDir,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) List<String> category,    // multiple categories
            @RequestParam(required = false) List<String> language     // multiple languages
    ) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = PageRequest.of(page, size, sort);

        // BASIC filters (location + price)
        Page<Guide> guidePage = guideRepository
                .findByStatusAndLocationContainingIgnoreCaseAndPriceBetween(
                        GuideStatus.APPROVED,
                        location,
                        minPrice,
                        maxPrice,
                        pageable
                );

        // CATEGORY + LANGUAGE filtering
        List<Guide> filteredGuides = guidePage.getContent().stream()
                .filter(g -> category == null || category.isEmpty() ||
                        g.getCategories().stream()
                                .map(Enum::name)
                                .anyMatch(category::contains))
                .filter(g -> language == null || language.isEmpty() ||
                        g.getLanguages().stream()
                                .map(Enum::name)
                                .anyMatch(language::contains))
                .toList();

        // BUILD RESPONSE (same format as your Tour API)
        List<Map<String, Object>> guideData = filteredGuides.stream().map(g -> {
            User u = g.getUser();

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("guideId", g.getId());
            data.put("expertise", g.getExpertise());
            data.put("bio", g.getBio());
            data.put("location", g.getLocation());
            data.put("price", g.getPrice());
            data.put("categories", g.getCategories());
            data.put("languages", g.getLanguages());

            // User details
            data.put("userId", u.getId());
            data.put("userName", u.getFirstName() + " " + u.getLastName());
            data.put("userEmail", u.getEmail());
            data.put("profilePic", encodeImage(u.getProfilePic()));

            return data;
        }).collect(Collectors.toList());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("message", "Guides fetched successfully.");
        response.put("data", guideData);
        response.put("currentPage", guidePage.getNumber());
        response.put("pageSize", guidePage.getSize());
        response.put("totalGuides", guidePage.getTotalElements());
        response.put("totalPages", guidePage.getTotalPages());

        return ResponseEntity.ok(response);
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
        data.put("languages", g.getLanguages());
        data.put("price", g.getPrice());
        data.put("location", g.getLocation());
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
