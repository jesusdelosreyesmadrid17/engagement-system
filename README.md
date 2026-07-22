# Engagement Template Update Decision Slice

This repository implements one targeted vertical slice of the proposed Template Update Service:

```http
POST /api/firms/{firmId}/engagements/{engagementId}/decisions/completed
```

The slice records a completed APPLY or DECLINE decision against lightweight engagement template metadata. It does not open engagement files and does not apply template content.

## Why this slice

Template updates are evaluated through two independent cursors:

- `appliedVersion`: the template version whose content is currently in the engagement
- `evaluatedThroughVersion`: the latest published version the user has already decided on

That distinction is the correctness-sensitive part of the design. APPLY advances both cursors. DECLINE advances only the evaluated cursor. Explicit `targetVersion` prevents accidental acceptance or rejection of a newer concurrent publication.

## Scope

Included:

- API contracts for completed decisions
- `template_catalog`, `engagement_template_state`, and `update_decision` persistence
- MapStruct conversions
- Explicit Spring `@Bean` service wiring
- One REST endpoint and domain validation
- Tests for APPLY, DECLINE, stale targets, and out-of-range targets

Assumed upstream and intentionally not implemented here:

- Template publication workflow
- Engagement registration workflow
- Pending-update query and human-readable JSON diff generation
- Actual application of template content into an engagement file

Tests seed catalog and engagement state directly to keep the slice focused.

## Modules

- `engagement-system-api`: request/response records and enums
- `engagement-system-client`: entities, repositories, MapStruct mapper, service contract/implementation, configuration
- `engagement-system-app`: Spring Boot composition root, controller, and exception handling

Service implementations are plain classes created through `@Configuration` `@Bean` methods. Lombok owns constructors and entity builders. MapStruct owns request/entity/response conversions. JPA lifecycle callbacks manage technical timestamps with UTC `Instant.now()`.

## Architecture diagrams

The diagrams describe the complete proposed system. This repository implements only the completed-decision step.

- [High-level architecture](schemas/high-level-architecture.mmd)
- [Template publication flow](schemas/template-publication-flow.mmd)
- [Update decision flow](schemas/decision-flow.mmd)
- [Metadata data model](schemas/data-model.mmd)

## Build and run

```shell
./gradlew clean test
./gradlew :engagement-system-app:bootRun
```

Local runtime uses in-memory H2 in PostgreSQL compatibility mode.

## Example

```bash
curl -X POST \
  localhost:8080/api/firms/firm-a/engagements/engagement-1/decisions/completed \
  -H 'Content-Type: application/json' \
  -d '{"decision":"DECLINE","targetVersion":3,"decidedBy":"reviewer@example.com"}'
```
