package com.campus.jobfair.service;

import com.campus.jobfair.entity.Notification;
import com.campus.jobfair.entity.enums.NotificationType;
import com.campus.jobfair.entity.enums.UserRole;
import com.campus.jobfair.repository.NotificationRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    public List<Notification> list(UserRole role, Long recipientId, boolean onlyUnread) {
        if (onlyUnread) {
            return notificationRepository.findByRecipientRoleAndRecipientIdAndReadFlag(role, recipientId, false);
        }
        return notificationRepository.findByRecipientRoleAndRecipientId(role, recipientId);
    }

    @Transactional
    public Notification markRead(Long id) {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "通知不存在"));
        notification.setReadFlag(true);
        notification.setReadAt(Instant.now());
        return notificationRepository.save(notification);
    }

    @Transactional
    public Notification send(UserRole role, Long recipientId, String title, String content, NotificationType type) {
        Notification notification = new Notification();
        notification.setRecipientRole(role);
        notification.setRecipientId(recipientId);
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setReadFlag(false);
        return notificationRepository.save(notification);
    }
}
