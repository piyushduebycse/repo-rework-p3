package com.revworkforce.service.impl;

import com.revworkforce.dto.request.GoalRequest;
import com.revworkforce.dto.request.GoalUpdate;
import com.revworkforce.dto.response.GoalResponse;
import com.revworkforce.entity.Employee;
import com.revworkforce.entity.Goal;
import com.revworkforce.entity.GoalPriority;
import com.revworkforce.entity.GoalStatus;
import com.revworkforce.exception.ResourceNotFoundException;
import com.revworkforce.repository.GoalRepository;
import com.revworkforce.service.interfaces.AuthService;
import com.revworkforce.service.interfaces.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final AuthService authService;

    @Override
    public GoalResponse createGoal(GoalRequest request) {
        Employee current = authService.getCurrentUser();

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
        Employee current = authService.getCurrentUser();
        return goalRepository.findByEmployeeId(current.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public GoalResponse updateGoal(Long id, GoalUpdate update) {
        Employee current = authService.getCurrentUser();
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
        Employee manager = authService.getCurrentUser();
        // Validation: verify that the employee belongs to this manager
        return goalRepository.findByEmployeeId(employeeId).stream()
                .filter(g -> g.getEmployee().getManager() != null && g.getEmployee().getManager().getId().equals(manager.getId()))
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public GoalResponse addComment(Long id, String comment) {
        Employee manager = authService.getCurrentUser();
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
        Employee current = authService.getCurrentUser();
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
