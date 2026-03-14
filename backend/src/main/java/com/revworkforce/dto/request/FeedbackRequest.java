package com.revworkforce.dto.request;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackRequest {

    private String managerFeedback;

    @Min(1)
    @Max(5)
    @NotNull(message = "Manager rating is required")
    private Integer managerRating;
}
