package com.revworkforce.leaveperformance.repository;

import com.revworkforce.leaveperformance.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {
}
