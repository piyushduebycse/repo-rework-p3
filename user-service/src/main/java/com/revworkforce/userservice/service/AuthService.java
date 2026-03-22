package com.revworkforce.userservice.service;

import com.revworkforce.common.dto.request.LoginRequest;
import com.revworkforce.common.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse login(LoginRequest request);
}
