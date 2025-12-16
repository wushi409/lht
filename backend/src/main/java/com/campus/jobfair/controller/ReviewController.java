package com.campus.jobfair.controller;

import com.campus.jobfair.dto.ApiResponse;
import com.campus.jobfair.dto.ReviewRequest;
import com.campus.jobfair.entity.Review;
import com.campus.jobfair.entity.enums.ReviewStatus;
import com.campus.jobfair.security.CustomUserDetails;
import com.campus.jobfair.service.ReviewService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @GetMapping("/companies/{id}/reviews")
    public ResponseEntity<ApiResponse<List<Review>>> list(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(reviewService.listVisibleByCompany(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/reviews")
    public ResponseEntity<ApiResponse<List<Review>>> listAll() {
        return ResponseEntity.ok(ApiResponse.ok(reviewService.listAll()));
    }

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/reviews")
    public ResponseEntity<ApiResponse<Review>> create(@AuthenticationPrincipal CustomUserDetails user,
                                                      @Valid @RequestBody ReviewRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(reviewService.create(user.getUsername(), request)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/reviews/{id}/visibility")
    public ResponseEntity<ApiResponse<Review>> visibility(@PathVariable Long id,
                                                          @RequestParam ReviewStatus status) {
        return ResponseEntity.ok(ApiResponse.ok(reviewService.changeVisibility(id, status)));
    }
}
