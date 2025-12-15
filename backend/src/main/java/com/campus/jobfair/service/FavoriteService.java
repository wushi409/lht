package com.campus.jobfair.service;

import com.campus.jobfair.entity.Company;
import com.campus.jobfair.entity.FavoriteCompany;
import com.campus.jobfair.entity.FavoriteJob;
import com.campus.jobfair.entity.Job;
import com.campus.jobfair.entity.Student;
import com.campus.jobfair.repository.CompanyRepository;
import com.campus.jobfair.repository.FavoriteCompanyRepository;
import com.campus.jobfair.repository.FavoriteJobRepository;
import com.campus.jobfair.repository.JobRepository;
import com.campus.jobfair.repository.StudentRepository;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
public class FavoriteService {

    private final FavoriteCompanyRepository favoriteCompanyRepository;
    private final FavoriteJobRepository favoriteJobRepository;
    private final StudentRepository studentRepository;
    private final JobRepository jobRepository;
    private final CompanyRepository companyRepository;

    public FavoriteService(FavoriteCompanyRepository favoriteCompanyRepository,
                           FavoriteJobRepository favoriteJobRepository,
                           StudentRepository studentRepository,
                           JobRepository jobRepository,
                           CompanyRepository companyRepository) {
        this.favoriteCompanyRepository = favoriteCompanyRepository;
        this.favoriteJobRepository = favoriteJobRepository;
        this.studentRepository = studentRepository;
        this.jobRepository = jobRepository;
        this.companyRepository = companyRepository;
    }

    private Student getStudent(String username) {
        return studentRepository.findByStudentNo(username)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "学生不存在"));
    }

    public List<FavoriteCompany> listFavoriteCompanies(String username) {
        return favoriteCompanyRepository.findByStudent(getStudent(username));
    }

    public List<FavoriteJob> listFavoriteJobs(String username) {
        return favoriteJobRepository.findByStudent(getStudent(username));
    }

    @Transactional
    public FavoriteCompany addCompany(String username, Long companyId) {
        Student student = getStudent(username);
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "企业不存在"));
        favoriteCompanyRepository.findByStudentAndCompany(student, company).ifPresent(fc -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "已收藏该企业");
        });
        FavoriteCompany favorite = new FavoriteCompany();
        favorite.setStudent(student);
        favorite.setCompany(company);
        return favoriteCompanyRepository.save(favorite);
    }

    @Transactional
    public void removeCompany(String username, Long companyId) {
        Student student = getStudent(username);
        Company company = companyRepository.findById(companyId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "企业不存在"));
        favoriteCompanyRepository.findByStudentAndCompany(student, company)
                .ifPresent(favoriteCompanyRepository::delete);
    }

    @Transactional
    public FavoriteJob addJob(String username, Long jobId) {
        Student student = getStudent(username);
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "岗位不存在"));
        favoriteJobRepository.findByStudentAndJob(student, job).ifPresent(fj -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "已收藏该岗位");
        });
        FavoriteJob favorite = new FavoriteJob();
        favorite.setStudent(student);
        favorite.setJob(job);
        favorite.setJobStatusSnapshot(job.getStatus().name());
        return favoriteJobRepository.save(favorite);
    }

    @Transactional
    public void removeJob(String username, Long jobId) {
        Student student = getStudent(username);
        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "岗位不存在"));
        favoriteJobRepository.findByStudentAndJob(student, job)
                .ifPresent(favoriteJobRepository::delete);
    }
}
