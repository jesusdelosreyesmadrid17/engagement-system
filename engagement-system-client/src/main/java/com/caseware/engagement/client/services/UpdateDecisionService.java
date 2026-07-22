package com.caseware.engagement.client.services;

import com.caseware.engagement.api.model.CompletedDecisionRequest;
import com.caseware.engagement.api.model.DecisionResponse;

public interface UpdateDecisionService {
    DecisionResponse completeDecision(
            String firmId,
            String engagementId,
            CompletedDecisionRequest request);
}
