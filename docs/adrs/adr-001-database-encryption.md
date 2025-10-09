# ADR-001: Database Encryption Strategy

- Status: Draft
- Date: 2025-10-10

## Context
Secure local storage is required for sensitive user and livestock data in an agricultural marketplace app.

## Decision
Use SQLCipher for Room database encryption with configurable passphrase management.

## Consequences
- Positive: Strong data-at-rest protection; compliance alignment.
- Negative: Performance overhead; key management complexity.
- Mitigation: Secure key derivation, rotation, and minimal sensitive data persistence.

## References
- Implementation: `app/src/main/java/com/rio/rostry/di/DatabaseModule.kt`
- Security overview: `docs/security-encryption.md`
