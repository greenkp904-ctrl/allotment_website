package com.college.allotment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    /**
     * Maps the root URL (http://localhost:8080/) to the index.html template.
     * This method is public and does not require authentication.
     */
    @GetMapping("/")
    public String index(Model model) {

        // CRITICAL: We explicitly set 'user' to null. This prevents the SpelEvaluationException
        // in your index.html's Navbar when checking for user.id on a public page.
        model.addAttribute("user", null);

        // Returns the template located at src/main/resources/templates/index.html
        return "index";
    }
}