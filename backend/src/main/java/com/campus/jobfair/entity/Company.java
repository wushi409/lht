package com.campus.jobfair.entity;

import com.campus.jobfair.entity.enums.CompanyStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "companies")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Company extends BaseEntity {

    @Column(nullable = false, unique = true, length = 200)
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    private String creditCode;

    @Column(length = 50)
    private String scale;

    @Column(length = 50)
    private String industry;

    @Column(length = 500)
    private String description;

    private String logoUrl;

    @Column(length = 100)
    private String contactName;

    @Column(length = 50)
    private String contactPhone;

    @Column(length = 100)
    private String contactEmail;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CompanyStatus status = CompanyStatus.PENDING;

    @Column(length = 200)
    private String rejectionReason;

    @JsonIgnore
    @OneToOne(mappedBy = "company")
    private UserAccount account;
}
