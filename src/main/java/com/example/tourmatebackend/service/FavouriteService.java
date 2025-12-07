package com.example.tourmatebackend.service;

import com.example.tourmatebackend.dto.favourite.FavouriteDTO;
import com.example.tourmatebackend.model.Favourite;
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


    private FavouriteDTO mapToDTO(Favourite fav) {

        String type = fav.getGuide() != null ? "GUIDE" : "TOUR";

        GuideSimpleDTO guideDTO = null;
        TourSimpleDTO tourDTO = null;

        if (fav.getGuide() != null) {
            Guide guide = fav.getGuide();
            User user = guide.getUser();

            guideDTO = new GuideSimpleDTO(
                    guide.getId(),
                    user.getFirstName() + " " + user.getLastName(),
                    user.getProfilePic()
            );
        }

        if (fav.getTour() != null) {
            Tour tour = fav.getTour();

            tourDTO = new TourSimpleDTO(
                    tour.getId(),
                    tour.getTitle(),
                    tour.getThumbnail()
            );
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
