package com.campus.jobfair.service;

import com.campus.jobfair.entity.AuditLog;
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
    public AuditLog log(String userType, String username, String action, String result) {
        AuditLog log = new AuditLog();
        log.setUserType(userType);
        log.setUsername(username);
        log.setAction(action);
        log.setResult(result);
        return auditLogRepository.save(log);
    }

    public List<AuditLog> listAll() {
        return auditLogRepository.findAll();
    }
}
