package com.revworkforce.leaveperformance.service.interfaces;

import com.revworkforce.common.dto.request.FeedbackRequest;
import com.revworkforce.common.dto.request.ReviewRequest;
import com.revworkforce.common.dto.response.ReviewResponse;

import java.util.List;

public interface PerformanceService {
    ReviewResponse saveReviewDraft(ReviewRequest request);
    ReviewResponse submitReview(Long id);
    List<ReviewResponse> getMyReviews();
    List<ReviewResponse> getTeamReviews();
    ReviewResponse submitFeedback(Long id, FeedbackRequest request);
}
