package com.revworkforce.authemployee.repository;

import com.revworkforce.authemployee.entity.ActivityLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ActivityLogRepository extends JpaRepository<ActivityLog, Long> {
    List<ActivityLog> findByEmployeeIdOrderByPerformedAtDesc(Long employeeId);
}
