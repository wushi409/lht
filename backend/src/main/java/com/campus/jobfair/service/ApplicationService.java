package com.campus.jobfair.service;

import com.campus.jobfair.dto.ApplicationRequest;
import com.campus.jobfair.dto.ApplicationStatusUpdateRequest;
import com.campus.jobfair.entity.ApplicationRecord;
import com.campus.jobfair.entity.Job;
import com.campus.jobfair.entity.Resume;
import com.campus.jobfair.entity.Student;
import com.campus.jobfair.entity.enums.ApplicationStatus;
import com.campus.jobfair.repository.ApplicationRecordRepository;
import com.campus.jobfair.repository.JobRepository;
import com.campus.jobfair.repository.ResumeRepository;
import com.campus.jobfair.repository.StudentRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ApplicationService {

    private final ApplicationRecordRepository applicationRecordRepository;
    private final StudentRepository studentRepository;
    private final JobRepository jobRepository;
    private final ResumeRepository resumeRepository;

    public ApplicationService(ApplicationRecordRepository applicationRecordRepository,
                              StudentRepository studentRepository,
                              JobRepository jobRepository,
                              ResumeRepository resumeRepository) {
        this.applicationRecordRepository = applicationRecordRepository;
        this.studentRepository = studentRepository;
        this.jobRepository = jobRepository;
        this.resumeRepository = resumeRepository;
    }

    @Transactional(readOnly = true)
    public List<ApplicationRecord> listMyApplications(String studentUsername) {
        Student student = studentRepository.findByStudentNo(studentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "学生不存在"));
        List<ApplicationRecord> applications = applicationRecordRepository.findByStudent(student);
        // 强制加载懒加载的关联对象
        applications.forEach(app -> {
            if (app.getJob() != null) {
                app.getJob().getTitle(); // 触发加载
                if (app.getJob().getCompany() != null) {
                    app.getJob().getCompany().getName(); // 触发加载
                }
            }
            if (app.getResume() != null) {
                app.getResume().getTitle(); // 触发加载
            }
        });
        return applications;
    }

    @Transactional
    public ApplicationRecord apply(String studentUsername, ApplicationRequest request) {
        Student student = studentRepository.findByStudentNo(studentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "学生不存在"));
        Job job = jobRepository.findById(request.getJobId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "岗位不存在"));

        applicationRecordRepository.findByStudentAndJob(student, job).ifPresent(a -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "已投递该岗位");
        });

        ApplicationRecord record = new ApplicationRecord();
        record.setStudent(student);
        record.setJob(job);
        record.setStatus(ApplicationStatus.SUBMITTED);
        record.setNotes(request.getNotes());
        if (request.getResumeId() != null) {
            Resume resume = resumeRepository.findById(request.getResumeId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "简历不存在"));
            record.setResume(resume);
        } else if (student.getDefaultResumeId() != null) {
            Resume resume = resumeRepository.findById(student.getDefaultResumeId())
                    .orElse(null);
            record.setResume(resume);
        }
        ApplicationRecord saved = applicationRecordRepository.save(record);
        // 通知功能已移除
        return saved;
    }

    @Transactional
    public void withdraw(String studentUsername, Long applicationId) {
        Student student = studentRepository.findByStudentNo(studentUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "学生不存在"));
        ApplicationRecord record = applicationRecordRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "投递不存在"));
        if (!record.getStudent().getId().equals(student.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权撤回该投递");
        }
        record.setStatus(ApplicationStatus.WITHDRAWN);
        record.setWithdrawnAt(Instant.now());
        applicationRecordRepository.save(record);
    }

    @Transactional
    public ApplicationRecord updateStatus(Long applicationId, ApplicationStatusUpdateRequest req) {
        ApplicationRecord record = applicationRecordRepository.findById(applicationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "投递不存在"));
        record.setStatus(req.getStatus());
        record.setNotes(req.getNotes());
        ApplicationRecord saved = applicationRecordRepository.save(record);
        // 通知功能已移除
        return saved;
    }

    @Transactional(readOnly = true)
    public List<ApplicationRecord> listByCompany(String companyUsername) {
        return applicationRecordRepository.findByCompanyUsername(companyUsername);
    }
}