package com.campus.jobfair.service;

import com.campus.jobfair.dto.ExportTaskRequest;
import com.campus.jobfair.entity.ExportTask;
import com.campus.jobfair.entity.enums.ExportStatus;
import com.campus.jobfair.entity.enums.UserRole;
import com.campus.jobfair.repository.ExportTaskRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExportService {

    private final ExportTaskRepository exportTaskRepository;

    public ExportService(ExportTaskRepository exportTaskRepository) {
        this.exportTaskRepository = exportTaskRepository;
    }

    public List<ExportTask> listAll() {
        return exportTaskRepository.findAll();
    }

    @Transactional
    public ExportTask createTask(UserRole role, Long requesterId, ExportTaskRequest request) {
        ExportTask task = new ExportTask();
        task.setType(request.getType());
        task.setRequestedByRole(role);
        task.setRequestedById(requesterId);
        if (request.getFromDate() != null) task.setFromDate(Instant.parse(request.getFromDate()));
        if (request.getToDate() != null) task.setToDate(Instant.parse(request.getToDate()));
        task.setStatus(ExportStatus.PROCESSING);
        ExportTask saved = exportTaskRepository.save(task);
        // 简化：立即完成并生成文件链接
        saved.setStatus(ExportStatus.COMPLETED);
        saved.setCompletedAt(Instant.now());
        saved.setFileUrl("/exports/" + UUID.randomUUID() + ".xlsx");
        return exportTaskRepository.save(saved);
    }
}
