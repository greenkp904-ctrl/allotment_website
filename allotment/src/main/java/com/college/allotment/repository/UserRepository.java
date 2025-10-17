package com.college.allotment.repository;

import com.college.allotment.model.User;
import com.college.allotment.model.Role; // 🔑 Import the Role enum
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List; // 🔑 Use List for multiple users
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Find user by email (used for login and admin utilities)
    Optional<User> findByEmail(String email);

    // 🔑 NEW: Find all users by a specific Role (for Admin Dashboard)
    List<User> findAllByRole(Role role);

    // 🔑 NEW: Find Candidate users (excluding ADMINs)
    // Assuming Role.ROLE_CANDIDATE is the role for regular users
    List<User> findByRole(Role role);

}