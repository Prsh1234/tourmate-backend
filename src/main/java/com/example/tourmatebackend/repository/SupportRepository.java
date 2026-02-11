package com.example.tourmatebackend.repository;

import com.example.tourmatebackend.model.Support;
import com.example.tourmatebackend.states.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupportRepository extends JpaRepository<Support, Long> {
    // Fetch support messages by user role
    List<Support> findByRole(Role role);
}
