package com.caseware.engagement.api.model;

import java.time.Instant;
import java.util.UUID;

public record DecisionResponse(
        UUID decisionId,
        String firmId,
        String engagementId,
        Decision decision,
        long appliedVersion,
        long evaluatedThroughVersion,
        long targetVersion,
        Instant decidedAt) {
}
