package com.campus.jobfair.service;

import com.campus.jobfair.dto.JobRequest;
import com.campus.jobfair.entity.Company;
import com.campus.jobfair.entity.Job;
import com.campus.jobfair.entity.enums.JobStatus;
import com.campus.jobfair.repository.CompanyRepository;
import com.campus.jobfair.repository.JobRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class JobService {

    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;

    public JobService(JobRepository jobRepository, CompanyRepository companyRepository) {
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
    }

    public List<Job> listPublished(String industry, String jobType, String location, String keyword) {
        if (keyword != null && !keyword.isBlank()) {
            return jobRepository.searchByKeyword(keyword);
        }
        return jobRepository.search(industry, jobType, location, JobStatus.PUBLISHED);
    }

    public Job getById(Long id) {
        return jobRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "岗位不存在"));
    }

    @Transactional
    public Job createJob(String companyUsername, JobRequest request) {
        Company company = companyRepository.findByCreditCode(companyUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "企业不存在"));
        Job job = new Job();
        job.setCompany(company);
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setSalaryRange(request.getSalaryRange());
        job.setHeadcount(request.getHeadcount());
        job.setSkills(request.getSkills());
        job.setLocation(request.getLocation());
        job.setJobType(request.getJobType());
        job.setStatus(JobStatus.PUBLISHED);
        job.setPublishAt(Instant.now());
        if (request.getDeadline() != null) job.setDeadline(Instant.parse(request.getDeadline()));
        return jobRepository.save(job);
    }

    @Transactional
    public Job updateJob(Long jobId, String companyUsername, JobRequest request) {
        Job job = getById(jobId);
        if (!job.getCompany().getCreditCode().equals(companyUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权修改该岗位");
        }
        job.setTitle(request.getTitle());
        job.setDescription(request.getDescription());
        job.setSalaryRange(request.getSalaryRange());
        job.setHeadcount(request.getHeadcount());
        job.setSkills(request.getSkills());
        job.setLocation(request.getLocation());
        job.setJobType(request.getJobType());
        if (request.getDeadline() != null) job.setDeadline(Instant.parse(request.getDeadline()));
        return jobRepository.save(job);
    }

    @Transactional
    public Job changeStatus(Long jobId, String companyUsername, JobStatus status) {
        Job job = getById(jobId);
        if (!job.getCompany().getCreditCode().equals(companyUsername)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "无权下架该岗位");
        }
        job.setStatus(status);
        return jobRepository.save(job);
    }

    public List<Job> listByCompany(String companyUsername) {
        Company company = companyRepository.findByCreditCode(companyUsername)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "企业不存在"));
        return jobRepository.findByCompany(company);
    }
}
