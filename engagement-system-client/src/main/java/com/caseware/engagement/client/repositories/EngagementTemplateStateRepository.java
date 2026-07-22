package com.caseware.engagement.client.repositories;

import com.caseware.engagement.client.entities.EngagementTemplateState;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EngagementTemplateStateRepository
        extends JpaRepository<EngagementTemplateState, EngagementTemplateState.Id> {
}
