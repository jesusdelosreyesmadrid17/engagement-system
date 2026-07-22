package com.caseware.engagement.app;

import com.caseware.engagement.api.model.CompletedDecisionRequest;
import com.caseware.engagement.api.model.Decision;
import com.caseware.engagement.api.model.DecisionResponse;
import com.caseware.engagement.client.entities.EngagementTemplateState;
import com.caseware.engagement.client.entities.TemplateCatalog;
import com.caseware.engagement.client.exception.DomainException;
import com.caseware.engagement.client.repositories.EngagementTemplateStateRepository;
import com.caseware.engagement.client.repositories.TemplateCatalogRepository;
import com.caseware.engagement.client.repositories.UpdateDecisionRepository;
import com.caseware.engagement.client.services.UpdateDecisionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@Transactional
class CompleteDecisionWorkflowTest {
    @Autowired
    private UpdateDecisionService service;
    @Autowired
    private TemplateCatalogRepository catalogRepository;
    @Autowired
    private EngagementTemplateStateRepository stateRepository;
    @Autowired
    private UpdateDecisionRepository decisionRepository;

    @BeforeEach
    void seedUpstreamState() {
        catalogRepository.save(TemplateCatalog.builder()
                .templateId("audit")
                .latestVersion(4)
                .publishedAt(Instant.parse("2026-01-01T00:00:00Z"))
                .build());

        stateRepository.save(EngagementTemplateState.builder()
                .id(EngagementTemplateState.Id.builder()
                        .firmId("firm-a")
                        .engagementId("engagement-1")
                        .build())
                .templateId("audit")
                .appliedVersion(1)
                .evaluatedThroughVersion(1)
                .build());
    }

    @Test
    void applyAdvancesBothVersionCursorsAndWritesAudit() {
        DecisionResponse response = service.completeDecision(
                "firm-a",
                "engagement-1",
                new CompletedDecisionRequest(Decision.APPLY, 3, "reviewer@example.com"));

        assertThat(response.decision()).isEqualTo(Decision.APPLY);
        assertThat(response.appliedVersion()).isEqualTo(3);
        assertThat(response.evaluatedThroughVersion()).isEqualTo(3);
        assertThat(response.targetVersion()).isEqualTo(3);
        assertThat(response.decidedAt()).isNotNull();
        assertThat(decisionRepository.count()).isEqualTo(1);

        EngagementTemplateState state = stateRepository.findById(
                EngagementTemplateState.Id.builder()
                        .firmId("firm-a")
                        .engagementId("engagement-1")
                        .build()).orElseThrow();
        assertThat(state.getAppliedVersion()).isEqualTo(3);
        assertThat(state.getEvaluatedThroughVersion()).isEqualTo(3);
    }

    @Test
    void declineAdvancesOnlyEvaluatedCursor() {
        DecisionResponse response = service.completeDecision(
                "firm-a",
                "engagement-1",
                new CompletedDecisionRequest(Decision.DECLINE, 3, "reviewer@example.com"));

        assertThat(response.decision()).isEqualTo(Decision.DECLINE);
        assertThat(response.appliedVersion()).isEqualTo(1);
        assertThat(response.evaluatedThroughVersion()).isEqualTo(3);

        EngagementTemplateState state = stateRepository.findById(
                EngagementTemplateState.Id.builder()
                        .firmId("firm-a")
                        .engagementId("engagement-1")
                        .build()).orElseThrow();
        assertThat(state.getAppliedVersion()).isEqualTo(1);
        assertThat(state.getEvaluatedThroughVersion()).isEqualTo(3);
        assertThat(catalogRepository.findById("audit").orElseThrow().getLatestVersion())
                .isGreaterThan(state.getEvaluatedThroughVersion());
    }

    @Test
    void rejectsStaleAndOutOfRangeTargets() {
        service.completeDecision(
                "firm-a",
                "engagement-1",
                new CompletedDecisionRequest(Decision.DECLINE, 3, "reviewer@example.com"));

        assertThatThrownBy(() -> service.completeDecision(
                "firm-a",
                "engagement-1",
                new CompletedDecisionRequest(Decision.APPLY, 3, "reviewer@example.com")))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("stale");

        assertThatThrownBy(() -> service.completeDecision(
                "firm-a",
                "engagement-1",
                new CompletedDecisionRequest(Decision.APPLY, 5, "reviewer@example.com")))
                .isInstanceOf(DomainException.class)
                .hasMessageContaining("exceeds");
    }
}
