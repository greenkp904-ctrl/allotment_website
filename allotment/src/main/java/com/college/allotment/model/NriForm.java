package com.college.allotment.model;

import jakarta.persistence.*;

@Entity
@Table(name = "nri_form")
public class NriForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 🔑 CRITICAL FIX APPLIED HERE: Added nullable=false and unique=true
    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false, unique = true)
    private User user;

    // ---------- NRI Quota Specific Details from HTML Form ----------
    private String nriParentName;

    // 🚩 FIX: Renamed from 'passportNumber' to 'sponsorPassport' to match the Controller.
    private String sponsorPassport;

    private String residentialId;
    private String country;

    // Academic Marks (Used for ranking)
    private Integer mathsMarks;
    private Integer physicsMarks;
    private Integer chemistryMarks;

    // Calculated fields
    private Integer totalMarks;

    // ---------- Ranking / Allotment Info ----------
    private Integer nriRankPosition;
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

    public String getNriParentName() {
        return nriParentName;
    }

    public void setNriParentName(String nriParentName) {
        this.nriParentName = nriParentName;
    }

    // 🚩 FIX: Getter renamed.
    public String getSponsorPassport() {
        return sponsorPassport;
    }

    // 🚩 FIX: Setter renamed.
    public void setSponsorPassport(String sponsorPassport) {
        this.sponsorPassport = sponsorPassport;
    }

    public String getResidentialId() {
        return residentialId;
    }

    public void setResidentialId(String residentialId) {
        this.residentialId = residentialId;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getMathsMarks() {
        return mathsMarks;
    }

    public void setMathsMarks(Integer mathsMarks) {
        this.mathsMarks = mathsMarks;
    }

    public Integer getPhysicsMarks() {
        return physicsMarks;
    }

    public void setPhysicsMarks(Integer physicsMarks) {
        this.physicsMarks = physicsMarks;
    }

    public Integer getChemistryMarks() {
        return chemistryMarks;
    }

    public void setChemistryMarks(Integer chemistryMarks) {
        this.chemistryMarks = chemistryMarks;
    }

    public Integer getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(Integer totalMarks) {
        this.totalMarks = totalMarks;
    }

    public Integer getNriRankPosition() {
        return nriRankPosition;
    }

    public void setNriRankPosition(Integer nriRankPosition) {
        this.nriRankPosition = nriRankPosition;
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