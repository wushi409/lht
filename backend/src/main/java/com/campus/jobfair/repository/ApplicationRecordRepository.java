package com.campus.jobfair.repository;

import com.campus.jobfair.entity.ApplicationRecord;
import com.campus.jobfair.entity.Job;
import com.campus.jobfair.entity.Resume;
import com.campus.jobfair.entity.Student;
import com.campus.jobfair.entity.enums.ApplicationStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationRecordRepository extends JpaRepository<ApplicationRecord, Long> {
    List<ApplicationRecord> findByStudent(Student student);
    List<ApplicationRecord> findByJob(Job job);
    Optional<ApplicationRecord> findByStudentAndJob(Student student, Job job);
    long countByJobAndStatus(Job job, ApplicationStatus status);
    long countByResume(Resume resume);
    long countByStatus(ApplicationStatus status);
}