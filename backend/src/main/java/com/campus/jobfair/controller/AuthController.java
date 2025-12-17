package com.campus.jobfair.controller;

import com.campus.jobfair.dto.ApiResponse;
import com.campus.jobfair.dto.AuthDtos.AuthResponse;
import com.campus.jobfair.dto.AuthDtos.CodeLoginRequest;
import com.campus.jobfair.dto.AuthDtos.CompanyRegisterRequest;
import com.campus.jobfair.dto.AuthDtos.LoginRequest;
import com.campus.jobfair.dto.AuthDtos.SendCodeRequest;
import com.campus.jobfair.dto.AuthDtos.StudentRegisterRequest;
import com.campus.jobfair.dto.ResetPasswordRequest;
import com.campus.jobfair.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register/student")
    public ResponseEntity<ApiResponse<AuthResponse>> registerStudent(@Valid @RequestBody StudentRegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("注册成功", authService.registerStudent(request)));
    }

    @PostMapping("/register/company")
    public ResponseEntity<ApiResponse<AuthResponse>> registerCompany(@Valid @RequestBody CompanyRegisterRequest request) {
        return ResponseEntity.ok(ApiResponse.ok("企业申请已提交，待审核", authService.registerCompany(request)));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.login(request)));
    }

    @PostMapping("/send-code")
    public ResponseEntity<ApiResponse<String>> sendCode(@Valid @RequestBody SendCodeRequest request) {
        String code = authService.sendCode(request);
        // 为了演示方便，直接返回验证码；正式环境应通过短信/邮箱发送，不直接返回
        return ResponseEntity.ok(ApiResponse.ok("验证码已发送", code));
    }

    @PostMapping("/code-login")
    public ResponseEntity<ApiResponse<AuthResponse>> codeLogin(@Valid @RequestBody CodeLoginRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(authService.codeLogin(request)));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.ok("密码已重置", null));
    }
}