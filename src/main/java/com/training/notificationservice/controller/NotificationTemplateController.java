package com.training.notificationservice.controller;

import com.training.notificationservice.dto.request.NotificationTemplateRequestDto;
import com.training.notificationservice.dto.response.ApiErrorResponse;
import com.training.notificationservice.dto.response.NotificationTemplateResponseDto;
import com.training.notificationservice.service.NotificationTemplateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * Thin REST controller for notification template CRUD. All business logic
 * lives in {@link NotificationTemplateService}, matching Developer 1's
 * layering convention in {@code NotificationController}.
 * <p>
 * Fully documented for {@code springdoc-openapi} - every endpoint declares
 * its success and error response shapes so Swagger UI shows accurate,
 * try-it-out-ready documentation for this slice without touching Dev 1's
 * shared {@code OpenApiConfig}.
 */
@RestController
@RequestMapping("/api/v1/templates")
@Tag(name = "Notification Templates", description = "CRUD operations for reusable notification templates")
public class NotificationTemplateController {

    private static final Logger log = LoggerFactory.getLogger(NotificationTemplateController.class);

    private final NotificationTemplateService templateService;

    public NotificationTemplateController(NotificationTemplateService templateService) {
        this.templateService = templateService;
    }

    @PostMapping
    @Operation(
            summary = "Create a notification template",
            description = "Creates a reusable template for a given channel. The template name must be unique across all channels."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Template created",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = NotificationTemplateResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed (e.g. missing name/bodyTemplate)",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "A template with this name already exists",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<NotificationTemplateResponseDto> create(
            @Valid @RequestBody NotificationTemplateRequestDto request) {
        log.info("POST /api/v1/templates name={}", request.getName());
        NotificationTemplateResponseDto created = templateService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Fetch a single template by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Template found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = NotificationTemplateResponseDto.class))),
            @ApiResponse(responseCode = "404", description = "No template exists with the given id",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<NotificationTemplateResponseDto> getById(
            @Parameter(description = "Template id", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID id) {
        log.info("GET /api/v1/templates/{}", id);
        return ResponseEntity.ok(templateService.getById(id));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an existing template",
            description = "Replaces the name, channel, subject and body of an existing template. Renaming to a name used by another template is rejected."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Template updated",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = NotificationTemplateResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Validation failed",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "404", description = "No template exists with the given id",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class))),
            @ApiResponse(responseCode = "409", description = "Another template already uses the requested name",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<NotificationTemplateResponseDto> update(
            @Parameter(description = "Template id", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID id,
            @Valid @RequestBody NotificationTemplateRequestDto request) {
        log.info("PUT /api/v1/templates/{}", id);
        return ResponseEntity.ok(templateService.update(id, request));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a template")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Template deleted"),
            @ApiResponse(responseCode = "404", description = "No template exists with the given id",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = ApiErrorResponse.class)))
    })
    public ResponseEntity<Void> delete(
            @Parameter(description = "Template id", example = "3fa85f64-5717-4562-b3fc-2c963f66afa6")
            @PathVariable UUID id) {
        log.info("DELETE /api/v1/templates/{}", id);
        templateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
