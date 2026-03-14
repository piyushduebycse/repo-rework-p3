package com.revworkforce.authemployee.service.impl;

import com.revworkforce.authemployee.entity.Employee;
import com.revworkforce.authemployee.repository.EmployeeRepository;
import com.revworkforce.authemployee.service.EmployeeService;
import com.revworkforce.common.dto.response.EmployeeResponse;
import com.revworkforce.common.exception.ResourceNotFoundException;
import com.revworkforce.common.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;

    @Override
    public EmployeeResponse getProfile() {
        Long userId = getCurrentUserId();
        Employee employee = employeeRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        return mapToResponse(employee);
    }

    @Override
    public EmployeeResponse getManager() {
        Long userId = getCurrentUserId();
        Employee employee = employeeRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
        if (employee.getManager() == null) {
            throw new ResourceNotFoundException("No manager assigned");
        }
        return mapToResponse(employee.getManager());
    }

    @Override
    public List<EmployeeResponse> getDirectory() {
        return employeeRepository.findAll().stream()
                .filter(Employee::getIsActive)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private Long getCurrentUserId() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getId();
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
