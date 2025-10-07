package com.college.allotment.service;

import com.college.allotment.model.KsrtcForm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.context.annotation.Lazy;


@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    // ✅ Inject AllotmentService here
    @Autowired
    @Lazy
    private AllotmentService allotmentService;

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

    // Example: sending an email and saving KSRTC form
    public void sendAndSaveKsrtcForm(KsrtcForm ksrtcForm) {
        // ✅ Correct: use instance, not class name
        allotmentService.submitKsrtcForm(ksrtcForm);

        sendEmail(
                "example@gmail.com",
                "KSRTC Form Submitted",
                "Your KSRTC form has been successfully submitted!"
        );
    }
}
