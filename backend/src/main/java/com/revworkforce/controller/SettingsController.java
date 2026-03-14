package com.revworkforce.controller;

import com.revworkforce.entity.CompanyHoliday;
import com.revworkforce.entity.Department;
import com.revworkforce.entity.Designation;
import com.revworkforce.entity.LeaveType;
import com.revworkforce.exception.ResourceNotFoundException;
import com.revworkforce.repository.CompanyHolidayRepository;
import com.revworkforce.repository.DepartmentRepository;
import com.revworkforce.repository.DesignationRepository;
import com.revworkforce.repository.LeaveTypeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/settings")
@RequiredArgsConstructor
public class SettingsController {

    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final CompanyHolidayRepository companyHolidayRepository;

    // ── Departments ──

    @GetMapping("/departments")
    public ResponseEntity<List<Department>> getAllDepartments() {
        return ResponseEntity.ok(departmentRepository.findAll());
    }

    @PostMapping("/departments")
    public ResponseEntity<Department> addDepartment(@RequestBody Department department) {
        department.setId(null);
        return new ResponseEntity<>(departmentRepository.save(department), HttpStatus.CREATED);
    }

    @PutMapping("/departments/{id}")
    public ResponseEntity<Department> updateDepartment(@PathVariable Long id, @RequestBody Department request) {
        Department dept = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        dept.setName(request.getName());
        dept.setIsActive(request.getIsActive());
        return ResponseEntity.ok(departmentRepository.save(dept));
    }

    // ── Designations ──

    @GetMapping("/designations")
    public ResponseEntity<List<Designation>> getAllDesignations() {
        return ResponseEntity.ok(designationRepository.findAll());
    }

    @PostMapping("/designations")
    public ResponseEntity<Designation> addDesignation(@RequestBody Designation designation) {
        designation.setId(null);
        return new ResponseEntity<>(designationRepository.save(designation), HttpStatus.CREATED);
    }

    @PutMapping("/designations/{id}")
    public ResponseEntity<Designation> updateDesignation(@PathVariable Long id, @RequestBody Designation request) {
        Designation desig = designationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found"));
        desig.setTitle(request.getTitle());
        desig.setIsActive(request.getIsActive());
        return ResponseEntity.ok(designationRepository.save(desig));
    }

    // ── Leave Types ──

    @GetMapping("/leave-types")
    public ResponseEntity<List<LeaveType>> getAllLeaveTypes() {
        return ResponseEntity.ok(leaveTypeRepository.findAll());
    }

    @PostMapping("/leave-types")
    public ResponseEntity<LeaveType> addLeaveType(@RequestBody LeaveType leaveType) {
        leaveType.setId(null);
        return new ResponseEntity<>(leaveTypeRepository.save(leaveType), HttpStatus.CREATED);
    }

    @PutMapping("/leave-types/{id}")
    public ResponseEntity<LeaveType> updateLeaveType(@PathVariable Long id, @RequestBody LeaveType request) {
        LeaveType lt = leaveTypeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave type not found"));
        lt.setName(request.getName());
        lt.setDefaultQuota(request.getDefaultQuota());
        lt.setIsActive(request.getIsActive());
        return ResponseEntity.ok(leaveTypeRepository.save(lt));
    }

    // ── Company Holidays ──

    @GetMapping("/holidays")
    public ResponseEntity<List<CompanyHoliday>> getAllHolidays(@RequestParam(required = false) Integer year) {
        if (year != null) {
            return ResponseEntity.ok(companyHolidayRepository.findByYear(year));
        }
        return ResponseEntity.ok(companyHolidayRepository.findAll());
    }

    @PostMapping("/holidays")
    public ResponseEntity<CompanyHoliday> addHoliday(@RequestBody CompanyHoliday holiday) {
        holiday.setId(null);
        if (holiday.getYear() == null && holiday.getDate() != null) {
            holiday.setYear(holiday.getDate().getYear());
        }
        return new ResponseEntity<>(companyHolidayRepository.save(holiday), HttpStatus.CREATED);
    }

    @DeleteMapping("/holidays/{id}")
    public ResponseEntity<Void> deleteHoliday(@PathVariable Long id) {
        if (!companyHolidayRepository.existsById(id)) {
            throw new ResourceNotFoundException("Holiday not found");
        }
        companyHolidayRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
