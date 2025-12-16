package com.campus.jobfair.repository;

import com.campus.jobfair.entity.Interview;
import com.campus.jobfair.entity.Job;
import com.campus.jobfair.entity.Student;
import com.campus.jobfair.entity.enums.InterviewStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface InterviewRepository extends JpaRepository<Interview, Long> {
    List<Interview> findByJob(Job job);
    List<Interview> findByStudent(Student student);
    List<Interview> findByStudentAndStatus(Student student, InterviewStatus status);

    @Query("SELECT i FROM Interview i LEFT JOIN FETCH i.job j LEFT JOIN FETCH j.company LEFT JOIN FETCH i.student WHERE j.company.creditCode = :creditCode")
    List<Interview> findByJobCompanyCreditCode(@Param("creditCode") String creditCode);

    @Query("SELECT i FROM Interview i LEFT JOIN FETCH i.job j LEFT JOIN FETCH j.company LEFT JOIN FETCH i.student WHERE i.student.id = :studentId")
    List<Interview> findByStudentId(@Param("studentId") Long studentId);
}