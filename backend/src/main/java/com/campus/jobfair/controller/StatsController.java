package com.campus.jobfair.controller;

import com.campus.jobfair.dto.ApiResponse;
import com.campus.jobfair.service.StatsService;
import java.util.List;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/stats")
@PreAuthorize("hasRole('ADMIN')")
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> summary() {
        return ResponseEntity.ok(ApiResponse.ok(statsService.summary()));
    }

    @GetMapping("/overview")
    public ResponseEntity<ApiResponse<Map<String, Object>>> overview() {
        return ResponseEntity.ok(ApiResponse.ok(statsService.overview()));
    }

    @GetMapping("/industry")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> industry() {
        return ResponseEntity.ok(ApiResponse.ok(statsService.industryStats()));
    }

    @GetMapping("/applications")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> applications() {
        return ResponseEntity.ok(ApiResponse.ok(statsService.applicationStats()));
    }

    @GetMapping("/job-types")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> jobTypes() {
        return ResponseEntity.ok(ApiResponse.ok(statsService.jobTypeStats()));
    }

    @GetMapping("/trend")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> trend() {
        return ResponseEntity.ok(ApiResponse.ok(statsService.trendStats()));
    }

    @GetMapping("/top-jobs")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> topJobs() {
        return ResponseEntity.ok(ApiResponse.ok(statsService.topJobs()));
    }
}
