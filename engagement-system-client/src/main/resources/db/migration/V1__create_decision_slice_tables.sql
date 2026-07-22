CREATE TABLE template_catalog (
    template_id VARCHAR(255) PRIMARY KEY,
    latest_version BIGINT NOT NULL,
    published_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    lock_version BIGINT NOT NULL DEFAULT 0
);

CREATE TABLE engagement_template_state (
    firm_id VARCHAR(255) NOT NULL,
    engagement_id VARCHAR(255) NOT NULL,
    template_id VARCHAR(255) NOT NULL,
    applied_version BIGINT NOT NULL,
    evaluated_through_version BIGINT NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL,
    lock_version BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (firm_id, engagement_id),
    CONSTRAINT fk_state_template FOREIGN KEY (template_id)
        REFERENCES template_catalog(template_id)
);

CREATE TABLE update_decision (
    decision_id UUID PRIMARY KEY,
    firm_id VARCHAR(255) NOT NULL,
    engagement_id VARCHAR(255) NOT NULL,
    decision VARCHAR(16) NOT NULL CHECK (decision IN ('APPLY', 'DECLINE')),
    from_applied_version BIGINT NOT NULL,
    target_version BIGINT NOT NULL,
    resulting_applied_version BIGINT NOT NULL,
    decided_by VARCHAR(255) NOT NULL,
    decided_at TIMESTAMP WITH TIME ZONE NOT NULL,
    CONSTRAINT fk_decision_engagement FOREIGN KEY (firm_id, engagement_id)
        REFERENCES engagement_template_state(firm_id, engagement_id)
);

CREATE INDEX idx_engagement_state_firm_template
    ON engagement_template_state(firm_id, template_id);
CREATE INDEX idx_engagement_state_pending
    ON engagement_template_state(template_id, evaluated_through_version);
CREATE INDEX idx_update_decision_engagement_time
    ON update_decision(firm_id, engagement_id, decided_at);
