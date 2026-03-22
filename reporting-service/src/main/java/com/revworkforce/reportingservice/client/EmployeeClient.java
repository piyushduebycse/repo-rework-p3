package com.revworkforce.reportingservice.client;

import com.revworkforce.reportingservice.dto.EmployeeSummaryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "employee-service", path = "/api/employees")
public interface EmployeeClient {

    @GetMapping
    List<EmployeeSummaryDTO> getAllEmployees(@RequestHeader("Authorization") String token);
}
