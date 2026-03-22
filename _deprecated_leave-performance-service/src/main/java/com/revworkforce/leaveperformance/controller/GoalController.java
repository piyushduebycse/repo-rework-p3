package com.revworkforce.leaveperformance.controller;

import com.revworkforce.common.dto.request.GoalRequest;
import com.revworkforce.common.dto.request.GoalUpdate;
import com.revworkforce.common.dto.response.GoalResponse;
import com.revworkforce.leaveperformance.service.interfaces.GoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping
    public ResponseEntity<GoalResponse> createGoal(@Valid @RequestBody GoalRequest request) {
        return new ResponseEntity<>(goalService.createGoal(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<GoalResponse>> getMyGoals() {
        return ResponseEntity.ok(goalService.getMyGoals());
    }

    @PutMapping("/{id}")
    public ResponseEntity<GoalResponse> updateGoal(@PathVariable Long id, @RequestBody GoalUpdate update) {
        return ResponseEntity.ok(goalService.updateGoal(id, update));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGoal(@PathVariable Long id) {
        goalService.deleteGoal(id);
        return ResponseEntity.noContent().build();
    }
}
