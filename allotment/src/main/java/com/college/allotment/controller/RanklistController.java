package com.college.allotment.controller;

import com.college.allotment.service.AllotmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RanklistController {

    private final AllotmentService allotmentService;

    public RanklistController(AllotmentService allotmentService) {
        this.allotmentService = allotmentService;
    }

    // --- KSRTC Ranklist View ---
    @GetMapping("/ranklist/ksrtc")
    public String viewKsrtcRanklist(Model model) {
        // CORRECTED: Calling the method that generates, saves, and returns the sorted list.
        model.addAttribute("ranklist", allotmentService.generateAndSaveKsrtcRanklist());
        model.addAttribute("type", "KSRTC");
        return "ranklist"; // Renders the Thymeleaf template named 'ranklist.html'
    }

    // --- NRI Ranklist View ---
    @GetMapping("/ranklist/nri")
    public String viewNriRanklist(Model model) {
        // CORRECTED: Calling the method that generates, saves, and returns the sorted list.
        model.addAttribute("ranklist", allotmentService.generateAndSaveNriRanklist());
        model.addAttribute("type", "NRI");
        return "ranklist";
    }
}