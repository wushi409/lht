package com.campus.jobfair.entity;

import com.campus.jobfair.entity.enums.VerificationScene;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "verification_codes")
public class VerificationCode extends BaseEntity {

    @Column(nullable = false, length = 100)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private VerificationScene scene;

    @Column(nullable = false, length = 10)
    private String code;

    @Column(nullable = false)
    private Instant expireAt;

    @Column(nullable = false)
    private boolean used = false;
}
