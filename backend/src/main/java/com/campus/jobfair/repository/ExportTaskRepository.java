package com.campus.jobfair.repository;

import com.campus.jobfair.entity.ExportTask;
import com.campus.jobfair.entity.enums.ExportStatus;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExportTaskRepository extends JpaRepository<ExportTask, Long> {
    long countByStatus(ExportStatus status);
}
