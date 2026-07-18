package com.training.notificationservice.dto.response;

import com.training.notificationservice.enums.NotificationChannel;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Outbound wire contract for a notification template.
 */
@Schema(name = "NotificationTemplateResponse", description = "A stored, reusable notification template")
public class NotificationTemplateResponseDto {

    @Schema(description = "Server-generated template id", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
    private UUID id;

    @Schema(description = "Unique template name", example = "order-confirmed-email")
    private String name;

    @Schema(description = "Notification channel this template targets", example = "EMAIL")
    private NotificationChannel channel;

    @Schema(description = "Subject line template, with {{placeholder}} tokens", example = "Your order #{{orderId}} is confirmed")
    private String subjectTemplate;

    @Schema(description = "Body template, with {{placeholder}} tokens", example = "Hi {{name}}, your order #{{orderId}} has shipped.")
    private String bodyTemplate;

    @Schema(description = "When the template was first created", example = "2026-07-16T10:15:30")
    private LocalDateTime createdAt;

    @Schema(description = "When the template was last updated", example = "2026-07-18T09:00:00")
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
