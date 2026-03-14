package com.revworkforce.service.impl;

import com.revworkforce.dto.response.EmployeeResponse;
import com.revworkforce.entity.Employee;
import com.revworkforce.exception.ResourceNotFoundException;
import com.revworkforce.repository.EmployeeRepository;
import com.revworkforce.service.interfaces.AuthService;
import com.revworkforce.service.interfaces.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final AuthService authService;

    @Override
    public EmployeeResponse getProfile() {
        Employee current = authService.getCurrentUser();
        return mapToResponse(current);
    }

    @Override
    public EmployeeResponse getManager() {
        Employee current = authService.getCurrentUser();
        if (current.getManager() == null) {
            throw new ResourceNotFoundException("Manager not found for specific employee");
        }
        return mapToResponse(current.getManager());
    }

    @Override
    public List<EmployeeResponse> getDirectory() {
        return employeeRepository.findAll().stream()
                .filter(Employee::getIsActive)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
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
