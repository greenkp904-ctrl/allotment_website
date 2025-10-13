package com.college.allotment.model;

import jakarta.persistence.*;

@Entity
@Table(name = "nri_form")
public class NriForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ---------- User Relationship ----------
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    // ---------- Personal Details ----------
    private String candidateName;
    private String fatherName;
    private String motherName;
    private String address;

    // ---------- Educational Details ----------
    private String lastSchool;
    private String board; // CBSE, ICSE, Kerala
    private int physicsMarks;
    private int chemistryMarks;
    private int mathMarks;

    // ---------- NRI Quota Specific ----------
    private String passportNumber;
    private String country;

    // ---------- Ranking & Allotment ----------
    private double percentage;      // total % based on (physics+chemistry+math)/600 * 100
    private Integer rankPosition;   // rank number in NRI list
    private boolean allocated = false;
    private String allocatedSeat;   // optional seat/course info

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

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

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

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public int getPhysicsMarks() {
        return physicsMarks;
    }

    public void setPhysicsMarks(int physicsMarks) {
        this.physicsMarks = physicsMarks;
    }

    public int getChemistryMarks() {
        return chemistryMarks;
    }

    public void setChemistryMarks(int chemistryMarks) {
        this.chemistryMarks = chemistryMarks;
    }

    public int getMathMarks() {
        return mathMarks;
    }

    public void setMathMarks(int mathMarks) {
        this.mathMarks = mathMarks;
    }

    public String getPassportNumber() {
        return passportNumber;
    }

    public void setPassportNumber(String passportNumber) {
        this.passportNumber = passportNumber;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public double getPercentage() {
        return percentage;
    }

    public void setPercentage(double percentage) {
        this.percentage = percentage;
    }

    public Integer getRankPosition() {
        return rankPosition;
    }

    public void setRankPosition(Integer rankPosition) {
        this.rankPosition = rankPosition;
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
