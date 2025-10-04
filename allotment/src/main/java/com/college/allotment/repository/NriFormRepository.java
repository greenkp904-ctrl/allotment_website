package com.college.allotment.repository;

import com.college.allotment.model.NriForm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NriFormRepository extends JpaRepository<NriForm, Long> {
    Optional<NriForm> findByUserId(Long userId);
}
