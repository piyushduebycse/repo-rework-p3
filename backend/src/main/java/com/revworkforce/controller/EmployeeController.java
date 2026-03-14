package com.revworkforce.controller;

import com.revworkforce.dto.response.EmployeeResponse;
import com.revworkforce.service.interfaces.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
