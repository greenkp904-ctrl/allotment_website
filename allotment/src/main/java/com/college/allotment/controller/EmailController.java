package com.college.allotment.controller;

import com.college.allotment.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/email")
public class EmailController {

    @Autowired
    private EmailService emailService;

    @GetMapping("/send")
    public String sendEmail() {
        // Must match EmailService method name and parameters
        emailService.sendEmail(
                "receiver@example.com",
                "Test Mail",
                "This is a test email from Spring Boot!"
        );
        return "Email sent successfully!";
    }
}
