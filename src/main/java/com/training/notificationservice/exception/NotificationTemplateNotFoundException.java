package com.training.notificationservice.exception;

import java.util.UUID;

/**
 * Thrown when a template lookup by id finds no matching row. Extends
 * {@link NotificationNotFoundException} so it is automatically caught by
 * {@link GlobalExceptionHandler}'s existing {@code NotificationNotFoundException}
 * handler and returns 404 - no changes to that shared class are needed.
 */
public class NotificationTemplateNotFoundException extends NotificationNotFoundException {

    public NotificationTemplateNotFoundException(UUID id) {
        super("Notification template not found with id: " + id);
    }
}
