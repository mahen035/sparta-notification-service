package com.training.notificationservice.service;

import com.training.notificationservice.dto.request.NotificationTemplateRequestDto;
import com.training.notificationservice.dto.response.NotificationTemplateResponseDto;

import java.util.UUID;

/**
 * Business-logic contract for notification template CRUD, kept independent
 * of HTTP and persistence so it can be unit tested in isolation.
 */
public interface NotificationTemplateService {

    NotificationTemplateResponseDto create(NotificationTemplateRequestDto request);

    NotificationTemplateResponseDto getById(UUID id);

    NotificationTemplateResponseDto update(UUID id, NotificationTemplateRequestDto request);

    void delete(UUID id);
}
