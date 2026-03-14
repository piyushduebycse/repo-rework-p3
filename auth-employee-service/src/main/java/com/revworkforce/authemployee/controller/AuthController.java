package com.revworkforce.authemployee.controller;

import com.revworkforce.authemployee.service.AuthService;
import com.revworkforce.common.dto.request.LoginRequest;
import com.revworkforce.common.dto.request.ProfileUpdateRequest;
import com.revworkforce.common.dto.response.AuthResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        return ResponseEntity.ok(Map.of("message", "Logged out successfully"));
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getProfile() {
        return ResponseEntity.ok(authService.getProfile());
    }

    @PutMapping("/me")
    public ResponseEntity<AuthResponse> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        return ResponseEntity.ok(authService.updateProfile(request));
    }
}
