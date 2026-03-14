package com.revworkforce.common.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewRequest {
    @NotNull(message = "Review year is required")
    private Integer reviewYear;
    private String keyDeliverables;
    private String accomplishments;
    private String areasOfImprovement;
    @Min(1)
    @Max(5)
    private Integer selfAssessmentRating;
}
