package com.college.allotment.repository;

import com.college.allotment.model.Result;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResultRepository extends JpaRepository<Result, Long> {

    // --- Methods Required by AllotmentServiceImpl ---

    /**
     * Required by AllotmentServiceImpl.publishAllotmentResults().
     * Fetches all results that have NOT yet been published (published = false).
     */
    // (This matches your existing definition: List<Result> findByPublished(boolean published);)
    List<Result> findByPublished(boolean published);

    /**
     * 🔑 CRITICAL METHOD ADDED/REPLACED 🔑
     * Required by AllotmentServiceImpl.getResultByUserId().
     * Fetches a user's result ONLY if the 'published' status matches the argument (typically 'true').
     */
    Optional<Result> findByUserIdAndPublished(Long userId, boolean published);


    // --- Optional/Utility Methods ---

    // Optional but good for direct admin lookup if needed, though covered by the method above.
    Optional<Result> findByUserId(Long userId);

    // List<Result> findAllByPublishedTrue(); // Functionally same as findByPublished(true)

}