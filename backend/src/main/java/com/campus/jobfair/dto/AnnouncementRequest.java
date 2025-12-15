package com.campus.jobfair.dto;

import com.campus.jobfair.entity.enums.AnnouncementTarget;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AnnouncementRequest {
    @NotBlank
    private String title;
    @NotBlank
    private String content;
    private AnnouncementTarget target = AnnouncementTarget.ALL;
    private String publishAt;
    private String expireAt;
    private boolean pinned;
}
