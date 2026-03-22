package com.revworkforce.leaveservice.repository;

import com.revworkforce.leaveservice.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {
}
