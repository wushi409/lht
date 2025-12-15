package com.campus.jobfair.service;

import com.campus.jobfair.dto.ReviewRequest;
import com.campus.jobfair.entity.Company;
import com.campus.jobfair.entity.Review;
import com.campus.jobfair.entity.Student;
import com.campus.jobfair.entity.enums.ReviewStatus;
import com.campus.jobfair.repository.CompanyRepository;
import com.campus.jobfair.repository.ReviewRepository;
import com.campus.jobfair.repository.StudentRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final CompanyRepository companyRepository;
    private final StudentRepository studentRepository;

    public ReviewService(ReviewRepository reviewRepository,
                         CompanyRepository companyRepository,
                         StudentRepository studentRepository) {
        this.reviewRepository = reviewRepository;
        this.companyRepository = companyRepository;
        this.studentRepository = studentRepository;
    }

    private Student getStudent(String username) {
        return studentRepository.findByStudentNo(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "学生不存在"));
    }

    public List<Review> listVisibleByCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "企业不存在"));
        return reviewRepository.findByCompanyAndStatus(company, ReviewStatus.VISIBLE);
    }

    public List<Review> listAllByCompany(Long companyId) {
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "企业不存在"));
        return reviewRepository.findByCompany(company);
    }

    @Transactional
    public Review create(String username, ReviewRequest req) {
        Student student = getStudent(username);
        Company company = companyRepository.findById(req.getCompanyId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "企业不存在"));
        Review review = new Review();
        review.setCompany(company);
        review.setStudent(student);
        review.setRating(req.getRating());
        review.setComment(req.getComment());
        review.setStatus(ReviewStatus.VISIBLE);
        return reviewRepository.save(review);
    }

    @Transactional
    public Review changeVisibility(Long reviewId, ReviewStatus status) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "评价不存在"));
        review.setStatus(status);
        return reviewRepository.save(review);
    }
}
