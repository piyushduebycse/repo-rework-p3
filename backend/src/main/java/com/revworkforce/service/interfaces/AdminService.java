package com.revworkforce.service.interfaces;

import com.revworkforce.dto.request.EmployeeRequest;
import com.revworkforce.dto.response.AdminMetricsResponse;
import com.revworkforce.dto.response.EmployeeResponse;

import java.util.List;

public interface AdminService {
    List<EmployeeResponse> getAllEmployees(String department, String designation, Boolean isActive);
    EmployeeResponse addEmployee(EmployeeRequest request);
    EmployeeResponse updateEmployee(Long id, EmployeeRequest request);
    void toggleEmployeeStatus(Long id, boolean isActive);
    AdminMetricsResponse getDashboardMetrics();
}
