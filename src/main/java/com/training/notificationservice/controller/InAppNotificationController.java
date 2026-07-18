package com.training.notificationservice.controller;

import com.training.notificationservice.dto.request.InAppNotificationRequestDto;
import com.training.notificationservice.dto.response.InAppNotificationResponseDto;
import com.training.notificationservice.service.InAppNotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/notifications/in-app")
@Tag(name = "In-App Notifications", description = "Send, list, and mark-as-read in-app notifications")
public class InAppNotificationController {

    private final InAppNotificationService service;

    public InAppNotificationController(InAppNotificationService service) {
        this.service = service;
    }

    @PostMapping
    @Operation(summary = "Send an in-app notification (stored unread)")
    public ResponseEntity<InAppNotificationResponseDto> create(
            @Valid @RequestBody InAppNotificationRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(request));
    }

    @GetMapping("/user/{recipient}")
    @Operation(summary = "List a recipient's in-app notifications, most recent first, paginated")
    public ResponseEntity<Page<InAppNotificationResponseDto>> listForUser(
            @PathVariable String recipient,
            @PageableDefault(size = 20, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable) {
        return ResponseEntity.ok(service.listForUser(recipient, pageable));
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Mark an in-app notification as read")
    public ResponseEntity<InAppNotificationResponseDto> markAsRead(@PathVariable UUID id) {
        return ResponseEntity.ok(service.markAsRead(id));
    }
}