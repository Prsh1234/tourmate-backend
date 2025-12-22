package com.example.tourmatebackend.controller.user;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.example.tourmatebackend.dto.guideRegistration.GuideRegisterRequestDTO;
import com.example.tourmatebackend.dto.guideRegistration.GuideRegisterResponseDTO;
import com.example.tourmatebackend.model.Guide;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.service.GuideService;
import com.example.tourmatebackend.repository.UserRepository;
import com.example.tourmatebackend.states.GuideStatus;
import com.example.tourmatebackend.utils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/api/user/guides")
public class GuideRegisterController {

    @Autowired
    private GuideService guideService;


    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    // -------------------------------
    // REGISTER GUIDE (only by current user)
    // -------------------------------



    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping(
            value = "/register/{userId}",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<?> registerGuide(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable int userId,
            @RequestPart("guide") String guideJson, // receive as String
            @RequestPart(value = "profilePic", required = false) MultipartFile profilePic,
            @RequestPart(value = "governmentPic", required = false) MultipartFile governmentPic
    ) {
        try {
            System.out.println("Received guideJson: " + guideJson);
            GuideRegisterRequestDTO guideRequest = objectMapper.readValue(guideJson, GuideRegisterRequestDTO.class);

            // Now proceed as before
            User tokenUser = userRepository.findByEmail(jwtUtil.extractEmail(authHeader.replace("Bearer ", ""))).orElseThrow();
            if (tokenUser.getId() != userId) {

                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of(
                        "status", "error",
                        "message", "Access denied!"
                ));
            }

            if (governmentPic != null) {
                guideRequest.setGovernmentPic(governmentPic.getBytes());
            }
            if (profilePic != null) {
                guideRequest.setProfilePic(profilePic.getBytes());
            }

            User user = userRepository.findById(userId).orElseThrow();
            Guide guide = guideService.registerGuide(user, guideRequest, profilePic, governmentPic);



            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Guide registration request submitted"
            ));

        } catch (Exception e) {


            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }


}
