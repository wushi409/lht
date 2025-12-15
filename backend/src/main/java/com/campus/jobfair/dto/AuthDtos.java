package com.campus.jobfair.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

public class AuthDtos {

    @Data
    public static class LoginRequest {
        @NotBlank
        private String username;
        @NotBlank
        private String password;
    }

    @Data
    public static class StudentRegisterRequest {
        @NotBlank
        private String studentNo;
        @NotBlank
        private String name;
        private String college;
        private String phone;
        private String email;
        @Size(min = 6)
        private String password;
    }

    @Data
    public static class CompanyRegisterRequest {
        @NotBlank
        private String name;
        @NotBlank
        private String creditCode;
        private String scale;
        private String industry;
        private String description;
        private String contactName;
        private String contactPhone;
        private String contactEmail;
        @Size(min = 6)
        private String password;
    }

    @Data
    public static class AuthResponse {
        private final String token;
        private final String role;
    }
}
