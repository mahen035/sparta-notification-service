package com.training.notificationservice.dto.request;

import com.training.notificationservice.enums.NotificationChannel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Inbound wire contract for creating/updating a notification template. Kept
 * separate from the {@code NotificationTemplate} entity so the persistence
 * model can evolve without breaking API consumers.
 */
@Schema(name = "NotificationTemplateRequest", description = "Payload for creating or updating a reusable notification template")
public class NotificationTemplateRequestDto {

    @Schema(description = "Unique, human-readable template name", example = "order-confirmed-email", maxLength = 100, requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "name is required")
    @Size(max = 100, message = "name must be at most 100 characters")
    private String name;

    @Schema(description = "Notification channel this template is designed for", example = "EMAIL", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "channel is required")
    private NotificationChannel channel;

    @Schema(description = "Optional subject line template (ignored for channels without a subject, e.g. SMS). Supports {{placeholder}} tokens.",
            example = "Your order #{{orderId}} is confirmed", maxLength = 150)
    @Size(max = 150, message = "subjectTemplate must be at most 150 characters")
    private String subjectTemplate;

    @Schema(description = "Body template. Supports {{placeholder}} tokens filled in at send time.",
            example = "Hi {{name}}, your order #{{orderId}} has shipped.", requiredMode = Schema.RequiredMode.REQUIRED)
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
