package com.training.notificationservice.service.impl;

import com.training.notificationservice.dto.request.InAppNotificationRequestDto;
import com.training.notificationservice.dto.response.InAppNotificationResponseDto;
import com.training.notificationservice.entity.Notification;
import com.training.notificationservice.enums.NotificationChannel;
import com.training.notificationservice.enums.NotificationStatus;
import com.training.notificationservice.exception.NotificationNotFoundException;
import com.training.notificationservice.repository.InAppNotificationRepository;
import com.training.notificationservice.service.InAppNotificationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
public class InAppNotificationServiceImpl implements InAppNotificationService {

    private final InAppNotificationRepository repository;
    private final InAppNotificationSender sender;

    public InAppNotificationServiceImpl(InAppNotificationRepository repository, InAppNotificationSender sender) {
        this.repository = repository;
        this.sender = sender;
    }

    @Override
    @Transactional
    public InAppNotificationResponseDto create(InAppNotificationRequestDto request) {
        Notification n = new Notification();
        n.setRecipient(request.recipient());
        n.setChannel(NotificationChannel.IN_APP);
        n.setSubject(request.subject());
        n.setMessage(request.message());
        n.setStatus(NotificationStatus.PENDING);
        n.setRead(false);
        Notification saved = repository.save(n);   // durable PENDING row first

        try {
            sender.send(saved);
            saved.setStatus(NotificationStatus.SENT);
        } catch (Exception e) {
            saved.setStatus(NotificationStatus.FAILED);
        }
        return toResponse(repository.save(saved));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<InAppNotificationResponseDto> listForUser(String recipient, Pageable pageable) {
        return repository.findByRecipientAndChannel(recipient, NotificationChannel.IN_APP, pageable)
                .map(this::toResponse);
    }

    @Override
    @Transactional
    public InAppNotificationResponseDto markAsRead(UUID id) {
        Notification n = repository.findByIdAndChannel(id, NotificationChannel.IN_APP)
                .orElseThrow(() -> new NotificationNotFoundException("In-app notification not found with id: " + id));
        n.setRead(true);
        return toResponse(repository.save(n));
    }

    private InAppNotificationResponseDto toResponse(Notification n) {
        return new InAppNotificationResponseDto(
                n.getId(), n.getRecipient(), n.getChannel(), n.getSubject(),
                n.getMessage(), n.getStatus(), n.isRead(), n.getCreatedAt(), n.getUpdatedAt());
    }
}