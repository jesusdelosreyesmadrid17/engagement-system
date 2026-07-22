package com.caseware.engagement.client.repositories;

import com.caseware.engagement.client.entities.UpdateDecision;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface UpdateDecisionRepository extends JpaRepository<UpdateDecision, UUID> {
}
