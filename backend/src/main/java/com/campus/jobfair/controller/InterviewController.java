package com.campus.jobfair.controller;

import com.campus.jobfair.dto.ApiResponse;
import com.campus.jobfair.dto.InterviewCreateRequest;
import com.campus.jobfair.dto.InterviewStatusUpdateRequest;
import com.campus.jobfair.entity.Interview;
import com.campus.jobfair.security.CustomUserDetails;
import com.campus.jobfair.service.InterviewService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/interviews")
public class InterviewController {

    private final InterviewService interviewService;

    public InterviewController(InterviewService interviewService) {
        this.interviewService = interviewService;
    }

    @PreAuthorize("hasRole('COMPANY')")
    @PostMapping
    public ResponseEntity<ApiResponse<Interview>> schedule(@AuthenticationPrincipal CustomUserDetails user,
                                                           @Valid @RequestBody InterviewCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(interviewService.schedule(user.getUsername(), request)));
    }

    @PreAuthorize("hasRole('COMPANY')")
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Interview>> reschedule(@AuthenticationPrincipal CustomUserDetails user,
                                                             @PathVariable Long id,
                                                             @RequestBody InterviewCreateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(interviewService.rescheduleByCompany(id, user.getUsername(), request)));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/{id}/status")
    public ResponseEntity<ApiResponse<Interview>> respond(@AuthenticationPrincipal CustomUserDetails user,
                                                          @PathVariable Long id,
                                                          @RequestBody InterviewStatusUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(interviewService.updateStatusForStudent(id, user.getId(), request)));
    }

    @PreAuthorize("hasRole('COMPANY')")
    @PostMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse<Interview>> cancel(@AuthenticationPrincipal CustomUserDetails user,
                                                         @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(interviewService.cancelByCompany(id, user.getUsername())));
    }

    @PreAuthorize("hasRole('COMPANY')")
    @GetMapping("/company/me")
    public ResponseEntity<ApiResponse<List<Interview>>> listCompany(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(interviewService.listForCompany(user.getUsername())));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @GetMapping("/student/me")
    public ResponseEntity<ApiResponse<List<Interview>>> listStudent(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(interviewService.listForStudent(user.getId())));
    }
}
