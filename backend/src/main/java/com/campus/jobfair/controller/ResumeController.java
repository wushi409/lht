package com.campus.jobfair.controller;

import com.campus.jobfair.dto.ApiResponse;
import com.campus.jobfair.dto.ResumeRequest;
import com.campus.jobfair.entity.Resume;
import com.campus.jobfair.entity.ResumeFile;
import com.campus.jobfair.security.CustomUserDetails;
import com.campus.jobfair.service.ResumeService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/resumes")
@PreAuthorize("hasRole('STUDENT')")
public class ResumeController {

    private final ResumeService resumeService;

    public ResumeController(ResumeService resumeService) {
        this.resumeService = resumeService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Resume>>> list(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(resumeService.listMyResumes(user.getUsername())));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<Resume>> create(@AuthenticationPrincipal CustomUserDetails user,
                                                      @Valid @RequestBody ResumeRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(resumeService.create(user.getUsername(), request)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<Resume>> update(@AuthenticationPrincipal CustomUserDetails user,
                                                      @PathVariable Long id,
                                                      @Valid @RequestBody ResumeRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(resumeService.update(user.getUsername(), id, request)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@AuthenticationPrincipal CustomUserDetails user,
                                                    @PathVariable Long id) {
        resumeService.delete(user.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.ok("删除成功", null));
    }

    @PostMapping("/{id}/default")
    public ResponseEntity<ApiResponse<Void>> setDefault(@AuthenticationPrincipal CustomUserDetails user,
                                                        @PathVariable Long id) {
        resumeService.setDefault(user.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.ok("已设为默认", null));
    }

    @PostMapping("/{id}/file")
    public ResponseEntity<ApiResponse<ResumeFile>> uploadFile(@AuthenticationPrincipal CustomUserDetails user,
                                                              @PathVariable Long id,
                                                              @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(ApiResponse.ok(resumeService.uploadFile(user.getUsername(), id, file)));
    }
}
