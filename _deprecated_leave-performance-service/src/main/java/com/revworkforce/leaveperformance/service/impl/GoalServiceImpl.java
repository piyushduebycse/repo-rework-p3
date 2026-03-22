package com.revworkforce.leaveperformance.service.impl;

import com.revworkforce.common.dto.request.GoalRequest;
import com.revworkforce.common.dto.request.GoalUpdate;
import com.revworkforce.common.dto.response.GoalResponse;
import com.revworkforce.common.exception.ResourceNotFoundException;
import com.revworkforce.common.security.UserPrincipal;
import com.revworkforce.leaveperformance.entity.Employee;
import com.revworkforce.leaveperformance.entity.Goal;
import com.revworkforce.leaveperformance.entity.GoalPriority;
import com.revworkforce.leaveperformance.entity.GoalStatus;
import com.revworkforce.leaveperformance.repository.EmployeeRepository;
import com.revworkforce.leaveperformance.repository.GoalRepository;
import com.revworkforce.leaveperformance.service.interfaces.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final EmployeeRepository employeeRepository;

    private Employee getCurrentEmployee() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return employeeRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
    }

    @Override
    public GoalResponse createGoal(GoalRequest request) {
        Employee current = getCurrentEmployee();

        Goal goal = Goal.builder()
                .employee(current)
                .description(request.getDescription())
                .deadline(request.getDeadline())
                .priority(request.getPriority() != null ? GoalPriority.valueOf(request.getPriority()) : GoalPriority.MEDIUM)
                .status(GoalStatus.NOT_STARTED)
                .progressPercentage(0)
                .build();

        return mapToResponse(goalRepository.save(goal));
    }

    @Override
    public List<GoalResponse> getMyGoals() {
        Employee current = getCurrentEmployee();
        return goalRepository.findByEmployeeId(current.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public GoalResponse updateGoal(Long id, GoalUpdate update) {
        Employee current = getCurrentEmployee();
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        if (!goal.getEmployee().getId().equals(current.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        if (update.getStatus() != null) {
            goal.setStatus(GoalStatus.valueOf(update.getStatus()));
            if (goal.getStatus() == GoalStatus.COMPLETED) {
                goal.setProgressPercentage(100);
            }
        }
        if (update.getProgressPercentage() != null) {
            goal.setProgressPercentage(update.getProgressPercentage());
            if (goal.getProgressPercentage() == 100) {
                goal.setStatus(GoalStatus.COMPLETED);
            }
        }

        return mapToResponse(goalRepository.save(goal));
    }

    @Override
    public List<GoalResponse> getTeamMemberGoals(Long employeeId) {
        Employee manager = getCurrentEmployee();
        return goalRepository.findByEmployeeId(employeeId).stream()
                .filter(g -> g.getEmployee().getManager() != null && g.getEmployee().getManager().getId().equals(manager.getId()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public GoalResponse addComment(Long id, String comment) {
        Employee manager = getCurrentEmployee();
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        if (goal.getEmployee().getManager() == null || !goal.getEmployee().getManager().getId().equals(manager.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        goal.setManagerComment(comment);
        return mapToResponse(goalRepository.save(goal));
    }

    @Override
    public void deleteGoal(Long id) {
        Employee current = getCurrentEmployee();
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        if (!goal.getEmployee().getId().equals(current.getId())) {
            throw new RuntimeException("Unauthorized to delete this goal");
        }
        goalRepository.delete(goal);
    }

    private GoalResponse mapToResponse(Goal goal) {
        return GoalResponse.builder()
                .id(goal.getId())
                .employeeId(goal.getEmployee().getId())
                .employeeName(goal.getEmployee().getFirstName() + " " + goal.getEmployee().getLastName())
                .description(goal.getDescription())
                .deadline(goal.getDeadline())
                .priority(goal.getPriority().name())
                .status(goal.getStatus().name())
                .progressPercentage(goal.getProgressPercentage())
                .managerComment(goal.getManagerComment())
                .build();
    }
}
