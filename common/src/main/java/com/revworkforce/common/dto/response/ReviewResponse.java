package com.revworkforce.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private Integer reviewYear;
    private String keyDeliverables;
    private String accomplishments;
    private String areasOfImprovement;
    private Integer selfAssessmentRating;
    private String status;
    private String managerFeedback;
    private Integer managerRating;
    private LocalDateTime submittedAt;
    private LocalDateTime reviewedAt;
}
