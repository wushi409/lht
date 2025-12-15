package com.campus.jobfair.repository;

import com.campus.jobfair.entity.FavoriteJob;
import com.campus.jobfair.entity.Job;
import com.campus.jobfair.entity.Student;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FavoriteJobRepository extends JpaRepository<FavoriteJob, Long> {
    List<FavoriteJob> findByStudent(Student student);
    Optional<FavoriteJob> findByStudentAndJob(Student student, Job job);
}
