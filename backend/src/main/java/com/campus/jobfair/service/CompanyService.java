package com.campus.jobfair.service;

import com.campus.jobfair.dto.CompanyUpdateRequest;
import com.campus.jobfair.entity.Company;
import com.campus.jobfair.entity.UserAccount;
import com.campus.jobfair.entity.enums.CompanyStatus;
import com.campus.jobfair.entity.enums.NotificationType;
import com.campus.jobfair.entity.enums.UserRole;
import com.campus.jobfair.repository.CompanyRepository;
import com.campus.jobfair.repository.UserAccountRepository;
import com.campus.jobfair.service.NotificationService;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class CompanyService {

    private final CompanyRepository companyRepository;
    private final UserAccountRepository userAccountRepository;
    private final NotificationService notificationService;

    public CompanyService(CompanyRepository companyRepository, UserAccountRepository userAccountRepository, NotificationService notificationService) {
        this.companyRepository = companyRepository;
        this.userAccountRepository = userAccountRepository;
        this.notificationService = notificationService;
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
        notificationService.send(UserRole.COMPANY, company.getId(),
                approved ? "企业审核通过" : "企业审核拒绝",
                approved ? "您的企业已通过审核" : "审核被拒绝: " + reason,
                NotificationType.APPROVAL_RESULT);
        return company;
    }
}