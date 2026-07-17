package com.training.notificationservice.repository;

import com.training.notificationservice.entity.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.UUID;

/**
 * A further, independent repository interface over {@link NotificationTemplate},
 * alongside {@code NotificationTemplateRepository}. Spring Data JPA supports
 * any number of repository interfaces over the same entity, so this does not
 * conflict with the existing template CRUD repository - it exists purely to
 * back the template catalog features (search/archive/bulk-import/duplicate)
 * with specification-based querying.
 */
public interface TemplateCatalogRepository extends JpaRepository<NotificationTemplate, UUID>,
        JpaSpecificationExecutor<NotificationTemplate> {

    boolean existsByName(String name);
}
