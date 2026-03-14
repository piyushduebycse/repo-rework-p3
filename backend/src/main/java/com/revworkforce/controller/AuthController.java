package com.revworkforce.controller;

import com.revworkforce.dto.request.LoginRequest;
import com.revworkforce.dto.response.AuthResponse;
import com.revworkforce.entity.Employee;
import com.revworkforce.service.interfaces.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(authService.login(loginRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // Handled client side
        return ResponseEntity.ok().build();
    }

    @GetMapping("/me")
    public ResponseEntity<AuthResponse> getCurrentUser() {
        return ResponseEntity.ok(authService.getProfile());
    }

    @PutMapping("/me")
    public ResponseEntity<AuthResponse> updateProfile(@RequestBody com.revworkforce.dto.request.ProfileUpdateRequest request) {
        return ResponseEntity.ok(authService.updateProfile(request));
    }
}
