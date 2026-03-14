package com.revworkforce.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminMetricsResponse {
    private long totalEmployees;
    private long activeEmployees;
    private long inactiveEmployees;
    private long pendingLeaves;
    private long submittedReviews;
}
