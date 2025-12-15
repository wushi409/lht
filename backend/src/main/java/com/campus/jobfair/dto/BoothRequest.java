package com.campus.jobfair.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class BoothRequest {
    @NotNull
    private Long jobFairId;
    @NotNull
    private Long companyId;
    @NotBlank
    private String boothNo;
    private String location;
}
