package com.training.notificationservice.dto.response;

import java.util.List;

/**
 * Outbound wire contract for a bulk template import: templates that were
 * created, and templates that were rejected (e.g. duplicate name), so a
 * partial batch failure doesn't lose visibility into what succeeded.
 */
public class TemplateBulkImportResponseDto {

    private List<TemplateCatalogResponseDto> created;
    private List<TemplateBulkImportFailureDto> failed;

    public List<TemplateCatalogResponseDto> getCreated() {
        return created;
    }

    public void setCreated(List<TemplateCatalogResponseDto> created) {
        this.created = created;
    }

    public List<TemplateBulkImportFailureDto> getFailed() {
        return failed;
    }

    public void setFailed(List<TemplateBulkImportFailureDto> failed) {
        this.failed = failed;
    }
}
