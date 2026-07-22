package com.caseware.engagement.client.mapper;

import com.caseware.engagement.api.model.CompletedDecisionRequest;
import com.caseware.engagement.api.model.DecisionResponse;
import com.caseware.engagement.client.entities.EngagementTemplateState;
import com.caseware.engagement.client.entities.UpdateDecision;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface UpdateDecisionMapper {
    @Mapping(target = "decisionId", source = "decisionId")
    @Mapping(target = "firmId", source = "firmId")
    @Mapping(target = "engagementId", source = "engagementId")
    @Mapping(target = "decision", source = "request.decision")
    @Mapping(target = "fromAppliedVersion", source = "fromAppliedVersion")
    @Mapping(target = "targetVersion", source = "request.targetVersion")
    @Mapping(target = "resultingAppliedVersion", source = "resultingAppliedVersion")
    @Mapping(target = "decidedBy", source = "request.decidedBy")
    @Mapping(target = "decidedAt", ignore = true)
    UpdateDecision toUpdateDecision(
            CompletedDecisionRequest request,
            UUID decisionId,
            String firmId,
            String engagementId,
            long fromAppliedVersion,
            long resultingAppliedVersion);

    @Mapping(target = "decisionId", source = "audit.decisionId")
    @Mapping(target = "firmId", source = "audit.firmId")
    @Mapping(target = "engagementId", source = "audit.engagementId")
    @Mapping(target = "decision", source = "audit.decision")
    @Mapping(target = "appliedVersion", source = "state.appliedVersion")
    @Mapping(target = "evaluatedThroughVersion", source = "state.evaluatedThroughVersion")
    @Mapping(target = "targetVersion", source = "audit.targetVersion")
    @Mapping(target = "decidedAt", source = "audit.decidedAt")
    DecisionResponse toDecisionResponse(UpdateDecision audit, EngagementTemplateState state);

    default EngagementTemplateState.Id toEngagementId(String firmId, String engagementId) {
        return EngagementTemplateState.Id.builder()
                .firmId(firmId)
                .engagementId(engagementId)
                .build();
    }
}
