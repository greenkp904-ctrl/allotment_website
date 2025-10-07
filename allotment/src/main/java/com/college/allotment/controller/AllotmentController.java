package com.college.allotment.controller;

import com.college.allotment.model.*;
import com.college.allotment.repository.UserRepository;
import com.college.allotment.service.AllotmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@Controller
public class AllotmentController {

    private final AllotmentService allotmentService;
    private final UserRepository userRepo;

    public AllotmentController(AllotmentService service, UserRepository userRepo) {
        this.allotmentService = service;
        this.userRepo = userRepo;
    }

    // Step 1: Show the initial quota selection
    @GetMapping("/allotment")
    public String showAllotmentPage(@RequestParam Long userId, Model model) {
        User user = userRepo.findById(userId).orElse(null);
        model.addAttribute("user", user);
        return "allotment-form";
    }

    // Step 2: Handle the quota selection and re-render form
    @PostMapping("/allotment")
    public String showQuotaForm(@RequestParam Long userId,
                                @RequestParam(required = false) String ksrtc,
                                @RequestParam(required = false) String nri,
                                Model model) {
        User user = userRepo.findById(userId).orElse(null);
        model.addAttribute("user", user);
        model.addAttribute("ksrtcSelected", ksrtc != null);
        model.addAttribute("nriSelected", nri != null);
        return "allotment-form";
    }

    // Step 3: Handle the final submission
    @PostMapping("/allotment-form")
    public String submitAllotmentForm(@RequestParam Long userId,
                                      @RequestParam(required = false) String ksrtcId,
                                      @RequestParam(required = false) String busRoute,
                                      @RequestParam(required = false) String passportNumber,
                                      @RequestParam(required = false) String country) {

        User user = userRepo.findById(userId).orElse(null);

        if (ksrtcId != null && !ksrtcId.isBlank()) {
            KsrtcForm form = new KsrtcForm();
            form.setKsrtcId(ksrtcId);
            form.setBusRoute(busRoute);
            form.setUser(user);
            allotmentService.submitKsrtcForm(form);
        }

        if (passportNumber != null && !passportNumber.isBlank()) {
            NriForm form = new NriForm();
            form.setPassportNumber(passportNumber);
            form.setCountry(country);
            form.setUser(user);
            allotmentService.submitNriForm(form);
        }

        return "redirect:/allotment?userId=" + userId;
    }
}
