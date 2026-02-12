package com.capestone.hrms_backend.entity.shared;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;


@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public class Document {
    @Column(name = "file_name",nullable = false)
    private String fileName;

    @Column(name = "file_size",nullable = false)
    private Long fileSize;

    private String description;

    @Column(name = "file_type")
    private String fileType;

    @Column(name = "file_path")
    private String filePath;
}
