package com.revpay.controller;

import com.revpay.dto.response.ApiResponse;
import com.revpay.dto.response.NotificationResponse;
import com.revpay.enums.NotificationType;
import com.revpay.security.UserPrincipal;
import com.revpay.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<NotificationResponse>>> getNotifications(
            @AuthenticationPrincipal UserPrincipal user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(ApiResponse.success(
            notificationService.getNotifications(user.getId(), page, size)));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getUnreadCount(
            @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(ApiResponse.success(
            Map.of("count", notificationService.getUnreadCount(user.getId()))));
    }

    @PutMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable Long id) {
        notificationService.markAsRead(id, user.getId());
        return ResponseEntity.ok(ApiResponse.success("Marked as read", null));
    }

    @PutMapping("/mark-all-read")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal UserPrincipal user) {
        notificationService.markAllAsRead(user.getId());
        return ResponseEntity.ok(ApiResponse.success("All marked as read", null));
    }

    @GetMapping("/preferences")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getPreferences(
            @AuthenticationPrincipal UserPrincipal user) {
        return ResponseEntity.ok(ApiResponse.success(notificationService.getPreferences(user.getId())));
    }

    @PutMapping("/preferences/{type}")
    public ResponseEntity<ApiResponse<Void>> updatePreference(
            @AuthenticationPrincipal UserPrincipal user,
            @PathVariable NotificationType type,
            @RequestParam boolean enabled) {
        notificationService.updatePreference(user.getId(), type, enabled);
        return ResponseEntity.ok(ApiResponse.success("Preference updated", null));
    }
}
