package com.college.allotment.repository;

import com.college.allotment.model.KsrtcForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface KsrtcFormRepository extends JpaRepository<KsrtcForm, Long> {

    // Add this method to find KSRTC form by userId
    Optional<KsrtcForm> findByUserId(Long userId);
}
