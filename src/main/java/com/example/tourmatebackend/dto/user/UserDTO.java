package com.example.tourmatebackend.dto.user;

import com.example.tourmatebackend.model.Guide;
import com.example.tourmatebackend.model.Tour;
import com.example.tourmatebackend.states.Role;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

public class UserDTO {
    private int id;
    private String email;
    private String firstName;
    private String lastName;

    private Role role = Role.TRAVELLER; // Default TRAVELLER

    // Getters & Setters
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Role getRole() {
        return role;
    }
    public void setRole(Role role) {
        this.role = role;
    }

}
