package com.revworkforce.authemployee.repository;

import com.revworkforce.authemployee.entity.LeaveApplication;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LeaveApplicationRepository extends JpaRepository<LeaveApplication, Long> {
}
