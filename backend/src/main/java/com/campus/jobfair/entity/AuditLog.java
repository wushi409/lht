package com.campus.jobfair.entity;

import com.campus.jobfair.entity.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "audit_logs")
public class AuditLog extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole actorRole;

    @Column(nullable = false)
    private Long actorId;

    @Column(nullable = false, length = 200)
    private String action;

    @Column(length = 1000)
    private String detail;

    private boolean success;

    @Column(length = 50)
    private String ip;
}
