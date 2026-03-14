package com.revworkforce.repository;

import com.revworkforce.entity.CompanyHoliday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CompanyHolidayRepository extends JpaRepository<CompanyHoliday, Long> {
    List<CompanyHoliday> findByYear(Integer year);
}
