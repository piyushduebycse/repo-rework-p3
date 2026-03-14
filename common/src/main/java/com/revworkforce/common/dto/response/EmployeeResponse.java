package com.revworkforce.common.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {
    private Long id;
    private String employeeId;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String emergencyContact;
    private String role;
    private Long departmentId;
    private String departmentName;
    private Long designationId;
    private String designationTitle;
    private Long managerId;
    private String managerName;
    private LocalDate joiningDate;
    private BigDecimal salary;
    private Boolean isActive;
}
