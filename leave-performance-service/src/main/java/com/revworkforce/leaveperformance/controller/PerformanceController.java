package com.revworkforce.leaveperformance.controller;

import com.revworkforce.common.dto.request.ReviewRequest;
import com.revworkforce.common.dto.response.ReviewResponse;
import com.revworkforce.leaveperformance.service.interfaces.PerformanceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/performance")
@RequiredArgsConstructor
public class PerformanceController {

    private final PerformanceService performanceService;

    @PostMapping("/review")
    public ResponseEntity<ReviewResponse> saveReviewDraft(@Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(performanceService.saveReviewDraft(request));
    }

    @PutMapping("/review/{id}/submit")
    public ResponseEntity<ReviewResponse> submitReview(@PathVariable Long id) {
        return ResponseEntity.ok(performanceService.submitReview(id));
    }

    @GetMapping("/reviews")
    public ResponseEntity<List<ReviewResponse>> getMyReviews() {
        return ResponseEntity.ok(performanceService.getMyReviews());
    }
}
