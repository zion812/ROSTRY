# Security & Encryption

Defense-in-depth across storage, transport, and application layers.

## Database Encryption

- SQLCipher via `DatabaseModule.kt` for Room encryption.
- Passphrase management with secure key derivation and rotation strategy.

## Biometrics

- `BiometricUtil.kt` for device-level auth gating sensitive actions.

## Sessions

- `SessionManager.kt` handles tokens, refresh, inactivity timeouts, and lock screen.

## Validation & Moderation

- Content validation utilities and `ContentValidation.kt` for scans and sanitization.

## Transport Security

- Enforce TLS; consider certificate pinning for high-risk endpoints.

## Privacy & Compliance

- Data minimization, user consent, and GDPR-friendly flows.

## Audit Logging

- `AuditLogEntity` for sensitive action trails.

## Secure Config

- Store secrets outside VCS; use encrypted keystores and CI secrets.

## Testing & Threat Modeling

- Regular security tests, dependency scanning, and STRIDE-based reviews.
