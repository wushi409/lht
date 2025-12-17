package com.campus.jobfair.controller;

import com.campus.jobfair.dto.ApiResponse;
import com.campus.jobfair.dto.BoothRequest;
import com.campus.jobfair.entity.Booth;
import com.campus.jobfair.service.BoothService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class BoothController {

    private final BoothService boothService;

    public BoothController(BoothService boothService) {
        this.boothService = boothService;
    }

    @GetMapping("/job-fairs/{id}/booths")
    public ResponseEntity<ApiResponse<List<Booth>>> list(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(boothService.listByFair(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/booths")
    public ResponseEntity<ApiResponse<List<Booth>>> listAll() {
        return ResponseEntity.ok(ApiResponse.ok(boothService.listAll()));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/booths")
    public ResponseEntity<ApiResponse<Booth>> create(@Valid @RequestBody BoothRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(boothService.create(request)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @org.springframework.web.bind.annotation.PutMapping("/booths/{id}")
    public ResponseEntity<ApiResponse<Booth>> update(@PathVariable Long id, @Valid @RequestBody BoothRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(boothService.update(id, request)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @org.springframework.web.bind.annotation.DeleteMapping("/booths/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        boothService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok("删除成功", null));
    }
}
