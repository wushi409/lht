package com.campus.jobfair.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InterviewCreateRequest {
    @NotNull
    private Long jobId;
    @NotNull
    private Long studentId;
    private String scheduledAt; // ISO datetime
    private String location;
    private String interviewer;
    private String notes;
}
