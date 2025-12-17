package com.campus.jobfair.dto;

import lombok.Data;

@Data
public class JobFairRequest {
    private String name;
    private String location;
    private String startTime;
    private String endTime;
    private String description;
    private Integer capacity;
}
