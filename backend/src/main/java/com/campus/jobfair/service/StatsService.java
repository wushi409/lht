package com.campus.jobfair.service;

import com.campus.jobfair.entity.enums.ApplicationStatus;
import com.campus.jobfair.entity.enums.CompanyStatus;
import com.campus.jobfair.entity.enums.JobStatus;
import com.campus.jobfair.repository.ApplicationRecordRepository;
import com.campus.jobfair.repository.CompanyRepository;
import com.campus.jobfair.repository.JobRepository;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class StatsService {

    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;
    private final ApplicationRecordRepository applicationRecordRepository;

    public StatsService(CompanyRepository companyRepository,
                        JobRepository jobRepository,
                        ApplicationRecordRepository applicationRecordRepository) {
        this.companyRepository = companyRepository;
        this.jobRepository = jobRepository;
        this.applicationRecordRepository = applicationRecordRepository;
    }

    public Map<String, Object> summary() {
        Map<String, Object> result = new HashMap<>();
        result.put("approvedCompanies", companyRepository.findByStatus(CompanyStatus.APPROVED).size());
        result.put("pendingCompanies", companyRepository.findByStatus(CompanyStatus.PENDING).size());
        result.put("publishedJobs", jobRepository.findByStatus(JobStatus.PUBLISHED).size());
        result.put("applications", applicationRecordRepository.count());
        result.put("hired", applicationRecordRepository.countByStatus(ApplicationStatus.HIRED));
        result.put("interviews", applicationRecordRepository.countByStatus(ApplicationStatus.INTERVIEW));
        return result;
    }
}
