package com.campus.jobfair.repository;

import com.campus.jobfair.entity.AuditLog;
import com.campus.jobfair.entity.enums.UserRole;
import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByActorRoleAndCreatedAtBetween(UserRole role, Instant from, Instant to);
}
