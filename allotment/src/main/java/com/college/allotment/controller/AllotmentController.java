package com.college.allotment.controller;

import com.college.allotment.model.*;
import com.college.allotment.repository.AllotmentRepository.*;
import com.college.allotment.repository.UserRepository;
import com.college.allotment.service.AllotmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AllotmentController {

    private final AllotmentService allotmentService;
    private final UserRepository userRepo;

    public AllotmentController(AllotmentService service, UserRepository userRepo) {
        this.allotmentService = service;
        this.userRepo = userRepo;
    }

    @GetMapping("/allotment")
    public String allotmentPage(@RequestParam Long userId, Model model) {
        User user = userRepo.findById(userId).orElse(null);
        model.addAttribute("user", user);
        return "allotment";
    }

    @PostMapping("/allotment/ksrtc")
    public String submitKsrtc(@RequestParam Long userId,
                              @RequestParam String ksrtcId,
                              @RequestParam String busRoute) {
        KsrtcForm form = new KsrtcForm();
        form.setKsrtcId(ksrtcId);
        form.setBusRoute(busRoute);
        form.setUser(userRepo.findById(userId).orElse(null));
        allotmentService.submitKsrtcForm(form);
        return "redirect:/allotment?userId=" + userId;
    }

    @PostMapping("/allotment/nri")
    public String submitNri(@RequestParam Long userId,
                            @RequestParam String passportNumber,
                            @RequestParam String country) {
        NriForm form = new NriForm();
        form.setPassportNumber(passportNumber);
        form.setCountry(country);
        form.setUser(userRepo.findById(userId).orElse(null));
        allotmentService.submitNriForm(form);
        return "redirect:/allotment?userId=" + userId;
    }

    // ✅ Add this new method here inside the same class
    @PostMapping("/allotment-form")
    public String submitAllotmentForm(@RequestParam Long userId,
                                      @RequestParam String quota,
                                      @RequestParam(required = false) String ksrtcId,
                                      @RequestParam(required = false) String busRoute,
                                      @RequestParam(required = false) String passportNumber,
                                      @RequestParam(required = false) String country) {

        if ("ksrtc".equals(quota)) {
            KsrtcForm form = new KsrtcForm();
            form.setKsrtcId(ksrtcId);
            form.setBusRoute(busRoute);
            form.setUser(userRepo.findById(userId).orElse(null));
            allotmentService.submitKsrtcForm(form);
        } else if ("nri".equals(quota)) {
            NriForm form = new NriForm();
            form.setPassportNumber(passportNumber);
            form.setCountry(country);
            form.setUser(userRepo.findById(userId).orElse(null));
            allotmentService.submitNriForm(form);
        }

        return "redirect:/allotment?userId=" + userId;
    }
}
