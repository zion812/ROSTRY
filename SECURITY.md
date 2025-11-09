# Security Policy

**Version:** 1.0  
**Last Updated:** 2025-01-15  
**Maintainers:** ROSTRY Security Team

---

## Supported Versions

We provide security updates for the following versions of ROSTRY:

| Version | Supported          | End of Support |
| ------- | ------------------ | -------------- |
| 1.0.x   | :white_check_mark: | Active         |
| < 1.0   | :x:                | Ended          |

---

## Reporting a Vulnerability

The ROSTRY team takes security vulnerabilities seriously. We appreciate your efforts to responsibly disclose your findings.

### How to Report

**Please do NOT report security vulnerabilities through public GitHub issues.**

Use one of these private channels:

1. **GitHub Security Advisory (Primary)**
   - Go to the [Security tab](../../security/advisories/new)
   - Click "Report a vulnerability"
   - Provide clear reproduction steps, impact, and environment details

2. **Email (Fallback)**
   - security@rostry.dev
   - Include reproduction steps and impact assessment
   - PGP available on request

### What to Include

To help us triage and address the issue quickly, please include:

- **Description**: Clear description of the vulnerability
- **Impact**: Potential impact and attack scenarios
- **Affected Components**: Which parts of the app are affected
- **Reproduction Steps**: Detailed steps to reproduce the issue
- **Proof of Concept**: Code, screenshots, or video demonstration
- **Suggested Fix**: If you have ideas for remediation
- **Your Environment**: Android version, device model, app version
- **Discovery Details**: How you discovered the vulnerability

### Response Timeline

- **Initial Response**: Within 48 hours (acknowledgment)
- **Status Update**: Within 7 days (triage and assessment)
- **Fix Timeline**: Depends on severity (see below)
- **Disclosure**: Coordinated disclosure after fix is deployed

### Severity Levels and Response Times

| Severity | Description | Response Time | Fix Target |
|----------|-------------|---------------|------------|
| **Critical** | Remote code execution, data breach, authentication bypass | 24 hours | 7 days |
| **High** | Privilege escalation, sensitive data exposure | 48 hours | 14 days |
| **Medium** | Denial of service, information disclosure | 7 days | 30 days |
| **Low** | Minor information leaks, low-impact issues | 14 days | 60 days |

### What to Expect

1. **Acknowledgment**: We'll confirm receipt of your report
2. **Validation**: We'll verify the vulnerability
3. **Fix Development**: We'll develop and test a patch
4. **Disclosure**: We'll coordinate disclosure with you
5. **Credit**: We'll publicly acknowledge your contribution (if desired)
6. **Release**: Security update will be published

---

## Security Best Practices

### For Users

If you're using ROSTRY, follow these security best practices:

1. **Keep Updated**: Always use the latest version of the app
2. **Device Security**: Enable device encryption and screen lock
3. **Biometric Authentication**: Enable biometric authentication in app settings
4. **Network Security**: Use secure Wi-Fi networks; avoid public Wi-Fi for sensitive operations
5. **Permissions**: Review and understand app permissions
6. **Demo Mode**: Remember that demo mode has reduced security features
7. **Report Suspicious Activity**: Report any unusual behavior immediately

### For Developers

If you're contributing to ROSTRY:

1. **Review Security Documentation**: Read `docs/security-encryption.md` thoroughly
2. **Secure Coding**: Follow the security guidelines in our documentation
3. **Input Validation**: Always validate and sanitize user input
4. **Authentication**: Never bypass authentication or authorization checks
5. **Sensitive Data**: Handle API keys, tokens, and credentials securely
6. **Dependencies**: Keep dependencies updated; monitor for vulnerabilities
7. **Code Review**: Security-focused code reviews are required for all PRs
8. **Testing**: Write security test cases for authentication and authorization flows

#### API Key Management

**CRITICAL**: Never commit API keys to version control!

**Setup Process**:
1. Copy `local.properties.template` to `local.properties`
2. Add your actual API keys to `local.properties` (this file is gitignored)
3. Do not place secrets in `gradle.properties` or source code

**Google Maps API Key**:
- Stored exclusively in `local.properties` as `MAPS_API_KEY`
- Exposed via `BuildConfig.MAPS_API_KEY` and injected to AndroidManifest via `manifestPlaceholders`
- Release builds fail if the key is missing or a placeholder value

**API Key Restrictions**:
- Restrict Maps API key to your Android app's package name and SHA-1 certificate fingerprint
- Use separate keys for development and production
- Enable only required APIs (Maps SDK, Places API)
- Set up billing alerts in Google Cloud Console
- Regularly rotate API keys

**Example local.properties**:
```properties
MAPS_API_KEY=AIzaSyXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX
```

