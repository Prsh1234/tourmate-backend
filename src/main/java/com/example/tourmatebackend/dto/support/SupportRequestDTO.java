package com.example.tourmatebackend.dto.support;


import com.example.tourmatebackend.states.Role;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SupportRequestDTO {
    private String subject;
    private String message;


    private String role;

    // Getters & setters
    public String getSubject() { return subject; }
    public void setSubject(String subject) { this.subject = subject; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}
