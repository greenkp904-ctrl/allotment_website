package com.college.allotment.repository;

import com.college.allotment.model.Result;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ResultRepository extends JpaRepository<Result, Long> {
    Optional<Result> findByUserId(Long userId); // one result per user
    List<Result> findAllByPublishedTrue(); // all published results
}
