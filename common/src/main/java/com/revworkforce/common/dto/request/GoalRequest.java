package com.revworkforce.common.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalRequest {
    @NotBlank(message = "Description is required")
    private String description;
    private LocalDate deadline;
    private String priority;
}
