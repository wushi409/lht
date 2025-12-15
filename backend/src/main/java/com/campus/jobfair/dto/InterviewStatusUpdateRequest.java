package com.campus.jobfair.dto;

import com.campus.jobfair.entity.enums.InterviewStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class InterviewStatusUpdateRequest {
    @NotNull
    private InterviewStatus status;
    private String feedback;
}
