package com.example.tourmatebackend.dto.support;


import com.example.tourmatebackend.dto.user.UserDTO;
import com.example.tourmatebackend.model.User;
import com.example.tourmatebackend.states.Role;

import java.time.LocalDateTime;

public class SupportDTO {
    private String subject;
    private String message;
    private Role role;
    private LocalDateTime createdAt;
    private Long id;
    private boolean view;
    private UserDTO user;

    public SupportDTO(String subject, String message, Role role, LocalDateTime createdAt) {
        this.subject = subject;
        this.message = message;
        this.role = role;
        this.createdAt = createdAt;
    }

    public SupportDTO(Long id, String subject, String message, Role role, LocalDateTime created_at, boolean view, User user) {
        this.subject = subject;
        this.message = message;
        this.role = role;
        this.createdAt = created_at;
        this.id =id;
        this.view =view;
        this.user = new UserDTO(user);
    }

    // Getters & setters
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public Role getRole() { return role; }
    public void setRole(Role role) { this.role = role; }


    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isView() {
        return view;
    }

    public void setView(boolean view) {
        this.view = view;
    }

    public UserDTO getUser() {
        return user;
    }

    public void setUser(UserDTO user) {
        this.user = user;
    }
}

