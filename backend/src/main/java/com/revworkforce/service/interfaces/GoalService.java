package com.revworkforce.service.interfaces;

import com.revworkforce.dto.request.GoalRequest;
import com.revworkforce.dto.request.GoalUpdate;
import com.revworkforce.dto.response.GoalResponse;

import java.util.List;

public interface GoalService {
    GoalResponse createGoal(GoalRequest request);
    List<GoalResponse> getMyGoals();
    GoalResponse updateGoal(Long id, GoalUpdate update);
    
    // Manager
    List<GoalResponse> getTeamMemberGoals(Long employeeId);
    GoalResponse addComment(Long id, String comment);
    
    void deleteGoal(Long id);
}
