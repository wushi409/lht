package com.campus.jobfair.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResumeRequest {
    @NotBlank
    private String title;
    private String summary;
    private String education;
    private String experience;
    private String skills;
    private boolean isDefault;
}
