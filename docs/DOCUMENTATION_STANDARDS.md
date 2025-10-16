# Documentation Standards

Version: 1.0
Last Updated: 2025-01-15
Audience: All contributors

---

## Principles
- Write for the intended audience (new devs, contributors, or R&D).
- Be concise, scannable, and actionable.
- Prefer links to avoid duplication (DRY).
- Keep examples real and runnable.
- Keep docs current with code changes.

## Documentation Hierarchy
The ROSTRY documentation follows a structured hierarchy to ensure easy navigation and maintenance:

- **Root-level docs**: Quick access files in the project root (e.g., `README.md`, `QUICK_START.md`, `CONTRIBUTING.md`) for immediate setup and contribution guidance.
- **docs/ directory organization**: Deep-dive guides organized by category (architecture, features, operations) under `docs/`, with subdirectories for specialized content like `adrs/`, `api/`, `archive/`, and `images/`.
- **Role of README-docs.md**: Serves as the primary documentation index, providing audience-based navigation, status indicators, and cross-references to all docs.
- **Role of blueprint.md**: Provides a high-level system overview and architecture summary for stakeholders, product managers, and architects, differentiating from detailed navigation by focusing on big-picture understanding.

This hierarchy ensures that users can quickly find what they need while maintaining a clear separation between overview and detailed guides.

## File Organization
- Place feature docs under `docs/` next to related topics.
- Images in `docs/images/` (architecture, screenshots, workflows, diagrams).
- Generated API docs in `docs/api/`.
- ADRs in `docs/adrs/`.
- Feature docs organization: Group related features under logical categories (e.g., Social & Community, Marketplace & Transactions, Farm Management, Infrastructure) as seen in `README-docs.md`.
- When to create new docs vs update existing: Create new docs for major features or architectural changes; update existing docs for enhancements or fixes to avoid fragmentation.
- Naming conventions for feature docs: Use descriptive, lower-kebab-case names that clearly indicate the feature (e.g., `ai-personalization.md`, `gamification.md`).

## Naming
- Use lower-kebab-case for filenames, e.g. `state-management.md`.
- Prefer clear, topic-centric names.

## Required Metadata
Every doc must include at the top:
- Title
- Version
- Last Updated
- Audience

For docs > 200 lines, include a Table of Contents.

## Writing Style
- Use active voice and short sentences.
- Prefer tables and lists over long paragraphs.
- Define acronyms on first use.
- Provide minimal runnable examples; link to source files.

## Markdown Formatting
- Use fenced code blocks with language.
- Keep line length reasonable (<120 chars where practical).
- Use relative links to keep docs portable.
- Embed images with descriptive alt text.

## Content Requirements
- Clear purpose and scope.
- Prerequisites section where applicable.
- Steps or patterns with examples.
- Links to related docs.
- Troubleshooting or pitfalls where applicable.

## Consolidation Guidelines
To avoid duplication and maintain consistency across documentation:

- **When to merge vs separate docs**: Merge content when topics overlap significantly (e.g., consolidate migration guides into a single canonical source); separate when topics are distinct but related (e.g., keep testing strategy separate from migration docs but cross-reference).
- **How to handle overlapping content**: Identify overlaps during reviews, move unique content to the most appropriate doc, and replace duplicates with cross-references.
- **Cross-referencing best practices**: Use clear, descriptive links (e.g., "For migration testing, see `database-migrations.md`"); include a brief context sentence before the link; ensure links are relative and validated regularly.

## Review & Maintenance
- Run the Documentation Quality Checklist (see `docs/README-docs.md`).
- Assign ownership for living docs.
- Schedule reviews (quarterly for core docs).
- Deprecate and archive outdated docs under `docs/archive/`.
- Quarterly review process: Conduct full reviews of core docs (e.g., architecture, feature guides) every quarter; spot-check others for currency.
- How to identify outdated docs: Check for broken links, outdated code examples, or references to removed features; use automated link checkers and manual reviews.
- Deprecation and archiving process: Mark outdated docs with deprecation notices, move to `docs/archive/` with clear supersession notes, and update all references.
- Link validation procedures: Run link checks quarterly using tools like markdown-link-check; fix or remove broken links immediately.

## Ownership Section
Documentation ownership ensures accountability and currency:

- **Assigning maintainers to doc areas**: Assign primary maintainers for each documentation category (e.g., architecture docs to lead architects, feature docs to feature owners).
- **Responsibility for keeping docs current**: Maintainers must update docs within one week of related code changes; review pull requests for documentation impacts.
- **Review cadence per doc type**: Core docs (e.g., architecture, standards) quarterly; feature docs with each major release; operational docs (e.g., deployment) before each release.

## Templates
- Feature guide
- API documentation
- Tutorial/how-to
- ADR (use existing `docs/adr-template.md`)
- Feature documentation (like ai-personalization.md): Include overview, components, data flow, implementation details, usage examples, testing, and future enhancements.
- Worker catalog entries: Detail worker purpose, scheduling, constraints, related features, and monitoring.
- Export utility docs: Cover supported formats, workflows, data preparation, performance considerations, and integration examples.

## Cross-References
- QUICK_START: `../QUICK_START.md`
- Cheat Sheet: `../CHEAT_SHEET.md`
- Architecture: `architecture.md`
- Testing: `testing-strategy.md`
- Contribution: `../CONTRIBUTING.md`