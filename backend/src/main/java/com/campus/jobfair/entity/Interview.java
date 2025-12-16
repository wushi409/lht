package com.campus.jobfair.entity;

import com.campus.jobfair.entity.enums.InterviewStatus;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "interviews")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Interview extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id")
    private Job job;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    private Instant scheduledAt;

    @Column(length = 200)
    private String location;

    @Column(length = 100)
    private String interviewer;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private InterviewStatus status = InterviewStatus.PENDING;

    @Column(length = 500)
    private String feedback;
}
