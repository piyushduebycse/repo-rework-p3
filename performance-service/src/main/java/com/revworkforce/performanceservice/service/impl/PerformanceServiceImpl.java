package com.revworkforce.performanceservice.service.impl;

import com.revworkforce.common.dto.EmployeeDTO;
import com.revworkforce.common.dto.request.FeedbackRequest;
import com.revworkforce.common.dto.request.ReviewRequest;
import com.revworkforce.common.dto.response.ReviewResponse;
import com.revworkforce.common.exception.ResourceNotFoundException;
import com.revworkforce.common.security.UserPrincipal;
import com.revworkforce.performanceservice.client.EmployeeClient;
import com.revworkforce.performanceservice.entity.*;
import com.revworkforce.performanceservice.repository.PerformanceReviewRepository;
import com.revworkforce.performanceservice.service.interfaces.PerformanceService;
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
    private final EmployeeClient employeeClient;

    private EmployeeDTO getCurrentEmployee() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return employeeClient.getEmployeeById(principal.getId());
    }

    @Override
    public ReviewResponse saveReviewDraft(ReviewRequest request) {
        EmployeeDTO current = getCurrentEmployee();

        Optional<PerformanceReview> existing = reviewRepository.findByEmployeeIdAndReviewYear(current.getId(), request.getReviewYear());
        PerformanceReview review;

        if (existing.isPresent()) {
            review = existing.get();
            if (review.getStatus() != ReviewStatus.DRAFT) {
                throw new RuntimeException("Cannot modify a review that is already submitted or reviewed");
            }
        } else {
            review = new PerformanceReview();
            review.setEmployeeId(current.getId());
            review.setReviewYear(request.getReviewYear());
            review.setStatus(ReviewStatus.DRAFT);
        }

        review.setKeyDeliverables(request.getKeyDeliverables());
        review.setAccomplishments(request.getAccomplishments());
        review.setAreasOfImprovement(request.getAreasOfImprovement());
        review.setSelfAssessmentRating(request.getSelfAssessmentRating());

        return mapToResponse(reviewRepository.save(review), current);
    }

    @Override
    public ReviewResponse submitReview(Long id) {
        EmployeeDTO current = getCurrentEmployee();
        PerformanceReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        if (!review.getEmployeeId().equals(current.getId())) {
            throw new RuntimeException("Unauthorized");
        }

        review.setStatus(ReviewStatus.SUBMITTED);
        review.setSubmittedAt(LocalDateTime.now());
        PerformanceReview saved = reviewRepository.save(review);

        // Notification triggered via notification-service (cross-service)

        return mapToResponse(saved, current);
    }

    @Override
    public List<ReviewResponse> getMyReviews() {
        EmployeeDTO current = getCurrentEmployee();
        return reviewRepository.findByEmployeeId(current.getId()).stream()
                .map(r -> mapToResponse(r, current))
                .collect(Collectors.toList());
    }

    @Override
    public List<ReviewResponse> getTeamReviews() {
        EmployeeDTO manager = getCurrentEmployee();
        
        // Find all employees reporting to this manager via OpenFeign
        List<EmployeeDTO> teamMembers = employeeClient.getEmployeesByManagerId(manager.getId());
        List<Long> teamIds = teamMembers.stream().map(EmployeeDTO::getId).collect(Collectors.toList());

        return reviewRepository.findAll().stream()
                .filter(r -> teamIds.contains(r.getEmployeeId()))
                .filter(r -> r.getStatus() != ReviewStatus.DRAFT)
                .map(r -> {
                    EmployeeDTO emp = teamMembers.stream().filter(t -> t.getId().equals(r.getEmployeeId())).findFirst().orElse(null);
                    return mapToResponse(r, emp);
                })
                .collect(Collectors.toList());
    }

    @Override
    public ReviewResponse submitFeedback(Long id, FeedbackRequest request) {
        EmployeeDTO manager = getCurrentEmployee();
        PerformanceReview review = reviewRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Review not found"));

        EmployeeDTO reviewOwner = employeeClient.getEmployeeById(review.getEmployeeId());

        if (reviewOwner.getManagerId() == null || !reviewOwner.getManagerId().equals(manager.getId())) {
            throw new RuntimeException("Unauthorized: You are not the manager of this employee");
        }

        review.setManagerFeedback(request.getManagerFeedback());
        review.setManagerRating(request.getManagerRating());
        review.setStatus(ReviewStatus.REVIEWED);
        review.setReviewedAt(LocalDateTime.now());

        PerformanceReview saved = reviewRepository.save(review);

        // Notification triggered via notification-service (cross-service)
        return mapToResponse(saved, reviewOwner);
    }

    private ReviewResponse mapToResponse(PerformanceReview review, EmployeeDTO employee) {
        String empName = employee != null ? employee.getFirstName() + " " + employee.getLastName() : "Unknown";
        return ReviewResponse.builder()
                .id(review.getId())
                .employeeId(review.getEmployeeId())
                .employeeName(empName)
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
