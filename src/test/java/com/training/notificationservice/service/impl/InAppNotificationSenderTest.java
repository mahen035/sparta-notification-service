package com.training.notificationservice.service.impl;

import com.training.notificationservice.entity.Notification;
import com.training.notificationservice.enums.NotificationChannel;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

class InAppNotificationSenderTest {

    private final InAppNotificationSender sender = new InAppNotificationSender();

    private Notification inAppNotification() {
        Notification notification = new Notification();
        notification.setId(UUID.randomUUID());
        notification.setRecipient("user-42");
        notification.setChannel(NotificationChannel.IN_APP);
        notification.setMessage("Your order has shipped");
        return notification;
    }

    @Test
    void reportsInAppChannel() {
        assertThat(sender.getChannel()).isEqualTo(NotificationChannel.IN_APP);
    }

    @Test
    void sendStoresNotificationWithoutThrowing() {
        // In-app "delivery" is simply persistence + a log line - it never calls
        // an external provider, so it must always succeed.
        assertThatCode(() -> sender.send(inAppNotification())).doesNotThrowAnyException();
    }
}
