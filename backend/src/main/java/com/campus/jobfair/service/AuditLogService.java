package com.campus.jobfair.service;

import com.campus.jobfair.entity.AuditLog;
import com.campus.jobfair.entity.enums.UserRole;
import com.campus.jobfair.repository.AuditLogRepository;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional
    public AuditLog log(UserRole actorRole, Long actorId, String action, String detail, boolean success) {
        AuditLog log = new AuditLog();
        log.setActorRole(actorRole);
        log.setActorId(actorId);
        log.setAction(action);
        log.setDetail(detail);
        log.setSuccess(success);
        return auditLogRepository.save(log);
    }

    public List<AuditLog> listAll() {
        return auditLogRepository.findAll();
    }
}
