package com.campus.jobfair.repository;

import com.campus.jobfair.entity.Resume;
import com.campus.jobfair.entity.ResumeFile;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeFileRepository extends JpaRepository<ResumeFile, Long> {
    List<ResumeFile> findByResume(Resume resume);
}
