package com.revworkforce.controller;

import com.revworkforce.dto.request.EmployeeRequest;
import com.revworkforce.dto.response.AdminMetricsResponse;
import com.revworkforce.dto.response.EmployeeResponse;
import com.revworkforce.service.interfaces.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/employees")
    public ResponseEntity<EmployeeResponse> addEmployee(@Valid @RequestBody EmployeeRequest request) {
        return new ResponseEntity<>(adminService.addEmployee(request), HttpStatus.CREATED);
    }

    @PutMapping("/employees/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(@PathVariable Long id, @Valid @RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(adminService.updateEmployee(id, request));
    }

    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeResponse>> getAllEmployees(
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String designation,
            @RequestParam(required = false) Boolean isActive) {
        return ResponseEntity.ok(adminService.getAllEmployees(department, designation, isActive));
    }

    @PutMapping("/employees/{id}/deactivate")
    public ResponseEntity<Void> deactivateEmployee(@PathVariable Long id) {
        adminService.toggleEmployeeStatus(id, false);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/employees/{id}/reactivate")
    public ResponseEntity<Void> reactivateEmployee(@PathVariable Long id) {
        adminService.toggleEmployeeStatus(id, true);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/dashboard/metrics")
    public ResponseEntity<AdminMetricsResponse> getMetrics() {
        return ResponseEntity.ok(adminService.getDashboardMetrics());
    }
}
