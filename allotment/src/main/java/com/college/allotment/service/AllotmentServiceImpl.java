package com.college.allotment.service;

import com.college.allotment.model.*;
import com.college.allotment.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
// NOTE: Assuming AllotmentService is the interface this class implements.
// If AllotmentService is an interface, this should be 'implements AllotmentService'.
// We'll proceed assuming it IMPlEMENTS the interface to match Spring standard practices.
public class AllotmentServiceImpl extends AllotmentService {

    private final KsrtcFormRepository ksrtcFormRepository;
    private final NriFormRepository nriFormRepository;
    private final OptionRegistrationRepository optionRegistrationRepository;
    private final UserRepository userRepository;
    private final ResultRepository resultRepository;

    public AllotmentServiceImpl(KsrtcFormRepository ksrtcFormRepository,
                                NriFormRepository nriFormRepository,
                                OptionRegistrationRepository optionRegistrationRepository,
                                UserRepository userRepository,
                                ResultRepository resultRepository) {
        this.ksrtcFormRepository = ksrtcFormRepository;
        this.nriFormRepository = nriFormRepository;
        this.optionRegistrationRepository = optionRegistrationRepository;
        this.userRepository = userRepository;
        this.resultRepository = resultRepository;
    }

    // --- User Allotment Form Submission (Called by AllotmentController) ---

    @Override
    public KsrtcForm submitKsrtcForm(KsrtcForm form) {
        // Find existing form to update instead of always creating new one (best practice for @OneToOne)
        Optional<KsrtcForm> existingForm = ksrtcFormRepository.findByUserId(form.getUser().getId());
        if (existingForm.isPresent()) {
            // Logic to update existing form fields goes here
            // For simplicity, we just save the current object which updates it if the ID is set
        }
        return ksrtcFormRepository.save(form);
    }

    @Override
    public NriForm submitNriForm(NriForm form) {
        // Find existing form to update instead of always creating new one
        Optional<NriForm> existingForm = nriFormRepository.findByUserId(form.getUser().getId());
        if (existingForm.isPresent()) {
            // Logic to update existing form fields goes here
        }
        // Save/Update the form
        return nriFormRepository.save(form);
    }

    @Override
    public void saveSelectedOptions(User user, List<String> options) {
        // Find existing registration or create a new one
        Optional<OptionRegistration> existingReg = optionRegistrationRepository.findByUserId(user.getId());
        OptionRegistration registration = existingReg.orElse(new OptionRegistration());

        registration.setUser(user);

        // 🛑 CRITICAL FIX: The options are joined into a single comma-separated STRING.
        // The model setter must be called with a String, NOT a List containing that String.
        // Assumes setSelectedOptions(String) exists in OptionRegistration model.
        registration.setSelectedOptions(Collections.singletonList(String.join(",", options)));

        // Save or update the registration
        optionRegistrationRepository.save(registration);
    }

    // ------------------- NEW REQUIRED METHOD -------------------
    /**
     * Required for user session/login handling in controllers.
     * Assumes findByEmail(String) exists in UserRepository.
     */
    @Override
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    // -----------------------------------------------------------


    // --- Admin Dashboard (Called by AdminController) ---

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public List<KsrtcForm> getAllKsrtcForms() {
        return ksrtcFormRepository.findAll();
    }

    @Override
    public List<NriForm> getAllNriForms() {
        return nriFormRepository.findAll();
    }

    @Override
    public List<Result> getAllResults() {
        return resultRepository.findAll();
    }

    // --- Admin Control Actions (Called by AdminController) ---

    /** 🚩 CRITICAL: Enforces admin control over publishing. */
    @Override
    public void publishAllotmentResults() {
        List<Result> unpublishedResults = resultRepository.findByPublished(false);
        for (Result result : unpublishedResults) {
            result.setPublished(true);
            // 🚨 TODO: Add email sending logic here!
        }
        resultRepository.saveAll(unpublishedResults);
    }

    @Override
    public void runAllotmentProcess() {
        // 🚧 PLACEHOLDER: This is where the heavy allocation algorithm would run.
        // It would generate Result entities and save them with published=false.
    }

    @Override
    public void resetPassword(String email, String newPassword) {
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // 🚨 TODO: Hash the newPassword before saving!
            user.setPassword(newPassword);
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User with email " + email + " not found.");
        }
    }

    // --- User Result Lookup (Needed later for Dashboard) ---

    @Override
    public Optional<Result> getResultByUserId(Long userId) {
        // Ensures only published results are shown to the user
        return resultRepository.findByUserIdAndPublished(userId, true);
    }

    @Override
    public Optional<KsrtcForm> getKsrtcFormByUserId(Long id) {
        return Optional.empty();
    }
}