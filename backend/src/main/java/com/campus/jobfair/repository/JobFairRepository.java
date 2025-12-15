package com.campus.jobfair.repository;

import com.campus.jobfair.entity.JobFair;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JobFairRepository extends JpaRepository<JobFair, Long> {
}
