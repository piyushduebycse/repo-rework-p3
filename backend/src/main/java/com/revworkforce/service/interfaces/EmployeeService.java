package com.revworkforce.service.interfaces;

import com.revworkforce.dto.response.EmployeeResponse;

import java.util.List;

public interface EmployeeService {
    EmployeeResponse getProfile();
    EmployeeResponse getManager();
    List<EmployeeResponse> getDirectory();
}
