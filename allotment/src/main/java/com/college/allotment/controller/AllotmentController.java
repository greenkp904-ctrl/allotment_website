package com.college.allotment.controller;

import com.college.allotment.model.*;
import com.college.allotment.service.AllotmentService;
import com.college.allotment.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.dao.DataIntegrityViolationException; // 🔑 Important import for error handling
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/allotment")
public class AllotmentController {

    private final AllotmentService allotmentService;
    private final UserRepository userRepository;

    // Available branches (Must exactly match <option> values in the HTML)
    private static final List<String> AVAILABLE_BRANCHES = Arrays.asList(
            "Computer Science and Engineering",
            "Computer Science (AI and ML) Engineering",
            "Electronics and Communication Engineering",
            "Mechanical Engineering",
            "Automobile Engineering",
            "Biotechnology Engineering"
    );

    public AllotmentController(AllotmentService allotmentService, UserRepository userRepository) {
        this.allotmentService = allotmentService;
        this.userRepository = userRepository;
    }

    /**
     * Helper method to authenticate user and fetch fresh data from the session.
     */
    private Optional<User> authenticateUser(HttpSession session) {
        // NOTE: The session attribute for User should match what is set during login.
        // Using "user" as it appears in the method below.
        User sessionUser = (User) session.getAttribute("user");
        if (sessionUser == null) {
            return Optional.empty();
        }
        return userRepository.findById(sessionUser.getId());
    }

    // --- Show Form Page: Handles GET request for the allotment form ---
    @GetMapping
    public String showAllotmentPage(HttpSession session,
                                    @ModelAttribute("errorMessage") String errorMessage,
                                    @ModelAttribute("successMessage") String successMessage,
                                    Model model) {

        Optional<User> optionalUser = authenticateUser(session);
        if (optionalUser.isEmpty()) {
            model.addAttribute("errorMessage", "Session expired. Please log in again.");
            return "redirect:/login";
        }

        User user = optionalUser.get();
        model.addAttribute("user", user);
        model.addAttribute("branches", AVAILABLE_BRANCHES);

        // 1. Fetch Existing Data for Pre-filling
        // Use service layer methods to retrieve existing forms
        KsrtcForm ksrtcForm = allotmentService.getKsrtcFormByUserId(user.getId()).orElse(new KsrtcForm());
        NriForm nriForm = allotmentService.getNriFormByUserId(user.getId()).orElse(new NriForm());

        model.addAttribute("ksrtcForm", ksrtcForm);
        model.addAttribute("nriForm", nriForm);

        // 2. Fetch Options
        OptionRegistration optionReg = user.getOptionRegistration();
        List<String> optionRegistrationList = (optionReg != null && optionReg.getSelectedOptions() != null)
                ? optionReg.getSelectedOptions()
                : Collections.emptyList();

        model.addAttribute("optionRegistrationList", optionRegistrationList);


        // --- Freeze Check Logic ---
        if (user.isFormSubmitted()) {
            model.addAttribute("isFrozen", true);
            model.addAttribute("freezeMessage", "⚠️ Your application is finalized and locked. No further changes are allowed.");
        } else {
            model.addAttribute("isFrozen", false);
        }

        // Pass flash attributes to the model
        if (!errorMessage.isEmpty()) { model.addAttribute("errorMessage", errorMessage); }
        if (!successMessage.isEmpty()) { model.addAttribute("successMessage", successMessage); }

        return "allotment-form";
    }

    // --- Dashboard View: Correctly displays options and results ---
    @GetMapping("/dashboard")
    public String userDashboard(HttpSession session, Model model) {

        Optional<User> optionalUser = authenticateUser(session);
        if (optionalUser.isEmpty()) {
            model.addAttribute("errorMessage", "Session expired. Please log in again.");
            return "redirect:/login";
        }
        User user = optionalUser.get();

        // 1. Fetch submitted options (Safe retrieval of List<String>)
        OptionRegistration optionReg = user.getOptionRegistration();

        List<String> options = (optionReg != null && optionReg.getSelectedOptions() != null)
                ? optionReg.getSelectedOptions()
                : Collections.emptyList();

        // 2. Fetch final result (Fetches both published and unpublished)
        Optional<Result> result = allotmentService.getResultByUserId(user.getId());

        model.addAttribute("user", user);
        model.addAttribute("options", options);
        model.addAttribute("result", result.orElse(null));

        return "dashboard";
    }

