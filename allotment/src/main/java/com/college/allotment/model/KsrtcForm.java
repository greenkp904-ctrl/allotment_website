package com.college.allotment.model;

import jakarta.persistence.*;

@Entity
public class KsrtcForm {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String ksrtcId;
    private String busRoute;

    @ManyToOne
    private User user;  // <--- Add this field

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getKsrtcId() { return ksrtcId; }
    public void setKsrtcId(String ksrtcId) { this.ksrtcId = ksrtcId; }

    public String getBusRoute() { return busRoute; }
    public void setBusRoute(String busRoute) { this.busRoute = busRoute; }

    public User getUser() { return user; }        // <--- getter
    public void setUser(User user) { this.user = user; }  // <--- setter
}
