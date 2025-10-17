package com.college.allotment.model;

import jakarta.persistence.*;

@Entity
@Table(name = "allotment_result")
public class Result {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Relationship to User (One Result per User)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // --- Allotment Decision Fields ---
    private String allocatedQuota;
    private String allocatedBranch;
    private boolean allocated = false; // true if a seat was allocated
    private String resultMessage;

    // --- Management Field: Controls Administrator Publish Status ---
    @Column(nullable = false)
    private boolean published = false; // Initial state: NOT published. Only Admin can set to true.

    // --- Getters and Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public String getAllocatedQuota() { return allocatedQuota; }
    public void setAllocatedQuota(String allocatedQuota) { this.allocatedQuota = allocatedQuota; }

    public String getAllocatedBranch() { return allocatedBranch; }
    public void setAllocatedBranch(String allocatedBranch) { this.allocatedBranch = allocatedBranch; }

    public boolean isAllocated() { return allocated; }
    public void setAllocated(boolean allocated) { this.allocated = allocated; }

    public String getResultMessage() { return resultMessage; }
    public void setResultMessage(String resultMessage) { this.resultMessage = resultMessage; }

    // Crucial for Admin Control
    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }
}