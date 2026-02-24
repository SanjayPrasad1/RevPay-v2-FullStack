package com.revpay.dto.response;

import com.revpay.enums.NotificationType;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class NotificationResponse {
    private Long id;
    private String title;
    private String message;
    private NotificationType type;
    private boolean isRead;
    private String referenceId;
    private LocalDateTime createdAt;
}
