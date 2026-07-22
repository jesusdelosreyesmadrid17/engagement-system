package com.caseware.engagement.client.entities;

import com.caseware.engagement.api.model.Decision;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "update_decision")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateDecision {
    @Id
    @Column(name = "decision_id", nullable = false)
    private UUID decisionId;
    @Column(name = "firm_id", nullable = false)
    private String firmId;
    @Column(name = "engagement_id", nullable = false)
    private String engagementId;
    @Enumerated(EnumType.STRING)
    @Column(name = "decision", nullable = false)
    private Decision decision;
    @Column(name = "from_applied_version", nullable = false)
    private long fromAppliedVersion;
    @Column(name = "target_version", nullable = false)
    private long targetVersion;
    @Column(name = "resulting_applied_version", nullable = false)
    private long resultingAppliedVersion;
    @Column(name = "decided_by", nullable = false)
    private String decidedBy;
    @Column(name = "decided_at", nullable = false)
    private Instant decidedAt;

    @PrePersist
    void initializeTechnicalTimestamp() {
        decidedAt = Instant.now();
    }
}
