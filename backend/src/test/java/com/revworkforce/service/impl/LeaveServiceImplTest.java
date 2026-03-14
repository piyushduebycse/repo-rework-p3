package com.revworkforce.service.impl;

import com.revworkforce.dto.request.LeaveRequest;
import com.revworkforce.dto.response.LeaveApplicationResponse;
import com.revworkforce.entity.*;
import com.revworkforce.exception.InsufficientLeaveBalanceException;
import com.revworkforce.exception.ResourceNotFoundException;
import com.revworkforce.repository.CompanyHolidayRepository;
import com.revworkforce.repository.LeaveApplicationRepository;
import com.revworkforce.repository.LeaveBalanceRepository;
import com.revworkforce.repository.LeaveTypeRepository;
import com.revworkforce.service.interfaces.AuthService;
import com.revworkforce.service.interfaces.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LeaveServiceImplTest {

    @Mock
    private LeaveApplicationRepository leaveApplicationRepository;
    @Mock
    private LeaveBalanceRepository leaveBalanceRepository;
    @Mock
    private LeaveTypeRepository leaveTypeRepository;
    @Mock
    private AuthService authService;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private LeaveServiceImpl leaveService;

    private Employee employee;
    private LeaveType leaveType;
    private LeaveBalance leaveBalance;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("John");
        employee.setLastName("Doe");

        leaveType = new LeaveType();
        leaveType.setId(1L);
        leaveType.setName("Sick Leave");

        leaveBalance = new LeaveBalance();
        leaveBalance.setId(1L);
        leaveBalance.setEmployee(employee);
        leaveBalance.setLeaveType(leaveType);
        leaveBalance.setYear(LocalDate.now().getYear());
        leaveBalance.setTotalDays(10);
        leaveBalance.setUsedDays(2);
    }

    @Test
    void applyLeave_Success() {
        LeaveRequest request = new LeaveRequest();
        request.setLeaveTypeId(1L);
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(2)); // 2 days
        request.setReason("Sick");

        when(authService.getCurrentUser()).thenReturn(employee);
        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(leaveType));
        when(leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(anyLong(), anyLong(), anyInt()))
                .thenReturn(Optional.of(leaveBalance));

        LeaveApplication savedApplication = new LeaveApplication();
        savedApplication.setId(1L);
        savedApplication.setEmployee(employee);
        savedApplication.setLeaveType(leaveType);
        savedApplication.setStartDate(request.getStartDate());
        savedApplication.setEndDate(request.getEndDate());
        savedApplication.setStatus(LeaveStatus.PENDING);

        when(leaveApplicationRepository.save(any(LeaveApplication.class))).thenReturn(savedApplication);

        LeaveApplicationResponse response = leaveService.applyLeave(request);

        assertNotNull(response);
        assertEquals(LeaveStatus.PENDING.name(), response.getStatus());
        verify(leaveApplicationRepository, times(1)).save(any(LeaveApplication.class));
    }

    @Test
    void applyLeave_InsufficientBalance() {
        leaveBalance.setUsedDays(9); // Only 1 day remaining, but requesting 2

        LeaveRequest request = new LeaveRequest();
        request.setLeaveTypeId(1L);
        request.setStartDate(LocalDate.now().plusDays(1));
        request.setEndDate(LocalDate.now().plusDays(2)); // 2 days
        request.setReason("Sick");

        when(authService.getCurrentUser()).thenReturn(employee);
        when(leaveTypeRepository.findById(1L)).thenReturn(Optional.of(leaveType));
        when(leaveBalanceRepository.findByEmployeeIdAndLeaveTypeIdAndYear(anyLong(), anyLong(), anyInt()))
                .thenReturn(Optional.of(leaveBalance));

        assertThrows(InsufficientLeaveBalanceException.class, () -> leaveService.applyLeave(request));
        verify(leaveApplicationRepository, never()).save(any(LeaveApplication.class));
    }
}
