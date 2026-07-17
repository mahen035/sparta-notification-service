package com.training.notificationservice.controller;

import com.training.notificationservice.dto.request.TemplateBulkImportRequestDto;
import com.training.notificationservice.dto.request.TemplateDuplicateRequestDto;
import com.training.notificationservice.dto.request.TemplatePreviewRequestDto;
import com.training.notificationservice.dto.response.TemplateBulkImportResponseDto;
import com.training.notificationservice.dto.response.TemplateCatalogResponseDto;
import com.training.notificationservice.dto.response.TemplatePreviewResponseDto;
import com.training.notificationservice.enums.NotificationChannel;
import com.training.notificationservice.service.TemplateCatalogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

/**
 * REST endpoints for the template catalog features (search, preview,
 * archive, bulk import, duplicate). Mounted on the same base path as
 * {@code NotificationTemplateController} but only adds sub-paths and an
 * unmapped {@code GET} that controller doesn't expose, so there is no route
 * collision - the original CRUD controller is never modified.
 */
@RestController
@RequestMapping("/api/v1/templates")
@Tag(name = "Template Catalog", description = "Preview, search, archive, bulk-import, and duplicate notification templates")
public class TemplateCatalogController {

    private static final Logger log = LoggerFactory.getLogger(TemplateCatalogController.class);

    private final TemplateCatalogService templateCatalogService;

    public TemplateCatalogController(TemplateCatalogService templateCatalogService) {
        this.templateCatalogService = templateCatalogService;
    }

    @GetMapping
    @Operation(summary = "Search/list notification templates with optional filters and pagination")
    public ResponseEntity<Page<TemplateCatalogResponseDto>> search(
            @RequestParam(required = false) NotificationChannel channel,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Boolean active,
            Pageable pageable) {
        log.info("GET /api/v1/templates channel={}, name={}, active={}, page={}", channel, name, active, pageable);
        return ResponseEntity.ok(templateCatalogService.search(channel, name, active, pageable));
    }

    @PostMapping("/{id}/preview")
    @Operation(summary = "Render a template's subject/body with supplied placeholder values")
    public ResponseEntity<TemplatePreviewResponseDto> preview(
            @PathVariable UUID id,
            @RequestBody(required = false) TemplatePreviewRequestDto request) {
        log.info("POST /api/v1/templates/{}/preview", id);
        return ResponseEntity.ok(templateCatalogService.preview(id, request));
    }

    @PatchMapping("/{id}/archive")
    @Operation(summary = "Archive a template (soft delete) instead of removing it")
    public ResponseEntity<TemplateCatalogResponseDto> archive(@PathVariable UUID id) {
        log.info("PATCH /api/v1/templates/{}/archive", id);
        return ResponseEntity.ok(templateCatalogService.archive(id));
    }

    @PostMapping("/bulk")
    @Operation(summary = "Create several templates in one call")
    public ResponseEntity<TemplateBulkImportResponseDto> bulkImport(
            @Valid @RequestBody TemplateBulkImportRequestDto request) {
        int count = request.getTemplates() == null ? 0 : request.getTemplates().size();
        log.info("POST /api/v1/templates/bulk count={}", count);
        return ResponseEntity.status(HttpStatus.CREATED).body(templateCatalogService.bulkImport(request));
    }

    @PostMapping("/{id}/duplicate")
    @Operation(summary = "Duplicate an existing template under a new name")
    public ResponseEntity<TemplateCatalogResponseDto> duplicate(
            @PathVariable UUID id,
            @Valid @RequestBody(required = false) TemplateDuplicateRequestDto request) {
        log.info("POST /api/v1/templates/{}/duplicate", id);
        TemplateCatalogResponseDto result = templateCatalogService.duplicate(id, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }
}
