package com.campus.jobfair.repository;

import com.campus.jobfair.entity.Company;
import com.campus.jobfair.entity.FavoriteCompany;
import com.campus.jobfair.entity.Student;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteCompanyRepository extends JpaRepository<FavoriteCompany, Long> {
    List<FavoriteCompany> findByStudent(Student student);
    Optional<FavoriteCompany> findByStudentAndCompany(Student student, Company company);
}
