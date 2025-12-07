package com.example.tourmatebackend.controller.traveller;

import com.example.tourmatebackend.dto.guide.TourItineraryDTO;
import com.example.tourmatebackend.dto.traveller.TourResponseDTO;
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
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/traveller")
public class SearchToursController {

    @Autowired
    private TourRepository tourRepository;

    // --------------------------
    // Helper: Map Tour → DTO
    // --------------------------
    private TourResponseDTO mapToDTO(Tour tour) {
        TourResponseDTO dto = new TourResponseDTO();
        dto.setId(tour.getId());
        dto.setTitle(tour.getTitle());
        dto.setDescription(tour.getDescription());
        dto.setLocation(tour.getLocation());
        dto.setPrice(tour.getPrice());
        dto.setStartDate(tour.getStartDate());
        dto.setEndDate(tour.getEndDate());
        dto.setCategories(tour.getCategories());
        dto.setLanguages(tour.getLanguages());
        dto.setIncluded(tour.getIncluded());
        dto.setNotIncluded(tour.getNotIncluded());
        dto.setImportantInformation(tour.getImportantInformation());
        dto.setGuideId(tour.getGuide().getId());
        dto.setGuideName(tour.getGuide().getUser().getFirstName() + " " + tour.getGuide().getUser().getLastName());
        dto.setGuideExpertise(tour.getGuide().getExpertise());
        dto.setItineraries(
                tour.getItineraries()
                        .stream()
                        .map(it -> new TourItineraryDTO(
                                it.getId(),
                                it.getStepNumber(),
                                it.getTime(),
                                it.getTitle(),
                                it.getDescription()
                        ))
                        .toList()
        );
        return dto;
    }

    // ============================
    // GET ALL TOURS WITH FILTERS
    // ============================
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
            @RequestParam(required = false) List<String> category,
            @RequestParam(required = false) List<String> language
    ) {
        LocalDate start = startDate != null ? LocalDate.parse(startDate) : LocalDate.of(1900,1,1);
        LocalDate end = endDate != null ? LocalDate.parse(endDate) : LocalDate.of(3000,1,1);

        Sort sort = "desc".equalsIgnoreCase(sortDir) ?
                Sort.by(sortBy).descending() :
                Sort.by(sortBy).ascending();

        Pageable pageable = org.springframework.data.domain.PageRequest.of(page, size, sort);

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

        // Filter by category and language
        List<TourResponseDTO> filteredTours = tourPage.getContent().stream()
                .filter(t -> category == null || category.isEmpty() ||
                        t.getCategories().stream()
                                .map(Enum::name)
                                .anyMatch(category::contains))
                .filter(t -> language == null || language.isEmpty() ||
                        t.getLanguages().stream()
                                .map(Enum::name)
                                .anyMatch(language::contains))
                .map(this::mapToDTO)
                .collect(Collectors.toList());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("status", "success");
        response.put("message", "Tours fetched successfully.");
        response.put("data", filteredTours);
        response.put("currentPage", tourPage.getNumber());
        response.put("pageSize", tourPage.getSize());
        response.put("totalTours", tourPage.getTotalElements());
        response.put("totalPages", tourPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    // ============================
    // GET SINGLE TOUR BY ID
    // ============================
    @GetMapping("/tours/{id}")
    public ResponseEntity<?> getTourById(@PathVariable int id) {
        return tourRepository.findById(id)
                .map(tour -> {
                    if (tour.getStatus() != TourStatus.POSTED) {
                        return ResponseEntity.status(404)
                                .body(Map.of("status", "error", "message", "Tour not found or not available."));
                    }

                    TourResponseDTO dto = mapToDTO(tour);

                    return ResponseEntity.ok(Map.of(
                            "status", "success",
                            "message", "Tour details fetched successfully.",
                            "data", dto
                    ));
                })
                .orElse(ResponseEntity.status(404)
                        .body(Map.of("status", "error", "message", "Tour not found.")));
    }

}
