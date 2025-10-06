package com.college.allotment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendEmail(String to, String subject, String body) { // 👈 method name & parameters
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("your_email@gmail.com"); // replace with your Gmail
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
        System.out.println("Mail sent successfully!");
    }
}
