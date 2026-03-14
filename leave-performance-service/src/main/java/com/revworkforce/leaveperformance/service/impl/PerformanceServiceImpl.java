package com.revworkforce.leaveperformance.service.impl;

import com.revworkforce.common.dto.request.FeedbackRequest;
import com.revworkforce.common.dto.request.ReviewRequest;
import com.revworkforce.common.dto.response.ReviewResponse;
import com.revworkforce.common.exception.ResourceNotFoundException;
import com.revworkforce.common.security.UserPrincipal;
import com.revworkforce.leaveperformance.entity.*;
import com.revworkforce.leaveperformance.repository.EmployeeRepository;
import com.revworkforce.leaveperformance.repository.NotificationRepository;
import com.revworkforce.leaveperformance.repository.PerformanceReviewRepository;
import com.revworkforce.leaveperformance.service.interfaces.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PerformanceServiceImpl implements PerformanceService {

    private final PerformanceReviewRepository reviewRepository;
    private final EmployeeRepository employeeRepository;
    private final NotificationRepository notificationRepository;

    private Employee getCurrentEmployee() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return employeeRepository.findById(principal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));
    }

    @Override
    public ReviewResponse saveReviewDraft(ReviewRequest request) {
        Employee current = getCurrentEmployee();

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
        Employee current = getCurrentEmployee();
        PerformanceReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getEmployee().getId().equals(current.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        review.setStatus(ReviewStatus.SUBMITTED);
        review.setSubmittedAt(LocalDateTime.now());
        PerformanceReview saved = reviewRepository.save(review);

        if (current.getManager() != null) {
            createNotification(
                    current.getManager(),
                    current.getFirstName() + " has submitted their performance review.",
                    NotificationType.REVIEW_SUBMITTED,
                    saved.getId()
            );
        }

        return mapToResponse(saved);
    }

    @Override
    public List<ReviewResponse> getMyReviews() {
        Employee current = getCurrentEmployee();
        return reviewRepository.findByEmployeeId(current.getId()).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getTeamReviews() {
        Employee manager = getCurrentEmployee();
        return reviewRepository.findAll().stream()
                .filter(r -> r.getEmployee().getManager() != null && r.getEmployee().getManager().getId().equals(manager.getId()))
                .filter(r -> r.getStatus() != ReviewStatus.DRAFT)
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponse submitFeedback(Long id, FeedbackRequest request) {
        Employee manager = getCurrentEmployee();
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

        createNotification(
                saved.getEmployee(),
                "Your manager has provided feedback on your performance review.",
                NotificationType.REVIEW_FEEDBACK,
                saved.getId()
        );

        return mapToResponse(saved);
    }

    private void createNotification(Employee recipient, String message, NotificationType type, Long referenceId) {
        Notification notification = Notification.builder()
                .recipient(recipient)
                .message(message)
                .type(type)
                .referenceId(referenceId)
                .build();
        notificationRepository.save(notification);
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
