package com.college.allotment.repository;

import com.college.allotment.model.Result;
import com.college.allotment.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AllotmentRepository extends JpaRepository<Result, Long> {
    Optional<Result> findByUser(User user);
}
