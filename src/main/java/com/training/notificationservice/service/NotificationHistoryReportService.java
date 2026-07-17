package com.training.notificationservice.service;

import com.training.notificationservice.dto.response.NotificationHistorySummaryResponseDto;
import com.training.notificationservice.dto.response.NotificationResponseDto;
import com.training.notificationservice.enums.NotificationChannel;
import com.training.notificationservice.enums.NotificationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Business-logic contract for the history reporting features (summary, CSV
 * export, per-template history), kept independent of HTTP and persistence.
 */
public interface NotificationHistoryReportService {

    NotificationHistorySummaryResponseDto summarize(LocalDateTime from, LocalDateTime to);

    List<NotificationResponseDto> exportHistory(String recipient, NotificationStatus status,
                                                 NotificationChannel channel, LocalDateTime from, LocalDateTime to);

    Page<NotificationResponseDto> findByTemplate(UUID templateId, Pageable pageable);
}
