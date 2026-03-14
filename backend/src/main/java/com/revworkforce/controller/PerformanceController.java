package com.revworkforce.controller;

import com.revworkforce.dto.request.ReviewRequest;
import com.revworkforce.dto.response.ReviewResponse;
import com.revworkforce.service.interfaces.PerformanceService;
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
