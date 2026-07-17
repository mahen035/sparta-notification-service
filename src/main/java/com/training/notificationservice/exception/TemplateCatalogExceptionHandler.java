package com.training.notificationservice.exception;

import com.training.notificationservice.controller.TemplateCatalogController;
import com.training.notificationservice.dto.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Scoped exception handler for the template catalog feature only (via
 * {@code assignableTypes}), mirroring {@link TemplateExceptionHandler}'s
 * pattern for {@code NotificationTemplateController}. Kept separate because
 * {@code assignableTypes} advice only applies to the listed controller
 * class(es) - reusing {@link DuplicateTemplateNameException} here requires
 * its own scoped mapping for {@link TemplateCatalogController}.
 */
@RestControllerAdvice(assignableTypes = TemplateCatalogController.class)
public class TemplateCatalogExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(TemplateCatalogExceptionHandler.class);

    @ExceptionHandler(DuplicateTemplateNameException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateName(DuplicateTemplateNameException ex,
                                                                 HttpServletRequest request) {
        return conflict(ex.getMessage(), request);
    }

    @ExceptionHandler(TemplateAlreadyArchivedException.class)
    public ResponseEntity<ApiErrorResponse> handleAlreadyArchived(TemplateAlreadyArchivedException ex,
                                                                   HttpServletRequest request) {
        return conflict(ex.getMessage(), request);
    }

    private ResponseEntity<ApiErrorResponse> conflict(String message, HttpServletRequest request) {
        log.warn("Conflict on {}: {}", request.getRequestURI(), message);
        ApiErrorResponse body = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }
}
