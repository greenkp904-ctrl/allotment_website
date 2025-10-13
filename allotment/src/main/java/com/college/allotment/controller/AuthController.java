package com.college.allotment.controller;

import com.college.allotment.model.User;
import com.college.allotment.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

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
                                Model model) {
        if (error != null) {
            model.addAttribute("errorMessage", "⚠️ Invalid email or password. Please try again.");
        }
        if (email != null) {
            model.addAttribute("enteredEmail", email);
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
                return "redirect:/dashboard?userId=" + user.getId();
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
        userRepo.save(user);

        return "redirect:/login";
    }
}
