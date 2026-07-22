package com.caseware.engagement.client.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Entity
@Table(name = "engagement_template_state")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class EngagementTemplateState {
    @EmbeddedId
    private Id id;
    @Column(name = "template_id", nullable = false)
    private String templateId;
    @Column(name = "applied_version", nullable = false)
    private long appliedVersion;
    @Column(name = "evaluated_through_version", nullable = false)
    private long evaluatedThroughVersion;
    @Column(name = "created_at", nullable = false)
    private Instant createdAt;
    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;
    @Version
    @Column(name = "lock_version", nullable = false)
    private long lockVersion;

    public void apply(long targetVersion) {
        appliedVersion = targetVersion;
        evaluatedThroughVersion = targetVersion;
    }

    public void decline(long targetVersion) {
        evaluatedThroughVersion = targetVersion;
    }

    @PrePersist
    void initializeTechnicalTimestamps() {
        Instant now = Instant.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    void updateTechnicalTimestamp() {
        updatedAt = Instant.now();
    }

    @Embeddable
    @Getter
    @Builder
    @EqualsAndHashCode
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor(access = AccessLevel.PRIVATE)
    public static class Id implements Serializable {
        @Column(name = "firm_id", nullable = false)
        private String firmId;
        @Column(name = "engagement_id", nullable = false)
        private String engagementId;
    }
}
