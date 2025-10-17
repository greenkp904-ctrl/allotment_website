package com.college.allotment.controller;

import com.college.allotment.model.*;
import com.college.allotment.model.Role; // 🔑 Added import for Role enum
import com.college.allotment.service.AllotmentService;
import com.college.allotment.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.UUID; // 🔑 Added import for generating unique ID

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AllotmentService service;

    @Autowired
    private UserRepository userRepository;

    /**
     * Helper method to enforce Admin access.
     */
    private boolean isAdminAuthenticated(HttpSession session) {
        // Check if the 'adminUser' session attribute (set during admin login) is present.
        return session.getAttribute("adminUser") != null;
    }


    // --- 0. ADMIN LOGIN/LOGOUT ---

    @GetMapping("/logout")
    public String adminLogout(HttpSession session) {
        session.removeAttribute("adminUser");
        return "redirect:/admin/login";
    }

    // --- 1. ADMIN DASHBOARD & STATS ---

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        if (!isAdminAuthenticated(session)) {
            return "redirect:/admin/login";
        }

        // 🔑 UPDATED: Fetch filtered user lists instead of getAllUsers()
        model.addAttribute("candidateUsers", service.getAllCandidateUsers());
        model.addAttribute("adminUsers", service.getAllAdminUsers());

        // Fetching other full lists
        model.addAttribute("ksrtcForms", service.getAllKsrtcForms());
        model.addAttribute("nriForms", service.getAllNriForms());
        model.addAttribute("results", service.getAllResults());

        return "admin-dashboard";
    }

    // --- 2. ALLOTMENT CONTROL (BULK) ---

    @PostMapping("/run-allotment-process")
    public String runAllotmentProcess(HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdminAuthenticated(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Session expired or access denied.");
            return "redirect:/admin/login";
        }

        service.runAllotmentProcess();
        redirectAttributes.addFlashAttribute("successMessage", "✅ Allocation process completed successfully! Results are generated and awaiting publication.");
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/publish-all-results")
    public String publishAllResults(HttpSession session, RedirectAttributes redirectAttributes) {
        if (!isAdminAuthenticated(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Session expired or access denied.");
            return "redirect:/admin/login";
        }

        service.publishAllotmentResults();
        redirectAttributes.addFlashAttribute("successMessage", "📢 All allotment results have been successfully published and emails sent!");
        return "redirect:/admin/dashboard";
    }

    // --- 3. UTILITIES ---

    @PostMapping("/reset-password")
    public String resetPassword(
            HttpSession session,
            @RequestParam String email,
            @RequestParam String newPassword,
            RedirectAttributes redirectAttributes
    ) {
        if (!isAdminAuthenticated(session)) {
            redirectAttributes.addFlashAttribute("errorMessage", "Session expired or access denied.");
            return "redirect:/admin/login";
        }

        try {
            service.resetPassword(email, newPassword);
            redirectAttributes.addFlashAttribute("successMessage", "🔑 Password for " + email + " reset successfully.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    // --- 4. INITIAL ADMIN SETUP (Highly Sensitive) ---

    @GetMapping("/register")
    public String showAdminRegisterPage() {
        // Serves the admin-register.html page.
        // NOTE: Restrict access to this page after initial setup for production security.
        return "admin-register";
    }

    @PostMapping("/register")
    public String registerAdmin(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password,
            RedirectAttributes redirectAttributes
    ) {
        // Simple check to prevent general public from abusing this
        if (userRepository.findByEmail(email).isPresent()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Email already registered.");
            return "redirect:/admin/register";
        }

        try {
            User adminUser = new User();
            adminUser.setName(name);
            adminUser.setEmail(email);
            // WARNING: Use BCryptPasswordEncoder in a production application!
            adminUser.setPassword(password);

            // 🔑 CRITICAL: Set the role to ADMIN
            adminUser.setRole(Role.ROLE_ADMIN);

            // Set mandatory fields to defaults to satisfy User entity constraints
            adminUser.setApplicationNumber("ADMIN-" + UUID.randomUUID().toString().substring(0, 8));
            adminUser.setFormSubmitted(true); // Treat admin account as complete

            userRepository.save(adminUser);

            redirectAttributes.addFlashAttribute("successMessage", "✅ Initial Admin account created! Please log in.");
            return "redirect:/admin/login";

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error during registration: " + e.getMessage());
            return "redirect:/admin/register";
        }
    }
}