package com.campus.jobfair.controller;

import com.campus.jobfair.dto.AdminCheckinRequest;
import com.campus.jobfair.dto.ApiResponse;
import com.campus.jobfair.dto.JobFairEventRequest;
import com.campus.jobfair.dto.JobFairRequest;
import com.campus.jobfair.entity.EventRegistration;
import com.campus.jobfair.entity.JobFair;
import com.campus.jobfair.entity.JobFairEvent;
import com.campus.jobfair.service.EventService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
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

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/checkin/{token}")
    public ResponseEntity<ApiResponse<java.util.Map<String, String>>> scanCheckin(@PathVariable String token) {
        try {
            Long registrationId = Long.parseLong(token);
            EventRegistration registration = eventService.checkIn(registrationId);
            
            java.util.Map<String, String> result = new java.util.HashMap<>();
            result.put("studentName", registration.getStudent() != null ? registration.getStudent().getName() : "未知");
            result.put("eventName", registration.getEvent() != null ? registration.getEvent().getName() : "未知");
            
            return ResponseEntity.ok(ApiResponse.ok("签到成功", result));
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body(ApiResponse.fail("无效的签到码"));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/event-registrations")
    public ResponseEntity<ApiResponse<List<EventRegistration>>> listRegistrations(@org.springframework.web.bind.annotation.RequestParam(required = false) Long eventId) {
        if (eventId == null) {
            // 获取所有活动的报名记录
            return ResponseEntity.ok(ApiResponse.ok(eventService.listAllRegistrations()));
        }
        return ResponseEntity.ok(ApiResponse.ok(eventService.listRegistrationsByEvent(eventId)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin/events/{eventId}/registrations")
    public ResponseEntity<ApiResponse<List<EventRegistration>>> getEventRegistrations(@PathVariable Long eventId) {
        return ResponseEntity.ok(ApiResponse.ok(eventService.listRegistrationsByEvent(eventId)));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin/checkin")
    public ResponseEntity<ApiResponse<java.util.Map<String, Object>>> manualCheckin(@Valid @RequestBody AdminCheckinRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(eventService.adminCheckIn(request.getEventId(), request.getRegistrationId())));
    }
}
