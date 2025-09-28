# Contributing to ROSTRY

Thank you for your interest in contributing! This guide explains how to set up your environment, follow coding standards, and submit changes.

## Getting Started

- Clone the repo and open in Android Studio (latest stable).
- Install JDK 17, Android SDKs, and NDK as required by `gradle.properties`.
- Configure Firebase: place `google-services.json` in `app/`.

## Branching & Workflow

- Use GitHub Flow: `main` is stable; create feature branches: `feat/<scope>`, `fix/<scope>`, `docs/<scope>`.
- Write descriptive commit messages; reference issues.

## Code Style

- Follow `CODE_STYLE.md`.
- Kotlin/Compose conventions; organize packages by feature.
- Prefer immutability and clear state flows.

## Testing

- Unit tests for ViewModels, repositories, and utilities.
- Instrumentation tests for critical flows.
- Aim for meaningful coverage; avoid flaky tests.

## Pull Requests

- Include summary, screenshots (if UI), and test evidence.
- Update docs when changing behavior.
- Pass CI (lint, tests, build).

## Security

- Do not commit secrets. Report vulnerabilities privately via security contact.

## Release Process

- Versioning via semantic versioning.
- Changelog and release notes prepared per sprint.

## Onboarding

- See `docs/developer-onboarding.md` and `docs/README-docs.md`.
