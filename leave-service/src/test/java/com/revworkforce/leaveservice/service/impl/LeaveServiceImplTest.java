package com.revworkforce.leaveservice.service.impl;

import com.revworkforce.common.dto.EmployeeDTO;
import com.revworkforce.common.dto.request.LeaveRequest;
import com.revworkforce.common.dto.response.LeaveApplicationResponse;
import com.revworkforce.common.exception.InsufficientLeaveBalanceException;
import com.revworkforce.common.exception.ResourceNotFoundException;
import com.revworkforce.leaveservice.client.EmployeeClient;
import com.revworkforce.leaveservice.entity.*;
import com.revworkforce.leaveservice.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import com.revworkforce.common.security.UserPrincipal;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveServiceImplTest {

    @Mock private LeaveApplicationRepository leaveApplicationRepository;
    @Mock private LeaveBalanceRepository leaveBalanceRepository;
    @Mock private LeaveTypeRepository leaveTypeRepository;
    @Mock private CompanyHolidayRepository companyHolidayRepository;
    @Mock private EmployeeClient employeeClient;

    @InjectMocks
    private LeaveServiceImpl leaveService;

    private EmployeeDTO employee;
    private LeaveType leaveType;
    private LeaveBalance leaveBalance;
    private UserPrincipal mockPrincipal;

    @BeforeEach
    void setUp() {
        employee = new EmployeeDTO();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setRole("EMPLOYEE");

        leaveType = new LeaveType();
        leaveType.setId(1L);
        leaveType.setName("Sick Leave");

        leaveBalance = new LeaveBalance();
        leaveBalance.setId(1L);
        leaveBalance.setEmployeeId(1L);
        leaveBalance.setLeaveType(leaveType);
        leaveBalance.setYear(LocalDate.now().getYear());
        leaveBalance.setTotalDays(10);
        leaveBalance.setUsedDays(2);

        // Mock the Spring Security context
        mockPrincipal = mock(UserPrincipal.class);
        when(mockPrincipal.getId()).thenReturn(1L);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mockPrincipal);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(employeeClient.getEmployeeById(1L)).thenReturn(employee);
    }

    @Test
    void applyLeave_Success() {
        LeaveRequest request = new LeaveRequest();
        request.setLeaveTypeId(1L);
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(2));
        request.setReason("Sick");

        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(leaveType));
        when(leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(anyLong(), anyLong(), anyInt()))
                .thenReturn(Optional.of(leaveBalance));

        LeaveApplication savedApplication = new LeaveApplication();
        savedApplication.setId(1L);
        savedApplication.setEmployeeId(1L);
        savedApplication.setLeaveType(leaveType);
        savedApplication.setStartDate(request.getStartDate());
        savedApplication.setEndDate(request.getEndDate());
        savedApplication.setStatus(LeaveStatus.PENDING);

        when(leaveApplicationRepository.save(any(LeaveApplication.class))).thenReturn(savedApplication);

        LeaveApplicationResponse response = leaveService.applyLeave(request);

        assertNotNull(response);
        assertEquals(LeaveStatus.PENDING.name(), response.getStatus());
        assertEquals(1L, response.getEmployeeId());
        verify(leaveApplicationRepository, times(1)).save(any(LeaveApplication.class));
    }

    @Test
    void applyLeave_InsufficientBalance() {
        leaveBalance.setUsedDays(9);

        LeaveRequest request = new LeaveRequest();
        request.setLeaveTypeId(1L);
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(2));
        request.setReason("Sick");

        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(leaveType));
        when(leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(anyLong(), anyLong(), anyInt()))
                .thenReturn(Optional.of(leaveBalance));

        assertThrows(InsufficientLeaveBalanceException.class, () -> leaveService.applyLeave(request));
        verify(leaveApplicationRepository, never()).save(any(LeaveApplication.class));
    }

    @Test
    void cancelLeave_ThrowsExceptionWhenNotOwner() {
        LeaveApplication application = new LeaveApplication();
        application.setId(1L);
        application.setEmployeeId(99L); // different employee
        application.setStatus(LeaveStatus.PENDING);

        when(leaveApplicationRepository.findById(1L)).thenReturn(Optional.of(application));

        assertThrows(RuntimeException.class, () -> leaveService.cancelLeave(1L));
        verify(leaveApplicationRepository, never()).save(any(LeaveApplication.class));
    }

    @Test
    void getHolidays_ReturnsCurrentYearHolidays() {
        int year = LocalDate.now().getYear();
        when(companyHolidayRepository.findByYear(year)).thenReturn(java.util.Collections.emptyList());

        var result = leaveService.getHolidays();

        assertNotNull(result);
        verify(companyHolidayRepository, times(1)).findByYear(year);
    }
}
