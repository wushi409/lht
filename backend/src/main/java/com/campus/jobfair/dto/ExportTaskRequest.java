package com.campus.jobfair.dto;

import com.campus.jobfair.entity.enums.ExportType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ExportTaskRequest {
    @NotNull
    private ExportType type;
    private String fromDate;
    private String toDate;
}
