package com.college.allotment.model;

import jakarta.persistence.*;

@Entity
public class Result {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ksrtcResult;
    private String nriResult;

    private String quota;          // KSRTC or NRI
    private String allocatedSeat;  // Allocated seat
    private boolean published;     // Published or not

    @ManyToOne
    private User user;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getKsrtcResult() { return ksrtcResult; }
    public void setKsrtcResult(String ksrtcResult) { this.ksrtcResult = ksrtcResult; }

    public String getNriResult() { return nriResult; }
    public void setNriResult(String nriResult) { this.nriResult = nriResult; }

    public String getQuota() { return quota; }
    public void setQuota(String quota) { this.quota = quota; }

    public String getAllocatedSeat() { return allocatedSeat; }
    public void setAllocatedSeat(String allocatedSeat) { this.allocatedSeat = allocatedSeat; }

    public boolean isPublished() { return published; }
    public void setPublished(boolean published) { this.published = published; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
}
