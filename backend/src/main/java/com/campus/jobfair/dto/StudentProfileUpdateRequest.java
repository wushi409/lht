package com.campus.jobfair.dto;

import lombok.Data;

@Data
public class StudentProfileUpdateRequest {
    private String name;
    private String college;
    private String phone;
    private String email;
    private Long defaultResumeId;
}
