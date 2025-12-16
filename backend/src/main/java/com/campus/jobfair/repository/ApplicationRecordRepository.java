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
    @org.springframework.data.jpa.repository.Query("SELECT a FROM ApplicationRecord a LEFT JOIN FETCH a.job j LEFT JOIN FETCH j.company LEFT JOIN FETCH a.student LEFT JOIN FETCH a.resume WHERE a.student = :student")
    List<ApplicationRecord> findByStudent(@org.springframework.data.repository.query.Param("student") Student student);
    
    @org.springframework.data.jpa.repository.Query("SELECT a FROM ApplicationRecord a LEFT JOIN FETCH a.job j LEFT JOIN FETCH j.company LEFT JOIN FETCH a.student LEFT JOIN FETCH a.resume WHERE a.job = :job")
    List<ApplicationRecord> findByJob(@org.springframework.data.repository.query.Param("job") Job job);
    
    Optional<ApplicationRecord> findByStudentAndJob(Student student, Job job);
    long countByJobAndStatus(Job job, ApplicationStatus status);
    long countByResume(Resume resume);
    long countByStatus(ApplicationStatus status);

    @org.springframework.data.jpa.repository.Query("SELECT a FROM ApplicationRecord a LEFT JOIN FETCH a.job j LEFT JOIN FETCH j.company c LEFT JOIN FETCH a.student LEFT JOIN FETCH a.resume WHERE c.creditCode = :companyUsername")
    List<ApplicationRecord> findByCompanyUsername(@org.springframework.data.repository.query.Param("companyUsername") String companyUsername);
}