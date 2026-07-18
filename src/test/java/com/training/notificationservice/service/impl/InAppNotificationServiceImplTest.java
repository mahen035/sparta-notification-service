package com.training.notificationservice.service.impl;

import com.training.notificationservice.dto.request.InAppNotificationRequestDto;
import com.training.notificationservice.dto.response.InAppNotificationResponseDto;
import com.training.notificationservice.entity.Notification;
import com.training.notificationservice.enums.NotificationChannel;
import com.training.notificationservice.enums.NotificationStatus;
import com.training.notificationservice.exception.NotificationNotFoundException;
import com.training.notificationservice.repository.InAppNotificationRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InAppNotificationServiceImplTest {

    @Mock
    private InAppNotificationRepository repository;

    @Mock
    private InAppNotificationSender sender;

    @InjectMocks
    private InAppNotificationServiceImpl service;

    private InAppNotificationRequestDto request() {
        return new InAppNotificationRequestDto("user-42", "Order update", "Your order has shipped");
    }

    private Notification inAppRow(boolean isRead) {
        Notification n = new Notification();
        n.setRecipient("user-42");
        n.setChannel(NotificationChannel.IN_APP);
        n.setMessage("Your order has shipped");
        n.setStatus(NotificationStatus.SENT);
        n.setRead(isRead);
        return n;
    }

    @Test
    void createPersistsUnreadRowThenMarksSentOnSuccessfulSend() {
        when(repository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        InAppNotificationResponseDto response = service.create(request());

        assertThat(response.recipient()).isEqualTo("user-42");
        assertThat(response.channel()).isEqualTo(NotificationChannel.IN_APP);
        assertThat(response.status()).isEqualTo(NotificationStatus.SENT);
        assertThat(response.isRead()).isFalse();

        // Delivery is attempted, and the row is persisted twice: an unread row
        // is saved first (durability checkpoint), then again after the send
        // flips it to SENT.
        verify(sender).send(any(Notification.class));
        verify(repository, times(2)).save(any(Notification.class));
    }

    @Test
    void createMarksFailedWhenSenderThrows() {
        when(repository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));
        doThrow(new RuntimeException("boom")).when(sender).send(any(Notification.class));

        InAppNotificationResponseDto response = service.create(request());

        assertThat(response.status()).isEqualTo(NotificationStatus.FAILED);
    }

    @Test
    void listForUserReturnsInAppRowsForRecipient() {
        Pageable pageable = PageRequest.of(0, 20);
        Page<Notification> page = new PageImpl<>(List.of(inAppRow(false)));
        when(repository.findByRecipientAndChannel("user-42", NotificationChannel.IN_APP, pageable))
                .thenReturn(page);

        Page<InAppNotificationResponseDto> result = service.listForUser("user-42", pageable);

        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).recipient()).isEqualTo("user-42");
        assertThat(result.getContent().get(0).channel()).isEqualTo(NotificationChannel.IN_APP);
    }

    @Test
    void markAsReadFlipsIsReadToTrue() {
        UUID id = UUID.randomUUID();
        when(repository.findByIdAndChannel(id, NotificationChannel.IN_APP))
                .thenReturn(Optional.of(inAppRow(false)));
        when(repository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        InAppNotificationResponseDto response = service.markAsRead(id);

        assertThat(response.isRead()).isTrue();
    }

    @Test
    void markAsReadThrowsNotFoundForUnknownId() {
        UUID id = UUID.randomUUID();
        when(repository.findByIdAndChannel(id, NotificationChannel.IN_APP)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.markAsRead(id))
                .isInstanceOf(NotificationNotFoundException.class)
                .hasMessageContaining(id.toString());
    }
}
