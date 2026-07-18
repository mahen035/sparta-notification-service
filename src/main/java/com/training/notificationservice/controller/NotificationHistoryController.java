package com.training.notificationservice.controller;

import com.training.notificationservice.dto.response.NotificationResponseDto;
import com.training.notificationservice.enums.NotificationChannel;
import com.training.notificationservice.enums.NotificationStatus;
import com.training.notificationservice.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Dedicated read-only route for notification history search, per the
 * Developer 5 task assignment.
 * <p>
 * Note for reviewers: Developer 1's {@code NotificationController} already
 * exposes an identical recipient/status/channel/page/size filter contract at
 * {@code GET /api/v1/notifications} (see {@code NotificationService#searchNotifications}
 * and {@code NotificationSpecifications}). Rather than re-implement that
 * query, this controller deliberately delegates to the same service method -
 * one filtering implementation, reused, so there is no drift between the two
 * routes. This class exists to give operations/support tooling a
 * discoverable, semantically-named {@code /history} path, not to duplicate
 * logic.
 */
@RestController
@RequestMapping("/api/v1/notifications/history")
@Tag(name = "Notification History", description = "Read-only historical search across all notification channels")
public class NotificationHistoryController {

    private static final Logger log = LoggerFactory.getLogger(NotificationHistoryController.class);

    private final NotificationService notificationService;

    public NotificationHistoryController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    @Operation(
            summary = "Search notification history",
            description = "Filters across every channel (email, SMS, in-app) by recipient, status and/or channel, with pagination. "
                    + "All filters are optional and combine with AND. When no rows match, returns 200 with an empty page - never a 404."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Page of matching notifications (possibly empty)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = NotificationResponseDto.class)))
    })
    public ResponseEntity<Page<NotificationResponseDto>> searchHistory(
            @Parameter(description = "Filter by exact recipient (email address, phone number, or user id)", example = "jane@example.com")
            @RequestParam(required = false) String recipient,
            @Parameter(description = "Filter by delivery status")
            @RequestParam(required = false) NotificationStatus status,
            @Parameter(description = "Filter by channel")
            @RequestParam(required = false) NotificationChannel channel,
            @Parameter(description = "Zero-based page index, page size, and optional sort, e.g. ?page=0&size=20&sort=createdAt,desc")
            Pageable pageable) {
        log.info("GET /api/v1/notifications/history recipient={}, status={}, channel={}, page={}",
                recipient, status, channel, pageable);
        return ResponseEntity.ok(notificationService.searchNotifications(recipient, status, channel, pageable));
    }
}
