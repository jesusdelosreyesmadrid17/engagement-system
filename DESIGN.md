# Template Update Visibility — Design Document

Caseware take-home architecture exercise. This document describes the proposed system for showing pending Product Template updates across Engagement Files without opening those files, and explains the targeted code slice delivered in this repository.

## 1. High-Level Architecture

### Problem constraints

- Engagement files store the template ID and version used at creation.
- Loading an engagement takes approximately one minute and is a hard constraint.
- Firms have on the order of hundreds of engagements across products.
- Template updates are infrequent (about once per week per product).
- Multiple updates may accumulate before a user decides.
- Applying template content into an engagement is out of scope.

Loading every engagement to discover pending updates is not viable (~100 minutes for 100 files). The system therefore keeps a lightweight metadata projection and never opens engagement content on the dashboard path.

### Core idea

Introduce a **Template Update Service** that owns:

- `template_catalog`: latest published version per shared template
- `engagement_template_state`: per-engagement cursors
  - `appliedVersion`: content currently in the engagement
  - `evaluatedThroughVersion`: last version the user already decided on
- `template_change_summary`: reusable diffs/summaries between template versions
- `update_decision`: audit of APPLY/DECLINE outcomes

Pending means:

```text
latestVersion > evaluatedThroughVersion
```

If an engagement is on v1 and v2/v3 arrive before a decision, the user sees one pending package targeting v3. ACCEPT advances both cursors to v3. DECLINE advances only `evaluatedThroughVersion` to v3. When v4 arrives, the engagement becomes pending again; the impact must be explained from applied v1 to target v4.

### Components

| Component | Responsibility |
|---|---|
| Existing Template DB | Shared product template archives by ID and version |
| Existing Engagement System | Create engagements, load files, process apply content |
| Template Update Service | Metadata, pending detection, summaries, decision recording |
| Metadata PostgreSQL | Fast firm-scoped queries |
| AWS SQS | Reliable delivery of `TemplatePublished` in production |
| Dashboard | List pending updates and submit decisions |

Diagrams:

- [High-level architecture](schemas/high-level-architecture.mmd)
- [Template publication flow](schemas/template-publication-flow.mmd)
- [Update decision flow](schemas/decision-flow.mmd)
- [Metadata data model](schemas/data-model.mmd)

### Assumptions

- Template versions are monotonic per `templateId`.
- Publication is notified after the Template DB write succeeds.
- Engagement registration emits template ID/version once at creation.
- Completed decisions are reported only after the Engagement System has finished processing.
- Summaries are derived from shared templates only; no customer engagement data enters the comparison path.

## 2. Implementation Plan

### Phase 0 — contracts

Define events/APIs:

- `TemplatePublished(templateId, version, previousVersion, publishedAt)`
- `EngagementCreated(firmId, engagementId, templateId, initialVersion)`
- `CompletedDecision(firmId, engagementId, decision, targetVersion, decidedBy)`

### Phase 1 — metadata foundation

1. Create catalog and engagement-state tables.
2. Register metadata on engagement creation.
3. Update catalog on template publication.
4. Backfill existing engagements once in background with controlled concurrency.

### Phase 2 — decision and pending query

1. Expose pending updates by joining firm engagement state to catalog latest version.
2. Record APPLY/DECLINE with explicit `targetVersion`.
3. Persist audit rows transactionally with cursor updates.

### Phase 3 — human-readable change summaries

1. Compare shared template versions with a deterministic JSON differ.
2. Cache results by `(templateId, fromVersion, toVersion)`.
3. Produce structured changes plus grounded readable text.
4. Optionally narrate with an LLM only over the structured diff, with deterministic fallback.

### Phase 4 — harden and roll out

Observability, retries/DLQ for publication events, multi-tenant checks, feature-flagged rollout.

### Targeted code delivered now

This repository implements Phase 2’s decision slice only:

```http
POST /api/firms/{firmId}/engagements/{engagementId}/decisions/completed
```

It validates target versions, updates APPLY/DECLINE cursors, writes audit history, and proves the behavior with tests. Publication, registration, pending query, and JSON diff remain design-only.

## 3. Testing Strategy

### Domain correctness

- APPLY advances both cursors.
- DECLINE advances only `evaluatedThroughVersion`.
- Accumulated releases present one target version.
- After DECLINE of v3, publishing v4 makes the engagement pending again.
- Stale `targetVersion` is rejected.
- Target above catalog latest is rejected.
- Failed apply does not update metadata.

### Integration

- Decision endpoint with H2/PostgreSQL-compatible schema.
- MapStruct conversion of request → audit entity → response.
- Idempotent handling of duplicate publication notifications in the full design.

### Non-goals for automated tests in this slice

- Engagement file load timing.
- Real ZIP merge into engagement content.
- LLM narrative quality.

## 4. Evaluation & Observability

Operational definition of “up to date”: publication or decision reflected in metadata within tens of seconds (target p95 < 30s).

Suggested metrics (CloudWatch / OpenTelemetry):

- Dashboard query latency (p50/p95)
- Publication processing lag
- Pending update count by firm/product
- Decision conflict rate (stale target)
- Diff cache hit rate
- Summary generation failures
- Backfill progress and failures

Logs should include `firmId`, `engagementId`, `templateId`, `targetVersion`, and correlation IDs for publication/decision flows. Alarms on elevated lag, conflict spikes, and summary failure rate.

## 5. Failure Modes & Tradeoffs

| Failure | Handling |
|---|---|
| Lost publication event | SQS retries + periodic catalog reconciliation |
| Template DB unavailable during diff | Retry; do not advance catalog until summary can be produced or deferred safely |
| Concurrent newer publication during decision | Explicit `targetVersion`; newer versions remain pending |
| Apply content fails | No metadata cursor update |
| Metadata divergence after backfill gaps | Background reconciliation against Engagement System inventory |
| Over-broad service design | Prefer one service with clear boundaries over many microservices for this scale |

### Tradeoffs accepted

- **Eventual consistency over synchronous fan-out.** Updates are infrequent; seconds of lag are acceptable.
- **Lazy cumulative diffs over precomputing every version pair.** Popular pairs are cached.
- **Deterministic structured summary as source of truth.** LLM text is optional and must cite structured changes.
- **Small code slice over full application.** The assignment asks for judgment and contracts; the decision cursor model is the highest-risk correctness piece.

### Where AI should not be trusted

- Professional audit judgment, materiality, or conclusions.
- Summaries not grounded in a structured template diff.
- Security/multi-tenant boundaries without human review.
- Expanding scope into a full platform when a narrow correctness slice better demonstrates the design.

## Deliverables mapping

1. **Design document:** this file.
2. **Code:** targeted decision slice in this repository.
3. **AI usage note:** [AI_USAGE.md](AI_USAGE.md).
4. **Diagrams:** [schemas/](schemas/).
