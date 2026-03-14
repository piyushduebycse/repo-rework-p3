package com.revworkforce.service.impl;

import com.revworkforce.dto.request.LeaveRequest;
import com.revworkforce.dto.response.LeaveApplicationResponse;
import com.revworkforce.dto.response.LeaveBalanceResponse;
import com.revworkforce.entity.*;
import com.revworkforce.exception.InsufficientLeaveBalanceException;
import com.revworkforce.exception.ResourceNotFoundException;
import com.revworkforce.repository.CompanyHolidayRepository;
import com.revworkforce.repository.LeaveApplicationRepository;
import com.revworkforce.repository.LeaveBalanceRepository;
import com.revworkforce.repository.LeaveTypeRepository;
import com.revworkforce.service.interfaces.AuthService;
import com.revworkforce.service.interfaces.LeaveService;
import com.revworkforce.service.interfaces.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LeaveServiceImpl implements LeaveService {

    private final LeaveApplicationRepository leaveApplicationRepository;
    private final LeaveBalanceRepository leaveBalanceRepository;
    private final LeaveTypeRepository leaveTypeRepository;
    private final CompanyHolidayRepository companyHolidayRepository;
    private final AuthService authService;
    private final NotificationService notificationService;

    @Override
    public List<LeaveBalanceResponse> getBalances() {
        Employee current = authService.getCurrentUser();
        int currentYear = LocalDate.now().getYear();

        List<LeaveBalance> balances = leaveBalanceRepository.findByEmployeeIdAndYear(current.getId(), currentYear);

        return balances.stream().map(b -> LeaveBalanceResponse.builder()
                .id(b.getId())
                .leaveTypeId(b.getLeaveType().getId())
                .leaveTypeName(b.getLeaveType().getName())
                .totalDays(b.getTotalDays())
                .usedDays(b.getUsedDays())
                .remainingDays(b.getTotalDays() - b.getUsedDays())
                .build()).collect(Collectors.toList());
    }

    @Override
    public LeaveApplicationResponse applyLeave(LeaveRequest request) {
        Employee current = authService.getCurrentUser();
        int currentYear = request.getStartDate().getYear();

        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave Type not found"));

        LeaveBalance balance = leaveBalanceRepository
                .findByEmployeeIdAndLeaveTypeIdAndYear(current.getId(), leaveType.getId(), currentYear)
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance record not found for this year"));

        long requestedDays = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1; // Simplistic date diff, usually need to exclude weekends and holidays

        if (balance.getTotalDays() - balance.getUsedDays() < requestedDays) {
            throw new InsufficientLeaveBalanceException("Insufficient leave balance for type: " + leaveType.getName());
        }

        boolean isAdmin = current.getRole() == Role.ADMIN;
        LeaveApplication application = LeaveApplication.builder()
                .employee(current)
                .leaveType(leaveType)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .reason(request.getReason())
                .status(isAdmin ? LeaveStatus.APPROVED : LeaveStatus.PENDING)
                .managerComment(isAdmin ? "Auto-approved for System Admin" : null)
                .build();

        if (isAdmin) {
            balance.setUsedDays((int) (balance.getUsedDays() + requestedDays));
            leaveBalanceRepository.save(balance);
        }

        LeaveApplication saved = leaveApplicationRepository.save(application);

        // Notify Manager
        if (current.getManager() != null && !isAdmin) {
            notificationService.createNotification(
                    current.getManager().getId(),
                    current.getFirstName() + " has applied for leave.",
                    NotificationType.LEAVE_APPLIED,
                    saved.getId()
            );
        }

        return mapToResponse(saved);
    }

    @Override
    public List<LeaveApplicationResponse> getMyLeaves() {
        Employee current = authService.getCurrentUser();
        return leaveApplicationRepository.findByEmployeeId(current.getId())
                .stream().map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void cancelLeave(Long id) {
        Employee current = authService.getCurrentUser();
        LeaveApplication application = leaveApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave application not found"));

        if (!application.getEmployee().getId().equals(current.getId())) {
            throw new RuntimeException("Unauthorized to cancel this leave");
        }

        if (application.getStatus() == LeaveStatus.APPROVED) {
            // Refund the balance if it was already approved
            int currentYear = application.getStartDate().getYear();
            LeaveBalance balance = leaveBalanceRepository
                    .findByEmployeeIdAndLeaveTypeIdAndYear(current.getId(), application.getLeaveType().getId(), currentYear)
                    .orElseThrow(() -> new ResourceNotFoundException("Balance not found"));
            
            long requestedDays = ChronoUnit.DAYS.between(application.getStartDate(), application.getEndDate()) + 1;
            balance.setUsedDays((int) (balance.getUsedDays() - requestedDays));
            leaveBalanceRepository.save(balance);
        } else if (application.getStatus() != LeaveStatus.PENDING) {
            throw new RuntimeException("Can only cancel PENDING or APPROVED leaves");
        }

        application.setStatus(LeaveStatus.CANCELLED);
        leaveApplicationRepository.save(application);
    }

    @Override
    public List<CompanyHoliday> getHolidays() {
        int currentYear = LocalDate.now().getYear();
        return companyHolidayRepository.findByYear(currentYear);
    }

    @Override
    public List<LeaveApplicationResponse> getTeamLeaves() {
        Employee currentManager = authService.getCurrentUser();
        // Since we don't have a direct query in repository for managerId, let's fetch all and filter or add it to repository
        // Alternatively, add a custom query to LeaveApplicationRepository: findByEmployeeManagerId
        // For simplicity now, assuming an admin or getting all leaves if manager. A proper implementation would fetch only reportees leaves.
        return leaveApplicationRepository.findAll().stream()
                .filter(l -> l.getEmployee().getManager() != null && l.getEmployee().getManager().getId().equals(currentManager.getId()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public LeaveApplicationResponse approveLeave(Long id, String comment) {
        LeaveApplication application = leaveApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave application not found"));

        application.setStatus(LeaveStatus.APPROVED);
        application.setManagerComment(comment);
        LeaveApplication saved = leaveApplicationRepository.save(application);

        // Deduct from balance
        int currentYear = saved.getStartDate().getYear();
        LeaveBalance balance = leaveBalanceRepository
                .findByEmployeeIdAndLeaveTypeIdAndYear(saved.getEmployee().getId(), saved.getLeaveType().getId(), currentYear)
                .orElseThrow(() -> new ResourceNotFoundException("Balance not found"));
        
        long requestedDays = ChronoUnit.DAYS.between(saved.getStartDate(), saved.getEndDate()) + 1;
        balance.setUsedDays((int) (balance.getUsedDays() + requestedDays));
        leaveBalanceRepository.save(balance);

        notificationService.createNotification(
                saved.getEmployee().getId(),
                "Your leave has been approved.",
                NotificationType.LEAVE_APPROVED,
                saved.getId()
        );

        return mapToResponse(saved);
    }

    @Override
    public LeaveApplicationResponse rejectLeave(Long id, String comment) {
        if (comment == null || comment.trim().isEmpty()) {
            throw new RuntimeException("Comment is required when rejecting a leave");
        }

        LeaveApplication application = leaveApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave application not found"));

        application.setStatus(LeaveStatus.REJECTED);
        application.setManagerComment(comment);
        LeaveApplication saved = leaveApplicationRepository.save(application);

        notificationService.createNotification(
                saved.getEmployee().getId(),
                "Your leave has been rejected.",
                NotificationType.LEAVE_REJECTED,
                saved.getId()
        );

        return mapToResponse(saved);
    }

    private LeaveApplicationResponse mapToResponse(LeaveApplication application) {
        return LeaveApplicationResponse.builder()
                .id(application.getId())
                .employeeId(application.getEmployee().getId())
                .employeeName(application.getEmployee().getFirstName() + " " + application.getEmployee().getLastName())
                .leaveTypeId(application.getLeaveType().getId())
                .leaveTypeName(application.getLeaveType().getName())
                .startDate(application.getStartDate())
                .endDate(application.getEndDate())
                .reason(application.getReason())
                .status(application.getStatus().name())
                .managerComment(application.getManagerComment())
                .appliedAt(application.getAppliedAt())
                .build();
    }
}
