package com.campus.jobfair.repository;

import com.campus.jobfair.entity.Company;
import com.campus.jobfair.entity.enums.CompanyStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository extends JpaRepository<Company, Long> {
    Optional<Company> findByCreditCode(String creditCode);
    boolean existsByCreditCode(String creditCode);
    List<Company> findByStatus(CompanyStatus status);

    Optional<Company> findByContactPhone(String contactPhone);
}
