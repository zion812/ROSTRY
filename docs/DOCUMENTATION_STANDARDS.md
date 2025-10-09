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

## File Organization
- Place feature docs under `docs/` next to related topics.
- Images in `docs/images/` (architecture, screenshots, workflows, diagrams).
- Generated API docs in `docs/api/`.
- ADRs in `docs/adrs/`.

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

## Review & Maintenance
- Run the Documentation Quality Checklist (see `docs/README-docs.md`).
- Assign ownership for living docs.
- Schedule reviews (quarterly for core docs).
- Deprecate and archive outdated docs under `docs/archive/`.

## Templates
- Feature guide
- API documentation
- Tutorial/how-to
- ADR (use existing `docs/adr-template.md`)

## Cross-References
- QUICK_START: `../QUICK_START.md`
- Cheat Sheet: `../CHEAT_SHEET.md`
- Architecture: `architecture.md`
- Testing: `testing-strategy.md`
- Contribution: `../CONTRIBUTING.md`
