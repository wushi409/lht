package com.campus.jobfair.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class EventRegistrationRequest {
    @NotNull
    private Long eventId;
    private String seatNo;
}
