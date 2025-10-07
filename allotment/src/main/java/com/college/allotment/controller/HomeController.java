package com.college.allotment.controller;

import com.college.allotment.model.User;
import com.college.allotment.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final UserRepository userRepo;

    public HomeController(UserRepository userRepo) {
        this.userRepo = userRepo;
    }

    @GetMapping("/")
    public String index(Model model) {
        // TODO: replace 1L with actual logged-in user ID
        User user = userRepo.findById(1L).orElse(null);
        model.addAttribute("user", user);
        return "index";
    }
}
