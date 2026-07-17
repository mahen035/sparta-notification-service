package com.training.notificationservice.dto.response;

/**
 * Describes a single entry that failed during a bulk template import, so the
 * caller can see which of the submitted templates succeeded and which did
 * not without the whole batch being rejected.
 */
public class TemplateBulkImportFailureDto {

    private String name;
    private String reason;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}
