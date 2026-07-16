package com.training.notificationservice.controller;

import com.training.notificationservice.dto.request.NotificationTemplateRequestDto;
import com.training.notificationservice.dto.response.NotificationTemplateResponseDto;
import com.training.notificationservice.service.NotificationTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Thin REST controller for notification template CRUD. All business logic
 * lives in {@link NotificationTemplateService}, matching Developer 1's
 * layering convention in {@code NotificationController}.
 */
@RestController
@RequestMapping("/api/v1/templates")
@Tag(name = "Notification Templates", description = "CRUD operations for reusable notification templates")
public class NotificationTemplateController {

    private static final Logger log = LoggerFactory.getLogger(NotificationTemplateController.class);

    private final NotificationTemplateService templateService;

    public NotificationTemplateController(NotificationTemplateService templateService) {
        this.templateService = templateService;
    }

    @PostMapping
    @Operation(summary = "Create a notification template")
    public ResponseEntity<NotificationTemplateResponseDto> create(
            @Valid @RequestBody NotificationTemplateRequestDto request) {
        log.info("POST /api/v1/templates name={}", request.getName());
        NotificationTemplateResponseDto created = templateService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Fetch a single template by id")
    public ResponseEntity<NotificationTemplateResponseDto> getById(@PathVariable UUID id) {
        log.info("GET /api/v1/templates/{}", id);
        return ResponseEntity.ok(templateService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing template")
    public ResponseEntity<NotificationTemplateResponseDto> update(
            @PathVariable UUID id,
            @Valid @RequestBody NotificationTemplateRequestDto request) {
        log.info("PUT /api/v1/templates/{}", id);
        return ResponseEntity.ok(templateService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a template")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        log.info("DELETE /api/v1/templates/{}", id);
        templateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
