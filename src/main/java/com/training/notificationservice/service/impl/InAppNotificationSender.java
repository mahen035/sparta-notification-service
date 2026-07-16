// service/impl/InAppNotificationSender.java
package com.training.notificationservice.service.impl;

import com.training.notificationservice.entity.Notification;
import com.training.notificationservice.enums.NotificationChannel;
import com.training.notificationservice.service.NotificationSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class InAppNotificationSender implements NotificationSender {

    private static final Logger log = LoggerFactory.getLogger(InAppNotificationSender.class);

    @Override
    public NotificationChannel getChannel() {
        return NotificationChannel.IN_APP;
    }

    @Override
    public void send(Notification notification) {
        log.info("In-app notification stored for recipient={}, id={}",
                notification.getRecipient(), notification.getId());
    }
}