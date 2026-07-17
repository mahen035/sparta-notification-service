package com.training.notificationservice.service.impl;

import com.training.notificationservice.dto.request.NotificationTemplateRequestDto;
import com.training.notificationservice.dto.request.TemplateBulkImportRequestDto;
import com.training.notificationservice.dto.request.TemplateDuplicateRequestDto;
import com.training.notificationservice.dto.request.TemplatePreviewRequestDto;
import com.training.notificationservice.dto.response.TemplateBulkImportResponseDto;
import com.training.notificationservice.dto.response.TemplateCatalogResponseDto;
import com.training.notificationservice.dto.response.TemplatePreviewResponseDto;
import com.training.notificationservice.entity.NotificationTemplate;
import com.training.notificationservice.enums.NotificationChannel;
import com.training.notificationservice.exception.DuplicateTemplateNameException;
import com.training.notificationservice.exception.NotificationTemplateNotFoundException;
import com.training.notificationservice.exception.TemplateAlreadyArchivedException;
import com.training.notificationservice.repository.TemplateCatalogRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TemplateCatalogServiceImplTest {

    @Mock
    private TemplateCatalogRepository templateCatalogRepository;

    @InjectMocks
    private TemplateCatalogServiceImpl templateCatalogService;

    private NotificationTemplate activeTemplate(UUID id) {
        NotificationTemplate template = new NotificationTemplate();
        template.setId(id);
        template.setName("order-confirmed-email");
        template.setChannel(NotificationChannel.EMAIL);
        template.setSubjectTemplate("Your order is confirmed");
        template.setBodyTemplate("Hi {{name}}, your order #{{orderId}} is confirmed.");
        template.setActive(true);
        return template;
    }

    @Test
    void preview_replacesKnownPlaceholdersAndLeavesUnknownOnesUntouched() {
        UUID id = UUID.randomUUID();
        NotificationTemplate template = activeTemplate(id);
        when(templateCatalogRepository.findById(id)).thenReturn(Optional.of(template));

        TemplatePreviewRequestDto request = new TemplatePreviewRequestDto();
        request.setPlaceholders(Map.of("name", "Priya"));

        TemplatePreviewResponseDto result = templateCatalogService.preview(id, request);

        assertThat(result.getRenderedSubject()).isEqualTo("Your order is confirmed");
        assertThat(result.getRenderedBody()).isEqualTo("Hi Priya, your order #{{orderId}} is confirmed.");
    }

    @Test
    void preview_whenMissing_throwsNotFound() {
        UUID id = UUID.randomUUID();
        when(templateCatalogRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> templateCatalogService.preview(id, new TemplatePreviewRequestDto()))
                .isInstanceOf(NotificationTemplateNotFoundException.class);
    }

    @Test
    void archive_whenActive_deactivatesTemplate() {
        UUID id = UUID.randomUUID();
        NotificationTemplate template = activeTemplate(id);
        when(templateCatalogRepository.findById(id)).thenReturn(Optional.of(template));
        when(templateCatalogRepository.save(any(NotificationTemplate.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TemplateCatalogResponseDto result = templateCatalogService.archive(id);

        assertThat(result.isActive()).isFalse();
    }

    @Test
    void archive_whenAlreadyArchived_throwsConflict() {
        UUID id = UUID.randomUUID();
        NotificationTemplate template = activeTemplate(id);
        template.setActive(false);
        when(templateCatalogRepository.findById(id)).thenReturn(Optional.of(template));

        assertThatThrownBy(() -> templateCatalogService.archive(id))
                .isInstanceOf(TemplateAlreadyArchivedException.class);
    }

    @Test
    void duplicate_withoutNewName_derivesCopySuffixedName() {
        UUID id = UUID.randomUUID();
        NotificationTemplate source = activeTemplate(id);
        when(templateCatalogRepository.findById(id)).thenReturn(Optional.of(source));
        when(templateCatalogRepository.existsByName("order-confirmed-email-copy")).thenReturn(false);
        when(templateCatalogRepository.save(any(NotificationTemplate.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TemplateCatalogResponseDto result = templateCatalogService.duplicate(id, null);

        assertThat(result.getName()).isEqualTo("order-confirmed-email-copy");
        assertThat(result.getBodyTemplate()).isEqualTo(source.getBodyTemplate());
    }

    @Test
    void duplicate_withExplicitNameAlreadyTaken_throwsConflict() {
        UUID id = UUID.randomUUID();
        NotificationTemplate source = activeTemplate(id);
        when(templateCatalogRepository.findById(id)).thenReturn(Optional.of(source));
        when(templateCatalogRepository.existsByName("taken-name")).thenReturn(true);

        TemplateDuplicateRequestDto request = new TemplateDuplicateRequestDto();
        request.setNewName("taken-name");

        assertThatThrownBy(() -> templateCatalogService.duplicate(id, request))
                .isInstanceOf(DuplicateTemplateNameException.class);
    }

    @Test
    void bulkImport_withOneDuplicateName_reportsPartialSuccess() {
        NotificationTemplateRequestDto ok = new NotificationTemplateRequestDto();
        ok.setName("new-template");
        ok.setChannel(NotificationChannel.EMAIL);
        ok.setBodyTemplate("body");

        NotificationTemplateRequestDto duplicate = new NotificationTemplateRequestDto();
        duplicate.setName("existing-template");
        duplicate.setChannel(NotificationChannel.SMS);
        duplicate.setBodyTemplate("body");

        when(templateCatalogRepository.existsByName("new-template")).thenReturn(false);
        when(templateCatalogRepository.existsByName("existing-template")).thenReturn(true);
        when(templateCatalogRepository.save(any(NotificationTemplate.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        TemplateBulkImportRequestDto request = new TemplateBulkImportRequestDto();
        request.setTemplates(List.of(ok, duplicate));

        TemplateBulkImportResponseDto result = templateCatalogService.bulkImport(request);

        assertThat(result.getCreated()).hasSize(1);
        assertThat(result.getCreated().get(0).getName()).isEqualTo("new-template");
        assertThat(result.getFailed()).hasSize(1);
        assertThat(result.getFailed().get(0).getName()).isEqualTo("existing-template");
    }
}
