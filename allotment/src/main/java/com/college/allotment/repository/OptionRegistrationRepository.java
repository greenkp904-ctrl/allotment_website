package com.college.allotment.repository;

import com.college.allotment.model.OptionRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OptionRegistrationRepository extends JpaRepository<OptionRegistration, Long> {
    Optional<OptionRegistration> findByUserId(Long userId);
}
