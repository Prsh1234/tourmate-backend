package com.example.tourmatebackend.repository;

import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.states.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    Optional<User> findByEmail(String email);
    Optional<User> findByPhoneNumber(String phone);
    Optional<User> findByVerificationToken(String token);
    Optional<User> findByResetToken(String token);


    //admin
    long countByRole(Role role);
    long countByRoleAndJoinedAfter(Role role, LocalDate date);
}
