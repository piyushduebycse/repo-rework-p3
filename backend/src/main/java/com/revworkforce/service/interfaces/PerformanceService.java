package com.revworkforce.service.interfaces;

import com.revworkforce.dto.request.FeedbackRequest;
import com.revworkforce.dto.request.ReviewRequest;
import com.revworkforce.dto.response.ReviewResponse;

import java.util.List;

public interface PerformanceService {
    ReviewResponse saveReviewDraft(ReviewRequest request);
    ReviewResponse submitReview(Long id);
    List<ReviewResponse> getMyReviews();
    
    // Manager
    List<ReviewResponse> getTeamReviews();
    ReviewResponse submitFeedback(Long id, FeedbackRequest request);
}
