package com.training.notificationservice.dto.request;

import java.util.Map;

/**
 * Inbound wire contract for rendering a template preview. Placeholder keys
 * are matched against {@code {{key}}} tokens in the template's subject/body;
 * any token without a supplied value is left unresolved in the rendered
 * output.
 */
public class TemplatePreviewRequestDto {

    private Map<String, String> placeholders;

    public Map<String, String> getPlaceholders() {
        return placeholders;
    }

    public void setPlaceholders(Map<String, String> placeholders) {
        this.placeholders = placeholders;
    }
}
