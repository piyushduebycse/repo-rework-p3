package com.revworkforce.authemployee.service;

import com.revworkforce.common.dto.request.EmployeeRequest;
import com.revworkforce.common.dto.response.AdminMetricsResponse;
import com.revworkforce.common.dto.response.EmployeeResponse;
import java.util.List;

public interface AdminService {
    List<EmployeeResponse> getAllEmployees(String department, String designation, Boolean isActive);
    EmployeeResponse addEmployee(EmployeeRequest request);
    EmployeeResponse updateEmployee(Long id, EmployeeRequest request);
    void toggleEmployeeStatus(Long id, boolean isActive);
    AdminMetricsResponse getDashboardMetrics();
}
