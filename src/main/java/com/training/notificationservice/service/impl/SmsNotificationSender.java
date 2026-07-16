package com.training.notificationservice.service.impl;

import com.training.notificationservice.entity.Notification;
import com.training.notificationservice.enums.NotificationChannel;
import com.training.notificationservice.exception.NotificationServiceException;
import com.training.notificationservice.service.NotificationSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

/**
 * Mock SMS provider adapter - no real carrier call is made (out of scope for
 * this phase). Simulates provider outcomes via a configurable failure rate so
 * {@code NotificationServiceImpl}'s PENDING/SENT/FAILED transition and retry
 * count can be exercised end to end without a real Twilio/MSG91 account.
 */
@Component
public class SmsNotificationSender implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(SmsNotificationSender.class);

    private final double failureRate;

    public SmsNotificationSender(@Value("${notification.sms.mock.failure-rate:0.2}") double failureRate) {
        this.failureRate = failureRate;
    }

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.SMS;
    }

    @Override
    public void send(Notification notification) {
        String maskedRecipient = mask(notification.getRecipient());
        double roll = ThreadLocalRandom.current().nextDouble();

        if (roll < failureRate) {
            log.warn("Simulated SMS send failure for recipient={}", maskedRecipient);
            throw new NotificationServiceException(
                    "Simulated SMS provider failure for recipient " + maskedRecipient);
        }

        log.info("Simulated SMS sent to recipient={}", maskedRecipient);
    }

    private String mask(String phone) {
        if (phone == null || phone.length() <= 2) {
            return "***";
        }
        int visible = 2;
        return "*".repeat(phone.length() - visible) + phone.substring(phone.length() - visible);
    }
}
