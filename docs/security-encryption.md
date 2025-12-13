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

## Security Configuration

- **API Keys**: Managed via `local.properties` (gitignored). Gradle injects them into `BuildConfig`. `MAPS_API_KEY` is required for release builds. `MAPS_JS_API_KEY` offers a separate scope for WebViews.
- **Phone Auth**: `PHONE_AUTH_APP_VERIFICATION_DISABLED_FOR_TESTING` (BuildConfig) gates testing behavior. App verification is DISABLED only in debug builds to facilitate testing without Play Integrity/reCAPTCHA blocking.
- **Root Detection**: Device integrity checks run on startup. If rooted (`device_rooted` pref):
  - A warning is shown to the user.
  - High-risk operations (e.g. Transfers) check `SecurityManager.isDeviceCompromised()` and may limit functionality.
  - Detection methods are audited locally and to observability tools.

## Testing & Threat Modeling

- Regular security tests, dependency scanning, and STRIDE-based reviews.
