package com.revworkforce.authemployee.repository;

import com.revworkforce.authemployee.entity.PerformanceReview;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceReviewRepository extends JpaRepository<PerformanceReview, Long> {
}
