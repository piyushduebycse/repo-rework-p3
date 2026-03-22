package com.revworkforce.authemployee.service.impl;

import com.revworkforce.authemployee.entity.*;
import com.revworkforce.authemployee.repository.*;
import com.revworkforce.authemployee.service.AdminService;
import com.revworkforce.common.dto.request.EmployeeRequest;
import com.revworkforce.common.dto.response.AdminMetricsResponse;
import com.revworkforce.common.dto.response.EmployeeResponse;
import com.revworkforce.common.exception.BadRequestException;
import com.revworkforce.common.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AdminServiceImpl implements AdminService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final DesignationRepository designationRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<EmployeeResponse> getAllEmployees(String department, String designation, Boolean isActive) {
        List<Employee> employees = employeeRepository.findAll();

        return employees.stream()
                .filter(emp -> department == null || (emp.getDepartment() != null && emp.getDepartment().getName().equalsIgnoreCase(department)))
                .filter(emp -> designation == null || (emp.getDesignation() != null && emp.getDesignation().getTitle().equalsIgnoreCase(designation)))
                .filter(emp -> isActive == null || emp.getIsActive().equals(isActive))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public EmployeeResponse addEmployee(EmployeeRequest request) {
        if (employeeRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new BadRequestException("Email already exists");
        }

        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
        Designation designation = designationRepository.findById(request.getDesignationId())
                .orElseThrow(() -> new ResourceNotFoundException("Designation not found"));

        Employee manager = null;
        if (request.getManagerId() != null) {
            manager = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
        }

        Employee employee = new Employee();
        employee.setEmployeeId(request.getEmployeeId());
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPassword(passwordEncoder.encode("Welcome@123"));
        employee.setRole(Role.valueOf(request.getRole()));
        employee.setDepartment(department);
        employee.setDesignation(designation);
        employee.setManager(manager);
        employee.setJoiningDate(request.getJoiningDate() != null ? request.getJoiningDate() : LocalDate.now());
        employee.setSalary(request.getSalary());
        employee.setIsActive(true);

        employee = employeeRepository.save(employee);

        // Create leave balances for current year
        int currentYear = LocalDate.now().getYear();
        List<LeaveType> leaveTypes = leaveTypeRepository.findAll();
        for (LeaveType lt : leaveTypes) {
            LeaveBalance balance = new LeaveBalance();
            balance.setEmployee(employee);
            balance.setLeaveType(lt);
            balance.setYear(currentYear);
            balance.setTotalDays(lt.getDefaultQuota());
            balance.setUsedDays(0);
            leaveBalanceRepository.save(balance);
        }

        return mapToResponse(employee);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(Long id, EmployeeRequest request) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

        if (request.getFirstName() != null) employee.setFirstName(request.getFirstName());
        if (request.getLastName() != null) employee.setLastName(request.getLastName());
        if (request.getRole() != null) employee.setRole(Role.valueOf(request.getRole()));
        if (request.getSalary() != null) employee.setSalary(request.getSalary());

        if (request.getDepartmentId() != null) {
            Department dept = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Department not found"));
            employee.setDepartment(dept);
        }
        if (request.getDesignationId() != null) {
            Designation desig = designationRepository.findById(request.getDesignationId())
                    .orElseThrow(() -> new ResourceNotFoundException("Designation not found"));
            employee.setDesignation(desig);
        }
        if (request.getManagerId() != null) {
            Employee mgr = employeeRepository.findById(request.getManagerId())
                    .orElseThrow(() -> new ResourceNotFoundException("Manager not found"));
            employee.setManager(mgr);
        }

        employee = employeeRepository.save(employee);
        return mapToResponse(employee);
    }

    @Override
    @Transactional
    public void toggleEmployeeStatus(Long id, boolean isActive) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        if (employee.getRole() == Role.ADMIN) {
            throw new BadRequestException("Cannot deactivate admin");
        }
        employee.setIsActive(isActive);
        employeeRepository.save(employee);
    }

    @Override
    public AdminMetricsResponse getDashboardMetrics() {
        List<Employee> allEmployees = employeeRepository.findAll();
        long total = allEmployees.size();
        long active = allEmployees.stream().filter(Employee::getIsActive).count();
        long inactive = total - active;

        return AdminMetricsResponse.builder()
                .totalEmployees(total)
                .activeEmployees(active)
                .inactiveEmployees(inactive)
                .build();
    }

    private EmployeeResponse mapToResponse(Employee emp) {
        return EmployeeResponse.builder()
                .id(emp.getId())
                .employeeId(emp.getEmployeeId())
                .firstName(emp.getFirstName())
                .lastName(emp.getLastName())
                .email(emp.getEmail())
                .phone(emp.getPhone())
                .address(emp.getAddress())
                .emergencyContact(emp.getEmergencyContact())
                .role(emp.getRole().name())
                .departmentId(emp.getDepartment() != null ? emp.getDepartment().getId() : null)
                .departmentName(emp.getDepartment() != null ? emp.getDepartment().getName() : null)
                .designationId(emp.getDesignation() != null ? emp.getDesignation().getId() : null)
                .designationTitle(emp.getDesignation() != null ? emp.getDesignation().getTitle() : null)
                .managerId(emp.getManager() != null ? emp.getManager().getId() : null)
                .managerName(emp.getManager() != null ? emp.getManager().getFirstName() + " " + emp.getManager().getLastName() : null)
                .joiningDate(emp.getJoiningDate())
                .salary(emp.getSalary())
                .isActive(emp.getIsActive())
                .build();
    }
}
