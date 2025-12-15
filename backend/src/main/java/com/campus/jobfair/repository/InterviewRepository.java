package com.campus.jobfair.repository;

import com.campus.jobfair.entity.Interview;
import com.campus.jobfair.entity.Job;
import com.campus.jobfair.entity.Student;
import com.campus.jobfair.entity.enums.InterviewStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByJob(Job job);
    List<Interview> findByStudent(Student student);
    List<Interview> findByStudentAndStatus(Student student, InterviewStatus status);
}
