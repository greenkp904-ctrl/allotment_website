package com.college.allotment.controller;

import com.college.allotment.model.User;
import com.college.allotment.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserRepository userRepo;

    public AuthController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/login")
    public String loginPage() { return "login"; }

    @GetMapping("/register")
    public String registerPage() { return "register"; }

    @PostMapping("/register")
    public String registerSubmit(@RequestParam String name, @RequestParam String email,
                                 @RequestParam String password, Model model) {
        if(userRepo.findByEmail(email).isPresent()) {
            model.addAttribute("error","Email already registered");
            return "register";
        }
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        userRepo.save(user);
        return "redirect:/login";
    }

    @PostMapping("/login")
    public String loginSubmit(@RequestParam String email, @RequestParam String password, Model model) {
        User user = userRepo.findByEmail(email).orElse(null);
        if(user == null || !user.getPassword().equals(password)) {
            model.addAttribute("error","Invalid credentials");
            return "login";
        }
        model.addAttribute("user",user);
        return "redirect:/allotment?userId="+user.getId();
    }
}
