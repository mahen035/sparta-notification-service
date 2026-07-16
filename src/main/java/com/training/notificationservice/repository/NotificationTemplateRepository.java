package com.training.notificationservice.repository;

import com.training.notificationservice.entity.NotificationTemplate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * A second, independent repository interface over the same JPA persistence
 * context as {@code NotificationRepository}. Spring Data JPA supports any
 * number of repository interfaces coexisting side by side, so this does not
 * conflict with Developer 1's or Developer 4's repositories.
 */
public interface NotificationTemplateRepository extends JpaRepository<NotificationTemplate, UUID> {

    Optional<NotificationTemplate> findByName(String name);

    boolean existsByName(String name);
}
