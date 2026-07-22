package com.caseware.engagement.client.mapper;

import com.caseware.engagement.api.model.CompletedDecisionRequest;
import com.caseware.engagement.api.model.Decision;
import com.caseware.engagement.client.entities.EngagementTemplateState;
import com.caseware.engagement.client.entities.UpdateDecision;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UpdateDecisionMapperTest {
    private final UpdateDecisionMapper mapper = Mappers.getMapper(UpdateDecisionMapper.class);

    @Test
    void mapsAuditFieldsAndLeavesLifecycleTimestampUnset() {
        UUID decisionId = UUID.fromString("40c5ebda-a28e-4536-bbe5-c2cf0512178f");

        UpdateDecision audit = mapper.toUpdateDecision(
                new CompletedDecisionRequest(Decision.DECLINE, 4, "reviewer@example.com"),
                decisionId,
                "firm-a",
                "engagement-1",
                1,
                1);

        assertThat(audit.getDecisionId()).isEqualTo(decisionId);
        assertThat(audit.getFirmId()).isEqualTo("firm-a");
        assertThat(audit.getEngagementId()).isEqualTo("engagement-1");
        assertThat(audit.getDecision()).isEqualTo(Decision.DECLINE);
        assertThat(audit.getFromAppliedVersion()).isEqualTo(1);
        assertThat(audit.getTargetVersion()).isEqualTo(4);
        assertThat(audit.getResultingAppliedVersion()).isEqualTo(1);
        assertThat(audit.getDecidedBy()).isEqualTo("reviewer@example.com");
        assertThat(audit.getDecidedAt()).isNull();
    }

    @Test
    void mapsDecisionResponseFromStateAndAudit() {
        EngagementTemplateState state = EngagementTemplateState.builder()
                .id(EngagementTemplateState.Id.builder()
                        .firmId("firm-a")
                        .engagementId("engagement-1")
                        .build())
                .templateId("audit")
                .appliedVersion(1)
                .evaluatedThroughVersion(3)
                .build();

        UpdateDecision audit = UpdateDecision.builder()
                .decisionId(UUID.randomUUID())
                .firmId("firm-a")
                .engagementId("engagement-1")
                .decision(Decision.DECLINE)
                .fromAppliedVersion(1)
                .targetVersion(3)
                .resultingAppliedVersion(1)
                .decidedBy("reviewer@example.com")
                .build();

        var response = mapper.toDecisionResponse(audit, state);

        assertThat(response.appliedVersion()).isEqualTo(1);
        assertThat(response.evaluatedThroughVersion()).isEqualTo(3);
        assertThat(response.targetVersion()).isEqualTo(3);
        assertThat(response.decision()).isEqualTo(Decision.DECLINE);
    }
}
