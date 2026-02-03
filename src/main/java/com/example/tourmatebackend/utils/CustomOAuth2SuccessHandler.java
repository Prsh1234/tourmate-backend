package com.example.tourmatebackend.utils;

import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
public class CustomOAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();

        String email = oauthUser.getAttribute("email");
        String fullName = oauthUser.getAttribute("name");
        String firstName = OAuth2Helper.extractFirstName(fullName);
        String lastName = OAuth2Helper.extractLastName(fullName);

        String pictureUrl = oauthUser.getAttribute("picture");
        byte[] profilePicBytes = null;

        // Download profile pic from Google if available
        if (pictureUrl != null) {
            try {
                profilePicBytes = new java.net.URL(pictureUrl).openStream().readAllBytes();
            } catch (Exception ignored) {}
        }

        // Check if user exists, otherwise save
        User user;
        Optional<User> existing = userRepository.findByEmail(email);
        if (existing.isEmpty()) {
            user = new User();
            user.setEmail(email);
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setPassword(null); // Google login = no password
            user.setProfilePic(profilePicBytes);

            userRepository.save(user);
        } else {
            user = existing.get();
        }

        // Generate JWT token
        String token = jwtUtil.generateAccessToken(user.getEmail());

        String redirectUrl = "http://localhost:5173/oauth2/redirect?token=" + token;
        getRedirectStrategy().sendRedirect(request, response, redirectUrl);
    }
}

