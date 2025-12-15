package com.campus.jobfair.repository;

import com.campus.jobfair.entity.Company;
import com.campus.jobfair.entity.Review;
import com.campus.jobfair.entity.enums.ReviewStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByCompanyAndStatus(Company company, ReviewStatus status);

    @Query("select avg(r.rating) from Review r where r.company = :company and r.status = 'VISIBLE'")
    Double averageRating(@Param("company") Company company);
}
