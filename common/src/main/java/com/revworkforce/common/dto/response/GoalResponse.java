package com.revworkforce.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoalResponse {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String description;
    private LocalDate deadline;
    private String priority;
    private String status;
    private Integer progressPercentage;
    private String managerComment;
}
