package com.campus.jobfair.service;

import com.campus.jobfair.entity.ApplicationRecord;
import com.campus.jobfair.entity.Company;
import com.campus.jobfair.entity.Job;
import com.campus.jobfair.entity.Student;
import com.campus.jobfair.entity.enums.ApplicationStatus;
import com.campus.jobfair.entity.enums.CompanyStatus;
import com.campus.jobfair.entity.enums.JobStatus;
import com.campus.jobfair.repository.ApplicationRecordRepository;
import com.campus.jobfair.repository.CompanyRepository;
import com.campus.jobfair.repository.JobRepository;
import com.campus.jobfair.repository.StudentRepository;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StatsService {

    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;
    private final ApplicationRecordRepository applicationRecordRepository;
    private final StudentRepository studentRepository;

    public StatsService(CompanyRepository companyRepository,
                        JobRepository jobRepository,
                        ApplicationRecordRepository applicationRecordRepository,
                        StudentRepository studentRepository) {
        this.companyRepository = companyRepository;
        this.jobRepository = jobRepository;
        this.applicationRecordRepository = applicationRecordRepository;
        this.studentRepository = studentRepository;
    }

    public Map<String, Object> summary() {
        Map<String, Object> result = new HashMap<>();
        result.put("approvedCompanies", companyRepository.findByStatus(CompanyStatus.APPROVED).size());
        result.put("pendingCompanies", companyRepository.findByStatus(CompanyStatus.PENDING).size());
        result.put("publishedJobs", jobRepository.findByStatus(JobStatus.PUBLISHED).size());
        result.put("applications", applicationRecordRepository.count());
        result.put("hired", applicationRecordRepository.countByStatus(ApplicationStatus.HIRED));
        result.put("interviews", applicationRecordRepository.countByStatus(ApplicationStatus.INTERVIEW));
        return result;
    }

    public Map<String, Object> overview() {
        Map<String, Object> result = new HashMap<>();
        result.put("studentCount", studentRepository.count());
        result.put("companyCount", companyRepository.findByStatus(CompanyStatus.APPROVED).size());
        result.put("jobCount", jobRepository.findByStatus(JobStatus.PUBLISHED).size());
        result.put("applicationCount", applicationRecordRepository.count());
        return result;
    }

    public List<Map<String, Object>> industryStats() {
        List<Company> companies = companyRepository.findByStatus(CompanyStatus.APPROVED);
        Map<String, Long> industryCount = companies.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getIndustry() != null ? c.getIndustry() : "未分类",
                        Collectors.counting()));
        
        return industryCount.entrySet().stream()
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("industry", e.getKey());
                    map.put("count", e.getValue());
                    return map;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> applicationStats() {
        List<Map<String, Object>> result = new ArrayList<>();
        for (ApplicationStatus status : ApplicationStatus.values()) {
            Map<String, Object> map = new HashMap<>();
            map.put("status", status.name());
            map.put("count", applicationRecordRepository.countByStatus(status));
            result.add(map);
        }
        return result;
    }

    public List<Map<String, Object>> jobTypeStats() {
        List<Job> jobs = jobRepository.findByStatus(JobStatus.PUBLISHED);
        Map<String, Long> typeCount = jobs.stream()
                .collect(Collectors.groupingBy(
                        j -> j.getJobType() != null ? j.getJobType() : "未分类",
                        Collectors.counting()));
        
        return typeCount.entrySet().stream()
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("jobType", e.getKey());
                    map.put("count", e.getValue());
                    return map;
                })
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> trendStats() {
        List<Map<String, Object>> result = new ArrayList<>();
        LocalDate now = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        // 构建最近6个月的时间窗口和初始计数
        Map<String, Long> monthCount = new LinkedHashMap<>();
        LocalDate firstMonth = now.minusMonths(5).withDayOfMonth(1);
        for (int i = 5; i >= 0; i--) {
            LocalDate month = now.minusMonths(i);
            monthCount.put(month.format(formatter), 0L);
        }

        Instant start = firstMonth.atStartOfDay(ZoneId.systemDefault()).toInstant();
        Instant end = now.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();
        List<ApplicationRecord> records = applicationRecordRepository.findByCreatedAtBetween(start, end);

        for (ApplicationRecord record : records) {
            if (record.getCreatedAt() == null) {
                continue;
            }
            LocalDateTime created = LocalDateTime.ofInstant(record.getCreatedAt(), ZoneId.systemDefault());
            String key = created.format(formatter);
            if (monthCount.containsKey(key)) {
                monthCount.put(key, monthCount.get(key) + 1);
            }
        }

        monthCount.forEach((month, count) -> {
            Map<String, Object> map = new HashMap<>();
            map.put("month", month);
            map.put("count", count);
            result.add(map);
        });
        return result;
    }

    public List<Map<String, Object>> topJobs() {
        List<Job> jobs = jobRepository.findByStatus(JobStatus.PUBLISHED);
        
        return jobs.stream()
                .map(job -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("title", job.getTitle());
                    map.put("companyName", job.getCompany() != null ? job.getCompany().getName() : "-");
                    map.put("applicationCount", applicationRecordRepository.findByJob(job).size());
                    map.put("salaryRange", job.getSalaryRange() != null ? job.getSalaryRange() : "-");
                    map.put("location", job.getLocation() != null ? job.getLocation() : "-");
                    return map;
                })
                .sorted((a, b) -> ((Integer) b.get("applicationCount")).compareTo((Integer) a.get("applicationCount")))
                .limit(10)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> jobIntentStats() {
        List<Student> students = studentRepository.findAll();
        Map<String, Long> intentCount = new HashMap<>();

        for (Student student : students) {
            String intent = student.getJobIntent();
            if (intent == null || intent.isBlank()) {
                continue;
            }
            String[] parts = intent.split("[,，/、;；\\s]+");
            for (String raw : parts) {
                String key = raw.trim();
                if (key.isEmpty()) {
                    continue;
                }
                intentCount.merge(key, 1L, Long::sum);
            }
        }

        return intentCount.entrySet().stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .map(e -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("intent", e.getKey());
                    map.put("count", e.getValue());
                    return map;
                })
                .collect(Collectors.toList());
    }
}
