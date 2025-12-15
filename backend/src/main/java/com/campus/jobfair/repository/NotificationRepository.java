package com.campus.jobfair.repository;

import com.campus.jobfair.entity.Notification;
import com.campus.jobfair.entity.enums.UserRole;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    List<Notification> findByRecipientRoleAndRecipientIdAndReadFlag(UserRole role, Long recipientId, boolean readFlag);
    List<Notification> findByRecipientRoleAndRecipientId(UserRole role, Long recipientId);
}
