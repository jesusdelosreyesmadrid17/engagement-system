package com.caseware.engagement.api.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record CompletedDecisionRequest(
        @NotNull Decision decision,
        @Positive long targetVersion,
        @NotBlank String decidedBy) {
}
