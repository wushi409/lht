package com.campus.jobfair.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class JobRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String description;
    private String salaryRange;
    private Integer headcount;
    private String skills;
    private String location;
    private String jobType;
    private String deadline; // ISO string from client
}
