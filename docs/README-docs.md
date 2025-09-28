# ROSTRY Documentation Index

The documentation folder consolidates deep-dive guides to help new and existing contributors understand the system architecture, feature domains, and operational requirements.

## Contents
- `architecture.md` — High-level overview of layers, navigation, background jobs, and integrations.
- `transfer-workflow.md` — Ownership transfer lifecycle, verifications, disputes, automation, and testing tips.
- `analytics-dashboard.md` — Role-specific dashboards, data pipelines, workers, and extension ideas.
- `social-platform.md` — Community features (feed, messaging, groups, moderation) and background automation.
- `data-contracts.md` — Room schema highlights, Firebase collections, Retrofit APIs, and validation utilities.
- `testing-operations.md` — Testing strategy, security practices, operational procedures, and incident response.

## New Feature Guides
- `payments-refunds.md` — Payment flow, refunds, UPI, validation, and error handling.
- `logistics-tracking.md` — Order lifecycle, hubs, routing, and real-time tracking.
- `farm-monitoring.md` — Monitoring modules, workers, analytics, and alerts.
- `rbac-permissions.md` — RBAC model, guards, enforcement, and testing.
- `media-pipeline.md` — Media compression, upload, caching, and display.
- `feature-toggles.md` — Flags, A/B testing, and lifecycle.
- `notification-system.md` — FCM integration, channels, and preferences.
- `api-integration.md` — Retrofit, interceptors, rate limiting, and offline-first.
- `security-encryption.md` — SQLCipher, biometrics, sessions, and audit logging.
- `background-jobs.md` — WorkManager workers, constraints, and monitoring.

## ADRs (Architecture Decision Records)
- `adrs/adr-001-database-encryption.md` — Database encryption with SQLCipher.
- `adrs/adr-002-offline-first-sync.md` — Offline-first strategy and conflict policy.
- `adrs/adr-003-worker-scheduling.md` — WorkManager scheduling strategy.

## Developer Resources
- `../CONTRIBUTING.md` — Contribution workflow, testing, and reviews.
- `../CODE_STYLE.md` — Code style, architecture, and testing conventions.
- `api/` — Generated API docs (Dokka output).
- `images/` — Diagrams and screenshots.

## How to Use
1. Start with `architecture.md` for overall context.
2. Use the Feature Guides for domain-specific tasks.
3. Consult `data-contracts.md` when updating database or remote schemas.
4. Review ADRs for historical decisions and rationale.
5. Align with `testing-operations.md` before release or security-sensitive changes.

Keep the docs up to date by mirroring code changes and capturing architecture decisions in separate ADRs when appropriate.