**Gradle configuration (conceptual)**:
```kotlin
// app/build.gradle.kts
val mapsApiKeyProvider = providers.gradleProperty("MAPS_API_KEY")

buildTypes {
    release {
        val releaseKey = mapsApiKeyProvider.orNull
            ?: throw GradleException("MAPS_API_KEY is not set. Add it to local.properties for release builds.")
        if (releaseKey == "<set me>" || releaseKey == "your_api_key_here" || releaseKey == "debug-placeholder") {
            throw GradleException("MAPS_API_KEY is a placeholder. Configure a real key in local.properties for release builds.")
        }
        buildConfigField("String", "MAPS_API_KEY", "\"$releaseKey\"")
        manifestPlaceholders["MAPS_API_KEY"] = releaseKey
    }
    debug {
        val debugKey = mapsApiKeyProvider.orElse("debug-placeholder").get()
        buildConfigField("String", "MAPS_API_KEY", "\"$debugKey\"")
        manifestPlaceholders["MAPS_API_KEY"] = debugKey
    }
}
```

---

## 🛡️ Security Checklist
Developers must complete this checklist **before every PR merge**:
- [ ] No hard-coded secrets (API keys, tokens, passwords)
- [ ] Input validation & output sanitization
- [ ] Proper authentication / authorization checks
- [ ] HTTPS for all network calls (Retrofit baseUrl uses `https://`)
- [ ] Room queries use parameter binding (no raw string concat)
- [ ] Crash logs do **not** expose sensitive data
- [ ] ProGuard / R8 rules preserve crypto classes
- [ ] New Firebase rules reviewed
- [ ] Updated dependency versions scanned for CVEs

> Detailed guidelines: [`docs/security-encryption.md`](docs/security-encryption.md)

---

## Security Features

ROSTRY implements multiple layers of security to protect user data:

### Data Protection

- **Database Encryption**: SQLCipher encryption for local Room database
- **Encrypted Preferences**: Sensitive preferences stored using EncryptedSharedPreferences
- **Secure Session Management**: Token-based authentication with automatic refresh
- **Biometric Authentication**: Optional fingerprint/face unlock
- **Data Masking**: Sensitive data masked in logs and screenshots

### Network Security

- **HTTPS Only**: All network communication over TLS 1.2+
- **Certificate Pinning**: Prevents MITM attacks (production builds)
- **Secure Headers**: Proper security headers in API requests
- **Request Signing**: Signed requests for sensitive operations

### Access Control

- **Role-Based Access Control (RBAC)**: Three user roles with distinct permissions
  - General User: Basic social features
  - Farmer: Full farm management + marketplace
  - Enthusiast: Social + limited marketplace access
- **Firebase Security Rules**: Server-side authorization for Firestore and Storage
- **Cloud Functions Security**: Authenticated and authorized cloud function calls
- **Rate Limiting**: Protection against brute force and abuse

### Audit and Monitoring

- **Audit Logging**: Critical operations logged for accountability
- **Transfer Audit Trail**: Complete history of fowl ownership transfers
- **Anomaly Detection**: Monitoring for unusual patterns
- **Crash Reporting**: Firebase Crashlytics with anonymized data

### Code Security

- **ProGuard/R8**: Code obfuscation in release builds
- **SafetyNet**: Device integrity verification
- **Root Detection**: Warning for rooted devices
- **Tamper Detection**: Checks for app modifications

---

## Known Security Considerations

### Demo Mode

**Important**: Demo mode has reduced security for ease of testing:
- Pre-populated demo credentials
- Simplified authentication flows
- Mock data without full encryption
- **Do not use demo mode with real user data**

### Development vs Production

Security configurations differ between environments:

| Feature | Development | Production |
|---------|-------------|------------|
| Certificate Pinning | Disabled | Enabled |
| Network Logging | Verbose | Minimal |
| Debug Flags | Enabled | Disabled |
| ProGuard | Disabled | Enabled |
| SafetyNet | Bypass | Enforced |

### Third-Party Dependencies

We regularly monitor and update dependencies with known vulnerabilities. See our dependency policy in `CONTRIBUTING.md`.

---

## Security Updates

Security updates are documented in `CHANGELOG.md` with the `[Security]` tag.

To stay informed:
- Watch this repository for releases
- Enable GitHub notifications for security advisories
- Check `CHANGELOG.md` regularly
- Subscribe to release announcements

### Recent Security Updates

- **v1.0.0** (2024-12-15): Initial release with comprehensive security implementation
  - SQLCipher database encryption
  - Firebase security rules
  - Secure transfer system with fraud detection
  - Biometric authentication

---

## Bug Bounty Program

**Status**: Currently not available

We're working on establishing a bug bounty program. Check back for updates.

---

## Security Resources

### Internal Documentation

- [Security & Encryption Guide](docs/security-encryption.md)
- [Firebase Setup & Security Rules](docs/firebase-setup.md)
- [Architecture Documentation](docs/architecture.md)
- [Data Contracts](docs/data-contracts.md)

### External Resources

- [OWASP Mobile Security Project](https://owasp.org/www-project-mobile-security/)
- [Android Security Best Practices](https://developer.android.com/training/articles/security-tips)
- [Firebase Security Rules](https://firebase.google.com/docs/rules)
- [CVE Database](https://cve.mitre.org/)

---

## Contact

For non-vulnerability questions:
- Email: security@rostry.dev
- GitHub Discussions: [Security Category](../../discussions/categories/security)

For general issues, use our [issue tracker](../../issues).

---

## Acknowledgments

We appreciate the security researchers and contributors who have helped make ROSTRY more secure. Security reports and responsible disclosure are always welcome.

---

**Thank you for helping keep ROSTRY and our users safe!**
