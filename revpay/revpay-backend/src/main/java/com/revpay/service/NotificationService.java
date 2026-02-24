package com.revpay.service;

import com.revpay.dto.response.NotificationResponse;
import com.revpay.entity.User;
import com.revpay.enums.NotificationType;
import org.springframework.data.domain.Page;

import java.util.List;

public interface NotificationService {
    void createNotification(User user, String title, String message, NotificationType type, String referenceId);
    Page<NotificationResponse> getNotifications(Long userId, int page, int size);
    long getUnreadCount(Long userId);
    void markAsRead(Long notificationId, Long userId);
    void markAllAsRead(Long userId);
    List<NotificationResponse> getPreferences(Long userId);
    void updatePreference(Long userId, NotificationType type, boolean enabled);
}
