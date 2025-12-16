package com.campus.jobfair.repository;

import com.campus.jobfair.entity.Job;
import com.campus.jobfair.entity.enums.JobStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.campus.jobfair.entity.Company;

public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByStatus(JobStatus status);

    @Query("SELECT j FROM Job j LEFT JOIN FETCH j.company WHERE (:industry is null or j.company.industry = :industry) " +
            "and (:jobType is null or j.jobType = :jobType) " +
            "and (:location is null or j.location = :location) " +
            "and (:status is null or j.status = :status)")
    List<Job> search(@Param("industry") String industry,
                     @Param("jobType") String jobType,
                     @Param("location") String location,
                     @Param("status") JobStatus status);

    @Query("SELECT j FROM Job j LEFT JOIN FETCH j.company WHERE lower(j.title) like lower(concat('%', :keyword, '%')) " +
            "or lower(j.company.name) like lower(concat('%', :keyword, '%'))")
    List<Job> searchByKeyword(@Param("keyword") String keyword);

    // 企业维度查询
    @Query("SELECT j FROM Job j LEFT JOIN FETCH j.company WHERE j.company = :company")
    List<Job> findByCompany(@Param("company") Company company);
}
