package com.training.notificationservice.dto.response;

import com.training.notificationservice.enums.NotificationChannel;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Outbound wire contract for the template catalog features (search, archive,
 * bulk import, duplicate). Kept separate from {@link NotificationTemplateResponseDto}
 * so the original template CRUD contract never changes shape - this DTO adds
 * the {@code active} flag that only these new endpoints need.
 */
public class TemplateCatalogResponseDto {

    private UUID id;
    private String name;
    private NotificationChannel channel;
    private String subjectTemplate;
    private String bodyTemplate;
    private boolean active;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public NotificationChannel getChannel() {
        return channel;
    }

    public void setChannel(NotificationChannel channel) {
        this.channel = channel;
    }

    public String getSubjectTemplate() {
        return subjectTemplate;
    }

    public void setSubjectTemplate(String subjectTemplate) {
        this.subjectTemplate = subjectTemplate;
    }

    public String getBodyTemplate() {
        return bodyTemplate;
    }

    public void setBodyTemplate(String bodyTemplate) {
        this.bodyTemplate = bodyTemplate;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
