package com.campus.jobfair.controller;

import com.campus.jobfair.dto.ApiResponse;
import com.campus.jobfair.dto.CompanyUpdateRequest;
import com.campus.jobfair.dto.JobRequest;
import com.campus.jobfair.entity.Company;
import com.campus.jobfair.entity.Job;
import com.campus.jobfair.entity.enums.JobStatus;
import com.campus.jobfair.security.CustomUserDetails;
import com.campus.jobfair.service.CompanyService;
import com.campus.jobfair.service.JobService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CompanyController {

    private final CompanyService companyService;
    private final JobService jobService;

    public CompanyController(CompanyService companyService, JobService jobService) {
        this.companyService = companyService;
        this.jobService = jobService;
    }

    @GetMapping("/companies")
    public ResponseEntity<ApiResponse<List<Company>>> listApproved() {
        return ResponseEntity.ok(ApiResponse.ok(companyService.listApproved()));
    }

    @GetMapping("/companies/{id}")
    public ResponseEntity<ApiResponse<Company>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(companyService.getById(id)));
    }

    @PreAuthorize("hasRole('COMPANY')")
    @GetMapping("/companies/me")
    public ResponseEntity<ApiResponse<Company>> myCompany(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(companyService.getByUsername(user.getUsername())));
    }

    @PreAuthorize("hasRole('COMPANY')")
    @PutMapping("/companies/me")
    public ResponseEntity<ApiResponse<Company>> updateCompany(@AuthenticationPrincipal CustomUserDetails user,
                                                              @RequestBody CompanyUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(companyService.updateProfile(user.getUsername(), request)));
    }

    @PreAuthorize("hasRole('COMPANY')")
    @PostMapping("/jobs")
    public ResponseEntity<ApiResponse<Job>> createJob(@AuthenticationPrincipal CustomUserDetails user,
                                                      @Valid @RequestBody JobRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(jobService.createJob(user.getUsername(), request)));
    }

    @PreAuthorize("hasRole('COMPANY')")
    @PutMapping("/jobs/{id}")
    public ResponseEntity<ApiResponse<Job>> updateJob(@AuthenticationPrincipal CustomUserDetails user,
                                                     @PathVariable Long id,
                                                     @Valid @RequestBody JobRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(jobService.updateJob(id, user.getUsername(), request)));
    }

    @PreAuthorize("hasRole('COMPANY')")
    @PostMapping("/jobs/{id}/status")
    public ResponseEntity<ApiResponse<Job>> changeStatus(@AuthenticationPrincipal CustomUserDetails user,
                                                         @PathVariable Long id,
                                                         @RequestParam JobStatus status) {
        return ResponseEntity.ok(ApiResponse.ok(jobService.changeStatus(id, user.getUsername(), status)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/companies/pending")
    public ResponseEntity<ApiResponse<List<Company>>> listPending() {
        return ResponseEntity.ok(ApiResponse.ok(companyService.listPending()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/companies/{id}/approve")
    public ResponseEntity<ApiResponse<Company>> review(@PathVariable Long id,
                                                       @RequestParam(defaultValue = "true") boolean approved,
                                                       @RequestParam(required = false) String reason) {
        return ResponseEntity.ok(ApiResponse.ok(companyService.reviewCompany(id, approved, reason)));
    }
}
