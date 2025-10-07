package com.college.allotment.service;

import com.college.allotment.model.*;
import com.college.allotment.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AllotmentService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private KsrtcFormRepository ksrtcFormRepository;

    @Autowired
    private NriFormRepository nriFormRepository;

    @Autowired
    private ResultRepository resultRepository;

    @Autowired
    private EmailService emailService;

    // ---------------- USER ----------------

    public User registerUser(User user) {
        return userRepository.save(user);
    }

    public Optional<User> findByEmailAndPassword(String email, String password) {
        return userRepository.findByEmailAndPassword(email, password);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    // ---------------- KSRTC FORM ----------------

    public KsrtcForm submitKsrtcForm(KsrtcForm form) {
        return ksrtcFormRepository.save(form);
    }

    public Optional<KsrtcForm> getKsrtcFormByUserId(Long userId) {
        return ksrtcFormRepository.findByUserId(userId);
    }

    public List<KsrtcForm> getAllKsrtcForms() {
        return ksrtcFormRepository.findAll(); // ✅ use instance, not class
    }

    // ---------------- NRI FORM ----------------

    public NriForm submitNriForm(NriForm form) {
        return nriFormRepository.save(form);
    }

    public Optional<NriForm> getNriFormByUserId(Long userId) {
        return nriFormRepository.findByUserId(userId);
    }

    public List<NriForm> getAllNriForms() {
        return nriFormRepository.findAll();
    }

    // ---------------- RESULT ----------------

    public Result publishResult(Result result) {
        Result savedResult = resultRepository.save(result);

        if (savedResult.isPublished()) {
            String subject = "Allotment Result Published";
            String body = "Dear " + savedResult.getUser().getName() + ",\n\n"
                    + "Your allotment result has been published.\n"
                    + "Quota: " + savedResult.getQuota() + "\n"
                    + "Allocated Seat: " + savedResult.getAllocatedSeat() + "\n\n"
                    + "Thank you,\nCollege Allotment Team";

            emailService.sendEmail(savedResult.getUser().getEmail(), subject, body);
        }

        return savedResult;
    }

    public Optional<Result> getResultByUserId(Long userId) {
        return resultRepository.findByUserId(userId);
    }

    public List<Result> getAllResults() {
        return resultRepository.findAll();
    }

    // ---------------- PASSWORD RESET ----------------

    public void resetPassword(String email, String newPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        userOpt.ifPresent(user -> {
            user.setPassword(newPassword);
            userRepository.save(user);

            String subject = "Password Reset Successful";
            String body = "Dear " + user.getName() + ",\n\n"
                    + "Your password has been successfully reset.\n"
                    + "New Password: " + newPassword + "\n\n"
                    + "If you did not request this change, please contact support immediately.\n\n"
                    + "Regards,\nCollege Allotment System";

            emailService.sendEmail(email, subject, body);
        });
    }

    // ---------------- UTILITIES ----------------

    public void deleteUser(Long userId) {
        ksrtcFormRepository.findByUserId(userId).ifPresent(ksrtcFormRepository::delete);
        nriFormRepository.findByUserId(userId).ifPresent(nriFormRepository::delete);
        resultRepository.findByUserId(userId).ifPresent(resultRepository::delete);
        userRepository.deleteById(userId);
    }
}
