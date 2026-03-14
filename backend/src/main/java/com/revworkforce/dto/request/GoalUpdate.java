package com.revworkforce.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalUpdate {

    private String status; // NOT_STARTED, IN_PROGRESS, COMPLETED
    private Integer progressPercentage;
}
