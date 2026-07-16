package com.training.notificationservice.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

/**
 * Inbound wire contract for the SMS-specific creation endpoint. Kept separate
 * from the generic {@link NotificationRequestDto} so the phone number format
 * can be validated up front, before it ever reaches the shared service layer.
 */
public class SmsNotificationRequestDto {

    @NotBlank(message = "recipient is required")
    @Pattern(regexp = "^\\+?[0-9]{7,15}$", message = "recipient must be a valid phone number (7-15 digits, optional leading +)")
    private String recipient;

    @NotBlank(message = "message is required")
    private String message;

    private UUID templateId;

    public SmsNotificationRequestDto() {
    }

    public String getRecipient() {
        return recipient;
    }

    public void setRecipient(String recipient) {
        this.recipient = recipient;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public UUID getTemplateId() {
        return templateId;
    }

    public void setTemplateId(UUID templateId) {
        this.templateId = templateId;
    }
}
