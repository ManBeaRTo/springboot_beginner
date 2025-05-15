package com.example.trial_one.model;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Embeddable
public class AuditDetails {
    @Column(updatable = false)
    @CreationTimestamp
    private LocalDateTime creationTime;

    @Column(updatable = false)
    @CreatedBy
    private String createdBy;

    @Column(updatable = true)
    @LastModifiedBy
    private String lastModifiedBy;

    @UpdateTimestamp
    private LocalDateTime lastModifiedTime;
}
