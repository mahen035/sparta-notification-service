package com.training.notificationservice.dto.response;

import java.util.UUID;

/**
 * Outbound wire contract for a rendered template preview.
 */
public class TemplatePreviewResponseDto {

    private UUID templateId;
    private String templateName;
    private String renderedSubject;
    private String renderedBody;

    public UUID getTemplateId() {
        return templateId;
    }

    public void setTemplateId(UUID templateId) {
        this.templateId = templateId;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public String getRenderedSubject() {
        return renderedSubject;
    }

    public void setRenderedSubject(String renderedSubject) {
        this.renderedSubject = renderedSubject;
    }

    public String getRenderedBody() {
        return renderedBody;
    }

    public void setRenderedBody(String renderedBody) {
        this.renderedBody = renderedBody;
    }
}
