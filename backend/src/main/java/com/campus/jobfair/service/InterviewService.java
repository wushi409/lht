package com.campus.jobfair.service;

import com.campus.jobfair.dto.InterviewCreateRequest;
import com.campus.jobfair.dto.InterviewStatusUpdateRequest;
import com.campus.jobfair.entity.ApplicationRecord;
import com.campus.jobfair.entity.Interview;
import com.campus.jobfair.entity.Job;
import com.campus.jobfair.entity.Student;
import com.campus.jobfair.entity.enums.ApplicationStatus;
import com.campus.jobfair.entity.enums.InterviewStatus;
import com.campus.jobfair.repository.ApplicationRecordRepository;
import com.campus.jobfair.repository.InterviewRepository;
import com.campus.jobfair.repository.JobRepository;
import com.campus.jobfair.repository.StudentRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class InterviewService {

    private final InterviewRepository interviewRepository;
    private final JobRepository jobRepository;
    private final StudentRepository studentRepository;
    private final ApplicationRecordRepository applicationRecordRepository;

    public InterviewService(InterviewRepository interviewRepository,
                            JobRepository jobRepository,
                            StudentRepository studentRepository,
                            ApplicationRecordRepository applicationRecordRepository) {
        this.interviewRepository = interviewRepository;
        this.jobRepository = jobRepository;
        this.studentRepository = studentRepository;
        this.applicationRecordRepository = applicationRecordRepository;
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

        // 同步更新该岗位下该学生的投递状态为面试中，便于学生端看到进度
        applicationRecordRepository.findByStudentAndJob(student, job).ifPresent(record -> {
            // 仅在当前状态尚未进入终态时更新
            if (record.getStatus() != ApplicationStatus.HIRED
                    && record.getStatus() != ApplicationStatus.REJECTED
                    && record.getStatus() != ApplicationStatus.WITHDRAWN) {
                record.setStatus(ApplicationStatus.INTERVIEW);
                applicationRecordRepository.save(record);
            }
        });

        // 通知功能已移除
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
    public List<Interview> listForStudent(String studentUsername) {
        Student student = studentRepository.findByStudentNo(studentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "学生不存在"));
        List<Interview> interviews = interviewRepository.findByStudent(student);
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
    public Interview updateStatusForStudent(Long interviewId, String studentUsername, InterviewStatusUpdateRequest req) {
        Interview interview = interviewRepository.findById(interviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "面试不存在"));

        // 根据登录学号找到学生实体，保证与面试记录关联的学生一致
        Student student = studentRepository.findByStudentNo(studentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "学生不存在"));

        if (!interview.getStudent().getId().equals(student.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权操作该面试");
        }

        interview.setStatus(req.getStatus());
        interview.setFeedback(req.getFeedback());
        Interview saved = interviewRepository.save(interview);
        // 通知功能已移除
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
        // 通知功能已移除
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
        // 通知功能已移除
        return saved;
    }
}
