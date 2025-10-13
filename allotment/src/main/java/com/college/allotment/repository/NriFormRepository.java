package com.college.allotment.repository;

import com.college.allotment.model.NriForm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NriFormRepository extends JpaRepository<NriForm, Long> {

    // ✅ Find NRI form by user ID
    Optional<NriForm> findByUserId(Long userId);

    // ✅ Get all NRI forms ordered by total marks percentage (descending)
    // Formula: ((physics + chemistry + math) / 600.0) * 100
    @Query("SELECT n FROM NriForm n ORDER BY ((n.physicsMarks + n.chemistryMarks + n.mathMarks) / 600.0) * 100 DESC")
    List<NriForm> findAllOrderByPercentageDesc();
}
