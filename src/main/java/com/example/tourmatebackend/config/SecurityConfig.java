package com.example.tourmatebackend.config;

import com.example.tourmatebackend.utils.CustomOAuth2SuccessHandler;
import com.example.tourmatebackend.utils.JwtFilter;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
public class SecurityConfig {

    @Autowired
    private CustomOAuth2SuccessHandler customOAuth2SuccessHandler;

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Open endpoints
                        .requestMatchers("/api/traveller/tour/payments/esewa/success").permitAll()
                        .requestMatchers("/api/traveller/tour/payments/esewa/failure").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/oauth2/**").permitAll()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/traveller/**").authenticated()
                        .requestMatchers("/api/payment/**").authenticated()
                        .requestMatchers("/api/support/**").authenticated()
                        // Only authenticated users (any role) can register as guide
                        .requestMatchers("/api/user/**").authenticated()

                        // Only guides or admins can access other guide endpoints
                        .requestMatchers("/api/guides/**").hasAnyRole("GUIDE", "ADMIN")

                        // All other endpoints require authentication
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            // Return JSON 401 instead of redirect
                            response.setContentType("application/json");
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.getWriter().write("{\"error\":\"Unauthorized\"}");
                        })
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .oauth2Login(oauth2 -> oauth2
                        .successHandler(customOAuth2SuccessHandler)
                )
                .httpBasic(httpBasic -> httpBasic.disable())
                .formLogin(form -> form.disable());

        return http.build();
    }
}
