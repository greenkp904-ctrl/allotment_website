package com.college.allotment.controller;

import com.college.allotment.model.User;
import com.college.allotment.repository.UserRepository;
import jakarta.servlet.http.HttpSession; // 🚩 New Required Import
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
public class DashboardController {

    private final UserRepository userRepo;

    public DashboardController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    /**
     * Helper method to authenticate user from session and fetch fresh data.
     */
    private Optional<User> authenticateUser(HttpSession session) {
        // 1. Check for the User object in the session
        User sessionUser = (User) session.getAttribute("user");

        if (sessionUser == null) {
            return Optional.empty();
        }

        // 2. Fetch fresh user data from DB using ID from session
        return userRepo.findById(sessionUser.getId());
    }

    // -------------------- Dashboard (FIXED) --------------------
    @GetMapping("/dashboard")
    // 🚩 FIX: Use HttpSession instead of @RequestParam Long userId
    public String showDashboard(Model model, HttpSession session) {
        Optional<User> optionalUser = authenticateUser(session);

        if (optionalUser.isEmpty()) {
            return "redirect:/login"; // Redirect if not logged in
        }

        model.addAttribute("user", optionalUser.get());
        // You would typically load dashboard-specific data here

        return "dashboard";
    }

    // -------------------- Apply Button → Redirect to Allotment Form (FIXED) --------------------
    @PostMapping("/apply")
    // 🚩 FIX: Use HttpSession for authorization instead of userId parameter
    public String applyForAllotment(HttpSession session) {
        // Simple check to ensure a user is logged in before redirecting
        if (session.getAttribute("user") == null) {
            return "redirect:/login";
        }

        // Use the correct path mapping for the AllotmentController
        return "redirect:/allotment";
    }

    // -------------------- Allotment Form Page (FIXED) --------------------
    @GetMapping("/allotment-form")
    // 🚩 FIX: Use HttpSession instead of @RequestParam Long userId
    public String showAllotmentForm(Model model, HttpSession session) {
        Optional<User> optionalUser = authenticateUser(session);

        if (optionalUser.isEmpty()) {
            return "redirect:/login"; // Redirect if not logged in
        }

        model.addAttribute("user", optionalUser.get());
        // NOTE: If you are using a separate AllotmentController for /allotment,
        // this method is redundant and can be removed.
        return "allotment-form";
    }

    // -------------------- Result Page (FIXED) --------------------
    @GetMapping("/result")
    // 🚩 FIX: Use HttpSession instead of @RequestParam Long userId
    public String viewResult(Model model, HttpSession session) {
        Optional<User> optionalUser = authenticateUser(session);

        if (optionalUser.isEmpty()) {
            return "redirect:/login"; // Redirect if not logged in
        }

        model.addAttribute("user", optionalUser.get());
        // Typically, you would load the result data using the user ID here

        return "result";
    }
}