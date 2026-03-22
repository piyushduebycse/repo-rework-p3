package com.revworkforce.performanceservice.repository;

import com.revworkforce.performanceservice.entity.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {
    List<PerformanceReview> findByEmployeeId(Long employeeId);
    Optional<PerformanceReview> findByEmployeeIdAndReviewYear(Long employeeId, Integer reviewYear);
}
