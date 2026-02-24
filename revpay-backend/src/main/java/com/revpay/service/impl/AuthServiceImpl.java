package com.revpay.service.impl;

import com.revpay.dto.request.ChangePasswordRequest;
import com.revpay.dto.request.LoginRequest;
import com.revpay.dto.request.RegisterRequest;
import com.revpay.dto.response.AuthResponse;
import com.revpay.entity.User;
import com.revpay.enums.AccountType;
import com.revpay.enums.NotificationType;
import com.revpay.exception.BadRequestException;
import com.revpay.repository.UserRepository;
import com.revpay.security.JwtTokenProvider;
import com.revpay.security.UserPrincipal;
import com.revpay.service.AuthService;
import com.revpay.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final NotificationService notificationService;

    @Override
    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new BadRequestException("Email already registered");
        }
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new BadRequestException("Username already taken");
        }
        if (request.getPhoneNumber() != null && userRepository.existsByPhoneNumber(request.getPhoneNumber())) {
            throw new BadRequestException("Phone number already registered");
        }

        if (request.getAccountType() == AccountType.BUSINESS) {
            if (request.getBusinessName() == null || request.getBusinessName().isBlank()) {
                throw new BadRequestException("Business name is required for business accounts");
            }
        }

        User user = User.builder()
            .username(request.getUsername())
            .fullName(request.getFullName())
            .email(request.getEmail())
            .phoneNumber(request.getPhoneNumber())
            .password(passwordEncoder.encode(request.getPassword()))
            .securityQuestion(request.getSecurityQuestion())
            .securityAnswer(request.getSecurityAnswer() != null
                ? passwordEncoder.encode(request.getSecurityAnswer()) : null)
            .accountType(request.getAccountType())
            .businessName(request.getBusinessName())
            .businessType(request.getBusinessType())
            .taxId(request.getTaxId())
            .businessAddress(request.getBusinessAddress())
            .build();

        user = userRepository.save(user);

        notificationService.createNotification(user, "Welcome to RevPay!",
            "Your account has been created successfully.", NotificationType.GENERAL, null);

        String token = tokenProvider.generateTokenFromId(user.getId());
        return buildAuthResponse(token, user);
    }

    @Override
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmailOrPhone())
            .or(() -> userRepository.findByPhoneNumber(request.getEmailOrPhone()))
            .orElseThrow(() -> new BadRequestException("Invalid credentials"));

        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getEmail(), request.getPassword())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.generateToken(authentication);
        return buildAuthResponse(token, user);
    }

    @Override
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BadRequestException("User not found"));

        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new BadRequestException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Override
    @Transactional
    public void setTransactionPin(Long userId, String pin) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new BadRequestException("User not found"));
        user.setTransactionPin(passwordEncoder.encode(pin));
        userRepository.save(user);
    }

    private AuthResponse buildAuthResponse(String token, User user) {
        return AuthResponse.builder()
            .token(token)
            .tokenType("Bearer")
            .userId(user.getId())
            .username(user.getUsername())
            .email(user.getEmail())
            .fullName(user.getFullName())
            .accountType(user.getAccountType())
            .build();
    }
}
