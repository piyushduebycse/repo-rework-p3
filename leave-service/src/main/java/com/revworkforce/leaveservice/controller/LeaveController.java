package com.revworkforce.leaveservice.controller;

import com.revworkforce.common.dto.request.LeaveRequest;
import com.revworkforce.common.dto.response.LeaveApplicationResponse;
import com.revworkforce.common.dto.response.LeaveBalanceResponse;
import com.revworkforce.leaveservice.entity.CompanyHoliday;
import com.revworkforce.leaveservice.service.interfaces.LeaveService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leaves")
@RequiredArgsConstructor
public class LeaveController {

    private final LeaveService leaveService;

    @GetMapping("/balance")
    public ResponseEntity<List<LeaveBalanceResponse>> getBalance() {
        return ResponseEntity.ok(leaveService.getBalances());
    }

    @PostMapping("/apply")
    public ResponseEntity<LeaveApplicationResponse> applyLeave(@Valid @RequestBody LeaveRequest request) {
        return new ResponseEntity<>(leaveService.applyLeave(request), HttpStatus.CREATED);
    }

    @GetMapping("/my")
    public ResponseEntity<List<LeaveApplicationResponse>> getMyLeaves() {
        return ResponseEntity.ok(leaveService.getMyLeaves());
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<Void> cancelLeave(@PathVariable Long id) {
        leaveService.cancelLeave(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/holidays")
    public ResponseEntity<List<CompanyHoliday>> getHolidays() {
        return ResponseEntity.ok(leaveService.getHolidays());
    }
}
