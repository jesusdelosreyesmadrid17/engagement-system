package com.caseware.engagement.client.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Entity
@Table(name = "template_catalog")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TemplateCatalog {
    @Id
    @Column(name = "template_id", nullable = false)
    private String templateId;
    @Column(name = "latest_version", nullable = false)
    private long latestVersion;
    @Column(name = "published_at", nullable = false)
    private Instant publishedAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    @Version
    @Column(name = "lock_version", nullable = false)
    private long lockVersion;

    @PrePersist
    @PreUpdate
    void updateTechnicalTimestamp() {
        updatedAt = Instant.now();
    }
}
