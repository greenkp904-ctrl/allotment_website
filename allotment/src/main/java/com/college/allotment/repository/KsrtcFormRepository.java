package com.college.allotment.repository;

import com.college.allotment.model.KsrtcForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface KsrtcFormRepository extends JpaRepository<KsrtcForm, Long> {

    // Find a form by user ID
    Optional<KsrtcForm> findByUserId(Long userId);

    // 📋 Get all KSRTC forms ordered by KEAM rank (ascending) - Useful for initial processing
    List<KsrtcForm> findAllByOrderByKeamRankAsc();

    // 🏆 Get all KSRTC forms ordered by the final calculated rank position (Used for displaying the final ranklist)
    List<KsrtcForm> findAllByOrderByKsrtcRankPositionAsc();

    // 🧾 Get all allocated forms
    List<KsrtcForm> findByAllocatedTrue();

    // 🧾 Get all unallocated forms
    List<KsrtcForm> findByAllocatedFalse();
}