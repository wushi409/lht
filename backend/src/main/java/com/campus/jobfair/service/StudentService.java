package com.campus.jobfair.service;

import com.campus.jobfair.dto.StudentProfileUpdateRequest;
import com.campus.jobfair.entity.Student;
import com.campus.jobfair.repository.StudentRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student getProfileByUsername(String username) {
        return studentRepository.findByStudentNo(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "学生不存在"));
    }

    @Transactional
    public Student updateProfile(String username, StudentProfileUpdateRequest request) {
        Student student = getProfileByUsername(username);
        if (request.getName() != null) student.setName(request.getName());
        if (request.getCollege() != null) student.setCollege(request.getCollege());
        if (request.getPhone() != null) student.setPhone(request.getPhone());
        if (request.getEmail() != null) student.setEmail(request.getEmail());
        if (request.getJobIntent() != null) student.setJobIntent(request.getJobIntent());
        if (request.getDefaultResumeId() != null) student.setDefaultResumeId(request.getDefaultResumeId());
        return studentRepository.save(student);
    }
}
