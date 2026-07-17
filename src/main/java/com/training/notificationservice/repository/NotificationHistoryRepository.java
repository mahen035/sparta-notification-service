package com.training.notificationservice.repository;

import com.training.notificationservice.entity.Notification;
import com.training.notificationservice.enums.NotificationChannel;
import com.training.notificationservice.enums.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * A further, independent repository interface over {@link Notification},
 * alongside {@code NotificationRepository}. Backs the history reporting
 * features (summary/export/by-template) with specification-based filtering
 * plus the grouped-count queries a plain {@link JpaSpecificationExecutor}
 * can't express.
 */
public interface NotificationHistoryRepository extends JpaRepository<Notification, UUID>,
        JpaSpecificationExecutor<Notification> {

    @Query("select n.status as status, count(n) as total from Notification n "
            + "where n.createdAt between :from and :to group by n.status")
    List<StatusCount> countByStatusInRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    @Query("select n.channel as channel, count(n) as total from Notification n "
            + "where n.createdAt between :from and :to group by n.channel")
    List<ChannelCount> countByChannelInRange(@Param("from") LocalDateTime from, @Param("to") LocalDateTime to);

    interface StatusCount {
        NotificationStatus getStatus();

        long getTotal();
    }

    interface ChannelCount {
        NotificationChannel getChannel();

        long getTotal();
    }
}
