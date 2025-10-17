package com.college.allotment.service;

import com.college.allotment.model.*;
import com.college.allotment.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Primary
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
    private OptionRegistrationRepository optionRegistrationRepository;

    @Autowired
    private EmailService emailService;

    // ---------------- SEAT CONFIGURATION ----------------
    // Seat definitions: KSRTC = 10 seats per branch; NRI = 5 seats per branch (Adjusted based on the prompt's matrix)
    private static final Map<String, Integer> KSRTC_SEAT_POOL = Map.of(
            "Computer Science and Engineering", 10,
            "Computer Science (AI and ML) Engineering", 10,
            "Electronics and Communication Engineering", 10,
            "Mechanical Engineering", 10,
            "Automobile Engineering", 10,
            "Biotechnology Engineering", 10
    );

    private static final Map<String, Integer> NRI_SEAT_POOL = Map.of(
            "Computer Science and Engineering", 5,
            "Computer Science (AI and ML) Engineering", 5,
            "Electronics and Communication Engineering", 5,
            "Mechanical Engineering", 5,
            "Automobile Engineering", 5,
            "Biotechnology Engineering", 5
    );


    // ---------------- USER & COMMON CRUD ----------------

    @Transactional
    public User registerUser(User user) {
        if (user.getApplicationNumber() == null || user.getApplicationNumber().isEmpty()) {
            user.setApplicationNumber("APP-" + System.currentTimeMillis());
        }
        return userRepository.save(user);
    }

    public Optional<User> findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public List<User> getAllCandidateUsers() {
        // Uses the derived query method in UserRepository
        return userRepository.findByRole(Role.ROLE_CANDIDATE);
    }

    public List<User> getAllAdminUsers() {
        // Uses the derived query method in UserRepository
        return userRepository.findByRole(Role.ROLE_ADMIN);
    }

    // ---------------- KSRTC FORM SUBMISSION & RETRIEVAL ----------------

    @Transactional
    public KsrtcForm submitKsrtcForm(KsrtcForm form) {
        Optional<KsrtcForm> existingForm = ksrtcFormRepository.findByUserId(form.getUser().getId());

        KsrtcForm formToSave = existingForm.orElse(new KsrtcForm());

        // Ensure the User relationship is set on a potentially new form
        if (formToSave.getId() == null) {
            formToSave.setUser(form.getUser());
        }

        // Update fields from the new submission
        formToSave.setKeamRank(form.getKeamRank());
        formToSave.setKsrtcEmployeeId(form.getKsrtcEmployeeId());
        formToSave.setKsrtcParentName(form.getKsrtcParentName());

        return ksrtcFormRepository.save(formToSave);
    }

    public List<KsrtcForm> getAllKsrtcForms() {
        return ksrtcFormRepository.findAll();
    }

    public Optional<KsrtcForm> getKsrtcFormByUserId(Long id) {
        return ksrtcFormRepository.findByUserId(id);
    }


    // ---------------- NRI FORM SUBMISSION & RETRIEVAL ----------------

    @Transactional
    public NriForm submitNriForm(NriForm form) {
        // Calculate Total Marks upon submission.
        int total = Optional.ofNullable(form.getMathsMarks()).orElse(0) +
                Optional.ofNullable(form.getPhysicsMarks()).orElse(0) +
                Optional.ofNullable(form.getChemistryMarks()).orElse(0);
        form.setTotalMarks(total);

        Optional<NriForm> existingForm = nriFormRepository.findByUserId(form.getUser().getId());

        NriForm formToSave = existingForm.orElse(new NriForm());

        // Ensure the User relationship is set on a potentially new form
        if (formToSave.getId() == null) {
            formToSave.setUser(form.getUser());
        }

        // Update fields
        formToSave.setMathsMarks(form.getMathsMarks());
        formToSave.setPhysicsMarks(form.getPhysicsMarks());
        formToSave.setChemistryMarks(form.getChemistryMarks());
        formToSave.setTotalMarks(total);
        formToSave.setSponsorPassport(form.getSponsorPassport());
        formToSave.setNriParentName(form.getNriParentName());

        // Add other NRI fields if they were omitted in the initial submission and are required (e.g., residentialId, country)
        formToSave.setResidentialId(form.getResidentialId());
        formToSave.setCountry(form.getCountry());

        // Note: Logic for 'overallPercentage' would be added here if that field were present.

        return nriFormRepository.save(formToSave);
    }

    public List<NriForm> getAllNriForms() {
        return nriFormRepository.findAll();
    }

    public Optional<NriForm> getNriFormByUserId(Long id) {
        return nriFormRepository.findByUserId(id);
    }

    // ---------------- OPTION REGISTRATION ----------------

    @Transactional
    public void saveSelectedOptions(User user, List<String> selectedOptions) {
        if (selectedOptions == null || selectedOptions.isEmpty() || selectedOptions.size() > 6 || selectedOptions.size() != new HashSet<>(selectedOptions).size()) {
            throw new IllegalArgumentException("Invalid option selection (must be 1-6 unique options).");
        }

        // 🔍 DEBUG: Verify user is persisted
        System.out.println("DEBUG: User ID = " + user.getId());
        System.out.println("DEBUG: User is new entity? " + (user.getId() == null || user.getId() == 0));

        Optional<OptionRegistration> existing = optionRegistrationRepository.findByUserId(user.getId());
        OptionRegistration optionRegistration = existing.orElse(new OptionRegistration());

        optionRegistration.setUser(user);
        optionRegistration.setApplicationNumber(user.getApplicationNumber());
        optionRegistration.setQuotaType(user.getQuotaType());
        optionRegistration.setSelectedOptions(new ArrayList<>(selectedOptions));

        optionRegistrationRepository.save(optionRegistration);
    }

    // ---------------- RANKLIST GENERATION & PERSISTENCE ----------------

    @Transactional
    public List<KsrtcForm> generateAndSaveKsrtcRanklist() {
        // Only consider applicants with a non-null KEAM Rank
        List<KsrtcForm> applicants = ksrtcFormRepository.findAll().stream()
                .filter(form -> form.getKeamRank() != null)
                .collect(Collectors.toList());

        // Sort by KEAM Rank (Ascending: Lower rank number is better)
        applicants.sort(Comparator.comparing(KsrtcForm::getKeamRank));

        for (int i = 0; i < applicants.size(); i++) {
            KsrtcForm form = applicants.get(i);
            form.setKsrtcRankPosition(i + 1); // 1-based rank
        }

        return ksrtcFormRepository.saveAll(applicants);
    }

    @Transactional
    public List<NriForm> generateAndSaveNriRanklist() {
        // Only consider applicants with a non-null Total Marks
        List<NriForm> applicants = nriFormRepository.findAll().stream()
                .filter(form -> form.getTotalMarks() != null)
                .collect(Collectors.toList());

        // Define the comprehensive sorting criteria (Highest Score/Marks is best)
        Comparator<NriForm> nriComparator = Comparator
                // 1. Primary Sort: Total Marks (Descending)
                .comparing(NriForm::getTotalMarks, Comparator.reverseOrder())
                // 2. Tie-breaker 1: Maths Marks (Descending)
                .thenComparing(NriForm::getMathsMarks, Comparator.reverseOrder())
                // 3. Tie-breaker 2: Physics Marks (Descending)
                .thenComparing(NriForm::getPhysicsMarks, Comparator.reverseOrder());

        // Apply sorting
        applicants.sort(nriComparator);

        for (int i = 0; i < applicants.size(); i++) {
            NriForm form = applicants.get(i);
            form.setNriRankPosition(i + 1); // 1-based rank
        }

        return nriFormRepository.saveAll(applicants);
    }

    // ---------------- CORE ALLOTMENT LOGIC ----------------

    @Transactional
    public void runAllotmentProcess() {
        System.out.println("--- Starting Allotment Process ---");

        // 1. Clear previous results to prepare for a fresh allotment round
        resultRepository.deleteAll();

        // 2. Generate and save the final rank lists
        List<KsrtcForm> ksrtcRanklist = generateAndSaveKsrtcRanklist();
        List<NriForm> nriRanklist = generateAndSaveNriRanklist();

        // 3. Get all registered options and map them for quick lookup
        Map<Long, OptionRegistration> optionsMap = optionRegistrationRepository.findAll().stream()
                .collect(Collectors.toMap(o -> o.getUser().getId(), o -> o));

        // Create mutable copies of seat pools
        Map<String, Integer> ksrtcSeatsAvailable = new HashMap<>(KSRTC_SEAT_POOL);
        Map<String, Integer> nriSeatsAvailable = new HashMap<>(NRI_SEAT_POOL);

        // Map to track who has been allocated a seat (User ID -> Result)
        Map<Long, Result> finalAllocations = new HashMap<>();

        // 4. Run KSRTC Allocation First (Higher Priority)
        System.out.println("--- Running KSRTC Allotment ---");
        // Convert KsrtcForm list to User list, sorted by their KSRTC rank position
        List<User> ksrtcApplicants = ksrtcRanklist.stream()
                .sorted(Comparator.comparing(KsrtcForm::getKsrtcRankPosition))
                .map(KsrtcForm::getUser)
                .toList();

        allocateSeats(ksrtcApplicants,
                ksrtcSeatsAvailable,
                optionsMap,
                finalAllocations,
                "KSRTC");

        // 5. Run NRI Allocation Next
        System.out.println("--- Running NRI Allotment ---");
        // Convert NriForm list to User list, sorted by their NRI rank position
        List<User> nriApplicants = nriRanklist.stream()
                .sorted(Comparator.comparing(NriForm::getNriRankPosition))
                .map(NriForm::getUser)
                .toList();

        allocateSeats(nriApplicants,
                nriSeatsAvailable,
                optionsMap,
                finalAllocations,
                "NRI");

        // 6. Save all results
        resultRepository.saveAll(finalAllocations.values());

        System.out.println("--- Allotment Process Complete. Results saved as unpublished ---");
    }

    private void allocateSeats(List<User> applicants,
                               Map<String, Integer> seatsAvailable,
                               Map<Long, OptionRegistration> optionsMap,
                               Map<Long, Result> finalAllocations,
                               String quotaType) {

        for (User user : applicants) {

            // If the user already has a final allocation from a higher priority quota, skip them.
            if (finalAllocations.containsKey(user.getId())) {
                continue;
            }

            // Check if the user is eligible for this specific quota (Must be the target quota or BOTH)
            if (!user.getQuotaType().equals(quotaType) && !user.getQuotaType().equals("BOTH")) {
                continue;
            }

            OptionRegistration options = optionsMap.get(user.getId());

            // Skip if user hasn't registered options
            if (options == null || options.getSelectedOptions() == null || options.getSelectedOptions().isEmpty()) {
                finalAllocations.put(user.getId(), createUnallocatedResult(user, quotaType, "No options registered."));
                continue;
            }

            boolean allocated = false;

            // Check options in order of preference
            for (String branch : options.getSelectedOptions()) {
                Integer availableSeats = seatsAvailable.getOrDefault(branch, 0);

                if (availableSeats > 0) {
                    // ALLOCATE SEAT
                    Result result = createAllocatedResult(user, quotaType, branch);

                    finalAllocations.put(user.getId(), result);
                    seatsAvailable.put(branch, availableSeats - 1); // Decrement count
                    allocated = true;
                    break;
                }
            }

            // If no seat was found after checking all options, add an unallocated result
            if (!allocated) {
                finalAllocations.put(user.getId(), createUnallocatedResult(user, quotaType, "No seats available for your preferred options."));
            }
        }
    }

    private Result createAllocatedResult(User user, String quotaType, String branch) {
        Result result = new Result();
        result.setUser(user);
        result.setAllocatedQuota(quotaType);
        result.setAllocatedBranch(branch);
        result.setResultMessage("CONGRATULATIONS! You have been allotted " + branch + " under the " + quotaType + " Quota.");
        result.setPublished(false);
        return result;
    }

    private Result createUnallocatedResult(User user, String quotaType, String message) {
        Result result = new Result();
        result.setUser(user);
        // Ensure allocated quota and branch are null if unallocated to avoid confusion in the DB
        result.setAllocatedQuota(quotaType);
        result.setAllocatedBranch(null);
        result.setResultMessage("Sorry. " + message);
        result.setPublished(false);
        return result;
    }

    // ---------------- ALLOTMENT RESULT CONTROL ----------------

    @Transactional
    public Result saveAllocationResult(Result result) {
        return resultRepository.save(result);
    }

    @Transactional
    public void publishAllotmentResults() {
        List<Result> unpublishedResults = resultRepository.findByPublished(false);

        for (Result result : unpublishedResults) {
            result.setPublished(true);
            resultRepository.save(result);

            // Send email upon publishing
            String subject = "Allotment Result Published";
            String body = String.format(
                    "Dear %s,\n\nYour allotment result has been published.\n"
                            + "Allocated Quota: %s\nAllocated Branch: %s\nMessage: %s\n\n"
                            + "Thank you,\nCollege Allotment Team",
                    result.getUser().getName(),
                    result.getAllocatedQuota(),
                    // Use a placeholder if the branch is null (unallocated)
                    result.getAllocatedBranch() != null ? result.getAllocatedBranch() : "N/A",
                    result.getResultMessage()
            );

            emailService.sendEmail(result.getUser().getEmail(), subject, body);
        }

        System.out.println("--- Allotment Results Published by Administrator ---");
    }

    public Optional<Result> getPublishedResultByUserId(Long userId) {
        return resultRepository.findByUserId(userId)
                .filter(Result::isPublished);
    }

    public List<Result> getAllResults() {
        return resultRepository.findAll();
    }

    public Optional<Result> getResultByUserId(Long userId) {
        return resultRepository.findByUserId(userId);
    }

    // ---------------- UTILITIES ----------------

    @Transactional
    public void resetPassword(String email, String newPassword) {
        Optional<User> userOpt = userRepository.findByEmail(email);

        userOpt.ifPresent(user -> {
            user.setPassword(newPassword);
            userRepository.save(user);

            String subject = "Password Reset Successful";
            String body = "Dear " + user.getName() + ",\n\nYour password has been successfully reset.\n\nRegards,\nCollege Allotment System";

            emailService.sendEmail(email, subject, body);
        });
    }

    @Transactional
    public void deleteUser(Long userId) {
        // Explicit deletion of dependent entities for robustness
        ksrtcFormRepository.findByUserId(userId).ifPresent(ksrtcFormRepository::delete);
        nriFormRepository.findByUserId(userId).ifPresent(nriFormRepository::delete);
        resultRepository.findByUserId(userId).ifPresent(resultRepository::delete);
        optionRegistrationRepository.findByUserId(userId).ifPresent(optionRegistrationRepository::delete);
        userRepository.deleteById(userId);
    }
}