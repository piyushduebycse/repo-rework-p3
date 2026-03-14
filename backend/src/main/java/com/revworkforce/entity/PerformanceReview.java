package com.revworkforce.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "performance_reviews")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @Column(name = "review_year", nullable = false)
    private Integer reviewYear;

    @Column(name = "key_deliverables", columnDefinition = "TEXT")
    private String keyDeliverables;

    @Column(columnDefinition = "TEXT")
    private String accomplishments;

    @Column(name = "areas_of_improvement", columnDefinition = "TEXT")
    private String areasOfImprovement;

    @Column(name = "self_assessment_rating")
    private Integer selfAssessmentRating;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    @Builder.Default
    private ReviewStatus status = ReviewStatus.DRAFT;

    @Column(name = "manager_feedback", columnDefinition = "TEXT")
    private String managerFeedback;

    @Column(name = "manager_rating")
    private Integer managerRating;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}
