package com.example.tourmatebackend.utils;

public class OAuth2Helper {

    public static String extractFirstName(String fullName) {
        if (fullName == null) return null;
        return fullName.split(" ")[0];
    }

    public static String extractLastName(String fullName) {
        if (fullName == null) return null;
        String[] parts = fullName.split(" ");
        return parts.length > 1 ? parts[parts.length - 1] : "";
    }
}
