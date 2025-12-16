package com.campus.jobfair.controller;

import com.campus.jobfair.dto.ApiResponse;
import com.campus.jobfair.dto.ApplicationRequest;
import com.campus.jobfair.dto.EventRegistrationRequest;
import com.campus.jobfair.dto.ResumeRequest;
import com.campus.jobfair.dto.StudentProfileUpdateRequest;
import com.campus.jobfair.entity.ApplicationRecord;
import com.campus.jobfair.entity.EventRegistration;
import com.campus.jobfair.entity.FavoriteCompany;
import com.campus.jobfair.entity.FavoriteJob;
import com.campus.jobfair.entity.Resume;
import com.campus.jobfair.entity.Student;
import com.campus.jobfair.security.CustomUserDetails;
import com.campus.jobfair.service.ApplicationService;
import com.campus.jobfair.service.EventService;
import com.campus.jobfair.service.FavoriteService;
import com.campus.jobfair.service.ResumeService;
import com.campus.jobfair.service.StudentService;
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
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/students")
@PreAuthorize("hasRole('STUDENT')")
public class StudentController {

    private final StudentService studentService;
    private final ApplicationService applicationService;
    private final FavoriteService favoriteService;
    private final EventService eventService;
    private final ResumeService resumeService;

    public StudentController(StudentService studentService,
                             ApplicationService applicationService,
                             FavoriteService favoriteService,
                             EventService eventService,
                             ResumeService resumeService) {
        this.studentService = studentService;
        this.applicationService = applicationService;
        this.favoriteService = favoriteService;
        this.eventService = eventService;
        this.resumeService = resumeService;
    }

