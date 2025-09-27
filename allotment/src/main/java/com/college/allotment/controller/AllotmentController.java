package com.college.allotment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AllotmentController {

    // ----------------- INDEX -----------------
    @GetMapping({"/", "/index"})
    public String index() {
        return "index";
    }

    // ----------------- LOGIN -----------------
    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login")
    public String submitLogin(@RequestParam String email,
                              @RequestParam String password,
                              Model model) {
        // TODO: Validate login (dummy check)
        if (email.equals("test@example.com") && password.equals("1234")) {
            return "redirect:/allotment-form";
        } else {
            model.addAttribute("error", "Invalid email or password");
            return "login";
        }
    }

    // ----------------- REGISTER -----------------
    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String submitRegister(@RequestParam String name,
                                 @RequestParam String email,
                                 @RequestParam String password,
                                 Model model) {
        // TODO: Save user details
        // For now, just redirect to login
        return "redirect:/login";
    }

    // ----------------- FORGOT PASSWORD -----------------
    @GetMapping("/forgot-password")
    public String forgotPassword() {
        return "forgot-password";
    }

    @PostMapping("/forgot-password")
    public String submitForgotPassword(@RequestParam String email, Model model) {
        // TODO: Implement email recovery logic
        model.addAttribute("email", email);
        return "forgot-password-result";
    }

    // ----------------- ALLOTMENT SELECTION -----------------
    @GetMapping("/allotment-form")
    public String allotmentForm() {
        return "allotment-form"; // Page with KSRTC/NRI radio buttons
    }

    @PostMapping("/allotment-form")
    public String submitAllotment(@RequestParam String quota, Model model) {
        if (quota.equalsIgnoreCase("ksrtc")) {
            return "ksrtc-form";
        } else if (quota.equalsIgnoreCase("nri")) {
            return "nri-form";
        } else {
            model.addAttribute("error", "Please select a quota");
            return "allotment-form";
        }
    }

    // ----------------- KSRTC FORM -----------------
    @GetMapping("/ksrtc-form")
    public String ksrtcForm() {
        return "ksrtc-form";
    }

    @PostMapping("/ksrtc-form")
    public String submitKsrtcForm(@RequestParam String ksrtcId,
                                  @RequestParam String busRoute,
                                  Model model) {
        // TODO: Save KSRTC form data
        model.addAttribute("ksrtcId", ksrtcId);
        model.addAttribute("busRoute", busRoute);
        return "ksrtc-result";
    }

    // ----------------- NRI FORM -----------------
    @GetMapping("/nri-form")
    public String nriForm() {
        return "nri-form";
    }

    @PostMapping("/nri-form")
    public String submitNriForm(@RequestParam String passportNumber,
                                @RequestParam String country,
                                Model model) {
        // TODO: Save NRI form data
        model.addAttribute("passportNumber", passportNumber);
        model.addAttribute("country", country);
        return "nri-result";
    }

    // ----------------- ALLOTMENT RESULT PAGE -----------------
    @GetMapping("/allotment-result")
    public String allotmentResult() {
        return "allotment-result";
    }
}
