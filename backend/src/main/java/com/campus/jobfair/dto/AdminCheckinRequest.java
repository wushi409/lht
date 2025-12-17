package com.campus.jobfair.dto;

import jakarta.validation.constraints.NotNull;

public class AdminCheckinRequest {
    @NotNull(message = "活动ID不能为空")
    private Long eventId;
    
    @NotNull(message = "报名ID不能为空")
    private Long registrationId;

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getRegistrationId() {
        return registrationId;
    }

    public void setRegistrationId(Long registrationId) {
        this.registrationId = registrationId;
    }
}
