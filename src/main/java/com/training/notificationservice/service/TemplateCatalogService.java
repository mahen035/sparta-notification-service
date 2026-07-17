package com.training.notificationservice.service;

import com.training.notificationservice.dto.request.TemplateBulkImportRequestDto;
import com.training.notificationservice.dto.request.TemplateDuplicateRequestDto;
import com.training.notificationservice.dto.request.TemplatePreviewRequestDto;
import com.training.notificationservice.dto.response.TemplateBulkImportResponseDto;
import com.training.notificationservice.dto.response.TemplateCatalogResponseDto;
import com.training.notificationservice.dto.response.TemplatePreviewResponseDto;
import com.training.notificationservice.enums.NotificationChannel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Business-logic contract for the template catalog features (preview,
 * search, archive, bulk import, duplicate) that sit on top of the original
 * template CRUD, kept independent of HTTP and persistence.
 */
public interface TemplateCatalogService {

    Page<TemplateCatalogResponseDto> search(NotificationChannel channel, String name, Boolean active,
                                             Pageable pageable);

    TemplatePreviewResponseDto preview(UUID id, TemplatePreviewRequestDto request);

    TemplateCatalogResponseDto archive(UUID id);

    TemplateBulkImportResponseDto bulkImport(TemplateBulkImportRequestDto request);

    TemplateCatalogResponseDto duplicate(UUID id, TemplateDuplicateRequestDto request);
}
