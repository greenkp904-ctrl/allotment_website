package com.college.allotment.model;

import jakarta.persistence.*;

@Entity
@Table(name = "ksrtc_form")
public class KsrtcForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔑 CRITICAL FIX APPLIED HERE: Added nullable=false and unique=true
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;

    // ---------- KSRTC Quota Specific Details from HTML Form ----------
    private String ksrtcParentName;

    // 🚩 FIX: Renamed from 'ksrtcId' to 'ksrtcEmployeeId' to match the Controller.
    private String ksrtcEmployeeId;

    private Integer keamRank;

    // ---------- Ranking / Allotment Info ----------
    private Integer ksrtcRankPosition;

    @Column(nullable = false, columnDefinition = "bit(1) default 0")
    private boolean allocated = false;

    private String allocatedSeat;

    // --- Getters & Setters ---

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

    public String getKsrtcParentName() {
        return ksrtcParentName;
    }

    public void setKsrtcParentName(String ksrtcParentName) {
        this.ksrtcParentName = ksrtcParentName;
    }

    // 🚩 FIX: Setter renamed to match the new field name.
    public String getKsrtcEmployeeId() {
        return ksrtcEmployeeId;
    }

    // 🚩 FIX: Getter renamed to match the new field name.
    public void setKsrtcEmployeeId(String ksrtcEmployeeId) {
        this.ksrtcEmployeeId = ksrtcEmployeeId;
    }

    public Integer getKeamRank() {
        return keamRank;
    }

    public void setKeamRank(Integer keamRank) {
        this.keamRank = keamRank;
    }

    public Integer getKsrtcRankPosition() {
        return ksrtcRankPosition;
    }

    public void setKsrtcRankPosition(Integer ksrtcRankPosition) {
        this.ksrtcRankPosition = ksrtcRankPosition;
    }

    public boolean isAllocated() {
        return allocated;
    }

    public void setAllocated(boolean allocated) {
        this.allocated = allocated;
    }

    public String getAllocatedSeat() {
        return allocatedSeat;
    }

    public void setAllocatedSeat(String allocatedSeat) {
        this.allocatedSeat = allocatedSeat;
    }
}