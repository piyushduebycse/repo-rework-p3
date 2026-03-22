package com.revworkforce.performanceservice.service.impl;

import com.revworkforce.common.dto.EmployeeDTO;
import com.revworkforce.common.dto.request.ReviewRequest;
import com.revworkforce.common.dto.response.ReviewResponse;
import com.revworkforce.common.exception.ResourceNotFoundException;
import com.revworkforce.performanceservice.client.EmployeeClient;
import com.revworkforce.performanceservice.entity.NotificationRepository;
import com.revworkforce.performanceservice.entity.PerformanceReview;
import com.revworkforce.performanceservice.entity.ReviewStatus;
import com.revworkforce.performanceservice.repository.NotificationRepository;
import com.revworkforce.performanceservice.repository.PerformanceReviewRepository;
import com.revworkforce.common.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PerformanceServiceImplTest {

    @Mock private PerformanceReviewRepository reviewRepository;
    @Mock private EmployeeClient employeeClient;
    @Mock private NotificationRepository notificationRepository;

    @InjectMocks
    private PerformanceServiceImpl performanceService;

    private EmployeeDTO employee;
    private UserPrincipal mockPrincipal;

    @BeforeEach
    void setUp() {
        employee = new EmployeeDTO();
        employee.setId(1L);
        employee.setFirstName("Jane");
        employee.setLastName("Doe");
        employee.setRole("EMPLOYEE");

        mockPrincipal = mock(UserPrincipal.class);
        when(mockPrincipal.getId()).thenReturn(1L);

        Authentication authentication = mock(Authentication.class);
        when(authentication.getPrincipal()).thenReturn(mockPrincipal);

        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(employeeClient.getEmployeeById(1L)).thenReturn(employee);
    }

    @Test
    void saveReviewDraft_CreatesNewDraft() {
        ReviewRequest request = new ReviewRequest();
        request.setReviewYear(2024);
        request.setKeyDeliverables("Delivered Project X");
        request.setAccomplishments("Promoted");
        request.setAreasOfImprovement("Communication");
        request.setSelfAssessmentRating(4);

        when(reviewRepository.findByEmployeeIdAndReviewYear(1L, 2024)).thenReturn(Optional.empty());

        PerformanceReview savedReview = new PerformanceReview();
        savedReview.setId(1L);
        savedReview.setEmployeeId(1L);
        savedReview.setReviewYear(2024);
        savedReview.setStatus(ReviewStatus.DRAFT);
        savedReview.setKeyDeliverables(request.getKeyDeliverables());

        when(reviewRepository.save(any(PerformanceReview.class))).thenReturn(savedReview);

        ReviewResponse response = performanceService.saveReviewDraft(request);

        assertNotNull(response);
        assertEquals(ReviewStatus.DRAFT.name(), response.getStatus());
        assertEquals(1L, response.getEmployeeId());
        verify(reviewRepository, times(1)).save(any(PerformanceReview.class));
    }

    @Test
    void submitReview_Success() {
        PerformanceReview review = new PerformanceReview();
        review.setId(1L);
        review.setEmployeeId(1L);
        review.setStatus(ReviewStatus.DRAFT);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));
        when(reviewRepository.save(any(PerformanceReview.class))).thenReturn(review);

        ReviewResponse response = performanceService.submitReview(1L);

        assertEquals(ReviewStatus.SUBMITTED.name(), response.getStatus());
        verify(reviewRepository, times(1)).save(any(PerformanceReview.class));
    }

    @Test
    void submitReview_ThrowsWhenNotOwner() {
        PerformanceReview review = new PerformanceReview();
        review.setId(1L);
        review.setEmployeeId(99L); // belongs to different employee
        review.setStatus(ReviewStatus.DRAFT);

        when(reviewRepository.findById(1L)).thenReturn(Optional.of(review));

        assertThrows(RuntimeException.class, () -> performanceService.submitReview(1L));
        verify(reviewRepository, never()).save(any(PerformanceReview.class));
    }

    @Test
    void submitReview_ThrowsWhenNotFound() {
        when(reviewRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> performanceService.submitReview(99L));
    }
}
