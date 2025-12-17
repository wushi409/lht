package com.campus.jobfair.dto;

import lombok.Data;

@Data
public class EventRequest {
    private Long jobFairId;
    private String name;
    private String type;
    private String location;
    private String startTime;
    private String endTime;
    private Integer capacity;
    private String description;
}
