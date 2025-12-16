package com.campus.jobfair.service;

import com.campus.jobfair.dto.ResumeRequest;
import com.campus.jobfair.entity.FileResource;
import com.campus.jobfair.entity.Resume;
import com.campus.jobfair.entity.ResumeFile;
import com.campus.jobfair.entity.Student;
import com.campus.jobfair.repository.ApplicationRecordRepository;
import com.campus.jobfair.repository.ResumeFileRepository;
import com.campus.jobfair.repository.ResumeRepository;
import com.campus.jobfair.repository.StudentRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final ResumeFileRepository resumeFileRepository;
    private final StudentRepository studentRepository;
    private final ApplicationRecordRepository applicationRecordRepository;
    private final FileStorageService fileStorageService;

    public ResumeService(ResumeRepository resumeRepository,
                         ResumeFileRepository resumeFileRepository,
                         StudentRepository studentRepository,
                         ApplicationRecordRepository applicationRecordRepository,
                         FileStorageService fileStorageService) {
        this.resumeRepository = resumeRepository;
        this.resumeFileRepository = resumeFileRepository;
        this.studentRepository = studentRepository;
        this.applicationRecordRepository = applicationRecordRepository;
        this.fileStorageService = fileStorageService;
    }

    private Student getStudent(String username) {
        return studentRepository.findByStudentNo(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "学生不存在"));
    }

    public List<Resume> listMyResumes(String username) {
        return resumeRepository.findByStudent(getStudent(username));
    }

    @Transactional(readOnly = true)
    public Resume getById(Long resumeId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "简历不存在"));
        // 强制加载关联对象
        if (resume.getStudent() != null) {
            resume.getStudent().getName();
        }
        return resume;
    }

    @Transactional
    public Resume create(String username, ResumeRequest request) {
        Student student = getStudent(username);
        Resume resume = new Resume();
        resume.setStudent(student);
        resume.setTitle(request.getTitle());
        resume.setSummary(request.getSummary());
        resume.setEducation(request.getEducation());
        resume.setExperience(request.getExperience());
        resume.setSkills(request.getSkills());
        resume.setDefault(request.isDefault());
        resume.setLastEditedAt(Instant.now());
        Resume saved = resumeRepository.save(resume);
        if (request.isDefault()) {
            student.setDefaultResumeId(saved.getId());
            studentRepository.save(student);
        }
        return saved;
    }

    @Transactional
    public Resume update(String username, Long resumeId, ResumeRequest request) {
        Student student = getStudent(username);
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "简历不存在"));
        if (!resume.getStudent().getId().equals(student.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权操作该简历");
        }
        resume.setTitle(request.getTitle());
        resume.setSummary(request.getSummary());
        resume.setEducation(request.getEducation());
        resume.setExperience(request.getExperience());
        resume.setSkills(request.getSkills());
        resume.setLastEditedAt(Instant.now());
        if (request.isDefault()) {
            student.setDefaultResumeId(resume.getId());
            resume.setDefault(true);
            studentRepository.save(student);
        }
        return resumeRepository.save(resume);
    }

    @Transactional
    public void delete(String username, Long resumeId) {
        Student student = getStudent(username);
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "简历不存在"));
        if (!resume.getStudent().getId().equals(student.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权删除该简历");
        }
        long used = applicationRecordRepository.countByResume(resume);
        if (used > 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "简历已用于投递，无法删除");
        }
        if (student.getDefaultResumeId() != null && student.getDefaultResumeId().equals(resumeId)) {
            student.setDefaultResumeId(null);
            studentRepository.save(student);
        }
        resumeRepository.delete(resume);
    }

    @Transactional
    public ResumeFile uploadFile(String username, Long resumeId, MultipartFile file) {
        Student student = getStudent(username);
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "简历不存在"));
        if (!resume.getStudent().getId().equals(student.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权上传该简历附件");
        }
        FileResource savedFile = fileStorageService.store("RESUME", resumeId, file);
        ResumeFile rf = new ResumeFile();
        rf.setResume(resume);
        rf.setFileName(savedFile.getFileName());
        rf.setUrl(savedFile.getUrl());
        rf.setContentType(savedFile.getContentType());
        rf.setSize(savedFile.getSize());
        return resumeFileRepository.save(rf);
    }

    @Transactional
    public void setDefault(String username, Long resumeId) {
        Student student = getStudent(username);
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "简历不存在"));
        if (!resume.getStudent().getId().equals(student.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权操作该简历");
        }
        student.setDefaultResumeId(resumeId);
        resume.setDefault(true);
        studentRepository.save(student);
        resumeRepository.save(resume);
    }
}
