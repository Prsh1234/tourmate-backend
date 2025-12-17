package com.example.tourmatebackend.service;

import com.example.tourmatebackend.dto.favourite.FavouriteDTO;
import com.example.tourmatebackend.dto.guide.TourItineraryDTO;
import com.example.tourmatebackend.dto.traveller.GuideResponseDTO;
import com.example.tourmatebackend.dto.traveller.TourResponseDTO;
import com.example.tourmatebackend.model.Favourite;
import com.example.tourmatebackend.model.Guide;
import com.example.tourmatebackend.model.Tour;
import com.example.tourmatebackend.repository.FavouriteRepository;
import com.example.tourmatebackend.repository.GuideRepository;
import com.example.tourmatebackend.repository.TourRepository;
import com.example.tourmatebackend.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public class FavouriteService {

    private GuideResponseDTO mapGuideToDTO(Guide guide) {
        GuideResponseDTO dto = new GuideResponseDTO();

        dto.setGuideId(guide.getId());
        dto.setBio(guide.getBio());
        dto.setPrice(guide.getPrice());
        dto.setCategories(guide.getCategories());
        dto.setLanguages(guide.getLanguages());

        // User details
        dto.setUserId(guide.getUser().getId());
        dto.setFullName(guide.getFullName());
        dto.setEmail(guide.getEmail());
        dto.setProfilePic(guide.getUser().getProfilePic());

        return dto;
    }
    private TourResponseDTO mapTourToDTO(Tour tour) {
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
        dto.setFavorited(true);
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
    private FavouriteDTO mapToDTO(Favourite fav) {

        String type = fav.getGuide() != null ? "GUIDE" : "TOUR";

        GuideResponseDTO guideDTO = null;
        TourResponseDTO tourDTO = null;

        // Map Guide → GuideResponseDTO
        if (fav.getGuide() != null) {
            guideDTO = mapGuideToDTO(fav.getGuide());
            guideDTO.setFavorited(true);
        }

        // Map Tour → TourResponseDTO
        if (fav.getTour() != null) {
            tourDTO = mapTourToDTO(fav.getTour());
            tourDTO.setFavorited(true);
        }

        return new FavouriteDTO(
                fav.getId(),
                type,
                guideDTO,
                tourDTO,
                fav.getCreatedAt()
        );
    }

    @Autowired
    private FavouriteRepository favoriteRepository;

    @Autowired
    private GuideRepository guideRepository;

    @Autowired
    private TourRepository tourRepository;
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public String toggleGuideFavorite(int userId, int guideId) {

        if (favoriteRepository.existsByUserIdAndGuideId(userId, guideId)) {
            favoriteRepository.deleteByUserIdAndGuideId(userId, guideId);
            return "Guide unfavorited";
        }

        Favourite fav = new Favourite();
        fav.setUser(userRepository.findById(userId).orElseThrow());
        fav.setGuide(guideRepository.findById(guideId).orElseThrow());
        fav.setType("GUIDE");
        favoriteRepository.save(fav);

        return "Guide favorited";
    }

    @Transactional
    public String toggleTourFavorite(int userId, int tourId) {

        if (favoriteRepository.existsByUserIdAndTourId(userId, tourId)) {
            favoriteRepository.deleteByUserIdAndTourId(userId, tourId);
            return "Tour unfavorited";
        }

        Favourite fav = new Favourite();
        fav.setUser(userRepository.findById(userId).orElseThrow());
        fav.setTour(tourRepository.findById(tourId).orElseThrow());
        fav.setType("TOUR");
        favoriteRepository.save(fav);

        return "Tour favorited";
    }
    public List<FavouriteDTO> getFavouriteGuides(int userId) {
        return favoriteRepository.findByUserIdAndType(userId, "GUIDE")
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public List<FavouriteDTO> getFavouriteTours(int userId) {
        return favoriteRepository.findByUserIdAndType(userId, "TOUR")
                .stream()
                .map(this::mapToDTO)
                .toList();
    }
    public List<FavouriteDTO> getUserFavorites(int userId) {
        return favoriteRepository.findByUserId(userId)
                .stream()
                .map(this::mapToDTO)
                .toList();
    }
}
