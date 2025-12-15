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
    Optional<EventRegistration> findByEventAndStudent(JobFairEvent event, Student student);
    long countByEventAndStatus(JobFairEvent event, RegistrationStatus status);
}
