package com.training.notificationservice.service;

import com.training.notificationservice.dto.request.InAppNotificationRequestDto;
import com.training.notificationservice.dto.response.InAppNotificationResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface InAppNotificationService {
    InAppNotificationResponseDto create(InAppNotificationRequestDto request);
    Page<InAppNotificationResponseDto> listForUser(String recipient, Pageable pageable);
    InAppNotificationResponseDto markAsRead(UUID id);
}