    @GetMapping("/me")
    public ResponseEntity<ApiResponse<Student>> me(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(studentService.getProfileByUsername(user.getUsername())));
    }

    @PutMapping("/me")
    public ResponseEntity<ApiResponse<Student>> update(@AuthenticationPrincipal CustomUserDetails user,
                                                       @RequestBody StudentProfileUpdateRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(studentService.updateProfile(user.getUsername(), request)));
    }

    @GetMapping("/me/applications")
    public ResponseEntity<ApiResponse<List<ApplicationRecord>>> myApplications(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.listMyApplications(user.getUsername())));
    }

    @PostMapping("/me/applications")
    public ResponseEntity<ApiResponse<ApplicationRecord>> apply(@AuthenticationPrincipal CustomUserDetails user,
                                                                 @Valid @RequestBody ApplicationRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(applicationService.apply(user.getUsername(), request)));
    }

    @DeleteMapping("/me/applications/{id}")
    public ResponseEntity<ApiResponse<Void>> withdraw(@AuthenticationPrincipal CustomUserDetails user,
                                                      @PathVariable Long id) {
        applicationService.withdraw(user.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.ok("撤回成功", null));
    }

    @GetMapping("/me/favorites/jobs")
    public ResponseEntity<ApiResponse<List<FavoriteJob>>> favoriteJobs(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(favoriteService.listFavoriteJobs(user.getUsername())));
    }

    @PostMapping("/me/favorites/jobs/{jobId}")
    public ResponseEntity<ApiResponse<FavoriteJob>> addFavoriteJob(@AuthenticationPrincipal CustomUserDetails user,
                                                                    @PathVariable Long jobId) {
        return ResponseEntity.ok(ApiResponse.ok(favoriteService.addJob(user.getUsername(), jobId)));
    }

    @DeleteMapping("/me/favorites/jobs/{jobId}")
    public ResponseEntity<ApiResponse<Void>> removeFavoriteJob(@AuthenticationPrincipal CustomUserDetails user,
                                                               @PathVariable Long jobId) {
        favoriteService.removeJob(user.getUsername(), jobId);
        return ResponseEntity.ok(ApiResponse.ok("取消收藏成功", null));
    }

    @GetMapping("/me/favorites/companies")
    public ResponseEntity<ApiResponse<List<FavoriteCompany>>> favoriteCompanies(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(favoriteService.listFavoriteCompanies(user.getUsername())));
    }

    @PostMapping("/me/favorites/companies/{companyId}")
    public ResponseEntity<ApiResponse<FavoriteCompany>> addFavoriteCompany(@AuthenticationPrincipal CustomUserDetails user,
                                                                           @PathVariable Long companyId) {
        return ResponseEntity.ok(ApiResponse.ok(favoriteService.addCompany(user.getUsername(), companyId)));
    }

    @DeleteMapping("/me/favorites/companies/{companyId}")
    public ResponseEntity<ApiResponse<Void>> removeFavoriteCompany(@AuthenticationPrincipal CustomUserDetails user,
                                                                   @PathVariable Long companyId) {
        favoriteService.removeCompany(user.getUsername(), companyId);
        return ResponseEntity.ok(ApiResponse.ok("取消收藏成功", null));
    }

    @PostMapping("/me/events/register")
    public ResponseEntity<ApiResponse<EventRegistration>> registerEvent(@AuthenticationPrincipal CustomUserDetails user,
                                                                        @Valid @RequestBody EventRegistrationRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(eventService.register(user.getUsername(), request)));
    }

    @GetMapping("/me/events")
    public ResponseEntity<ApiResponse<List<EventRegistration>>> myEvents(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(eventService.listMyRegistrations(user.getUsername())));
    }

    // 简历管理
    @GetMapping("/me/resumes")
    public ResponseEntity<ApiResponse<List<Resume>>> myResumes(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(resumeService.listMyResumes(user.getUsername())));
    }

    @PostMapping("/me/resumes")
    public ResponseEntity<ApiResponse<Resume>> createResume(@AuthenticationPrincipal CustomUserDetails user,
                                                            @Valid @RequestBody ResumeRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(resumeService.create(user.getUsername(), request)));
    }

    @PutMapping("/me/resumes/{id}")
    public ResponseEntity<ApiResponse<Resume>> updateResume(@AuthenticationPrincipal CustomUserDetails user,
                                                            @PathVariable Long id,
                                                            @Valid @RequestBody ResumeRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(resumeService.update(user.getUsername(), id, request)));
    }

    @DeleteMapping("/me/resumes/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteResume(@AuthenticationPrincipal CustomUserDetails user,
                                                          @PathVariable Long id) {
        resumeService.delete(user.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.ok("删除成功", null));
    }

    @PutMapping("/me/resumes/{id}/default")
    public ResponseEntity<ApiResponse<Void>> setDefaultResume(@AuthenticationPrincipal CustomUserDetails user,
                                                              @PathVariable Long id) {
        resumeService.setDefault(user.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.ok("已设为默认", null));
    }

    // 活动报名（兼容前端路由）
    @GetMapping("/me/registrations")
    public ResponseEntity<ApiResponse<List<EventRegistration>>> myRegistrations(@AuthenticationPrincipal CustomUserDetails user) {
        return ResponseEntity.ok(ApiResponse.ok(eventService.listMyRegistrations(user.getUsername())));
    }

    @PostMapping("/me/registrations")
    public ResponseEntity<ApiResponse<EventRegistration>> createRegistration(@AuthenticationPrincipal CustomUserDetails user,
                                                                             @Valid @RequestBody EventRegistrationRequest request) {
        return ResponseEntity.ok(ApiResponse.ok(eventService.register(user.getUsername(), request)));
    }

    @DeleteMapping("/me/registrations/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelRegistration(@AuthenticationPrincipal CustomUserDetails user,
                                                                @PathVariable Long id) {
        eventService.cancelRegistration(user.getUsername(), id);
        return ResponseEntity.ok(ApiResponse.ok("取消报名成功", null));
    }

    @PostMapping("/me/registrations/{id}/checkin")
    public ResponseEntity<ApiResponse<EventRegistration>> selfCheckin(@AuthenticationPrincipal CustomUserDetails user,
                                                                      @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.ok(eventService.selfCheckIn(user.getUsername(), id)));
    }
}