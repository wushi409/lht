package com.campus.jobfair.service;

import com.campus.jobfair.dto.BoothRequest;
import com.campus.jobfair.entity.Booth;
import com.campus.jobfair.entity.Company;
import com.campus.jobfair.entity.JobFair;
import com.campus.jobfair.repository.BoothRepository;
import com.campus.jobfair.repository.CompanyRepository;
import com.campus.jobfair.repository.JobFairRepository;
import java.time.Instant;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class BoothService {

    private final BoothRepository boothRepository;
    private final CompanyRepository companyRepository;
    private final JobFairRepository jobFairRepository;

    public BoothService(BoothRepository boothRepository,
                        CompanyRepository companyRepository,
                        JobFairRepository jobFairRepository) {
        this.boothRepository = boothRepository;
        this.companyRepository = companyRepository;
        this.jobFairRepository = jobFairRepository;
    }

    public List<Booth> listByFair(Long fairId) {
        JobFair fair = jobFairRepository.findById(fairId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "双选会不存在"));
        return boothRepository.findByJobFair(fair);
    }

    @Transactional(readOnly = true)
    public List<Booth> listAll() {
        List<Booth> booths = boothRepository.findAll();
        // 强制加载关联对象
        for (Booth booth : booths) {
            if (booth.getCompany() != null) {
                booth.getCompany().getName();
            }
            if (booth.getJobFair() != null) {
                booth.getJobFair().getName();
            }
        }
        return booths;
    }

    @Transactional
    public Booth create(BoothRequest request) {
        JobFair fair = jobFairRepository.findById(request.getJobFairId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "双选会不存在"));
        Company company = companyRepository.findById(request.getCompanyId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "企业不存在"));
        Booth booth = new Booth();
        booth.setJobFair(fair);
        booth.setCompany(company);
        booth.setBoothNo(request.getBoothNo());
        booth.setLocation(request.getLocation());
        booth.setCheckedIn(false);
        return boothRepository.save(booth);
    }

    @Transactional
    public Booth checkIn(Long boothId) {
        Booth booth = boothRepository.findById(boothId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "展位不存在"));
        booth.setCheckedIn(true);
        booth.setCheckinTime(Instant.now());
        return boothRepository.save(booth);
    }

    @Transactional
    public Booth cancelCheckIn(Long boothId) {
        Booth booth = boothRepository.findById(boothId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "展位不存在"));
        booth.setCheckedIn(false);
        booth.setCheckinTime(null);
        return boothRepository.save(booth);
    }

    @Transactional
    public Booth update(Long boothId, BoothRequest request) {
        Booth booth = boothRepository.findById(boothId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "展位不存在"));
        
        if (request.getCompanyId() != null) {
            Company company = companyRepository.findById(request.getCompanyId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "企业不存在"));
            booth.setCompany(company);
        } else {
            booth.setCompany(null);
        }
        
        if (request.getBoothNo() != null) {
            booth.setBoothNo(request.getBoothNo());
        }
        if (request.getLocation() != null) {
            booth.setLocation(request.getLocation());
        }
        
        return boothRepository.save(booth);
    }

    @Transactional
    public void delete(Long boothId) {
        Booth booth = boothRepository.findById(boothId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "展位不存在"));
        boothRepository.delete(booth);
    }
}
