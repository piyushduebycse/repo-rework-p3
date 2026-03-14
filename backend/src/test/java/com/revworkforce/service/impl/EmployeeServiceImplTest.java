package com.revworkforce.service.impl;

import com.revworkforce.dto.response.EmployeeResponse;
import com.revworkforce.entity.Department;
import com.revworkforce.entity.Designation;
import com.revworkforce.entity.Employee;
import com.revworkforce.entity.Role;
import com.revworkforce.repository.EmployeeRepository;
import com.revworkforce.service.interfaces.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private AuthService authService;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private Employee employee1;
    private Employee employee2;

    @BeforeEach
    void setUp() {
        Department dept = new Department();
        dept.setId(1L);
        dept.setName("Engineering");

        Designation desig = new Designation();
        desig.setId(1L);
        desig.setTitle("Software Engineer");

        employee1 = new Employee();
        employee1.setId(1L);
        employee1.setFirstName("Alice");
        employee1.setLastName("Smith");
        employee1.setEmail("alice@test.com");
        employee1.setRole(Role.EMPLOYEE);
        employee1.setDepartment(dept);
        employee1.setDesignation(desig);
        employee1.setIsActive(true);
        employee1.setJoiningDate(LocalDate.now());
        employee1.setSalary(BigDecimal.valueOf(80000));

        employee2 = new Employee();
        employee2.setId(2L);
        employee2.setFirstName("Bob");
        employee2.setLastName("Jones");
        employee2.setEmail("bob@test.com");
        employee2.setRole(Role.MANAGER);
        employee2.setDepartment(dept);
        employee2.setDesignation(desig);
        employee2.setIsActive(true);
        employee2.setJoiningDate(LocalDate.now());
        employee2.setSalary(BigDecimal.valueOf(100000));
        
        employee1.setManager(employee2);
    }

    @Test
    void getProfile_Success() {
        when(authService.getCurrentUser()).thenReturn(employee1);

        EmployeeResponse response = employeeService.getProfile();

        assertEquals(employee1.getId(), response.getId());
        assertEquals(employee1.getFirstName(), response.getFirstName());
        assertEquals(employee1.getDepartment().getName(), response.getDepartmentName());
    }

    @Test
    void getDirectory_Success() {
        when(employeeRepository.findAll()).thenReturn(Arrays.asList(employee1, employee2));

        List<EmployeeResponse> directory = employeeService.getDirectory();

        assertEquals(2, directory.size());
    }
}
