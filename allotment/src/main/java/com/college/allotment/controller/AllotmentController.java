package com.college.allotment.controller;

import com.college.allotment.model.*;
import com.college.allotment.repository.UserRepository;
import com.college.allotment.service.AllotmentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class AllotmentController {

    private final AllotmentService allotmentService;
    private final UserRepository userRepo;

    public AllotmentController(AllotmentService service, UserRepository userRepo) {
        this.allotmentService = service;
        this.userRepo = userRepo;
    }

    // -------------------- Show Allotment Form --------------------
    @GetMapping("/allotment")
    public String showAllotmentPage(@RequestParam Long userId,
                                    @RequestParam(value = "error", required = false) String error,
                                    Model model) {
        User user = userRepo.findById(userId).orElse(null);
        model.addAttribute("user", user);

        // List of available branches (full form)
        List<String> branches = Arrays.asList(
                "Computer Science and Engineering",
                "Computer Science (Artificial Intelligence and Machine Learning) Engineering",
                "Electronics and Communication Engineering",
                "Mechanical Engineering",
                "Automobile Engineering",
                "Biotechnology Engineering"
        );
        model.addAttribute("branches", branches);

        if (error != null) {
            model.addAttribute("errorMessage", "⚠️ Please select up to 6 unique options.");
        }

        return "allotment-form";
    }

    // -------------------- Handle Form Submission --------------------
    @PostMapping("/allotment")
    public String submitAllotmentForm(@RequestParam Long userId,
                                      @RequestParam(value = "options", required = false) List<String> options) {
        User user = userRepo.findById(userId).orElse(null);

        if (user == null) {
            return "redirect:/dashboard?error=userNotFound";
        }

        // Validation: must select 1–6 unique options
        if (options == null || options.isEmpty() || options.size() > 6) {
            return "redirect:/allotment?userId=" + userId + "&error=true";
        }

        Set<String> uniqueOptions = new HashSet<>(options);
        if (uniqueOptions.size() < options.size()) {
            return "redirect:/allotment?userId=" + userId + "&error=true";
        }

        // Save the selected options
        allotmentService.saveSelectedOptions(user, new ArrayList<>(uniqueOptions));

        return "redirect:/result?userId=" + userId;
    }
}
