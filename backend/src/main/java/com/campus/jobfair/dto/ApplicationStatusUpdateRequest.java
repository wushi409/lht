package com.campus.jobfair.dto;

import com.campus.jobfair.entity.enums.ApplicationStatus;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ApplicationStatusUpdateRequest {
    @NotNull
    private ApplicationStatus status;
    private String notes;
}
