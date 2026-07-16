package com.training.notificationservice.dto.request;

import jakarta.validation.constraints.NotBlank;

public record InAppNotificationRequestDto(
        @NotBlank(message = "recipient is required") String recipient,
        String subject,                                    // optional for in-app
        @NotBlank(message = "message is required") String message
) {}