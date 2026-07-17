package com.training.notificationservice.repository;

import com.training.notificationservice.entity.NotificationTemplate;
import com.training.notificationservice.enums.NotificationChannel;
import org.springframework.data.jpa.domain.Specification;

/**
 * Specification pattern for the template catalog search/list endpoint,
 * mirroring the style of {@link NotificationSpecifications}.
 */
public final class TemplateCatalogSpecifications {

    private TemplateCatalogSpecifications() {
    }

    public static Specification<NotificationTemplate> withChannel(NotificationChannel channel) {
        return (root, query, criteriaBuilder) -> channel == null ? null
                : criteriaBuilder.equal(root.get("channel"), channel);
    }

    public static Specification<NotificationTemplate> withNameContaining(String name) {
        return (root, query, criteriaBuilder) -> (name == null || name.isBlank()) ? null
                : criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<NotificationTemplate> withActive(Boolean active) {
        return (root, query, criteriaBuilder) -> active == null ? null
                : criteriaBuilder.equal(root.get("active"), active);
    }

    public static Specification<NotificationTemplate> filterBy(NotificationChannel channel, String name,
                                                                 Boolean active) {
        return Specification.where(withChannel(channel))
                .and(withNameContaining(name))
                .and(withActive(active));
    }
}
