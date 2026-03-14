package com.revworkforce.service.impl;

import com.revworkforce.dto.request.FeedbackRequest;
import com.revworkforce.dto.request.ReviewRequest;
import com.revworkforce.dto.response.ReviewResponse;
import com.revworkforce.entity.Employee;
import com.revworkforce.entity.NotificationType;
import com.revworkforce.entity.PerformanceReview;
import com.revworkforce.entity.ReviewStatus;
import com.revworkforce.exception.ResourceNotFoundException;
import com.revworkforce.repository.PerformanceReviewRepository;
import com.revworkforce.service.interfaces.AuthService;
import com.revworkforce.service.interfaces.NotificationService;
import com.revworkforce.service.interfaces.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerformanceServiceImpl implements PerformanceService {

    private final PerformanceReviewRepository reviewRepository;
    private final AuthService authService;
    private final NotificationService notificationService;

    @Override
    public ReviewResponse saveReviewDraft(ReviewRequest request) {
        Employee current = authService.getCurrentUser();

        Optional<PerformanceReview> existing = reviewRepository.findByEmployeeIdAndReviewYear(current.getId(), request.getReviewYear());
        PerformanceReview review;

        if (existing.isPresent()) {
            review = existing.get();
            if (review.getStatus() != ReviewStatus.DRAFT) {
                throw new RuntimeException("Cannot modify a review that is already submitted or reviewed");
            }
        } else {
            review = new PerformanceReview();
            review.setEmployee(current);
            review.setReviewYear(request.getReviewYear());
            review.setStatus(ReviewStatus.DRAFT);
        }

        review.setKeyDeliverables(request.getKeyDeliverables());
        review.setAccomplishments(request.getAccomplishments());
        review.setAreasOfImprovement(request.getAreasOfImprovement());
        review.setSelfAssessmentRating(request.getSelfAssessmentRating());

        return mapToResponse(reviewRepository.save(review));
    }

    @Override
    public ReviewResponse submitReview(Long id) {
        Employee current = authService.getCurrentUser();
        PerformanceReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getEmployee().getId().equals(current.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        review.setStatus(ReviewStatus.SUBMITTED);
        review.setSubmittedAt(LocalDateTime.now());
        PerformanceReview saved = reviewRepository.save(review);

        if (current.getManager() != null) {
            notificationService.createNotification(
                    current.getManager().getId(),
                    current.getFirstName() + " has submitted their performance review.",
                    NotificationType.REVIEW_SUBMITTED,
                    saved.getId()
            );
        }

        return mapToResponse(saved);
    }

    @Override
    public List<ReviewResponse> getMyReviews() {
        Employee current = authService.getCurrentUser();
        return reviewRepository.findByEmployeeId(current.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getTeamReviews() {
        Employee manager = authService.getCurrentUser();
        // Since no specific repository method for this, we fetch all and filter by manager
        // In a real application, create findByEmployeeManagerId in Repository
        return reviewRepository.findAll().stream()
                .filter(r -> r.getEmployee().getManager() != null && r.getEmployee().getManager().getId().equals(manager.getId()))
                .filter(r -> r.getStatus() != ReviewStatus.DRAFT) // Optional: typically managers only see submitted
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponse submitFeedback(Long id, FeedbackRequest request) {
        Employee manager = authService.getCurrentUser();
        PerformanceReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (review.getEmployee().getManager() == null || !review.getEmployee().getManager().getId().equals(manager.getId())) {
            throw new RuntimeException("Unauthorized: You are not the manager of this employee");
        }

        review.setManagerFeedback(request.getManagerFeedback());
        review.setManagerRating(request.getManagerRating());
        review.setStatus(ReviewStatus.REVIEWED);
        review.setReviewedAt(LocalDateTime.now());

        PerformanceReview saved = reviewRepository.save(review);

        notificationService.createNotification(
                saved.getEmployee().getId(),
                "Your manager has provided feedback on your performance review.",
                NotificationType.REVIEW_FEEDBACK,
                saved.getId()
        );

        return mapToResponse(saved);
    }

    private ReviewResponse mapToResponse(PerformanceReview review) {
        return ReviewResponse.builder()
                .id(review.getId())
                .employeeId(review.getEmployee().getId())
                .employeeName(review.getEmployee().getFirstName() + " " + review.getEmployee().getLastName())
                .reviewYear(review.getReviewYear())
                .keyDeliverables(review.getKeyDeliverables())
                .accomplishments(review.getAccomplishments())
                .areasOfImprovement(review.getAreasOfImprovement())
                .selfAssessmentRating(review.getSelfAssessmentRating())
                .status(review.getStatus().name())
                .managerFeedback(review.getManagerFeedback())
                .managerRating(review.getManagerRating())
                .submittedAt(review.getSubmittedAt())
                .reviewedAt(review.getReviewedAt())
                .build();
    }
}
