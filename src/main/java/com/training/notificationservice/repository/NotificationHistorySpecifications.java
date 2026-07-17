package com.training.notificationservice.repository;

import com.training.notificationservice.entity.Notification;
import com.training.notificationservice.enums.NotificationChannel;
import com.training.notificationservice.enums.NotificationStatus;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Specification pattern for the history reporting features (export,
 * by-template), covering the {@code templateId}/date-range filters that
 * {@link NotificationSpecifications} doesn't need for its own recipient/
 * status/channel search. Kept as a new class rather than extending the
 * existing one, since that file belongs to Developer 1's search feature.
 */
public final class NotificationHistorySpecifications {

    private NotificationHistorySpecifications() {
    }

    public static Specification<Notification> withRecipient(String recipient) {
        return (root, query, criteriaBuilder) -> recipient == null ? null
                : criteriaBuilder.equal(root.get("recipient"), recipient);
    }

    public static Specification<Notification> withStatus(NotificationStatus status) {
        return (root, query, criteriaBuilder) -> status == null ? null
                : criteriaBuilder.equal(root.get("status"), status);
    }

    public static Specification<Notification> withChannel(NotificationChannel channel) {
        return (root, query, criteriaBuilder) -> channel == null ? null
                : criteriaBuilder.equal(root.get("channel"), channel);
    }

    public static Specification<Notification> withTemplateId(UUID templateId) {
        return (root, query, criteriaBuilder) -> templateId == null ? null
                : criteriaBuilder.equal(root.get("templateId"), templateId);
    }

    public static Specification<Notification> createdFrom(LocalDateTime from) {
        return (root, query, criteriaBuilder) -> from == null ? null
                : criteriaBuilder.greaterThanOrEqualTo(root.get("createdAt"), from);
    }

    public static Specification<Notification> createdTo(LocalDateTime to) {
        return (root, query, criteriaBuilder) -> to == null ? null
                : criteriaBuilder.lessThanOrEqualTo(root.get("createdAt"), to);
    }

    public static Specification<Notification> filterBy(String recipient, NotificationStatus status,
                                                         NotificationChannel channel, UUID templateId,
                                                         LocalDateTime from, LocalDateTime to) {
        return Specification.where(withRecipient(recipient))
                .and(withStatus(status))
                .and(withChannel(channel))
                .and(withTemplateId(templateId))
                .and(createdFrom(from))
                .and(createdTo(to));
    }
}
