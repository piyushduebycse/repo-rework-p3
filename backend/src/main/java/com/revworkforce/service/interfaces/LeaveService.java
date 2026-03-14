package com.revworkforce.service.interfaces;

import com.revworkforce.dto.request.LeaveRequest;
import com.revworkforce.dto.response.LeaveApplicationResponse;
import com.revworkforce.dto.response.LeaveBalanceResponse;
import com.revworkforce.entity.CompanyHoliday;

import java.util.List;

public interface LeaveService {
    List<LeaveBalanceResponse> getBalances();
    LeaveApplicationResponse applyLeave(LeaveRequest request);
    List<LeaveApplicationResponse> getMyLeaves();
    void cancelLeave(Long id);
    List<CompanyHoliday> getHolidays();
    
    // Manager endpoints
    List<LeaveApplicationResponse> getTeamLeaves();
    LeaveApplicationResponse approveLeave(Long id, String comment);
    LeaveApplicationResponse rejectLeave(Long id, String comment);
}
