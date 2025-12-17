package com.campus.jobfair.dto;

import com.campus.jobfair.entity.enums.ApplicationStatus;
import lombok.Data;

@Data
public class ApplicationStatusUpdateRequest {
    private ApplicationStatus status;
    private String notes;
    private String tag;  // 企业标记
}
