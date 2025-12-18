package com.campus.jobfair.repository;

import com.campus.jobfair.entity.EventRegistration;
import com.campus.jobfair.entity.JobFairEvent;
import com.campus.jobfair.entity.Student;
import com.campus.jobfair.entity.enums.RegistrationStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {
    List<EventRegistration> findByStudent(Student student);
    List<EventRegistration> findByEvent(JobFairEvent event);
    org.springframework.data.domain.Page<EventRegistration> findByEvent(JobFairEvent event, org.springframework.data.domain.Pageable pageable);
    Optional<EventRegistration> findByEventAndStudent(JobFairEvent event, Student student);
    long countByEventAndStatus(JobFairEvent event, RegistrationStatus status);
    
    // 全局搜索：搜索学生姓名、学号、活动名称
    @org.springframework.data.jpa.repository.Query("SELECT r FROM EventRegistration r " +
            "LEFT JOIN r.student s " +
            "LEFT JOIN r.event e " +
            "WHERE s.name LIKE :keyword " +
            "OR s.studentNo LIKE :keyword " +
            "OR e.name LIKE :keyword")
    org.springframework.data.domain.Page<EventRegistration> searchAll(
            @org.springframework.data.repository.query.Param("keyword") String keyword,
            org.springframework.data.domain.Pageable pageable);
    
    // 指定活动内搜索
    @org.springframework.data.jpa.repository.Query("SELECT r FROM EventRegistration r " +
            "LEFT JOIN r.student s " +
            "LEFT JOIN r.event e " +
            "WHERE e.id = :eventId " +
            "AND (s.name LIKE :keyword OR s.studentNo LIKE :keyword)")
    org.springframework.data.domain.Page<EventRegistration> searchByEvent(
            @org.springframework.data.repository.query.Param("eventId") Long eventId,
            @org.springframework.data.repository.query.Param("keyword") String keyword,
            org.springframework.data.domain.Pageable pageable);
}
