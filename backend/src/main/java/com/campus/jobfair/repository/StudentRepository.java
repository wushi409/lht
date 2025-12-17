package com.campus.jobfair.repository;

import com.campus.jobfair.entity.Student;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByStudentNo(String studentNo);
    boolean existsByStudentNo(String studentNo);

    Optional<Student> findByPhone(String phone);
}
