package com.campus.jobfair.repository;

import com.campus.jobfair.entity.JobFairEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface JobFairEventRepository extends JpaRepository<JobFairEvent, Long> {
    List<JobFairEvent> findByJobFairId(Long jobFairId);
}
