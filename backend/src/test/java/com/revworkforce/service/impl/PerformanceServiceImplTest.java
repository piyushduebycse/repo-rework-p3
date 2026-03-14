package com.revworkforce.service.impl;

import com.revworkforce.dto.request.ReviewRequest;
import com.revworkforce.dto.response.ReviewResponse;
import com.revworkforce.entity.Employee;
import com.revworkforce.entity.PerformanceReview;
import com.revworkforce.entity.ReviewStatus;
import com.revworkforce.repository.PerformanceReviewRepository;
import com.revworkforce.service.interfaces.AuthService;
import com.revworkforce.service.interfaces.NotificationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PerformanceServiceImplTest {

    @Mock
    private PerformanceReviewRepository reviewRepository;
    @Mock
    private AuthService authService;
    @Mock
    private NotificationService notificationService;

    @InjectMocks
    private PerformanceServiceImpl performanceService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setFirstName("Jane");
        employee.setLastName("Doe");
    }

    @Test
    void saveReviewDraft_NewDraft() {
        ReviewRequest request = new ReviewRequest();
        request.setReviewYear(2023);
        request.setKeyDeliverables("Delivered Project X");
        request.setAccomplishments("Promoted");
        request.setAreasOfImprovement("Communication");
        request.setSelfAssessmentRating(4);

        when(authService.getCurrentUser()).thenReturn(employee);
        when(reviewRepository.findByEmployeeIdAndReviewYear(1L, 2023)).thenReturn(Optional.empty());

        PerformanceReview savedReview = new PerformanceReview();
        savedReview.setId(1L);
        savedReview.setEmployee(employee);
        savedReview.setReviewYear(2023);
        savedReview.setStatus(ReviewStatus.DRAFT);
        savedReview.setKeyDeliverables(request.getKeyDeliverables());
        
        when(reviewRepository.save(any(PerformanceReview.class))).thenReturn(savedReview);

        ReviewResponse response = performanceService.saveReviewDraft(request);

        assertNotNull(response);
        assertEquals(ReviewStatus.DRAFT.name(), response.getStatus());
        verify(reviewRepository, times(1)).save(any(PerformanceReview.class));
    }

    @Test
    void submitReview_Success() {
        PerformanceReview review = new PerformanceReview();
        review.setId(1L);
        review.setEmployee(employee);
        review.setStatus(ReviewStatus.DRAFT);

        when(authService.getCurrentUser()).thenReturn(employee);
        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(PerformanceReview.class))).thenReturn(review);

        ReviewResponse response = performanceService.submitReview(1L);

        assertEquals(ReviewStatus.SUBMITTED.name(), response.getStatus());
        verify(reviewRepository, times(1)).save(any(PerformanceReview.class));
    }
}
