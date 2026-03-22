package com.revworkforce.authemployee.repository;

import com.revworkforce.authemployee.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
}
