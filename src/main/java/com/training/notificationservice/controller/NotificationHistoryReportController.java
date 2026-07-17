package com.training.notificationservice.controller;

import com.training.notificationservice.dto.response.NotificationHistorySummaryResponseDto;
import com.training.notificationservice.dto.response.NotificationResponseDto;
import com.training.notificationservice.enums.NotificationChannel;
import com.training.notificationservice.enums.NotificationStatus;
import com.training.notificationservice.service.NotificationHistoryReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * REST endpoints for the history reporting features (summary stats, CSV
 * export, per-template history). Mounted on the same base path as
 * {@code NotificationHistoryController} but only adds new sub-paths
 * ({@code /summary}, {@code /export}, {@code /by-template/{id}}), so there
 * is no route collision with that controller's {@code GET} search endpoint
 * - it is never modified.
 */
@RestController
@RequestMapping("/api/v1/notifications/history")
@Tag(name = "Notification History Reports", description = "Summary stats, CSV export, and per-template history")
public class NotificationHistoryReportController {

    private static final Logger log = LoggerFactory.getLogger(NotificationHistoryReportController.class);

    private final NotificationHistoryReportService historyReportService;

    public NotificationHistoryReportController(NotificationHistoryReportService historyReportService) {
        this.historyReportService = historyReportService;
    }

    @GetMapping("/summary")
    @Operation(summary = "Counts of notifications by status and channel over a date range")
    public ResponseEntity<NotificationHistorySummaryResponseDto> summary(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        log.info("GET /api/v1/notifications/history/summary from={}, to={}", from, to);
        return ResponseEntity.ok(historyReportService.summarize(from, to));
    }

    @GetMapping(value = "/export", produces = "text/csv")
    @Operation(summary = "Export filtered notification history as a downloadable CSV")
    public ResponseEntity<byte[]> export(
            @RequestParam(required = false) String recipient,
            @RequestParam(required = false) NotificationStatus status,
            @RequestParam(required = false) NotificationChannel channel,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to) {
        log.info("GET /api/v1/notifications/history/export recipient={}, status={}, channel={}, from={}, to={}",
                recipient, status, channel, from, to);
        List<NotificationResponseDto> rows = historyReportService.exportHistory(recipient, status, channel, from, to);
        byte[] csv = toCsv(rows).getBytes(StandardCharsets.UTF_8);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.attachment().filename("notification-history.csv").build());
        headers.setContentType(MediaType.parseMediaType("text/csv"));
        return ResponseEntity.ok().headers(headers).body(csv);
    }

    @GetMapping("/by-template/{templateId}")
    @Operation(summary = "All notifications sent from a given template")
    public ResponseEntity<Page<NotificationResponseDto>> byTemplate(@PathVariable UUID templateId,
                                                                     Pageable pageable) {
        log.info("GET /api/v1/notifications/history/by-template/{}", templateId);
        return ResponseEntity.ok(historyReportService.findByTemplate(templateId, pageable));
    }

    private String toCsv(List<NotificationResponseDto> rows) {
        StringBuilder sb = new StringBuilder();
        sb.append("id,recipient,channel,subject,message,templateId,status,retryCount,read,createdAt,updatedAt\n");
        for (NotificationResponseDto row : rows) {
            sb.append(csvEscape(row.getId())).append(',')
                    .append(csvEscape(row.getRecipient())).append(',')
                    .append(csvEscape(row.getChannel())).append(',')
                    .append(csvEscape(row.getSubject())).append(',')
                    .append(csvEscape(row.getMessage())).append(',')
                    .append(csvEscape(row.getTemplateId())).append(',')
                    .append(csvEscape(row.getStatus())).append(',')
                    .append(csvEscape(row.getRetryCount())).append(',')
                    .append(csvEscape(row.isRead())).append(',')
                    .append(csvEscape(row.getCreatedAt())).append(',')
                    .append(csvEscape(row.getUpdatedAt()))
                    .append('\n');
        }
        return sb.toString();
    }

    private String csvEscape(Object value) {
        if (value == null) {
            return "";
        }
        String text = value.toString();
        if (text.contains(",") || text.contains("\"") || text.contains("\n")) {
            return "\"" + text.replace("\"", "\"\"") + "\"";
        }
        return text;
    }
}
