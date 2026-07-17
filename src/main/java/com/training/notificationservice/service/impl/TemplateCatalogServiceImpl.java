package com.training.notificationservice.service.impl;

import com.training.notificationservice.dto.request.NotificationTemplateRequestDto;
import com.training.notificationservice.dto.request.TemplateBulkImportRequestDto;
import com.training.notificationservice.dto.request.TemplateDuplicateRequestDto;
import com.training.notificationservice.dto.request.TemplatePreviewRequestDto;
import com.training.notificationservice.dto.response.TemplateBulkImportFailureDto;
import com.training.notificationservice.dto.response.TemplateBulkImportResponseDto;
import com.training.notificationservice.dto.response.TemplateCatalogResponseDto;
import com.training.notificationservice.dto.response.TemplatePreviewResponseDto;
import com.training.notificationservice.entity.NotificationTemplate;
import com.training.notificationservice.enums.NotificationChannel;
import com.training.notificationservice.exception.DuplicateTemplateNameException;
import com.training.notificationservice.exception.NotificationTemplateNotFoundException;
import com.training.notificationservice.exception.TemplateAlreadyArchivedException;
import com.training.notificationservice.repository.TemplateCatalogRepository;
import com.training.notificationservice.repository.TemplateCatalogSpecifications;
import com.training.notificationservice.service.TemplateCatalogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Orchestrates the template catalog features (preview/search/archive/bulk
 * import/duplicate) on top of the original template CRUD. Mirrors the
 * layering and logging conventions of {@code NotificationTemplateServiceImpl}.
 */
@Service
public class TemplateCatalogServiceImpl implements TemplateCatalogService {

    private static final Logger log = LoggerFactory.getLogger(TemplateCatalogServiceImpl.class);
    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{\\{\\s*(\\w+)\\s*}}");

    private final TemplateCatalogRepository templateCatalogRepository;

    public TemplateCatalogServiceImpl(TemplateCatalogRepository templateCatalogRepository) {
        this.templateCatalogRepository = templateCatalogRepository;
    }

    @Override
    public Page<TemplateCatalogResponseDto> search(NotificationChannel channel, String name, Boolean active,
                                                     Pageable pageable) {
        log.debug("Searching templates channel={}, name={}, active={}, page={}", channel, name, active, pageable);
        return templateCatalogRepository
                .findAll(TemplateCatalogSpecifications.filterBy(channel, name, active), pageable)
                .map(this::toResponseDto);
    }

    @Override
    public TemplatePreviewResponseDto preview(UUID id, TemplatePreviewRequestDto request) {
        NotificationTemplate template = findOrThrow(id);
        Map<String, String> placeholders = (request == null || request.getPlaceholders() == null)
                ? Map.of() : request.getPlaceholders();

        TemplatePreviewResponseDto response = new TemplatePreviewResponseDto();
        response.setTemplateId(template.getId());
        response.setTemplateName(template.getName());
        response.setRenderedSubject(render(template.getSubjectTemplate(), placeholders));
        response.setRenderedBody(render(template.getBodyTemplate(), placeholders));
        return response;
    }

    @Override
    @Transactional
    public TemplateCatalogResponseDto archive(UUID id) {
        NotificationTemplate template = findOrThrow(id);
        if (!template.isActive()) {
            log.warn("Rejected archive of already-archived template {}", id);
            throw new TemplateAlreadyArchivedException(id);
        }
        template.setActive(false);
        NotificationTemplate saved = templateCatalogRepository.save(template);
        log.info("Archived notification template {}", id);
        return toResponseDto(saved);
    }

    @Override
    @Transactional
    public TemplateBulkImportResponseDto bulkImport(TemplateBulkImportRequestDto request) {
        List<TemplateCatalogResponseDto> created = new ArrayList<>();
        List<TemplateBulkImportFailureDto> failed = new ArrayList<>();

        for (NotificationTemplateRequestDto item : request.getTemplates()) {
            try {
                if (templateCatalogRepository.existsByName(item.getName())) {
                    throw new DuplicateTemplateNameException(item.getName());
                }
                NotificationTemplate template = new NotificationTemplate();
                applyRequest(template, item);
                NotificationTemplate saved = templateCatalogRepository.save(template);
                created.add(toResponseDto(saved));
            } catch (RuntimeException ex) {
                log.warn("Bulk import failed for template '{}': {}", item.getName(), ex.getMessage());
                TemplateBulkImportFailureDto failure = new TemplateBulkImportFailureDto();
                failure.setName(item.getName());
                failure.setReason(ex.getMessage());
                failed.add(failure);
            }
        }

        log.info("Bulk import completed: {} created, {} failed", created.size(), failed.size());
        TemplateBulkImportResponseDto response = new TemplateBulkImportResponseDto();
        response.setCreated(created);
        response.setFailed(failed);
        return response;
    }

    @Override
    @Transactional
    public TemplateCatalogResponseDto duplicate(UUID id, TemplateDuplicateRequestDto request) {
        NotificationTemplate source = findOrThrow(id);
        String requestedName = request == null ? null : request.getNewName();
        String candidateName = (requestedName != null && !requestedName.isBlank())
                ? requestedName
                : nextAvailableCopyName(source.getName());

        if (templateCatalogRepository.existsByName(candidateName)) {
            throw new DuplicateTemplateNameException(candidateName);
        }

        NotificationTemplate copy = new NotificationTemplate();
        copy.setName(candidateName);
        copy.setChannel(source.getChannel());
        copy.setSubjectTemplate(source.getSubjectTemplate());
        copy.setBodyTemplate(source.getBodyTemplate());
        NotificationTemplate saved = templateCatalogRepository.save(copy);

        log.info("Duplicated notification template {} as {} ({})", id, saved.getId(), saved.getName());
        return toResponseDto(saved);
    }

    private String nextAvailableCopyName(String sourceName) {
        String base = sourceName + "-copy";
        String candidate = base;
        int suffix = 2;
        while (templateCatalogRepository.existsByName(candidate)) {
            candidate = base + "-" + suffix++;
        }
        return candidate;
    }

    private String render(String template, Map<String, String> placeholders) {
        if (template == null) {
            return null;
        }
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String key = matcher.group(1);
            String value = placeholders.getOrDefault(key, matcher.group());
            matcher.appendReplacement(result, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private NotificationTemplate findOrThrow(UUID id) {
        return templateCatalogRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Notification template {} not found", id);
                    return new NotificationTemplateNotFoundException(id);
                });
    }

    private void applyRequest(NotificationTemplate template, NotificationTemplateRequestDto request) {
        template.setName(request.getName());
        template.setChannel(request.getChannel());
        template.setSubjectTemplate(request.getSubjectTemplate());
        template.setBodyTemplate(request.getBodyTemplate());
    }

    private TemplateCatalogResponseDto toResponseDto(NotificationTemplate template) {
        TemplateCatalogResponseDto dto = new TemplateCatalogResponseDto();
        dto.setId(template.getId());
        dto.setName(template.getName());
        dto.setChannel(template.getChannel());
        dto.setSubjectTemplate(template.getSubjectTemplate());
        dto.setBodyTemplate(template.getBodyTemplate());
        dto.setActive(template.isActive());
        dto.setCreatedAt(template.getCreatedAt());
        dto.setUpdatedAt(template.getUpdatedAt());
        return dto;
    }
}