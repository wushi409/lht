package com.campus.jobfair.service;

import com.campus.jobfair.dto.InterviewCreateRequest;
import com.campus.jobfair.dto.InterviewStatusUpdateRequest;
import com.campus.jobfair.entity.Interview;
import com.campus.jobfair.entity.Job;
import com.campus.jobfair.entity.Student;
import com.campus.jobfair.entity.enums.InterviewStatus;
import com.campus.jobfair.entity.enums.NotificationType;
import com.campus.jobfair.entity.enums.UserRole;
import com.campus.jobfair.repository.InterviewRepository;
import com.campus.jobfair.repository.JobRepository;
import com.campus.jobfair.repository.StudentRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final JobRepository jobRepository;
    private final StudentRepository studentRepository;
    private final NotificationService notificationService;

    public InterviewService(InterviewRepository interviewRepository,
                            JobRepository jobRepository,
                            StudentRepository studentRepository,
                            NotificationService notificationService) {
        this.interviewRepository = interviewRepository;
        this.jobRepository = jobRepository;
        this.studentRepository = studentRepository;
        this.notificationService = notificationService;
    }

    private Student getStudent(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "学生不存在"));
    }

    @Transactional
    public Interview schedule(String companyUsername, InterviewCreateRequest request) {
        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "岗位不存在"));
        if (!job.getCompany().getCreditCode().equals(companyUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权为该岗位安排面试");
        }
        Student student = getStudent(request.getStudentId());
        Interview interview = new Interview();
        interview.setJob(job);
        interview.setStudent(student);
        if (request.getScheduledAt() != null) {
            interview.setScheduledAt(Instant.parse(request.getScheduledAt()));
        }
        interview.setLocation(request.getLocation());
        interview.setInterviewer(request.getInterviewer());
        interview.setStatus(InterviewStatus.PENDING);
        Interview saved = interviewRepository.save(interview);

        notificationService.send(UserRole.STUDENT, student.getId(),
                "面试邀请", "您收到岗位" + job.getTitle() + "的面试邀请", NotificationType.INTERVIEW_INVITE);
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Interview> listForCompany(String companyUsername) {
        List<Interview> interviews = interviewRepository.findByJobCompanyCreditCode(companyUsername);
        // 强制加载关联对象
        for (Interview interview : interviews) {
            if (interview.getStudent() != null) {
                interview.getStudent().getName();
            }
            if (interview.getJob() != null) {
                interview.getJob().getTitle();
            }
        }
        return interviews;
    }

    @Transactional(readOnly = true)
    public List<Interview> listForStudent(Long studentId) {
        List<Interview> interviews = interviewRepository.findByStudentId(studentId);
        // 强制加载关联对象
        for (Interview interview : interviews) {
            if (interview.getJob() != null) {
                interview.getJob().getTitle();
                if (interview.getJob().getCompany() != null) {
                    interview.getJob().getCompany().getName();
                }
            }
        }
        return interviews;
    }

    @Transactional
    public Interview updateStatusForStudent(Long interviewId, Long studentId, InterviewStatusUpdateRequest req) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "面试不存在"));
        if (!interview.getStudent().getId().equals(studentId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权操作该面试");
        }
        interview.setStatus(req.getStatus());
        interview.setFeedback(req.getFeedback());
        Interview saved = interviewRepository.save(interview);
        notificationService.send(UserRole.COMPANY, interview.getJob().getCompany().getId(),
                "候选人反馈", "候选人对面试有新反馈", NotificationType.INTERVIEW_INVITE);
        return saved;
    }

    @Transactional
    public Interview rescheduleByCompany(Long interviewId, String companyUsername, InterviewCreateRequest req) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "面试不存在"));
        if (!interview.getJob().getCompany().getCreditCode().equals(companyUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权操作该面试");
        }
        if (req.getScheduledAt() != null) {
            interview.setScheduledAt(Instant.parse(req.getScheduledAt()));
        }
        interview.setLocation(req.getLocation());
        interview.setInterviewer(req.getInterviewer());
        Interview saved = interviewRepository.save(interview);
        notificationService.send(UserRole.STUDENT, interview.getStudent().getId(),
                "面试更新", "您的面试安排已更新", NotificationType.INTERVIEW_INVITE);
        return saved;
    }

    @Transactional
    public Interview cancelByCompany(Long interviewId, String companyUsername) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "面试不存在"));
        if (!interview.getJob().getCompany().getCreditCode().equals(companyUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权操作该面试");
        }
        interview.setStatus(InterviewStatus.CANCELLED);
        Interview saved = interviewRepository.save(interview);
        notificationService.send(UserRole.STUDENT, interview.getStudent().getId(),
                "面试取消", "您的面试已被企业取消", NotificationType.INTERVIEW_INVITE);
        return saved;
    }
}
