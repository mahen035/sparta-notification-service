package com.training.notificationservice.service.impl;

import com.training.notificationservice.dto.response.NotificationHistorySummaryResponseDto;
import com.training.notificationservice.dto.response.NotificationResponseDto;
import com.training.notificationservice.entity.Notification;
import com.training.notificationservice.enums.NotificationChannel;
import com.training.notificationservice.enums.NotificationStatus;
import com.training.notificationservice.repository.NotificationHistoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationHistoryReportServiceImplTest {

    @Mock
    private NotificationHistoryRepository historyRepository;

    private NotificationHistoryReportServiceImpl service() {
        return new NotificationHistoryReportServiceImpl(historyRepository);
    }

    private static NotificationHistoryRepository.StatusCount statusCount(NotificationStatus status, long total) {
        return new NotificationHistoryRepository.StatusCount() {
            @Override
            public NotificationStatus getStatus() {
                return status;
            }

            @Override
            public long getTotal() {
                return total;
            }
        };
    }

    private static NotificationHistoryRepository.ChannelCount channelCount(NotificationChannel channel, long total) {
        return new NotificationHistoryRepository.ChannelCount() {
            @Override
            public NotificationChannel getChannel() {
                return channel;
            }

            @Override
            public long getTotal() {
                return total;
            }
        };
    }

    @Test
    void summarize_aggregatesCountsByStatusAndChannel() {
        when(historyRepository.countByStatusInRange(any(), any()))
                .thenReturn(List.of(statusCount(NotificationStatus.SENT, 3L), statusCount(NotificationStatus.FAILED, 1L)));
        when(historyRepository.countByChannelInRange(any(), any()))
                .thenReturn(List.of(channelCount(NotificationChannel.EMAIL, 4L)));

        NotificationHistorySummaryResponseDto result = service().summarize(null, null);

        assertThat(result.getTotalCount()).isEqualTo(4L);
        assertThat(result.getCountsByStatus()).containsEntry("SENT", 3L).containsEntry("FAILED", 1L);
        assertThat(result.getCountsByChannel()).containsEntry("EMAIL", 4L);
    }

    @Test
    void findByTemplate_mapsPagedEntitiesToResponseDtos() {
        UUID templateId = UUID.randomUUID();
        Notification notification = new Notification();
        notification.setId(UUID.randomUUID());
        notification.setRecipient("jane@example.com");
        notification.setChannel(NotificationChannel.EMAIL);
        notification.setMessage("Hello");
        notification.setTemplateId(templateId);
        notification.setStatus(NotificationStatus.SENT);
        notification.setRetryCount(0);

        Pageable pageable = PageRequest.of(0, 20);
        Page<Notification> page = new PageImpl<>(List.of(notification));
        when(historyRepository.findAll(any(Specification.class), org.mockito.ArgumentMatchers.eq(pageable)))
                .thenReturn(page);

        Page<NotificationResponseDto> result = service().findByTemplate(templateId, pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getTemplateId()).isEqualTo(templateId);
    }

    @Test
    void exportHistory_mapsMatchingRowsToResponseDtos() {
        Notification notification = new Notification();
        notification.setId(UUID.randomUUID());
        notification.setRecipient("jane@example.com");
        notification.setChannel(NotificationChannel.SMS);
        notification.setMessage("Hi there");
        notification.setStatus(NotificationStatus.PENDING);
        notification.setRetryCount(0);

        when(historyRepository.findAll(any(Specification.class), any(org.springframework.data.domain.Sort.class)))
                .thenReturn(List.of(notification));

        List<NotificationResponseDto> result = service().exportHistory(null, null, null,
                LocalDateTime.now().minusDays(1), LocalDateTime.now());

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getRecipient()).isEqualTo("jane@example.com");
    }
}
