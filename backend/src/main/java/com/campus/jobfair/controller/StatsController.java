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
public class StatsController {

    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    // 公开接口 - 首页统计
    @GetMapping("/public/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> publicStats() {
        return ResponseEntity.ok(ApiResponse.ok(statsService.overview()));
    }

    @GetMapping("/admin/stats/summary")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> summary() {
        return ResponseEntity.ok(ApiResponse.ok(statsService.summary()));
    }

    @GetMapping("/admin/stats/overview")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> overview() {
        return ResponseEntity.ok(ApiResponse.ok(statsService.overview()));
    }

    @GetMapping("/admin/stats/industry")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> industry() {
        return ResponseEntity.ok(ApiResponse.ok(statsService.industryStats()));
    }

    @GetMapping("/admin/stats/applications")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> applications() {
        return ResponseEntity.ok(ApiResponse.ok(statsService.applicationStats()));
    }

    @GetMapping("/admin/stats/job-types")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> jobTypes() {
        return ResponseEntity.ok(ApiResponse.ok(statsService.jobTypeStats()));
    }

    @GetMapping("/admin/stats/trend")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> trend() {
        return ResponseEntity.ok(ApiResponse.ok(statsService.trendStats()));
    }

    @GetMapping("/admin/stats/top-jobs")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> topJobs() {
        return ResponseEntity.ok(ApiResponse.ok(statsService.topJobs()));
    }

    @GetMapping("/admin/stats/job-intents")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<List<Map<String, Object>>>> jobIntents() {
        return ResponseEntity.ok(ApiResponse.ok(statsService.jobIntentStats()));
    }
}
