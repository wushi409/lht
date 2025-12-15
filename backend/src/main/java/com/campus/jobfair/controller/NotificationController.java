package com.campus.jobfair.controller;

import com.campus.jobfair.dto.ApiResponse;
import com.campus.jobfair.entity.Notification;
import com.campus.jobfair.security.CustomUserDetails;
import com.campus.jobfair.service.NotificationService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/notifications")
    public ResponseEntity<ApiResponse<List<Notification>>> list(@AuthenticationPrincipal CustomUserDetails user,
                                                                @RequestParam(defaultValue = "false") boolean unread) {
        return ResponseEntity.ok(ApiResponse.ok(notificationService.list(user.getRole(), user.getId(), unread)));
    }

    @PostMapping("/notifications/{id}/read")
    public ResponseEntity<ApiResponse<Notification>> markRead(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(notificationService.markRead(id)));
    }
}
