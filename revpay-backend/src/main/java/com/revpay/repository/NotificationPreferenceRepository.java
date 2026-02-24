package com.revpay.repository;

import com.revpay.entity.NotificationPreference;
import com.revpay.entity.User;
import com.revpay.enums.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface NotificationPreferenceRepository extends JpaRepository<NotificationPreference, Long> {
    List<NotificationPreference> findByUser(User user);
    Optional<NotificationPreference> findByUserAndNotificationType(User user, NotificationType type);
}
