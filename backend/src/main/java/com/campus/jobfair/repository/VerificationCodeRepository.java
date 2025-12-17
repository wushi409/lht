package com.campus.jobfair.repository;

import com.campus.jobfair.entity.VerificationCode;
import com.campus.jobfair.entity.enums.VerificationScene;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {

    Optional<VerificationCode> findTopByUsernameAndSceneOrderByCreatedAtDesc(String username, VerificationScene scene);
}
