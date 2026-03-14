package com.revworkforce.controller;

import com.revworkforce.dto.request.FeedbackRequest;
import com.revworkforce.dto.response.EmployeeResponse;
import com.revworkforce.dto.response.GoalResponse;
import com.revworkforce.dto.response.LeaveApplicationResponse;
import com.revworkforce.dto.response.ReviewResponse;
import com.revworkforce.service.interfaces.EmployeeService;
import com.revworkforce.service.interfaces.GoalService;
import com.revworkforce.service.interfaces.LeaveService;
import com.revworkforce.service.interfaces.PerformanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/manager")
@RequiredArgsConstructor
public class ManagerController {

    private final LeaveService leaveService;
    private final PerformanceService performanceService;
    private final GoalService goalService;
    private final EmployeeService employeeService; // if needed to get team list


    @GetMapping("/leaves/team")
    public ResponseEntity<List<LeaveApplicationResponse>> getTeamLeaves() {
        return ResponseEntity.ok(leaveService.getTeamLeaves());
    }

    @PutMapping("/leaves/{id}/approve")
    public ResponseEntity<LeaveApplicationResponse> approveLeave(@PathVariable Long id, @RequestBody(required = false) String comment) {
        return ResponseEntity.ok(leaveService.approveLeave(id, comment));
    }

    @PutMapping("/leaves/{id}/reject")
    public ResponseEntity<LeaveApplicationResponse> rejectLeave(@PathVariable Long id, @RequestBody String comment) {
        return ResponseEntity.ok(leaveService.rejectLeave(id, comment));
    }

    @GetMapping("/performance/reviews")
    public ResponseEntity<List<ReviewResponse>> getTeamReviews() {
        return ResponseEntity.ok(performanceService.getTeamReviews());
    }

    @PutMapping("/performance/reviews/{id}/feedback")
    public ResponseEntity<ReviewResponse> submitFeedback(@PathVariable Long id, @Valid @RequestBody FeedbackRequest request) {
        return ResponseEntity.ok(performanceService.submitFeedback(id, request));
    }

    @GetMapping("/goals/{empId}")
    public ResponseEntity<List<GoalResponse>> getTeamMemberGoals(@PathVariable Long empId) {
        return ResponseEntity.ok(goalService.getTeamMemberGoals(empId));
    }

    @PutMapping("/goals/{id}/comment")
    public ResponseEntity<GoalResponse> addGoalComment(@PathVariable Long id, @RequestBody String comment) {
        return ResponseEntity.ok(goalService.addComment(id, comment));
    }
}
