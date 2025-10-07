package com.college.allotment.model;

import jakarta.persistence.*;

@Entity
public class KsrtcForm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ---------- User Relationship ----------
    @ManyToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;  // ✅ Now you can do form.setUser(user)

    // ---------- Personal Details ----------
    private String candidateName;
    private String fatherName;
    private String motherName;
    private String address;

    // ---------- Educational Details ----------
    private String lastSchool;
    private String board; // CBSE, ICSE, Kerala
    private int keamRank;

    // ---------- KSRTC Quota Specific ----------
    private String ksrtcId;
    private String busRoute;

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

    public int getKeamRank() {
        return keamRank;
    }

    public void setKeamRank(int keamRank) {
        this.keamRank = keamRank;
    }

    public String getKsrtcId() {
        return ksrtcId;
    }

    public void setKsrtcId(String ksrtcId) {
        this.ksrtcId = ksrtcId;
    }

    public String getBusRoute() {
        return busRoute;
    }

    public void setBusRoute(String busRoute) {
        this.busRoute = busRoute;
    }
}
