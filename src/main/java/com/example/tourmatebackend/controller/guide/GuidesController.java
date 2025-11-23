package com.example.tourmatebackend.controller.guide;

import com.example.tourmatebackend.repository.GuideRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user/guides")
public class GuidesController {

    @Autowired
    private GuideRepository guideRepository;


}
