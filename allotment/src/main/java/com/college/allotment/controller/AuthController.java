package com.college.allotment.controller;

import com.college.allotment.model.User;
import com.college.allotment.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {

    private final UserRepository userRepo;

    public AuthController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    // ---------- LOGIN PAGE ----------
    @GetMapping("/login")
    public String showLoginPage(@RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "email", required = false) String email,
                                Model model,
                                @ModelAttribute("successMessage") String successMessage) {
        if (error != null) {
            model.addAttribute("errorMessage", "⚠️ Invalid email or password. Please try again.");
        }
        if (email != null) {
            model.addAttribute("enteredEmail", email);
        }

        if (successMessage != null && !successMessage.isEmpty()) {
            model.addAttribute("successMessage", successMessage);
        }
        return "login";
    }

    // ---------- HANDLE LOGIN ----------
    @PostMapping("/login")
    public String loginSubmit(@RequestParam String email,
                              @RequestParam String password,
                              Model model,
                              HttpSession session) {

        Optional<User> userOptional = userRepo.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // For now, simple plain-text check (replace with hashed password in production)
            if (user.getPassword().equals(password)) {
                session.setAttribute("user", user);
                return "redirect:/dashboard";
            }
        }

        // If login fails
        model.addAttribute("errorMessage", "Invalid email or password!");
        model.addAttribute("enteredEmail", email);
        return "login";
    }

    // ---------- REGISTRATION PAGE ----------
    @GetMapping("/register")
    public String showRegisterPage() {
        return "register";
    }

    // ---------- HANDLE REGISTRATION ----------
    @PostMapping("/register")
    public String registerSubmit(@RequestParam String name,
                                 @RequestParam String email,
                                 @RequestParam String password,
                                 Model model) {
        if (userRepo.findByEmail(email).isPresent()) {
            model.addAttribute("errorMessage", "Email already registered");
            return "register";
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);

        // Generate applicationNumber before saving
        String applicationNumber = "APP-" + System.currentTimeMillis();
        user.setApplicationNumber(applicationNumber);

        // NOTE: If you are using AllotmentService for registration, replace userRepo.save(user)
        userRepo.save(user);

        return "redirect:/login";
    }

    // ---------- LOGOUT ----------
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    // ---------- FORGOT PASSWORD PAGE ----------
    @GetMapping("/forgot-password")
    public String showForgotPasswordPage() {
        return "forgot-password";
    }

    // ---------- HANDLE FORGOT PASSWORD ----------
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, Model model) {
        Optional<User> userOptional = userRepo.findByEmail(email);

        if (userOptional.isEmpty()) {
            model.addAttribute("errorMessage", "No account found with that email.");
            return "forgot-password";
        }

        model.addAttribute("email", email);
        return "reset-password";
    }

    // ---------- HANDLE RESET PASSWORD ----------
    @PostMapping("/reset-password")
    public String resetPassword(@RequestParam String email,
                                @RequestParam("password") String password,
                                Model model,
                                RedirectAttributes redirectAttributes) {

        Optional<User> userOptional = userRepo.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            // 💡 CRITICAL: Ensure you hash the password in a production environment!
            user.setPassword(password);
            userRepo.save(user);

            redirectAttributes.addFlashAttribute("successMessage", "Password updated successfully. You can now log in.");
            return "redirect:/login";
        }

        model.addAttribute("errorMessage", "Error updating password. Try again.");
        return "reset-password";
    }
}