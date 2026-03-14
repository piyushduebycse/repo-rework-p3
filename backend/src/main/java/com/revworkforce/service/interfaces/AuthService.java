package com.revworkforce.service.interfaces;

import com.revworkforce.dto.request.LoginRequest;
import com.revworkforce.dto.request.ProfileUpdateRequest;
import com.revworkforce.dto.response.AuthResponse;
import com.revworkforce.entity.Employee;

public interface AuthService {
    AuthResponse login(LoginRequest loginRequest);
    Employee getCurrentUser();
    AuthResponse getProfile();
    AuthResponse updateProfile(ProfileUpdateRequest request);
}
