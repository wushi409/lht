package com.campus.jobfair.entity;

import com.campus.jobfair.entity.enums.ExportStatus;
import com.campus.jobfair.entity.enums.ExportType;
import com.campus.jobfair.entity.enums.UserRole;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "export_tasks")
public class ExportTask extends BaseEntity {

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private ExportType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ExportStatus status = ExportStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private UserRole requestedByRole;

    @Column(nullable = false)
    private Long requestedById;

    private Instant fromDate;

    private Instant toDate;

    private String fileUrl;

    private Instant completedAt;

    @Column(length = 200)
    private String errorMessage;
}
