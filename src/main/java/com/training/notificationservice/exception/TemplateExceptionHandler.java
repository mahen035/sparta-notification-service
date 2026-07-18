package com.training.notificationservice.exception;

import com.training.notificationservice.controller.NotificationTemplateController;
import com.training.notificationservice.dto.response.ApiErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

/**
 * Scoped exception handler for the templates feature only (via
 * {@code assignableTypes}). Multiple {@code @RestControllerAdvice} beans are
 * a first-class Spring MVC feature - this is not a workaround - so this
 * class adds the 409 CONFLICT mapping for {@link DuplicateTemplateNameException}
 * without modifying Developer 1's shared {@code GlobalExceptionHandler}.
 * <p>
 * {@code @Order(HIGHEST_PRECEDENCE)} is required here: since
 * {@code DuplicateTemplateNameException} extends {@code NotificationServiceException},
 * Dev 1's {@code GlobalExceptionHandler} also has a matching (but less specific)
 * handler for it. Spring resolves competing {@code @RestControllerAdvice} beans
 * in declaration/registration order when neither declares {@code @Order}, which
 * is unreliable - without this annotation the generic 500 handler can win the
 * race instead of this 409 handler.
 */
@RestControllerAdvice(assignableTypes = NotificationTemplateController.class)
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TemplateExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(TemplateExceptionHandler.class);

    @ExceptionHandler(DuplicateTemplateNameException.class)
    public ResponseEntity<ApiErrorResponse> handleDuplicateName(DuplicateTemplateNameException ex,
                                                                 HttpServletRequest request) {
        log.warn("Duplicate template name on {}: {}", request.getRequestURI(), ex.getMessage());
        ApiErrorResponse body = new ApiErrorResponse(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                HttpStatus.CONFLICT.getReasonPhrase(),
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }
}
