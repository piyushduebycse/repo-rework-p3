package com.revworkforce.reportingservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaveApplicationSummaryDTO {
    private Long id;
    private Long employeeId;
    private String employeeName;
    private String leaveTypeName;
    private String status;
    private String startDate;
    private String endDate;
}
