package com.revworkforce.leaveperformance.service.interfaces;

import com.revworkforce.common.dto.request.GoalRequest;
import com.revworkforce.common.dto.request.GoalUpdate;
import com.revworkforce.common.dto.response.GoalResponse;

import java.util.List;

public interface GoalService {
    GoalResponse createGoal(GoalRequest request);
    List<GoalResponse> getMyGoals();
    GoalResponse updateGoal(Long id, GoalUpdate update);
    List<GoalResponse> getTeamMemberGoals(Long employeeId);
    GoalResponse addComment(Long id, String comment);
    void deleteGoal(Long id);
}
