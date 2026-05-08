package com.stockanalyzer.service;

import com.stockanalyzer.dto.request.LoginRequest;
import com.stockanalyzer.dto.request.RegisterRequest;
import com.stockanalyzer.dto.response.AuthResponse;
import com.stockanalyzer.dto.response.UserResponse;

/**
 * Auth service interface — demonstrates ABSTRACTION.
 */
public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    AuthResponse refreshToken(String refreshToken);
    UserResponse getCurrentUser(String email);
}
