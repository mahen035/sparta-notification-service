package com.training.notificationservice.service.impl;

import com.training.notificationservice.dto.request.NotificationTemplateRequestDto;
import com.training.notificationservice.dto.response.NotificationTemplateResponseDto;
import com.training.notificationservice.entity.NotificationTemplate;
import com.training.notificationservice.exception.DuplicateTemplateNameException;
import com.training.notificationservice.exception.NotificationTemplateNotFoundException;
import com.training.notificationservice.repository.NotificationTemplateRepository;
import com.training.notificationservice.service.NotificationTemplateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Orchestrates notification template create/read/update/delete. Mirrors the
 * layering and logging conventions Developer 1 established in
 * {@code NotificationServiceImpl} so the codebase stays consistent across
 * independently-developed slices.
 */
@Service
public class NotificationTemplateServiceImpl implements NotificationTemplateService {

    private static final Logger log = LoggerFactory.getLogger(NotificationTemplateServiceImpl.class);

    private final NotificationTemplateRepository templateRepository;

    public NotificationTemplateServiceImpl(NotificationTemplateRepository templateRepository) {
        this.templateRepository = templateRepository;
    }

    @Override
    @Transactional
    public NotificationTemplateResponseDto create(NotificationTemplateRequestDto request) {
        if (templateRepository.existsByName(request.getName())) {
            log.warn("Rejected duplicate template name: {}", request.getName());
            throw new DuplicateTemplateNameException(request.getName());
        }

        NotificationTemplate template = new NotificationTemplate();
        applyRequest(template, request);
        NotificationTemplate saved = templateRepository.save(template);

        log.info("Created notification template {} ({})", saved.getId(), saved.getName());
        return toResponseDto(saved);
    }

    @Override
    public NotificationTemplateResponseDto getById(UUID id) {
        log.debug("Looking up notification template {}", id);
        return toResponseDto(findOrThrow(id));
    }

    @Override
    @Transactional
    public NotificationTemplateResponseDto update(UUID id, NotificationTemplateRequestDto request) {
        NotificationTemplate template = findOrThrow(id);

        templateRepository.findByName(request.getName())
                .filter(existing -> !existing.getId().equals(id))
                .ifPresent(existing -> {
                    throw new DuplicateTemplateNameException(request.getName());
                });

        applyRequest(template, request);
        NotificationTemplate saved = templateRepository.save(template);

        log.info("Updated notification template {}", saved.getId());
        return toResponseDto(saved);
    }

    @Override
    @Transactional
    public void delete(UUID id) {
        NotificationTemplate template = findOrThrow(id);
        templateRepository.delete(template);
        log.info("Deleted notification template {}", id);
    }

    private NotificationTemplate findOrThrow(UUID id) {
        return templateRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Notification template {} not found", id);
                    return new NotificationTemplateNotFoundException(id);
                });
    }

    private void applyRequest(NotificationTemplate template, NotificationTemplateRequestDto request) {
        template.setName(request.getName());
        template.setChannel(request.getChannel());
        template.setSubjectTemplate(request.getSubjectTemplate());
        template.setBodyTemplate(request.getBodyTemplate());
    }

    private NotificationTemplateResponseDto toResponseDto(NotificationTemplate template) {
        NotificationTemplateResponseDto dto = new NotificationTemplateResponseDto();
        dto.setId(template.getId());
        dto.setName(template.getName());
        dto.setChannel(template.getChannel());
        dto.setSubjectTemplate(template.getSubjectTemplate());
        dto.setBodyTemplate(template.getBodyTemplate());
        dto.setCreatedAt(template.getCreatedAt());
        dto.setUpdatedAt(template.getUpdatedAt());
        return dto;
    }
}
