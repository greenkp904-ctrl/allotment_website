package com.college.allotment.repository;

import com.college.allotment.model.NriForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NriFormRepository extends JpaRepository<NriForm, Long> {

    // Find NRI form by user ID
    // CRITICAL: Used by AllotmentService for checking existence/updating forms.
    Optional<NriForm> findByUserId(Long userId);

    // Get all NRI forms ordered by the pre-calculated total marks (for ranking)
    List<NriForm> findAllByOrderByTotalMarksDesc();

    // 🏆 Get all NRI forms ordered by the final calculated rank position (Used for displaying the final ranklist)
    List<NriForm> findAllByOrderByNriRankPositionAsc();

    // 🧾 Get all allocated forms
    List<NriForm> findByAllocatedTrue();

    // 🧾 Get all unallocated forms
    List<NriForm> findByAllocatedFalse();
}