package com.revworkforce.authemployee.service;

import com.revworkforce.authemployee.entity.Employee;
import com.revworkforce.common.dto.request.LoginRequest;
import com.revworkforce.common.dto.request.ProfileUpdateRequest;
import com.revworkforce.common.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
    Employee getCurrentUser();
    AuthResponse getProfile();
    AuthResponse updateProfile(ProfileUpdateRequest request);
}
