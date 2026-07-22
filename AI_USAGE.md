# AI Usage and Session History

## How AI was used

AI assisted with:

- Exploring architecture alternatives for pending template updates
- Drafting Mermaid diagrams and scaffolding Spring modules
- Proposing test cases for APPLY/DECLINE cursor behavior
- Helping write documentation drafts

## What was corrected or rejected

- An early AI suggestion produced an overbuilt multi-worker event platform. That design was narrowed to one Template Update Service and a small decision vertical slice.
- AI-generated diagrams were revised for clarity, orientation, and production-path simplicity.
- Scope was intentionally limited to completed-decision handling rather than implementing publication, registration, pending query, and JSON diff in code.

## Engineer responsibilities

Engineers own domain assumptions, architecture tradeoffs, acceptance criteria, code review, and final delivery. Generated code and text were treated as drafts requiring verification against the take-home constraints.

## Trust boundaries

AI is not trusted for:

- Accounting conclusions, materiality, or professional judgment
- Ungrounded natural-language summaries of template changes
- Defining security or multi-tenant isolation without review

In the broader design, any human-readable update summary must be grounded in a structured, traceable template diff. Deterministic comparison remains the source of truth; generative narration is optional and secondary.

## Guiding other engineers

Use AI to accelerate exploration and boilerplate, then force a scope check: Does this solve the one-minute load constraint? Does it preserve APPLY/DECLINE semantics? Can a reviewer explain every decision without the model present? If not, simplify before merging.
