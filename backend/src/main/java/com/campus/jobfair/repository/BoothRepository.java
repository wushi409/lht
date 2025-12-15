package com.campus.jobfair.repository;

import com.campus.jobfair.entity.Booth;
import com.campus.jobfair.entity.Company;
import com.campus.jobfair.entity.JobFair;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BoothRepository extends JpaRepository<Booth, Long> {
    List<Booth> findByJobFair(JobFair jobFair);
    Optional<Booth> findByJobFairAndBoothNo(JobFair jobFair, String boothNo);
    List<Booth> findByCompany(Company company);
}
