package com.training.notificationservice.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

/**
 * Inbound wire contract for creating several templates in one call, e.g. to
 * seed a set of standard templates.
 */
public class TemplateBulkImportRequestDto {

    @NotEmpty(message = "templates must not be empty")
    @Valid
    private List<NotificationTemplateRequestDto> templates;

    public List<NotificationTemplateRequestDto> getTemplates() {
        return templates;
    }

    public void setTemplates(List<NotificationTemplateRequestDto> templates) {
        this.templates = templates;
    }
}
