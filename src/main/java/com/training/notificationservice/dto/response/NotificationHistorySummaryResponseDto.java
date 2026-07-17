package com.training.notificationservice.dto.response;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Outbound wire contract for the notification history summary: total count
 * plus breakdowns by status and channel over the requested date range - a
 * dashboard-widget-friendly aggregate, distinct from the row-level
 * {@link NotificationResponseDto} used by search/export/by-template.
 */
public class NotificationHistorySummaryResponseDto {

    private LocalDateTime from;
    private LocalDateTime to;
    private long totalCount;
    private Map<String, Long> countsByStatus;
    private Map<String, Long> countsByChannel;

    public LocalDateTime getFrom() {
        return from;
    }

    public void setFrom(LocalDateTime from) {
        this.from = from;
    }

    public LocalDateTime getTo() {
        return to;
    }

    public void setTo(LocalDateTime to) {
        this.to = to;
    }

    public long getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(long totalCount) {
        this.totalCount = totalCount;
    }

    public Map<String, Long> getCountsByStatus() {
        return countsByStatus;
    }

    public void setCountsByStatus(Map<String, Long> countsByStatus) {
        this.countsByStatus = countsByStatus;
    }

    public Map<String, Long> getCountsByChannel() {
        return countsByChannel;
    }

    public void setCountsByChannel(Map<String, Long> countsByChannel) {
        this.countsByChannel = countsByChannel;
    }
}
