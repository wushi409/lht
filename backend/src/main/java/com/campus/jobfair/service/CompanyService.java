package com.campus.jobfair.service;

import com.campus.jobfair.dto.CompanyUpdateRequest;
import com.campus.jobfair.entity.Company;
import com.campus.jobfair.entity.FileResource;
import com.campus.jobfair.entity.UserAccount;
import com.campus.jobfair.entity.enums.CompanyStatus;
import com.campus.jobfair.repository.CompanyRepository;
import com.campus.jobfair.repository.UserAccountRepository;
import com.campus.jobfair.service.FileStorageService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserAccountRepository userAccountRepository;
    private final FileStorageService fileStorageService;

    public CompanyService(CompanyRepository companyRepository, UserAccountRepository userAccountRepository, FileStorageService fileStorageService) {
        this.companyRepository = companyRepository;
        this.userAccountRepository = userAccountRepository;
        this.fileStorageService = fileStorageService;
    }

    public Company getById(Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "企业不存在"));
    }

    public List<Company> listApproved() {
        return companyRepository.findByStatus(CompanyStatus.APPROVED);
    }

    public List<Company> listPending() {
        return companyRepository.findByStatus(CompanyStatus.PENDING);
    }

    public List<Company> listAll() {
        return companyRepository.findAll();
    }

    public Company getByUsername(String username) {
        return companyRepository.findByCreditCode(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "企业不存在"));
    }

    @Transactional
    public Company updateProfile(String username, CompanyUpdateRequest request) {
        Company company = getByUsername(username);
        if (request.getName() != null) company.setName(request.getName());
        if (request.getScale() != null) company.setScale(request.getScale());
        if (request.getIndustry() != null) company.setIndustry(request.getIndustry());
        if (request.getDescription() != null) company.setDescription(request.getDescription());
        if (request.getLogoUrl() != null) company.setLogoUrl(request.getLogoUrl());
        if (request.getContactName() != null) company.setContactName(request.getContactName());
        if (request.getContactPhone() != null) company.setContactPhone(request.getContactPhone());
        if (request.getContactEmail() != null) company.setContactEmail(request.getContactEmail());
        return companyRepository.save(company);
    }

    @Transactional
    public Company reviewCompany(Long id, boolean approved, String reason) {
        Company company = getById(id);
        company.setStatus(approved ? CompanyStatus.APPROVED : CompanyStatus.REJECTED);
        company.setRejectionReason(approved ? null : reason);
        companyRepository.save(company);

        userAccountRepository.findByUsername(company.getCreditCode()).ifPresent(acc -> {
            acc.setActive(approved);
            userAccountRepository.save(acc);
        });
        // 通知功能已移除
        return company;
    }

    @Transactional
    public Company uploadLogo(String username, MultipartFile file) {
        Company company = getByUsername(username);
        FileResource res = fileStorageService.store("COMPANY_LOGO", company.getId(), file);
        company.setLogoUrl(res.getUrl());
        return companyRepository.save(company);
    }

    @Transactional
    public Company uploadLicense(String username, MultipartFile file) {
        Company company = getByUsername(username);
        FileResource res = fileStorageService.store("COMPANY_LICENSE", company.getId(), file);
        company.setLicenseUrl(res.getUrl());
        return companyRepository.save(company);
    }
}