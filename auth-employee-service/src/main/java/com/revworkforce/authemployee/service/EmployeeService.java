package com.revworkforce.authemployee.service;

import com.revworkforce.common.dto.response.EmployeeResponse;
import java.util.List;

public interface EmployeeService {
    EmployeeResponse getProfile();
    EmployeeResponse getManager();
    List<EmployeeResponse> getDirectory();
}
