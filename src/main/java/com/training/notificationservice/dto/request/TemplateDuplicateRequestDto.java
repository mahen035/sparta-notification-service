package com.training.notificationservice.dto.request;

import jakarta.validation.constraints.Size;

/**
 * Inbound wire contract for duplicating a template. {@code newName} is
 * optional - when omitted, the service derives a unique "-copy" suffixed
 * name from the source template.
 */
public class TemplateDuplicateRequestDto {

    @Size(max = 100, message = "newName must be at most 100 characters")
    private String newName;

    public String getNewName() {
        return newName;
    }

    public void setNewName(String newName) {
        this.newName = newName;
    }
}
