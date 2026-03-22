package com.revworkforce.leaveservice.service.impl;

import com.revworkforce.common.dto.EmployeeDTO;
import com.revworkforce.common.dto.request.LeaveRequest;
import com.revworkforce.common.dto.response.LeaveApplicationResponse;
import com.revworkforce.common.dto.response.LeaveBalanceResponse;
import com.revworkforce.common.exception.InsufficientLeaveBalanceException;
import com.revworkforce.common.exception.ResourceNotFoundException;
import com.revworkforce.common.security.UserPrincipal;
import com.revworkforce.leaveservice.client.EmployeeClient;
import com.revworkforce.leaveservice.entity.*;
import com.revworkforce.leaveservice.repository.*;
import com.revworkforce.leaveservice.service.interfaces.LeaveService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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
    private final EmployeeClient employeeClient;

    private EmployeeDTO getCurrentEmployee() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return employeeClient.getEmployeeById(principal.getId());
    }

    @Override
    public List<LeaveBalanceResponse> getBalances() {
        EmployeeDTO current = getCurrentEmployee();
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
        EmployeeDTO current = getCurrentEmployee();
        int currentYear = request.getStartDate().getYear();

        LeaveType leaveType = leaveTypeRepository.findById(request.getLeaveTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Leave Type not found"));

        LeaveBalance balance = leaveBalanceRepository
                .findByEmployeeIdAndLeaveTypeIdAndYear(current.getId(), leaveType.getId(), currentYear)
                .orElseThrow(() -> new ResourceNotFoundException("Leave balance record not found for this year"));

        long requestedDays = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate()) + 1;

        if (balance.getTotalDays() - balance.getUsedDays() < requestedDays) {
            throw new InsufficientLeaveBalanceException("Insufficient leave balance for type: " + leaveType.getName());
        }

        boolean isAdmin = "ADMIN".equals(current.getRole());
        LeaveApplication application = LeaveApplication.builder()
                .employeeId(current.getId())
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

        // Notify Manager via Feign lookup if needed (omitted for brevity)
        if (current.getManagerId() != null && !isAdmin) {
            createNotification(
                    current.getManagerId(),
                    current.getFirstName() + " has applied for leave.",
                    NotificationType.LEAVE_APPLIED,
                    saved.getId()
            );
        }

        return mapToResponse(saved, current);
    }

    @Override
    public List<LeaveApplicationResponse> getMyLeaves() {
        EmployeeDTO current = getCurrentEmployee();
        return leaveApplicationRepository.findByEmployeeId(current.getId())
                .stream().map(l -> mapToResponse(l, current))
                .collect(Collectors.toList());
    }

    @Override
    public void cancelLeave(Long id) {
        EmployeeDTO current = getCurrentEmployee();
        LeaveApplication application = leaveApplicationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Leave application not found"));

        if (!application.getEmployeeId().equals(current.getId())) {
            throw new RuntimeException("Unauthorized to cancel this leave");
        }

        if (application.getStatus() == LeaveStatus.APPROVED) {
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
        EmployeeDTO currentManager = getCurrentEmployee();
        
        // Find all employees reporting to this manager via OpenFeign
        List<EmployeeDTO> teamMembers = employeeClient.getEmployeesByManagerId(currentManager.getId());
        List<Long> teamIds = teamMembers.stream().map(EmployeeDTO::getId).collect(Collectors.toList());

        return leaveApplicationRepository.findAll().stream()
                .filter(l -> teamIds.contains(l.getEmployeeId()))
                .map(l -> {
                    EmployeeDTO emp = teamMembers.stream().filter(t -> t.getId().equals(l.getEmployeeId())).findFirst().orElse(null);
                    return mapToResponse(l, emp);
                })
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
                .findByEmployeeIdAndLeaveTypeIdAndYear(saved.getEmployeeId(), saved.getLeaveType().getId(), currentYear)
                .orElseThrow(() -> new ResourceNotFoundException("Balance not found"));

        long requestedDays = ChronoUnit.DAYS.between(saved.getStartDate(), saved.getEndDate()) + 1;
        balance.setUsedDays((int) (balance.getUsedDays() + requestedDays));
        leaveBalanceRepository.save(balance);

        // Notification triggered via notification-service (cross-service)

        EmployeeDTO emp = employeeClient.getEmployeeById(saved.getEmployeeId());
        return mapToResponse(saved, emp);
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

        // Notification triggered via notification-service (cross-service)

        EmployeeDTO emp = employeeClient.getEmployeeById(saved.getEmployeeId());
        return mapToResponse(saved, emp);
    }

    private LeaveApplicationResponse mapToResponse(LeaveApplication application, EmployeeDTO employee) {
        String empName = employee != null ? employee.getFirstName() + " " + employee.getLastName() : "Unknown";
        return LeaveApplicationResponse.builder()
                .id(application.getId())
                .employeeId(application.getEmployeeId())
                .employeeName(empName)
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
