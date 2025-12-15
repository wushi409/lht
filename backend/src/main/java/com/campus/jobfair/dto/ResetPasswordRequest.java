package com.campus.jobfair.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ResetPasswordRequest {
    @NotBlank
    private String username;
    @NotBlank
    private String code;
    @NotBlank
    private String newPassword;
}
