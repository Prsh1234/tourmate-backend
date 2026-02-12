package com.example.tourmatebackend.config;

import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.repository.UserRepository;
import com.example.tourmatebackend.states.Role;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class AdminInitializer {

    @Bean
    CommandLineRunner createAdmin(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByEmail("admin@tourmate.com").isEmpty()) {

                BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

                User admin = new User();
                admin.setFirstName("System");
                admin.setLastName("Admin");
                admin.setEmail("admin@tourmate.com");
                admin.setPhoneNumber("9800000000");
                admin.setPassword(encoder.encode("Admin@123"));
                admin.setEnabled(true);
                admin.setRole(Role.ADMIN);

                userRepository.save(admin);

                System.out.println("✅ Default Admin Created");
            }
        };
    }
}
