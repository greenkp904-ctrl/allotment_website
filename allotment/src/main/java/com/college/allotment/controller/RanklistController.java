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

    @GetMapping("/ranklist/ksrtc")
    public String viewKsrtcRanklist(Model model) {
        model.addAttribute("ranklist", allotmentService.generateKsrtcRanklist());
        model.addAttribute("type", "KSRTC");
        return "ranklist";
    }

    @GetMapping("/ranklist/nri")
    public String viewNriRanklist(Model model) {
        model.addAttribute("ranklist", allotmentService.generateNriRanklist());
        model.addAttribute("type", "NRI");
        return "ranklist";
    }
}
