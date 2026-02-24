package com.revpay.service.impl;

import com.revpay.dto.response.NotificationResponse;
import com.revpay.entity.Notification;
import com.revpay.entity.NotificationPreference;
import com.revpay.entity.User;
import com.revpay.enums.NotificationType;
import com.revpay.exception.BadRequestException;
import com.revpay.repository.NotificationPreferenceRepository;
import com.revpay.repository.NotificationRepository;
import com.revpay.repository.UserRepository;
import com.revpay.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationPreferenceRepository preferenceRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public void createNotification(User user, String title, String message,
                                    NotificationType type, String referenceId) {
        // Check preferences
        boolean enabled = preferenceRepository.findByUserAndNotificationType(user, type)
            .map(NotificationPreference::isEnabled)
            .orElse(true);

        if (enabled) {
            Notification notification = Notification.builder()
                .user(user)
                .title(title)
                .message(message)
                .type(type)
                .referenceId(referenceId)
                .build();
            notificationRepository.save(notification);
        }
    }

    @Override
    public Page<NotificationResponse> getNotifications(Long userId, int page, int size) {
        User user = userRepository.getReferenceById(userId);
        return notificationRepository.findByUserOrderByCreatedAtDesc(user, PageRequest.of(page, size))
            .map(this::mapToResponse);
    }

    @Override
    public long getUnreadCount(Long userId) {
        User user = userRepository.getReferenceById(userId);
        return notificationRepository.countByUserAndIsReadFalse(user);
    }

    @Override
    @Transactional
    public void markAsRead(Long notificationId, Long userId) {
        Notification n = notificationRepository.findById(notificationId)
            .orElseThrow(() -> new BadRequestException("Notification not found"));
        if (!n.getUser().getId().equals(userId)) {
            throw new BadRequestException("Access denied");
        }
        n.setRead(true);
        notificationRepository.save(n);
    }

    @Override
    @Transactional
    public void markAllAsRead(Long userId) {
        User user = userRepository.getReferenceById(userId);
        notificationRepository.markAllAsRead(user);
    }

    @Override
    public List<NotificationResponse> getPreferences(Long userId) {
        User user = userRepository.getReferenceById(userId);
        List<NotificationPreference> prefs = preferenceRepository.findByUser(user);
        // Initialize if missing
        if (prefs.isEmpty()) {
            prefs = initializePreferences(user);
        }
        return prefs.stream().map(p -> NotificationResponse.builder()
            .id(p.getId())
            .type(p.getNotificationType())
            .isRead(p.isEnabled())
            .build()).toList();
    }

    @Override
    @Transactional
    public void updatePreference(Long userId, NotificationType type, boolean enabled) {
        User user = userRepository.getReferenceById(userId);
        NotificationPreference pref = preferenceRepository.findByUserAndNotificationType(user, type)
            .orElseGet(() -> NotificationPreference.builder().user(user).notificationType(type).build());
        pref.setEnabled(enabled);
        preferenceRepository.save(pref);
    }

    private List<NotificationPreference> initializePreferences(User user) {
        return Arrays.stream(NotificationType.values()).map(type ->
            preferenceRepository.save(NotificationPreference.builder()
                .user(user).notificationType(type).enabled(true).build())
        ).toList();
    }

    private NotificationResponse mapToResponse(Notification n) {
        return NotificationResponse.builder()
            .id(n.getId())
            .title(n.getTitle())
            .message(n.getMessage())
            .type(n.getType())
            .isRead(n.isRead())
            .referenceId(n.getReferenceId())
            .createdAt(n.getCreatedAt())
            .build();
    }
}
