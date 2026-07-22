package com.caseware.engagement.app;

import com.caseware.engagement.api.model.CompletedDecisionRequest;
import com.caseware.engagement.api.model.DecisionResponse;
import com.caseware.engagement.client.services.UpdateDecisionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class EngagementController {
    private final UpdateDecisionService service;

    @PostMapping("/firms/{firmId}/engagements/{engagementId}/decisions/completed")
    public DecisionResponse completeDecision(
            @PathVariable String firmId,
            @PathVariable String engagementId,
            @Valid @RequestBody CompletedDecisionRequest request) {
        return service.completeDecision(firmId, engagementId, request);
    }
}
