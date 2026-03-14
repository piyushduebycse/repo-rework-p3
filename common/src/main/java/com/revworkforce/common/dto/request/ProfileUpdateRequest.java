package com.revworkforce.common.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProfileUpdateRequest {
    private String phone;
    private String address;
    private String emergencyContact;
}
