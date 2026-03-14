package com.revworkforce.common.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeRequest {
    @NotBlank(message = "Employee ID is required")
    private String employeeId;
    @NotBlank(message = "First name is required")
    private String firstName;
    @NotBlank(message = "Last name is required")
    private String lastName;
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    @NotBlank(message = "Role is required")
    private String role;
    @NotNull(message = "Department ID is required")
    private Long departmentId;
    @NotNull(message = "Designation ID is required")
    private Long designationId;
    private Long managerId;
    private LocalDate joiningDate;
    private BigDecimal salary;
}
