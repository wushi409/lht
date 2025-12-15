package com.campus.jobfair.repository;

import com.campus.jobfair.entity.JobFair;
import com.campus.jobfair.entity.JobFairEvent;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobFairEventRepository extends JpaRepository<JobFairEvent, Long> {
    List<JobFairEvent> findByJobFair(JobFair jobFair);
}
