package com.campus.jobfair.service;

import com.campus.jobfair.dto.ExportTaskRequest;
import com.campus.jobfair.entity.ExportTask;
import com.campus.jobfair.entity.enums.ExportStatus;
import com.campus.jobfair.entity.enums.ExportType;
import com.campus.jobfair.entity.enums.UserRole;
import com.campus.jobfair.repository.*;
import java.time.Instant;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExportService {

    private final ExportTaskRepository exportTaskRepository;
    private final ApplicationRecordRepository applicationRepository;
    private final CompanyRepository companyRepository;
    private final StudentRepository studentRepository;
    private final JobRepository jobRepository;

    public ExportService(ExportTaskRepository exportTaskRepository,
                        ApplicationRecordRepository applicationRepository,
                        CompanyRepository companyRepository,
                        StudentRepository studentRepository,
                        JobRepository jobRepository) {
        this.exportTaskRepository = exportTaskRepository;
        this.applicationRepository = applicationRepository;
        this.companyRepository = companyRepository;
        this.studentRepository = studentRepository;
        this.jobRepository = jobRepository;
    }

    public List<ExportTask> listAll() {
        return exportTaskRepository.findAll();
    }

    @Transactional
    public ExportTask createTask(UserRole role, Long requesterId, ExportTaskRequest request) {
        ExportTask task = new ExportTask();
        task.setType(request.getType());
        task.setRequestedByRole(role);
        task.setRequestedById(requesterId);
        if (request.getFromDate() != null) task.setFromDate(Instant.parse(request.getFromDate()));
        if (request.getToDate() != null) task.setToDate(Instant.parse(request.getToDate()));
        task.setStatus(ExportStatus.PROCESSING);
        ExportTask saved = exportTaskRepository.save(task);
        // 简化：立即完成并生成文件链接
        saved.setStatus(ExportStatus.COMPLETED);
        saved.setCompletedAt(Instant.now());
        saved.setFileUrl("/api/admin/exports/" + saved.getId() + "/download");
        return exportTaskRepository.save(saved);
    }

    public ExportTask getById(Long id) {
        return exportTaskRepository.findById(id).orElse(null);
    }

    public String generateExportData(ExportTask task) {
        StringBuilder csv = new StringBuilder();
        
        switch (task.getType()) {
            case STUDENT_APPLICATIONS:
                csv.append("投递ID,学生学号,学生姓名,职位标题,公司名称,投递状态,投递时间\n");
                applicationRepository.findAll().forEach(app -> {
                    csv.append(String.format("%d,%s,%s,%s,%s,%s,%s\n",
                        app.getId(),
                        app.getStudent() != null ? app.getStudent().getStudentNo() : "",
                        app.getStudent() != null ? app.getStudent().getName() : "",
                        app.getJob() != null ? app.getJob().getTitle() : "",
                        app.getJob() != null && app.getJob().getCompany() != null ? app.getJob().getCompany().getName() : "",
                        translateApplicationStatus(app.getStatus()),
                        app.getCreatedAt()));
                });
                break;
                
            case COMPANY_PARTICIPATION:
                csv.append("企业ID,企业名称,统一社会信用代码,行业,规模,审核状态,注册时间\n");
                companyRepository.findAll().forEach(company -> {
                    csv.append(String.format("%d,%s,%s,%s,%s,%s,%s\n",
                        company.getId(),
                        company.getName(),
                        company.getCreditCode(),
                        company.getIndustry() != null ? company.getIndustry() : "",
                        company.getScale() != null ? company.getScale() : "",
                        translateCompanyStatus(company.getStatus()),
                        company.getCreatedAt()));
                });
                break;
                
            case EMPLOYMENT_STATS:
                csv.append("学生ID,学号,姓名,学院,投递数量,面试数量\n");
                studentRepository.findAll().forEach(student -> {
                    long appCount = applicationRepository.countByStudentId(student.getId());
                    csv.append(String.format("%d,%s,%s,%s,%d,0\n",
                        student.getId(),
                        student.getStudentNo(),
                        student.getName(),
                        student.getCollege() != null ? student.getCollege() : "",
                        appCount));
                });
                break;
                
            case LOGS:
                csv.append("日志类型,内容\n");
                csv.append("SYSTEM,导出功能演示日志\n");
                break;
        }
        
        return csv.toString();
    }
    
    private String translateApplicationStatus(Object status) {
        if (status == null) return "";
        String statusStr = status.toString();
        switch (statusStr) {
            case "SUBMITTED": return "已投递";
            case "REVIEWED": return "查看";
            case "VIEWED": return "查看";
            case "INTERVIEW": return "已面试";
            case "OFFERED": return "已录用";
            case "REJECTED": return "已拒绝";
            case "WITHDRAWN": return "已撤回";
            default: return statusStr;
        }
    }
    
    private String translateCompanyStatus(Object status) {
        if (status == null) return "";
        String statusStr = status.toString();
        switch (statusStr) {
            case "PENDING": return "待审核";
            case "APPROVED": return "已通过";
            case "REJECTED": return "已拒绝";
            default: return statusStr;
        }
    }
}