    // -------------------- Handle FINAL, LOCKED Submission (/submitAllotment) --------------------
    @PostMapping("/submitAllotment")
    @Transactional
    public String submitAllotment(HttpSession session,
                                  RedirectAttributes redirectAttributes,

                                  // 🎯 Form Type Checkboxes
                                  @RequestParam(value = "formType", required = false) List<String> formTypes,

                                  // --- Common Personal Details ---
                                  @RequestParam String fatherName,
                                  @RequestParam String motherName,
                                  @RequestParam String fatherOccupation,
                                  @RequestParam String motherOccupation,
                                  @RequestParam String address,
                                  @RequestParam String lastSchool,

                                  // 🚌 KSRTC Fields
                                  @RequestParam(value = "ksrtcParentName", required = false) String ksrtcParentName,
                                  @RequestParam(value = "keamRank", required = false) Integer keamRank,
                                  @RequestParam(value = "ksrtcId", required = false) String ksrtcEmployeeId,

                                  // 🌍 NRI Fields
                                  @RequestParam(value = "nriParentName", required = false) String nriParentName,
                                  @RequestParam(value = "passportNumber", required = false) String sponsorPassport,
                                  @RequestParam(value = "residentialId", required = false) String residentialId,
                                  @RequestParam(value = "country", required = false) String country,
                                  @RequestParam(value = "mathsMarks", required = false) Integer mathsMarks,
                                  @RequestParam(value = "physicsMarks", required = false) Integer physicsMarks,
                                  @RequestParam(value = "chemistryMarks", required = false) Integer chemistryMarks,

                                  // 📝 Options Field
                                  @RequestParam(value = "options", required = false) List<String> options) {

        Optional<User> optionalUser = authenticateUser(session);
        if (optionalUser.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Authentication failed. Please log in.");
            return "redirect:/login";
        }
        User user = optionalUser.get();

        // 1. 🔒 FREEZE CHECK
        if (user.isFormSubmitted()) {
            redirectAttributes.addFlashAttribute("errorMessage", "⚠️ Application is already finalized. Cannot submit again.");
            return "redirect:/allotment/dashboard";
        }

        // 2. Quota Check (Must select at least one)
        if (formTypes == null || formTypes.isEmpty()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Please select at least one form type (KSRTC or NRI).");
            return "redirect:/allotment";
        }
        boolean isKsrtcSelected = formTypes.contains("KSRTC");
        boolean isNriSelected = formTypes.contains("NRI");

        // Set the user's final QuotaType based on selections
        if (isKsrtcSelected && isNriSelected) {
            user.setQuotaType("BOTH");
        } else if (isKsrtcSelected) {
            user.setQuotaType("KSRTC");
        } else {
            user.setQuotaType("NRI");
        }

        // 🔑 NEW BLOCK A: STRICT VALIDATION FOR COMMON FIELDS
        // This targets the possibility that one of these fields is NOT NULL in User.java but received as empty string.
        if (fatherName == null || fatherName.trim().isEmpty() || motherName == null || motherName.trim().isEmpty() ||
                fatherOccupation == null || fatherOccupation.trim().isEmpty() || motherOccupation == null || motherOccupation.trim().isEmpty() ||
                address == null || address.trim().isEmpty() || lastSchool == null || lastSchool.trim().isEmpty()) {

            redirectAttributes.addFlashAttribute("errorMessage", "All common details (Names, Occupations, Address, Last School) are required fields.");
            return "redirect:/allotment";
        }

        // 🔑 NEW BLOCK B: CRITICAL CHECK FOR APPLICATION NUMBER
        // This prevents the OptionRegistration save from failing if the ApplicationNumber is missing.
        if (user.getApplicationNumber() == null || user.getApplicationNumber().trim().isEmpty()) {
            System.err.println("FATAL DATA ERROR: User " + user.getId() + " is missing application number.");
            redirectAttributes.addFlashAttribute("errorMessage", "Application number is missing from your profile. Please contact support immediately.");
            return "redirect:/allotment";
        }


        // 3. UPDATE USER MODEL WITH COMMON DETAILS
        user.setFatherName(fatherName);
        user.setMotherName(motherName);
        user.setFatherOccupation(fatherOccupation);
        user.setMotherOccupation(motherOccupation);
        user.setAddress(address);
        user.setLastSchool(lastSchool);


        // 4. Options Validation
        if (options == null || options.isEmpty() || options.size() > 6 || options.size() != new HashSet<>(options).size()) {
            redirectAttributes.addFlashAttribute("errorMessage", "Invalid option selection (must be 1-6 unique options).");
            return "redirect:/allotment";
        }


        try {
            // 5. CRITICAL STEP: Save User Updates *BEFORE* child forms to get managed entity
            User savedUser = userRepository.save(user); // Parent saved first
            userRepository.flush();

            // --- 6. KSRTC Quota Submission (Validation and Save) ---
            if (isKsrtcSelected) {
                if (keamRank == null || ksrtcEmployeeId == null || ksrtcEmployeeId.trim().isEmpty() || ksrtcParentName == null || ksrtcParentName.trim().isEmpty()) {
                    redirectAttributes.addFlashAttribute("errorMessage", "KSRTC Quota requires KEAM Rank, KSRTC ID, and Parent Name.");
                    return "redirect:/allotment";
                }

                KsrtcForm ksrtcForm = allotmentService.getKsrtcFormByUserId(savedUser.getId())
                        .orElse(new KsrtcForm());

                // Use the MANAGED, SAVED USER for the foreign key!
                ksrtcForm.setUser(savedUser);
                ksrtcForm.setKeamRank(keamRank);
                ksrtcForm.setKsrtcEmployeeId(ksrtcEmployeeId);
                ksrtcForm.setKsrtcParentName(ksrtcParentName);
                allotmentService.submitKsrtcForm(ksrtcForm);
            }

            // --- 7. NRI Quota Submission (Validation and Save) ---
            if (isNriSelected) {
                // Simplified validation check (Consider strengthening this if NRI forms fail)
                if (mathsMarks == null || physicsMarks == null || chemistryMarks == null || sponsorPassport == null || sponsorPassport.trim().isEmpty()) {
                    redirectAttributes.addFlashAttribute("errorMessage", "NRI Quota requires all marks and Passport Number.");
                    return "redirect:/allotment";
                }

                NriForm nriForm = allotmentService.getNriFormByUserId(savedUser.getId())
                        .orElse(new NriForm());

                // Use the MANAGED, SAVED USER for the foreign key!
                nriForm.setUser(savedUser);
                nriForm.setMathsMarks(mathsMarks);
                nriForm.setPhysicsMarks(physicsMarks);
                nriForm.setChemistryMarks(chemistryMarks);
                nriForm.setSponsorPassport(sponsorPassport);
                nriForm.setNriParentName(nriParentName);
                nriForm.setResidentialId(residentialId);
                nriForm.setCountry(country);
                allotmentService.submitNriForm(nriForm);
            }

            // --- 8. Save Options & FREEZE ---
            allotmentService.saveSelectedOptions(savedUser, new ArrayList<>(options));

            // 9. FINAL ACTION: Lock the user's form submission status
            savedUser.setFormSubmitted(true);
            User finalUser = userRepository.save(savedUser); // Final save to lock and update user fields
            userRepository.flush();
            session.setAttribute("user", finalUser); // Update the session object

            // 🏆 SUCCESS REDIRECT to Dashboard
            redirectAttributes.addFlashAttribute("successMessage", "✅ Application successfully submitted and **LOCKED**. You cannot modify it further.");
            return "redirect:/allotment/dashboard";

        } catch (DataIntegrityViolationException e) {
            // Catches the SQL foreign key error from your previous logs.
            System.err.println("DATABASE ERROR during form submission: " + e.getMessage());
            // Direct the user back to the form with a clear message about the likely cause (schema mismatch or missing field)
            redirectAttributes.addFlashAttribute("errorMessage", "Submission failed due to a database integrity error. This usually means a required field was missing. Please check all fields.");
            return "redirect:/allotment";

        } catch (Exception e) {
            // Catches any other unexpected exceptions
            System.err.println("UNEXPECTED ERROR during form submission: " + e.getMessage());
            redirectAttributes.addFlashAttribute("errorMessage", "An unexpected error occurred during final submission. Please contact support.");
            return "redirect:/allotment";
        }
    }
}