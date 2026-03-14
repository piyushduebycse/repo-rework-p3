package com.revworkforce.service.impl;

import com.revworkforce.dto.request.EmployeeRequest;
import com.revworkforce.dto.response.AdminMetricsResponse;
import com.revworkforce.dto.response.EmployeeResponse;
import com.revworkforce.entity.*;
import com.revworkforce.exception.ResourceNotFoundException;
import com.revworkforce.repository.*;
import com.revworkforce.service.interfaces.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final LeaveApplicationRepository leaveApplicationRepository;
    private final PerformanceReviewRepository reviewRepository;
    private final PasswordEncoder passwordEncoder;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;

    @Override
    public List<EmployeeResponse> getAllEmployees(String department, String designation, Boolean isActive) {
        return employeeRepository.findAll().stream()
                .filter(e -> department == null || (e.getDepartment() != null && e.getDepartment().getName().equalsIgnoreCase(department)))
                .filter(e -> designation == null || (e.getDesignation() != null && e.getDesignation().getTitle().equalsIgnoreCase(designation)))
                .filter(e -> isActive == null || e.getIsActive().equals(isActive))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public EmployeeResponse addEmployee(EmployeeRequest request) {
        if (employeeRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already in use");
        }
        if (employeeRepository.findByEmployeeId(request.getEmployeeId()).isPresent()) {
            throw new RuntimeException("Employee ID already exists");
        }

        Department dept = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        
        Designation desig = designationRepository.findById(request.getDesignationId())
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found"));

        Employee manager = null;
        if (request.getManagerId() != null) {
            manager = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
        }

        Employee employee = Employee.builder()
                .employeeId(request.getEmployeeId())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .password(passwordEncoder.encode("Welcome@123")) // Default password
                .role(Role.valueOf(request.getRole()))
                .department(dept)
                .designation(desig)
                .manager(manager)
                .joiningDate(request.getJoiningDate())
                .salary(request.getSalary())
                .isActive(true)
                .build();

        Employee savedEmployee = employeeRepository.save(employee);

        int currentYear = java.time.LocalDate.now().getYear();
        List<LeaveType> leaveTypes = leaveTypeRepository.findAll();
        for (LeaveType leaveType : leaveTypes) {
            LeaveBalance balance = new LeaveBalance();
            balance.setEmployee(savedEmployee);
            balance.setLeaveType(leaveType);
            balance.setYear(currentYear);
            balance.setTotalDays(leaveType.getDefaultQuota());
            balance.setUsedDays(0);
            leaveBalanceRepository.save(balance);
        }

        return mapToResponse(savedEmployee);
    }

    @Override
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        // Allow update of specific fields
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setRole(Role.valueOf(request.getRole()));

        if (request.getDepartmentId() != null && (employee.getDepartment() == null || !employee.getDepartment().getId().equals(request.getDepartmentId()))) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
            employee.setDepartment(dept);
        }

        if (request.getDesignationId() != null && (employee.getDesignation() == null || !employee.getDesignation().getId().equals(request.getDesignationId()))) {
            Designation desig = designationRepository.findById(request.getDesignationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Designation not found"));
            employee.setDesignation(desig);
        }

        if (request.getManagerId() != null) {
            Employee manager = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
            employee.setManager(manager);
        } else {
            employee.setManager(null);
        }

        employee.setJoiningDate(request.getJoiningDate());
        employee.setSalary(request.getSalary());

        return mapToResponse(employeeRepository.save(employee));
    }

    @Override
    public void toggleEmployeeStatus(Long id, boolean isActive) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
                
        if (employee.getRole() == Role.ADMIN) {
            throw new RuntimeException("Cannot modify the active status of an Admin user");
        }
        
        employee.setIsActive(isActive);
        employeeRepository.save(employee);
    }

    @Override
    public AdminMetricsResponse getDashboardMetrics() {
        long totalEmployees = employeeRepository.count();
        long activeEmployees = employeeRepository.findAll().stream().filter(Employee::getIsActive).count();
        long inactiveEmployees = totalEmployees - activeEmployees;

        long pendingLeaves = leaveApplicationRepository.findAll().stream()
                .filter(l -> l.getStatus() == LeaveStatus.PENDING).count();

        long submittedReviews = reviewRepository.findAll().stream()
                .filter(r -> r.getStatus() == ReviewStatus.SUBMITTED).count();

        return AdminMetricsResponse.builder()
                .totalEmployees(totalEmployees)
                .activeEmployees(activeEmployees)
                .inactiveEmployees(inactiveEmployees)
                .pendingLeaves(pendingLeaves)
                .submittedReviews(submittedReviews)
                .build();
    }

    private EmployeeResponse mapToResponse(Employee employee) {
        return EmployeeResponse.builder()
                .id(employee.getId())
                .employeeId(employee.getEmployeeId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .email(employee.getEmail())
                .phone(employee.getPhone())
                .address(employee.getAddress())
                .emergencyContact(employee.getEmergencyContact())
                .role(employee.getRole().name())
                .departmentId(employee.getDepartment() != null ? employee.getDepartment().getId() : null)
                .departmentName(employee.getDepartment() != null ? employee.getDepartment().getName() : null)
                .designationId(employee.getDesignation() != null ? employee.getDesignation().getId() : null)
                .designationTitle(employee.getDesignation() != null ? employee.getDesignation().getTitle() : null)
                .managerId(employee.getManager() != null ? employee.getManager().getId() : null)
                .managerName(employee.getManager() != null ? employee.getManager().getFirstName() + " " + employee.getManager().getLastName() : null)
                .joiningDate(employee.getJoiningDate())
                .salary(employee.getSalary())
                .isActive(employee.getIsActive())
                .build();
    }
}
