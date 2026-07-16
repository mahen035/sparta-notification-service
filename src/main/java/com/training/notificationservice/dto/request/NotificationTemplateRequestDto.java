package com.training.notificationservice.dto.request;

import com.training.notificationservice.enums.NotificationChannel;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Inbound wire contract for creating/updating a notification template. Kept
 * separate from the {@code NotificationTemplate} entity so the persistence
 * model can evolve without breaking API consumers.
 */
public class NotificationTemplateRequestDto {

    @NotBlank(message = "name is required")
    @Size(max = 100, message = "name must be at most 100 characters")
    private String name;

    @NotNull(message = "channel is required")
    private NotificationChannel channel;

    @Size(max = 150, message = "subjectTemplate must be at most 150 characters")
    private String subjectTemplate;

    @NotBlank(message = "bodyTemplate is required")
    private String bodyTemplate;

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
}
