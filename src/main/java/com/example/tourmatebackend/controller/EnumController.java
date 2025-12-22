package com.example.tourmatebackend.controller;


import com.example.tourmatebackend.states.Category;
import com.example.tourmatebackend.states.GuideExperience;
import com.example.tourmatebackend.states.Language;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api/user/enums")

public class EnumController {

    @GetMapping("/languages")
    public List<String> getLanguages() {

        return Arrays.stream(Language.values())
                .map(Enum::name) // Send as uppercase
                .toList();
    }

    @GetMapping("/categories")
    public List<String> getCategories() {
        return Arrays.stream(Category.values())
                .map(Enum::name)
                .toList();
    }

    @GetMapping("/experience")
    public List<String> getExperience() {
        return Arrays.stream(GuideExperience.values())
                .map(Enum::name)
                .toList();
    }
}
