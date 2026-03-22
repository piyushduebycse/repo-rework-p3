package com.revworkforce.reportingservice.controller;

import com.revworkforce.reportingservice.client.EmployeeClient;
import com.revworkforce.reportingservice.client.LeaveClient;
import com.revworkforce.reportingservice.dto.EmployeeSummaryDTO;
import com.revworkforce.reportingservice.dto.LeaveApplicationSummaryDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/reports")
@RequiredArgsConstructor
public class ReportingController {

    private final EmployeeClient employeeClient;
    private final LeaveClient leaveClient;

    /**
     * Admin HR Dashboard Endpoint.
     * Aggregates data from both employee-service and leave-service via OpenFeign.
     */
    @GetMapping("/dashboard")
    public ResponseEntity<Map<String, Object>> getDashboardMetrics(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");

        // Fetch data from remote services
        List<EmployeeSummaryDTO> employees = employeeClient.getAllEmployees(authHeader);
        List<LeaveApplicationSummaryDTO> allLeaves = leaveClient.getAllLeaves(authHeader);

        // Aggregate: Total Headcount
        long totalEmployees = employees.size();

        // Aggregate: Employees by Role
        Map<String, Long> employeesByRole = employees.stream()
                .collect(Collectors.groupingBy(EmployeeSummaryDTO::getRole, Collectors.counting()));

        // Aggregate: Leave stats
        long totalPending  = allLeaves.stream().filter(l -> "PENDING".equals(l.getStatus())).count();
        long totalApproved = allLeaves.stream().filter(l -> "APPROVED".equals(l.getStatus())).count();
        long totalRejected = allLeaves.stream().filter(l -> "REJECTED".equals(l.getStatus())).count();

        // Build the final aggregated JSON response
        Map<String, Object> report = new HashMap<>();
        report.put("totalHeadcount",      totalEmployees);
        report.put("employeesByRole",     employeesByRole);
        report.put("totalLeavesPending",  totalPending);
        report.put("totalLeavesApproved", totalApproved);
        report.put("totalLeavesRejected", totalRejected);
        report.put("totalLeavesSubmitted", allLeaves.size());

        return ResponseEntity.ok(report);
    }

    /**
     * Returns a flat list of all employees for HR export.
     */
    @GetMapping("/employees")
    public ResponseEntity<List<EmployeeSummaryDTO>> getAllEmployees(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return ResponseEntity.ok(employeeClient.getAllEmployees(authHeader));
    }

    /**
     * Returns a flat list of all leave applications for HR audits.
     */
    @GetMapping("/leaves")
    public ResponseEntity<List<LeaveApplicationSummaryDTO>> getAllLeaves(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        return ResponseEntity.ok(leaveClient.getAllLeaves(authHeader));
    }
}
