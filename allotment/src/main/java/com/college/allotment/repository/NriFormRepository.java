package com.college.allotment.repository;

import com.college.allotment.model.NriForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NriFormRepository extends JpaRepository<NriForm, Long> {

    // Add this method to find NRI form by userId
    Optional<NriForm> findByUserId(Long userId);
}
