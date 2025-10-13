package com.college.allotment.controller;

import com.college.allotment.model.User;
import com.college.allotment.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class DashboardController {

    private final UserRepository userRepo;

    public DashboardController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // -------------------- Dashboard --------------------
    @GetMapping("/dashboard")
    public String showDashboard(@RequestParam Long userId, Model model) {
        User user = userRepo.findById(userId).orElse(null);
        model.addAttribute("user", user);
        return "dashboard";
    }

    // -------------------- Apply Button → Redirect to Allotment Form --------------------
    @PostMapping("/apply")
    public String applyForAllotment(@RequestParam Long userId) {
        // ✅ Directly redirect to the allotment form page
        return "redirect:/allotment-form?userId=" + userId;
    }

    // -------------------- Allotment Form Page --------------------
    @GetMapping("/allotment-form")
    public String showAllotmentForm(@RequestParam Long userId, Model model) {
        User user = userRepo.findById(userId).orElse(null);
        model.addAttribute("user", user);
        return "allotment-form";  // ✅ Make sure allotment-form.html exists inside templates/
    }

    // -------------------- Result Page --------------------
    @GetMapping("/result")
    public String viewResult(@RequestParam Long userId, Model model) {
        User user = userRepo.findById(userId).orElse(null);
        model.addAttribute("user", user);
        return "result";
    }
}
