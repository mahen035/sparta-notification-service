package com.training.notificationservice.controller;

import com.training.notificationservice.dto.response.NotificationResponseDto;
import com.training.notificationservice.enums.NotificationChannel;
import com.training.notificationservice.enums.NotificationStatus;
import com.training.notificationservice.service.NotificationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Verifies that the history endpoint is a pure delegate to
 * {@link NotificationService#searchNotifications}, with no filtering logic
 * of its own to test in isolation.
 */
@ExtendWith(MockitoExtension.class)
class NotificationHistoryControllerTest {

    @Mock
    private NotificationService notificationService;

    @Test
    void searchHistory_delegatesToNotificationServiceWithSameArguments() {
        NotificationHistoryController controller = new NotificationHistoryController(notificationService);
        Pageable pageable = PageRequest.of(0, 20);
        Page<NotificationResponseDto> page = new PageImpl<>(List.of(new NotificationResponseDto()));
        when(notificationService.searchNotifications("jane@example.com", NotificationStatus.SENT,
                NotificationChannel.EMAIL, pageable)).thenReturn(page);

        ResponseEntity<Page<NotificationResponseDto>> response = controller.searchHistory(
                "jane@example.com", NotificationStatus.SENT, NotificationChannel.EMAIL, pageable);

        assertThat(response.getBody()).isEqualTo(page);
        verify(notificationService).searchNotifications("jane@example.com", NotificationStatus.SENT,
                NotificationChannel.EMAIL, pageable);
    }
}
