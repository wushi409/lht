package com.campus.jobfair.controller;

import com.campus.jobfair.dto.ApiResponse;
import com.campus.jobfair.entity.EventRegistration;
import com.campus.jobfair.entity.JobFair;
import com.campus.jobfair.entity.JobFairEvent;
import com.campus.jobfair.service.EventService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/job-fairs")
    public ResponseEntity<ApiResponse<List<JobFair>>> listFairs() {
        return ResponseEntity.ok(ApiResponse.ok(eventService.listJobFairs()));
    }

    @GetMapping("/events")
    public ResponseEntity<ApiResponse<List<JobFairEvent>>> listAllEvents() {
        return ResponseEntity.ok(ApiResponse.ok(eventService.listAllEvents()));
    }

    @GetMapping("/job-fairs/{id}/events")
    public ResponseEntity<ApiResponse<List<JobFairEvent>>> listEvents(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(eventService.listEventsByFair(id)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/events/{registrationId}/checkin")
    public ResponseEntity<ApiResponse<EventRegistration>> checkin(@PathVariable Long registrationId) {
        return ResponseEntity.ok(ApiResponse.ok(eventService.checkIn(registrationId)));
    }
}
