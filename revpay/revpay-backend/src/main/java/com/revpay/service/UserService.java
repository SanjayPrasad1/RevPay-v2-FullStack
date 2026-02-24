package com.revpay.service;

import com.revpay.dto.request.UpdateProfileRequest;
import com.revpay.dto.response.DashboardResponse;
import com.revpay.dto.response.UserProfileResponse;

public interface UserService {
    UserProfileResponse getProfile(Long userId);
    UserProfileResponse updateProfile(Long userId, UpdateProfileRequest request);
    DashboardResponse getDashboard(Long userId);
}
