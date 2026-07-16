package com.training.notificationservice.exception;

/**
 * Thrown when a template create/update would violate the unique name
 * constraint. Handled by a scoped {@link TemplateExceptionHandler} rather
 * than the shared {@link GlobalExceptionHandler}, since a 409 CONFLICT
 * mapping for this case is specific to the templates feature.
 */
public class DuplicateTemplateNameException extends NotificationServiceException {

    public DuplicateTemplateNameException(String name) {
        super("A notification template named '" + name + "' already exists");
    }
}
