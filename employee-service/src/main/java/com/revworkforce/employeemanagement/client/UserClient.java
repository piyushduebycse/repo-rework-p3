package com.revworkforce.employeemanagement.client;

import com.revworkforce.common.dto.response.AuthResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * OpenFeign client to communicate with the user-service without needing 
 * to share a database or wire up RestTemplates manually.
 */
@FeignClient(name = "user-service", path = "/api/auth")
public interface UserClient {

    @GetMapping("/users/{id}")
    AuthResponse getUserDetails(@PathVariable("id") Long id);
}
