package com.college.allotment.model;

import jakarta.persistence.*;
import java.util.List;

@Entity
@Table(name = "option_registration") // Added explicit table name for clarity
public class OptionRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔑 CRITICAL FIX APPLIED HERE: Added unique=true
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;

    // Identifier for the submission (e.g., generated system ID)
    @Column(unique = true, nullable = false)
    private String applicationNumber;

    // Quota Type: Useful to quickly filter applications (e.g., "KSRTC", "NRI", or "BOTH")
    @Column(nullable = false)
    private String quotaType;

    // Store up to 6 selected options
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "selected_options", joinColumns = @JoinColumn(name = "option_registration_id"))
    @Column(name = "branch_name", nullable = false)
    private List<String> selectedOptions;

    // ---------- Getters & Setters ----------

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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

    public List<String> getSelectedOptions() {
        return selectedOptions;
    }

    public void setSelectedOptions(List<String> selectedOptions) {
        this.selectedOptions = selectedOptions;
    }
}