package com.campus.jobfair.controller;

import com.campus.jobfair.dto.ApiResponse;
import com.campus.jobfair.dto.ExportTaskRequest;
import com.campus.jobfair.entity.ExportTask;
import com.campus.jobfair.security.CustomUserDetails;
import com.campus.jobfair.service.ExportService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/exports")
@PreAuthorize("hasRole('ADMIN')")
public class ExportController {

    private final ExportService exportService;

    public ExportController(ExportService exportService) {
        this.exportService = exportService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ExportTask>>> list() {
        return ResponseEntity.ok(ApiResponse.ok(exportService.listAll()));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ExportTask>> create(@AuthenticationPrincipal CustomUserDetails user,
                                                          @Valid @RequestBody ExportTaskRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(exportService.createTask(user.getRole(), user.getId(), request)));
    }

    @GetMapping("/{id}/download")
    public ResponseEntity<ApiResponse<String>> download(@PathVariable Long id) {
        ExportTask task = exportService.getById(id);
        if (task == null || task.getStatus() != com.campus.jobfair.entity.enums.ExportStatus.COMPLETED) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("任务不存在或未完成"));
        }
        // 生成并返回实际的导出数据
        String exportData = exportService.generateExportData(task);
        return ResponseEntity.ok(ApiResponse.ok(exportData));
    }
}
