package com.campus.jobfair.repository;

import com.campus.jobfair.entity.Resume;
import com.campus.jobfair.entity.Student;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResumeRepository extends JpaRepository<Resume, Long> {
    List<Resume> findByStudent(Student student);
    Optional<Resume> findByIdAndStudent(Long id, Student student);
}
