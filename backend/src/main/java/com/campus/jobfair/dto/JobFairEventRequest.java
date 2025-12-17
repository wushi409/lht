package com.campus.jobfair.dto;

import com.campus.jobfair.entity.enums.EventType;
import lombok.Data;

@Data
public class JobFairEventRequest {
    private Long jobFairId;
    private String name;
    private EventType type;
    private String location;
    /** ISO-8601 字符串 */
    private String startTime;
    /** ISO-8601 字符串 */
    private String endTime;
    private Integer capacity;
    private String description;
}
