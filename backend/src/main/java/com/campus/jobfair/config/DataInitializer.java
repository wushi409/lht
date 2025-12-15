package com.campus.jobfair.config;

import com.campus.jobfair.entity.Announcement;
import com.campus.jobfair.entity.Company;
import com.campus.jobfair.entity.Job;
import com.campus.jobfair.entity.JobFair;
import com.campus.jobfair.entity.JobFairEvent;
import com.campus.jobfair.entity.Student;
import com.campus.jobfair.entity.UserAccount;
import com.campus.jobfair.entity.enums.AnnouncementTarget;
import com.campus.jobfair.entity.enums.CompanyStatus;
import com.campus.jobfair.entity.enums.EventType;
import com.campus.jobfair.entity.enums.JobStatus;
import com.campus.jobfair.entity.enums.UserRole;
import com.campus.jobfair.repository.AnnouncementRepository;
import com.campus.jobfair.repository.CompanyRepository;
import com.campus.jobfair.repository.JobFairEventRepository;
import com.campus.jobfair.repository.JobFairRepository;
import com.campus.jobfair.repository.JobRepository;
import com.campus.jobfair.repository.StudentRepository;
import com.campus.jobfair.repository.UserAccountRepository;
import java.time.Instant;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DataInitializer implements CommandLineRunner {

    private final UserAccountRepository userAccountRepository;
    private final StudentRepository studentRepository;
    private final CompanyRepository companyRepository;
    private final JobRepository jobRepository;
    private final JobFairRepository jobFairRepository;
    private final JobFairEventRepository jobFairEventRepository;
    private final AnnouncementRepository announcementRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserAccountRepository userAccountRepository,
                           StudentRepository studentRepository,
                           CompanyRepository companyRepository,
                           JobRepository jobRepository,
                           JobFairRepository jobFairRepository,
                           JobFairEventRepository jobFairEventRepository,
                           AnnouncementRepository announcementRepository,
                           PasswordEncoder passwordEncoder) {
        this.userAccountRepository = userAccountRepository;
        this.studentRepository = studentRepository;
        this.companyRepository = companyRepository;
        this.jobRepository = jobRepository;
        this.jobFairRepository = jobFairRepository;
        this.jobFairEventRepository = jobFairEventRepository;
        this.announcementRepository = announcementRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (userAccountRepository.count() > 0) {
            return;
        }

        // admin
        UserAccount admin = new UserAccount();
        admin.setUsername("admin");
        admin.setPasswordHash(passwordEncoder.encode("admin123"));
        admin.setRole(UserRole.ADMIN);
        admin.setActive(true);
        userAccountRepository.save(admin);

        // student
        Student student = new Student();
        student.setStudentNo("20250001");
        student.setName("示例同学");
        student.setCollege("计算机学院");
        student.setEmail("student@example.com");
        studentRepository.save(student);

        UserAccount studentAccount = new UserAccount();
        studentAccount.setUsername(student.getStudentNo());
        studentAccount.setPasswordHash(passwordEncoder.encode("student123"));
        studentAccount.setRole(UserRole.STUDENT);
        studentAccount.setActive(true);
        studentAccount.setStudent(student);
        userAccountRepository.save(studentAccount);

        // company
        Company company = new Company();
        company.setName("示例科技有限公司");
        company.setCreditCode("CRED-001");
        company.setIndustry("互联网");
        company.setScale("200-500人");
        company.setDescription("校园招聘示例企业");
        company.setContactName("HR");
        company.setContactPhone("13800000000");
        company.setContactEmail("hr@example.com");
        company.setStatus(CompanyStatus.APPROVED);
        companyRepository.save(company);

        UserAccount companyAccount = new UserAccount();
        companyAccount.setUsername(company.getCreditCode());
        companyAccount.setPasswordHash(passwordEncoder.encode("company123"));
        companyAccount.setRole(UserRole.COMPANY);
        companyAccount.setActive(true);
        companyAccount.setCompany(company);
        userAccountRepository.save(companyAccount);

        // job
        Job job = new Job();
        job.setCompany(company);
        job.setTitle("Java后端实习生");
        job.setDescription("参与后台开发与接口联调，熟悉Spring Boot");
        job.setJobType("实习");
        job.setLocation("上海");
        job.setSkills("Java,Spring,MySQL");
        job.setStatus(JobStatus.PUBLISHED);
        job.setPublishAt(Instant.now());
        job.setDeadline(Instant.now().plusSeconds(60L * 60 * 24 * 30));
        jobRepository.save(job);

        // job fair & event
        JobFair fair = new JobFair();
        fair.setName("2025春季校园双选会");
        fair.setLocation("大学体育馆");
        fair.setDescription("示例数据：校园招聘双选会");
        fair.setStartTime(Instant.now().plusSeconds(60L * 60 * 24 * 7));
        fair.setEndTime(Instant.now().plusSeconds(60L * 60 * 24 * 8));
        fair.setCapacity(500);
        jobFairRepository.save(fair);

        JobFairEvent event = new JobFairEvent();
        event.setJobFair(fair);
        event.setName("企业宣讲会");
        event.setType(EventType.PRESENTATION);
        event.setLocation("教学楼A101");
        event.setStartTime(fair.getStartTime());
        event.setEndTime(fair.getStartTime().plusSeconds(3600));
        event.setCapacity(200);
        event.setDescription("示例宣讲活动");
        jobFairEventRepository.save(event);

        // announcement
        Announcement ann = new Announcement();
        ann.setTitle("欢迎使用校园招聘平台");
        ann.setContent("这是示例公告，您可以在此发布重要通知。");
        ann.setTarget(AnnouncementTarget.ALL);
        ann.setPublishAt(Instant.now());
        announcementRepository.save(ann);
    }
}
