package com.revworkforce.authemployee.repository;

import com.revworkforce.authemployee.entity.LeaveType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveTypeRepository extends JpaRepository<LeaveType, Long> {
}
