package com.campus.jobfair.controller;

import com.campus.jobfair.dto.ApiResponse;
import com.campus.jobfair.entity.FileResource;
import com.campus.jobfair.security.CustomUserDetails;
import com.campus.jobfair.service.FileStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/files")
public class FileController {

    private final FileStorageService fileStorageService;

    public FileController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/upload")
    public ResponseEntity<ApiResponse<FileResource>> upload(@AuthenticationPrincipal CustomUserDetails user,
                                                            @RequestParam("file") MultipartFile file,
                                                            @RequestParam(defaultValue = "USER") String ownerType) {
        FileResource res = fileStorageService.store(ownerType, user != null ? user.getId() : null, file);
        return ResponseEntity.ok(ApiResponse.ok(res));
    }
}
