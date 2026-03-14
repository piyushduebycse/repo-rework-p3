package com.revworkforce.authemployee.controller;

import com.revworkforce.authemployee.service.EmployeeService;
import com.revworkforce.common.dto.response.EmployeeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    @GetMapping("/profile")
    public ResponseEntity<EmployeeResponse> getProfile() {
        return ResponseEntity.ok(employeeService.getProfile());
    }

    @GetMapping("/manager")
    public ResponseEntity<EmployeeResponse> getManager() {
        return ResponseEntity.ok(employeeService.getManager());
    }

    @GetMapping("/directory")
    public ResponseEntity<List<EmployeeResponse>> getDirectory() {
        return ResponseEntity.ok(employeeService.getDirectory());
    }
}
