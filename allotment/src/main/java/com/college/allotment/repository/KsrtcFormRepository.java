package com.college.allotment.repository;

import com.college.allotment.model.KsrtcForm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface KsrtcFormRepository extends JpaRepository<KsrtcForm, Long> {
    Optional<KsrtcForm> findByUserId(Long userId);
}
