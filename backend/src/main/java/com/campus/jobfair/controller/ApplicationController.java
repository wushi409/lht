package com.campus.jobfair.controller;

import com.campus.jobfair.dto.ApiResponse;
import com.campus.jobfair.dto.ApplicationStatusUpdateRequest;
import com.campus.jobfair.entity.ApplicationRecord;
import com.campus.jobfair.security.CustomUserDetails;
import com.campus.jobfair.service.ApplicationService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/applications")
public class ApplicationController {

    private final ApplicationService applicationService;

    public ApplicationController(ApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PreAuthorize("hasAnyRole('COMPANY','ADMIN')")
    @RequestMapping(value = "/{id}/status", method = {org.springframework.web.bind.annotation.RequestMethod.POST, org.springframework.web.bind.annotation.RequestMethod.PUT})
    public ResponseEntity<ApiResponse<ApplicationRecord>> updateStatus(@AuthenticationPrincipal CustomUserDetails user,
                                                                       @PathVariable Long id,
                                                                       @RequestBody ApplicationStatusUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.updateStatus(id, request)));
    }
}
