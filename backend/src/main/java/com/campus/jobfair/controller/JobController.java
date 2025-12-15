package com.campus.jobfair.controller;

import com.campus.jobfair.dto.ApiResponse;
import com.campus.jobfair.entity.Job;
import com.campus.jobfair.service.JobService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    @GetMapping("/jobs")
    public ResponseEntity<ApiResponse<List<Job>>> list(
            @RequestParam(required = false) String industry,
            @RequestParam(required = false) String jobType,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String keyword) {
        return ResponseEntity.ok(ApiResponse.ok(jobService.listPublished(industry, jobType, location, keyword)));
    }

    @GetMapping("/jobs/{id}")
    public ResponseEntity<ApiResponse<Job>> get(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(jobService.getById(id)));
    }
}
