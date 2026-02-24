package com.revpay.service;

import com.revpay.dto.request.ChangePasswordRequest;
import com.revpay.dto.request.LoginRequest;
import com.revpay.dto.request.RegisterRequest;
import com.revpay.dto.response.AuthResponse;

public interface AuthService {
    AuthResponse register(RegisterRequest request);
    AuthResponse login(LoginRequest request);
    void changePassword(Long userId, ChangePasswordRequest request);
    void setTransactionPin(Long userId, String pin);
}
