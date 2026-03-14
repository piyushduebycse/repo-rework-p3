package com.revworkforce.repository;

import com.revworkforce.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByEmployeeIdOrderByPerformedAtDesc(Long employeeId);
}
