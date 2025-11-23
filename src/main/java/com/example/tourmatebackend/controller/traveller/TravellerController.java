package com.example.tourmatebackend.controller.traveller;

import com.example.tourmatebackend.model.Tour;
import com.example.tourmatebackend.repository.TourRepository;
import com.example.tourmatebackend.states.TourStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/traveller")
public class TravellerController {

    @Autowired
    private TourRepository tourRepository;

    @GetMapping("/tours")
    public ResponseEntity<?> getPostedTours(
            @RequestParam(required = false, defaultValue = "") String location,
            @RequestParam(required = false, defaultValue = "0") Double minPrice,
            @RequestParam(required = false, defaultValue = "10000000000") Double maxPrice,
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false, defaultValue = "startDate") String sortBy,
            @RequestParam(required = false, defaultValue = "asc") String sortDir,
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "10") int size,
            @RequestParam(required = false) List<String> category,  // New category filter
            @RequestParam(required = false) List<String> language   // New language filter
    ) {
        LocalDate start = startDate != null ? java.time.LocalDate.parse(startDate) : java.time.LocalDate.of(1900,1,1);
        LocalDate end = endDate != null ? java.time.LocalDate.parse(endDate) : java.time.LocalDate.of(3000,1,1);

        Sort sort = "desc".equalsIgnoreCase(sortDir) ?
                org.springframework.data.domain.Sort.by(sortBy).descending() :
                org.springframework.data.domain.Sort.by(sortBy).ascending();

        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, sort);

        // Fetch tours with basic filters
        Page<Tour> tourPage = tourRepository
                .findByStatusAndLocationContainingIgnoreCaseAndPriceBetweenAndStartDateGreaterThanEqualAndEndDateLessThanEqual(
                        TourStatus.POSTED,
                        location,
                        minPrice,
                        maxPrice,
                        start,
                        end,
                        pageable
                );

        // Filter by category and language in memory (can also move to DB later)
        List<Tour> filteredTours = tourPage.getContent().stream()
                .filter(t -> category == null || category.isEmpty() ||
                        t.getCategories().stream()
                                .map(Enum::name)            // convert Category enum to String
                                .anyMatch(category::contains))  // match with request strings
                .filter(t -> language == null || language.isEmpty() ||
                        t.getLanguages().stream()
                                .map(Enum::name)
                                .anyMatch(language::contains))
                .toList();


        List<Map<String, Object>> tourData = filteredTours.stream().map(tour -> {
            Map<String, Object> guideData = Map.of(
                    "guideName", tour.getGuide().getUser().getFirstName() + " " + tour.getGuide().getUser().getLastName(),
                    "guideExpertise", tour.getGuide().getExpertise()
            );

            Map<String, Object> t = new LinkedHashMap<>();
            t.put("id", tour.getId());
            t.put("title", tour.getTitle());
            t.put("description", tour.getDescription());
            t.put("location", tour.getLocation());
            t.put("price", tour.getPrice());
            t.put("startDate", tour.getStartDate());
            t.put("endDate", tour.getEndDate());
            t.put("categories", tour.getCategories());  // include categories
            t.put("languages", tour.getLanguages());    // include languages
            t.putAll(guideData);
            return t;
        }).collect(Collectors.toList());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("message", "Tours fetched successfully.");
        response.put("data", tourData);
        response.put("currentPage", tourPage.getNumber());
        response.put("pageSize", tourPage.getSize());
        response.put("totalTours", tourPage.getTotalElements());
        response.put("totalPages", tourPage.getTotalPages());

        return ResponseEntity.ok(response);
    }





    @GetMapping("/tours/{id}")
    public ResponseEntity<?> getTourById(@PathVariable int id) {
        return tourRepository.findById(id)
                .map(tour -> {
                    // Only allow POSTED tours
                    if (tour.getStatus() != TourStatus.POSTED) {
                        return ResponseEntity.status(404)
                                .body(Map.of(
                                        "status", "error",
                                        "message", "Tour not found or not available."
                                ));
                    }

                    Map<String, Object> guideData = Map.of(
                            "guideName", tour.getGuide().getUser().getFirstName() + " " + tour.getGuide().getUser().getLastName(),
                            "guideExpertise", tour.getGuide().getExpertise()
                    );

                    Map<String, Object> tourData = new LinkedHashMap<>();
                    tourData.put("id", tour.getId());
                    tourData.put("title", tour.getTitle());
                    tourData.put("description", tour.getDescription());
                    tourData.put("location", tour.getLocation());
                    tourData.put("price", tour.getPrice());
                    tourData.put("startDate", tour.getStartDate());
                    tourData.put("endDate", tour.getEndDate());
                    tourData.put("categories", tour.getCategories());
                    tourData.put("languages", tour.getLanguages());
                    tourData.putAll(guideData);

                    return ResponseEntity.ok(Map.of(
                            "status", "success",
                            "message", "Tour details fetched successfully.",
                            "data", tourData
                    ));
                })
                .orElse(ResponseEntity.status(404)
                        .body(Map.of(
                                "status", "error",
                                "message", "Tour not found."
                        )));
    }

}