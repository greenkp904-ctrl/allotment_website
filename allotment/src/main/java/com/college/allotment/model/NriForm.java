package com.college.allotment.model;

import jakarta.persistence.*;

@Entity
public class NriForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String passportNumber;
    private String country;

    @ManyToOne
    private User user;  // <--- Add this field

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getPassportNumber() { return passportNumber; }
    public void setPassportNumber(String passportNumber) { this.passportNumber = passportNumber; }

    public String getCountry() { return country; }
    public void setCountry(String country) { this.country = country; }

    public User getUser() { return user; }           // <--- getter
    public void setUser(User user) { this.user = user; }  // <--- setter
}
