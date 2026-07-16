package com.training.notificationservice.controller;

import com.training.notificationservice.dto.request.NotificationRequestDto;
import com.training.notificationservice.dto.request.SmsNotificationRequestDto;
import com.training.notificationservice.dto.response.NotificationResponseDto;
import com.training.notificationservice.enums.NotificationChannel;
import com.training.notificationservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * SMS-specific creation endpoint. Thin by design: validates the phone-number
 * shaped request, maps it onto the shared {@link NotificationRequestDto}, and
 * delegates everything else (persistence, dispatch, status transitions) to
 * {@link NotificationService} - the same orchestration Developer 1 already
 * built for every channel.
 */
@RestController
@RequestMapping("/api/v1/notifications/sms")
@Tag(name = "SMS Notifications", description = "SMS-specific notification creation")
public class SmsNotificationController {

    private static final Logger log = LoggerFactory.getLogger(SmsNotificationController.class);

    private final NotificationService notificationService;

    public SmsNotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @PostMapping
    @Operation(summary = "Create and dispatch an SMS notification")
    public ResponseEntity<NotificationResponseDto> createSmsNotification(
            @Valid @RequestBody SmsNotificationRequestDto request) {
        log.info("POST /api/v1/notifications/sms");
        NotificationResponseDto created = notificationService.createNotification(toGenericRequest(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    private NotificationRequestDto toGenericRequest(SmsNotificationRequestDto request) {
        NotificationRequestDto generic = new NotificationRequestDto();
        generic.setRecipient(request.getRecipient());
        generic.setChannel(NotificationChannel.SMS);
        generic.setMessage(request.getMessage());
        generic.setTemplateId(request.getTemplateId());
        return generic;
    }
}
