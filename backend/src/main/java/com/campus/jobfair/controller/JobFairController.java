package com.campus.jobfair.controller;

import com.campus.jobfair.dto.ApiResponse;
import com.campus.jobfair.dto.EventRequest;
import com.campus.jobfair.dto.JobFairRequest;
import com.campus.jobfair.entity.JobFair;
import com.campus.jobfair.entity.JobFairEvent;
import com.campus.jobfair.service.JobFairService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class JobFairController {

    private final JobFairService jobFairService;

    public JobFairController(JobFairService jobFairService) {
        this.jobFairService = jobFairService;
    }

    @GetMapping("/job-fairs")
    public ResponseEntity<ApiResponse<List<JobFair>>> list() {
        return ResponseEntity.ok(ApiResponse.ok(jobFairService.listAll()));
    }

    @GetMapping("/job-fairs/{id}")
    public ResponseEntity<ApiResponse<JobFair>> detail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(jobFairService.getById(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/job-fairs")
    public ResponseEntity<ApiResponse<JobFair>> create(@Valid @RequestBody JobFairRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(jobFairService.create(request)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/job-fairs/{id}")
    public ResponseEntity<ApiResponse<JobFair>> update(@PathVariable Long id, @Valid @RequestBody JobFairRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(jobFairService.update(id, request)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/events")
    public ResponseEntity<ApiResponse<JobFairEvent>> createEvent(@Valid @RequestBody EventRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(jobFairService.createEvent(request)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/admin/events/{id}")
    public ResponseEntity<ApiResponse<JobFairEvent>> updateEvent(@PathVariable Long id, @Valid @RequestBody EventRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(jobFairService.updateEvent(id, request)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/job-fairs/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteFair(@PathVariable Long id) {
        jobFairService.deleteFair(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/admin/events/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable Long id) {
        jobFairService.deleteEvent(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
