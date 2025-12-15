package com.campus.jobfair.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.time.Instant;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "job_fairs")
public class JobFair extends BaseEntity {

    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 300)
    private String location;

    private Instant startTime;

    private Instant endTime;

    @Column(length = 500)
    private String description;

    private Integer capacity;
}
