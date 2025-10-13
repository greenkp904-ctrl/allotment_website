package com.college.allotment.service;

import com.college.allotment.model.*;
import com.college.allotment.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

    @Autowired
    private OptionRegistrationRepository optionRegistrationRepository; // ✅ new repository

    // ---------------- USER ----------------

    public User registerUser(User user) {
        return userRepository.save(user);
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
        return ksrtcFormRepository.findAll();
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
            user.setPassword(newPassword); // TODO: hash password in production
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

    // ---------------- OPTION REGISTRATION ----------------
    public void saveSelectedOptions(User user, List<String> options) {
        if (options == null || options.isEmpty()) {
            throw new IllegalArgumentException("No options selected");
        }
        if (options.size() > 6) {
            throw new IllegalArgumentException("A maximum of 6 options can be selected");
        }

        // Remove duplicate branches if any
        List<String> uniqueOptions = options.stream().distinct().collect(Collectors.toList());

        OptionRegistration optionRegistration = new OptionRegistration();
        optionRegistration.setUser(user);
        optionRegistration.setOptions(uniqueOptions);
        optionRegistrationRepository.save(optionRegistration);
    }

    public List<OptionRegistration> getAllOptionRegistrations() {
        return optionRegistrationRepository.findAll();
    }

    public Optional<OptionRegistration> getOptionRegistrationByUser(Long userId) {
        return optionRegistrationRepository.findByUserId(userId);
    }

    // ---------------- UTILITIES ----------------

    public void deleteUser(Long userId) {
        ksrtcFormRepository.findByUserId(userId).ifPresent(ksrtcFormRepository::delete);
        nriFormRepository.findByUserId(userId).ifPresent(nriFormRepository::delete);
        resultRepository.findByUserId(userId).ifPresent(resultRepository::delete);
        optionRegistrationRepository.findByUserId(userId).ifPresent(optionRegistrationRepository::delete);
        userRepository.deleteById(userId);
    }

    // ---------------- ALLOTMENT ALGORITHMS ----------------

    /** 🚌 Generate KSRTC Ranklist */
    public List<KsrtcForm> generateKsrtcRanklist() {
        return ksrtcFormRepository.findAll()
                .stream()
                .sorted(Comparator.comparingInt(KsrtcForm::getKeamRank))
                .collect(Collectors.toList());
    }

    /** 🌍 Generate NRI Ranklist */
    public List<NriForm> generateNriRanklist() {
        return nriFormRepository.findAll()
                .stream()
                .peek(form -> {
                    int physics = form.getPhysicsMarks();
                    int chemistry = form.getChemistryMarks();
                    int maths = form.getMathMarks();

                    if (physics > 200 || chemistry > 200 || maths > 200) {
                        throw new IllegalArgumentException("Each subject mark must be ≤ 200");
                    }

                    double percentage = (physics + chemistry + maths) / 600.0 * 100;
                    form.setPercentage(percentage);
                })
                .sorted(Comparator.comparingDouble(NriForm::getPercentage).reversed())
                .collect(Collectors.toList());
    }
}
