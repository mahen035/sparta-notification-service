package com.training.notificationservice.service.impl;

import com.training.notificationservice.entity.Notification;
import com.training.notificationservice.enums.NotificationChannel;
import com.training.notificationservice.exception.NotificationServiceException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class SmsNotificationSenderTest {

    private Notification smsNotification() {
        Notification notification = new Notification();
        notification.setRecipient("+15551234567");
        notification.setChannel(NotificationChannel.SMS);
        notification.setMessage("Your order has shipped");
        return notification;
    }

    @Test
    void reportsSmsChannel() {
        SmsNotificationSender sender = new SmsNotificationSender(0.0);
        assertThat(sender.getChannel()).isEqualTo(NotificationChannel.SMS);
    }

    @Test
    void sendsSuccessfullyWhenFailureRateIsZero() {
        SmsNotificationSender sender = new SmsNotificationSender(0.0);
        assertThatCode(() -> sender.send(smsNotification())).doesNotThrowAnyException();
    }

    @Test
    void throwsNotificationServiceExceptionWhenFailureRateIsOne() {
        SmsNotificationSender sender = new SmsNotificationSender(1.0);
        assertThatThrownBy(() -> sender.send(smsNotification()))
                .isInstanceOf(NotificationServiceException.class)
                .hasMessageContaining("Simulated SMS provider failure");
    }
}
