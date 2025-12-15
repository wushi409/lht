package com.campus.jobfair.dto;

import lombok.Data;

@Data
public class CompanyUpdateRequest {
    private String name;
    private String scale;
    private String industry;
    private String description;
    private String logoUrl;
    private String contactName;
    private String contactPhone;
    private String contactEmail;
}
