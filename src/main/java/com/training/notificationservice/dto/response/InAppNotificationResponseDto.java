package com.training.notificationservice.dto.response;

import com.training.notificationservice.enums.NotificationChannel;
import com.training.notificationservice.enums.NotificationStatus;
import java.time.LocalDateTime;   // changed from java.time.Instant
import java.util.UUID;

public record InAppNotificationResponseDto(
        UUID id,
        String recipient,
        NotificationChannel channel,
        String subject,
        String message,
        NotificationStatus status,
        boolean isRead,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}