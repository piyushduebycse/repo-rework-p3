package com.revworkforce.performanceservice.client;

import com.revworkforce.common.dto.EmployeeDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "employee-service", path = "/api/employees")
public interface EmployeeClient {

    @GetMapping("/{id}")
    EmployeeDTO getEmployeeById(@PathVariable("id") Long id);

    @GetMapping("/manager/{managerId}")
    List<EmployeeDTO> getEmployeesByManagerId(@PathVariable("managerId") Long managerId);
}
