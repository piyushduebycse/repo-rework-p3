package com.revworkforce.reportingservice.client;

import com.revworkforce.reportingservice.dto.LeaveApplicationSummaryDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@FeignClient(name = "leave-service", path = "/api/leaves")
public interface LeaveClient {

    @GetMapping("/all")
    List<LeaveApplicationSummaryDTO> getAllLeaves(@RequestHeader("Authorization") String token);
}
