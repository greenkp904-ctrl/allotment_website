package com.college.allotment.controller;

import com.college.allotment.model.*;
import com.college.allotment.service.AllotmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AllotmentService service;

    // Admin dashboard
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        List<User> users = service.getAllUsers();
        List<KsrtcForm> ksrtcForms = service.getAllKsrtcForms();
        List<NriForm> nriForms = service.getAllNriForms();
        List<Result> results = service.getAllResults();

        model.addAttribute("users", users);
        model.addAttribute("ksrtcForms", ksrtcForms);
        model.addAttribute("nriForms", nriForms);
        model.addAttribute("results", results);

        return "admin-dashboard";
    }

    // Publish allotment result for a user
    @PostMapping("/publish-result")
    public String publishResult(
            @RequestParam String email,
            @RequestParam String quota,
            @RequestParam String allocatedSeat
    ) {
        Optional<User> userOpt = service.findUserByEmail(email);
        if (userOpt.isPresent()) {
            User user = userOpt.get();

            Result result = new Result();
            result.setUser(user);
            result.setQuota(quota);
            result.setAllocatedSeat(allocatedSeat);
            result.setPublished(true);

            service.publishResult(result);
        }
        return "redirect:/admin/dashboard";
    }

    // Reset password for a user
    @PostMapping("/reset-password")
    public String resetPassword(
            @RequestParam String email,
            @RequestParam String newPassword
    ) {
        service.resetPassword(email, newPassword);
        return "redirect:/admin/dashboard";
    }
}
