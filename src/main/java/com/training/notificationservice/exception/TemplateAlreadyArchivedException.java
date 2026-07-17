package com.training.notificationservice.exception;

import java.util.UUID;

/**
 * Thrown when an archive request targets a template that is already
 * inactive. Handled by the scoped {@link TemplateCatalogExceptionHandler}
 * with a 409 CONFLICT, matching the pattern {@link DuplicateTemplateNameException}
 * uses for the original template CRUD feature.
 */
public class TemplateAlreadyArchivedException extends NotificationServiceException {

    public TemplateAlreadyArchivedException(UUID id) {
        super("Notification template " + id + " is already archived");
    }
}
