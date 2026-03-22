package com.revworkforce.performanceservice.service.impl;

import com.revworkforce.common.dto.EmployeeDTO;
import com.revworkforce.common.dto.request.GoalRequest;
import com.revworkforce.common.dto.request.GoalUpdate;
import com.revworkforce.common.dto.response.GoalResponse;
import com.revworkforce.common.exception.ResourceNotFoundException;
import com.revworkforce.common.security.UserPrincipal;
import com.revworkforce.performanceservice.client.EmployeeClient;
import com.revworkforce.performanceservice.entity.Goal;
import com.revworkforce.performanceservice.entity.GoalPriority;
import com.revworkforce.performanceservice.entity.GoalStatus;
import com.revworkforce.performanceservice.repository.GoalRepository;
import com.revworkforce.performanceservice.service.interfaces.GoalService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final EmployeeClient employeeClient;

    private EmployeeDTO getCurrentEmployee() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return employeeClient.getEmployeeById(principal.getId());
    }

    @Override
    public GoalResponse createGoal(GoalRequest request) {
        EmployeeDTO current = getCurrentEmployee();

        Goal goal = Goal.builder()
                .employeeId(current.getId())
                .description(request.getDescription())
                .deadline(request.getDeadline())
                .priority(request.getPriority() != null ? GoalPriority.valueOf(request.getPriority()) : GoalPriority.MEDIUM)
                .status(GoalStatus.NOT_STARTED)
                .progressPercentage(0)
                .build();

        return mapToResponse(goalRepository.save(goal), current);
    }

    @Override
    public List<GoalResponse> getMyGoals() {
        EmployeeDTO current = getCurrentEmployee();
        return goalRepository.findByEmployeeId(current.getId()).stream()
                .map(g -> mapToResponse(g, current))
                .collect(Collectors.toList());
    }

    @Override
    public List<GoalResponse> getTeamMemberGoals(Long employeeId) {
        EmployeeDTO manager = getCurrentEmployee();
        EmployeeDTO teamMember = employeeClient.getEmployeeById(employeeId);

        if (teamMember.getManagerId() == null || !teamMember.getManagerId().equals(manager.getId())) {
            throw new RuntimeException("Unauthorized: You are not the manager of this employee");
        }

        return goalRepository.findByEmployeeId(employeeId).stream()
                .map(g -> mapToResponse(g, teamMember))
                .collect(Collectors.toList());
    }

    @Override
    public GoalResponse updateGoal(Long id, GoalUpdate update) {
        EmployeeDTO current = getCurrentEmployee();
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        if (!goal.getEmployeeId().equals(current.getId())) {
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

        return mapToResponse(goalRepository.save(goal), current);
    }

    @Override
    public GoalResponse addComment(Long id, String comment) {
        EmployeeDTO manager = getCurrentEmployee();
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        EmployeeDTO goalOwner = employeeClient.getEmployeeById(goal.getEmployeeId());

        if (goalOwner.getManagerId() == null || !goalOwner.getManagerId().equals(manager.getId())) {
            throw new RuntimeException("Unauthorized: You are not this employee's manager");
        }

        goal.setManagerComment(comment);
        return mapToResponse(goalRepository.save(goal), goalOwner);
    }

    @Override
    public void deleteGoal(Long id) {
        EmployeeDTO current = getCurrentEmployee();
        Goal goal = goalRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        if (!goal.getEmployeeId().equals(current.getId())) {
            throw new RuntimeException("Unauthorized to delete this goal");
        }
        goalRepository.delete(goal);
    }

    private GoalResponse mapToResponse(Goal goal, EmployeeDTO employee) {
        String empName = employee != null ? employee.getFirstName() + " " + employee.getLastName() : "Unknown";
        return GoalResponse.builder()
                .id(goal.getId())
                .employeeId(goal.getEmployeeId())
                .employeeName(empName)
                .description(goal.getDescription())
                .deadline(goal.getDeadline())
                .priority(goal.getPriority().name())
                .status(goal.getStatus().name())
                .progressPercentage(goal.getProgressPercentage())
                .managerComment(goal.getManagerComment())
                .build();
    }
}
