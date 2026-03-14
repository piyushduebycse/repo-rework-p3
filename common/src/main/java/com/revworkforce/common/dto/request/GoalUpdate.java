package com.revworkforce.common.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GoalUpdate {
    private String status;
    private Integer progressPercentage;
}
