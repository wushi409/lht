package com.campus.jobfair.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "file_resources")
public class FileResource extends BaseEntity {

    @Column(nullable = false, length = 50)
    private String ownerType;

    private Long ownerId;

    @Column(nullable = false, length = 200)
    private String fileName;

    @Column(nullable = false)
    private String url;

    @Column(length = 100)
    private String contentType;

    private Long size;
}
