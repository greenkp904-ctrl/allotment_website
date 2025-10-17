package com.college.allotment.model;

import jakarta.persistence.*;
// The Role enum is now in its own file and imported implicitly because it's in the same package.

@Entity
@Table(name = "app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // --- Authentication and Basic Details ---
    @Column(nullable = false)
    private String name;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    // 2. Add the Role Field (Uses the external Role.java)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role = Role.ROLE_CANDIDATE; // Default all new users to CANDIDATE

    // --- Allotment System Identifiers ---
    @Column(unique = true, nullable = false)
    private String applicationNumber;

    // NOTE: quotaType is allowed to be nullable if a user registers but hasn't selected a quota yet,
    // but we'll enforce NOT NULL since it's set just before submission.
    @Column(nullable = true)
    private String quotaType; // e.g., "NRI", "KSRTC", "BOTH"

    // 🔑 CRITICAL FIELD ADDED FOR SUBMISSION FREEZE LOGIC
    @Column(nullable = false)
    private boolean formSubmitted = false;

    // --- Common Personal Details from HTML Form (CRITICAL FIX APPLIED HERE) ---
    @Column(nullable = true)
    private String fatherName;

    @Column(nullable = true)
    private String motherName;

    @Column(nullable = true)
    private String fatherOccupation;

    @Column(nullable = true)
    private String motherOccupation;

    @Column(nullable = true)
    private String address;

    @Column(nullable = true)
    private String lastSchool;

    // --- Relationships (One-to-One) ---
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private KsrtcForm ksrtcForm;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private NriForm nriForm;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private OptionRegistration optionRegistration;

    // --- Constructors (Added for JPA compliance and flexibility) ---

    // Default Constructor required by JPA
    public User() {
    }

    // --- Getters and Setters (No changes needed here) ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public String getApplicationNumber() {
        return applicationNumber;
    }

    public void setApplicationNumber(String applicationNumber) {
        this.applicationNumber = applicationNumber;
    }

    public String getQuotaType() {
        return quotaType;
    }

    public void setQuotaType(String quotaType) {
        this.quotaType = quotaType;
    }

    public boolean isFormSubmitted() {
        return formSubmitted;
    }

    public void setFormSubmitted(boolean formSubmitted) {
        this.formSubmitted = formSubmitted;
    }

    // Common Personal Details Getters and Setters
    public String getFatherName() {
        return fatherName;
    }

    public void setFatherName(String fatherName) {
        this.fatherName = fatherName;
    }

    public String getMotherName() {
        return motherName;
    }

    public void setMotherName(String motherName) {
        this.motherName = motherName;
    }

    public String getFatherOccupation() {
        return fatherOccupation;
    }

    public void setFatherOccupation(String fatherOccupation) {
        this.fatherOccupation = fatherOccupation;
    }

    public String getMotherOccupation() {
        return motherOccupation;
    }

    public void setMotherOccupation(String motherOccupation) {
        this.motherOccupation = motherOccupation;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLastSchool() {
        return lastSchool;
    }

    public void setLastSchool(String lastSchool) {
        this.lastSchool = lastSchool;
    }

    // Relationships Getters and Setters
    public KsrtcForm getKsrtcForm() {
        return ksrtcForm;
    }

    public void setKsrtcForm(KsrtcForm ksrtcForm) {
        this.ksrtcForm = ksrtcForm;
    }

    public NriForm getNriForm() {
        return nriForm;
    }

    public void setNriForm(NriForm nriForm) {
        this.nriForm = nriForm;
    }

    public OptionRegistration getOptionRegistration() {
        return optionRegistration;
    }

    public void setOptionRegistration(OptionRegistration optionRegistration) {
        this.optionRegistration = optionRegistration;
    }
}