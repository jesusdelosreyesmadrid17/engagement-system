package com.caseware.engagement.client.services.impl;

import com.caseware.engagement.api.model.CompletedDecisionRequest;
import com.caseware.engagement.api.model.Decision;
import com.caseware.engagement.api.model.DecisionResponse;
import com.caseware.engagement.client.entities.EngagementTemplateState;
import com.caseware.engagement.client.entities.TemplateCatalog;
import com.caseware.engagement.client.entities.UpdateDecision;
import com.caseware.engagement.client.exception.DomainException;
import com.caseware.engagement.client.mapper.UpdateDecisionMapper;
import com.caseware.engagement.client.repositories.EngagementTemplateStateRepository;
import com.caseware.engagement.client.repositories.TemplateCatalogRepository;
import com.caseware.engagement.client.repositories.UpdateDecisionRepository;
import com.caseware.engagement.client.services.UpdateDecisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@RequiredArgsConstructor
public class DefaultUpdateDecisionService implements UpdateDecisionService {
    private final EngagementTemplateStateRepository stateRepository;
    private final TemplateCatalogRepository catalogRepository;
    private final UpdateDecisionRepository decisionRepository;
    private final UpdateDecisionMapper mapper;

    @Override
    @Transactional
    public DecisionResponse completeDecision(
            String firmId, String engagementId, CompletedDecisionRequest request) {
        EngagementTemplateState state = stateRepository.findById(
                        mapper.toEngagementId(firmId, engagementId))
                .orElseThrow(() -> DomainException.notFound("Engagement is not registered"));

        TemplateCatalog catalog = catalogRepository.findById(state.getTemplateId())
                .orElseThrow(() -> DomainException.notFound("Template catalog not found"));

        if (request.targetVersion() > catalog.getLatestVersion()) {
            throw DomainException.badRequest("Target version exceeds the catalog latest version");
        }
        if (request.targetVersion() <= state.getEvaluatedThroughVersion()) {
            throw DomainException.conflict("Decision target is stale or already evaluated");
        }

        long fromApplied = state.getAppliedVersion();
        if (request.decision() == Decision.APPLY) {
            state.apply(request.targetVersion());
        } else {
            state.decline(request.targetVersion());
        }

        UpdateDecision audit = decisionRepository.saveAndFlush(mapper.toUpdateDecision(
                request,
                UUID.randomUUID(),
                firmId,
                engagementId,
                fromApplied,
                state.getAppliedVersion()));

        return mapper.toDecisionResponse(audit, state);
    }
}
