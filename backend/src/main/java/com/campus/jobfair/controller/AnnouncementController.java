package com.campus.jobfair.controller;

import com.campus.jobfair.dto.ApiResponse;
import com.campus.jobfair.dto.AnnouncementRequest;
import com.campus.jobfair.entity.Announcement;
import com.campus.jobfair.service.AnnouncementService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AnnouncementController {

    private final AnnouncementService announcementService;

    public AnnouncementController(AnnouncementService announcementService) {
        this.announcementService = announcementService;
    }

    @GetMapping("/announcements")
    public ResponseEntity<ApiResponse<List<Announcement>>> list() {
        return ResponseEntity.ok(ApiResponse.ok(announcementService.listAll()));
    }

    @GetMapping("/announcements/{id}")
    public ResponseEntity<ApiResponse<Announcement>> detail(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(announcementService.getById(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/announcements")
    public ResponseEntity<ApiResponse<Announcement>> create(@Valid @RequestBody AnnouncementRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(announcementService.create(request)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/announcements/{id}")
    public ResponseEntity<ApiResponse<Announcement>> update(@PathVariable Long id,
                                                            @Valid @RequestBody AnnouncementRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(announcementService.update(id, request)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/announcements/{id}/delete")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        announcementService.delete(id);
        return ResponseEntity.ok(ApiResponse.ok(null));
    }
}
