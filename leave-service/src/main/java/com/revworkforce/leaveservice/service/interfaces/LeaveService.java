package com.revworkforce.leaveservice.service.interfaces;

import com.revworkforce.common.dto.request.LeaveRequest;
import com.revworkforce.common.dto.response.LeaveApplicationResponse;
import com.revworkforce.common.dto.response.LeaveBalanceResponse;
import com.revworkforce.leaveservice.entity.CompanyHoliday;

import java.util.List;

public interface LeaveService {
    List<LeaveBalanceResponse> getBalances();
    LeaveApplicationResponse applyLeave(LeaveRequest request);
    List<LeaveApplicationResponse> getMyLeaves();
    void cancelLeave(Long id);
    List<CompanyHoliday> getHolidays();
    List<LeaveApplicationResponse> getTeamLeaves();
    LeaveApplicationResponse approveLeave(Long id, String comment);
    LeaveApplicationResponse rejectLeave(Long id, String comment);
}
