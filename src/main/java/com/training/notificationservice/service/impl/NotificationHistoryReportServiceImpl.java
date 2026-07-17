package com.training.notificationservice.service.impl;

import com.training.notificationservice.dto.response.NotificationHistorySummaryResponseDto;
import com.training.notificationservice.dto.response.NotificationResponseDto;
import com.training.notificationservice.entity.Notification;
import com.training.notificationservice.enums.NotificationChannel;
import com.training.notificationservice.enums.NotificationStatus;
import com.training.notificationservice.repository.NotificationHistoryRepository;
import com.training.notificationservice.repository.NotificationHistorySpecifications;
import com.training.notificationservice.service.NotificationHistoryReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Orchestrates the history reporting features (summary/export/by-template),
 * built on {@link NotificationHistoryRepository} so it never has to modify
 * Developer 1's {@code NotificationRepository} or {@code NotificationServiceImpl}.
 */
@Service
public class NotificationHistoryReportServiceImpl implements NotificationHistoryReportService {

    private static final Logger log = LoggerFactory.getLogger(NotificationHistoryReportServiceImpl.class);

    /** Sensible default lower bound for an unbounded summary - well within Postgres's timestamp range. */
    private static final LocalDateTime DEFAULT_RANGE_START = LocalDateTime.of(2000, 1, 1, 0, 0);

    private final NotificationHistoryRepository historyRepository;

    public NotificationHistoryReportServiceImpl(NotificationHistoryRepository historyRepository) {
        this.historyRepository = historyRepository;
    }

    @Override
    public NotificationHistorySummaryResponseDto summarize(LocalDateTime from, LocalDateTime to) {
        LocalDateTime rangeStart = from != null ? from : DEFAULT_RANGE_START;
        LocalDateTime rangeEnd = to != null ? to : LocalDateTime.now();

        List<NotificationHistoryRepository.StatusCount> statusCounts =
                historyRepository.countByStatusInRange(rangeStart, rangeEnd);
        List<NotificationHistoryRepository.ChannelCount> channelCounts =
                historyRepository.countByChannelInRange(rangeStart, rangeEnd);

        Map<String, Long> byStatus = statusCounts.stream()
                .collect(Collectors.toMap(sc -> sc.getStatus().name(),
                        NotificationHistoryRepository.StatusCount::getTotal));
        Map<String, Long> byChannel = channelCounts.stream()
                .collect(Collectors.toMap(cc -> cc.getChannel().name(),
                        NotificationHistoryRepository.ChannelCount::getTotal));

        long total = byStatus.values().stream().mapToLong(Long::longValue).sum();

        log.info("Computed notification history summary from={} to={} total={}", rangeStart, rangeEnd, total);

        NotificationHistorySummaryResponseDto dto = new NotificationHistorySummaryResponseDto();
        dto.setFrom(from);
        dto.setTo(to);
        dto.setTotalCount(total);
        dto.setCountsByStatus(byStatus);
        dto.setCountsByChannel(byChannel);
        return dto;
    }

    @Override
    public List<NotificationResponseDto> exportHistory(String recipient, NotificationStatus status,
                                                         NotificationChannel channel, LocalDateTime from,
                                                         LocalDateTime to) {
        List<Notification> notifications = historyRepository.findAll(
                NotificationHistorySpecifications.filterBy(recipient, status, channel, null, from, to),
                Sort.by(Sort.Direction.DESC, "createdAt"));
        log.info("Exporting {} notifications to CSV recipient={}, status={}, channel={}",
                notifications.size(), recipient, status, channel);
        return notifications.stream().map(this::toResponseDto).toList();
    }

    @Override
    public Page<NotificationResponseDto> findByTemplate(UUID templateId, Pageable pageable) {
        log.debug("Looking up notification history for template {}", templateId);
        return historyRepository
                .findAll(NotificationHistorySpecifications.withTemplateId(templateId), pageable)
                .map(this::toResponseDto);
    }

    private NotificationResponseDto toResponseDto(Notification notification) {
        NotificationResponseDto dto = new NotificationResponseDto();
        dto.setId(notification.getId());
        dto.setRecipient(notification.getRecipient());
        dto.setChannel(notification.getChannel());
        dto.setSubject(notification.getSubject());
        dto.setMessage(notification.getMessage());
        dto.setTemplateId(notification.getTemplateId());
        dto.setStatus(notification.getStatus());
        dto.setRetryCount(notification.getRetryCount());
        dto.setRead(notification.isRead());
        dto.setCreatedAt(notification.getCreatedAt());
        dto.setUpdatedAt(notification.getUpdatedAt());
        return dto;
    }
}
