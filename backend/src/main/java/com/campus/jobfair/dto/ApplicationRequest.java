package com.campus.jobfair.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplicationRequest {
    @NotNull
    private Long jobId;
    private Long resumeId;
    private String notes;
}
