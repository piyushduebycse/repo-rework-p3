package com.revworkforce.userservice.service.impl;

import com.revworkforce.common.dto.request.LoginRequest;
import com.revworkforce.common.dto.response.AuthResponse;
import com.revworkforce.common.exception.ResourceNotFoundException;
import com.revworkforce.common.security.JwtTokenProvider;
import com.revworkforce.userservice.entity.User;
import com.revworkforce.userservice.repository.UserRepository;
import com.revworkforce.userservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getIdentifier(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        // Since we are moving to Microservices, user details might just be extracted via email
        User user = userRepository.findByEmail(request.getIdentifier())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
                
        String token = jwtTokenProvider.generateToken(authentication);

        return AuthResponse.builder()
                .token(token)
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole().name())
                // Other HR fields are returned as null natively by builder because they live in Employee Management Service
                .build();
    }
}
