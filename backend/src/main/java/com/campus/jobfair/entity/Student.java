package com.campus.jobfair.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "students")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Student extends BaseEntity {

    @Column(nullable = false, unique = true, length = 50)
    private String studentNo;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 100)
    private String college;

    @Column(length = 50)
    private String phone;

    @Column(length = 100)
    private String email;

    private Long defaultResumeId;

    @JsonIgnore
    @OneToOne(mappedBy = "student")
    private UserAccount account;
}
