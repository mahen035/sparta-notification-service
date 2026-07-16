package com.training.notificationservice.repository;

import com.training.notificationservice.entity.Notification;
import com.training.notificationservice.enums.NotificationChannel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface InAppNotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByRecipientAndChannel(String recipient, NotificationChannel channel, Pageable pageable);
    Optional<Notification> findByIdAndChannel(UUID id, NotificationChannel channel);
}