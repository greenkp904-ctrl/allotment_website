package com.college.allotment.controller;

import com.college.allotment.model.User;
import com.college.allotment.model.Role; // Make sure your Role enum is accessible here
import com.college.allotment.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AdminLoginController {

    @Autowired
    private UserRepository userRepository;

    /**
     * Handles the GET request to display the Admin Login form.
     * Maps to: http://localhost:8080/admin/login
     */
    @GetMapping("/admin/login")
    public String showAdminLoginPage(@ModelAttribute("errorMessage") String errorMessage,
                                     @ModelAttribute("successMessage") String successMessage,
                                     Model model) {

        // Pass flash attributes (error/success messages) to the model
        if (!errorMessage.isEmpty()) { model.addAttribute("errorMessage", errorMessage); }
        if (!successMessage.isEmpty()) { model.addAttribute("successMessage", successMessage); }

        // Returns the Thymeleaf template src/main/resources/templates/admin-login.html
        return "admin-login";
    }

    /**
     * Handles the POST request to authenticate the administrator.
     */
    @PostMapping("/admin/login")
    public String authenticateAdmin(@RequestParam String email,
                                    @RequestParam String password,
                                    HttpSession session,
                                    RedirectAttributes redirectAttributes) {

        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid Credentials.");
            return "redirect:/admin/login";
        }

        User user = optionalUser.get();

        // 1. Check Password AND Role
        // NOTE: Use a secure password encoder (like BCrypt) in production!
        if (user.getPassword().equals(password) && user.getRole() == Role.ROLE_ADMIN) {

            // 2. Set Admin Session and redirect
            session.setAttribute("adminUser", user);

            // Clear any existing candidate session just in case
            session.removeAttribute("user");

            return "redirect:/admin/dashboard"; // Redirect to the secured admin dashboard

        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid Credentials or insufficient privileges.");
            return "redirect:/admin/login";
        }
    }
}