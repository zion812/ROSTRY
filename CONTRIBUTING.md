# Contributing to ROSTRY

**Version:** 2.0  
**Last Updated:** 2025-01-15

---

Thank you for your interest in contributing! This guide explains how to set up your environment, follow coding standards, and submit changes.

## Table of Contents

- [Getting Started](#getting-started)
- [Code of Conduct](#code-of-conduct)
- [Development Workflow](#development-workflow)
- [Code Style](#code-style)
- [Testing Requirements](#testing-requirements)
- [Documentation Requirements](#documentation-requirements)
- [Pull Request Process](#pull-request-process)
- [Code Review](#code-review)
- [CI/CD Pipeline](#cicd-pipeline)
- [Security](#security)
- [Issue Reporting](#issue-reporting)
- [Release Process](#release-process)

## 📝 Quick Contribution Checklist
- [ ] Read the **[Quick Start](../QUICK_START.md)** and run the app
- [ ] Create a feature branch: `feat/<short-description>`
- [ ] Follow **[CODE_STYLE.md](CODE_STYLE.md)** conventions
- [ ] Write or update **tests** and **docs**
- [ ] Run `./gradlew ktlintCheck detekt test` locally
- [ ] Submit a PR using the provided template

---

## Getting Started

1. **Clone the repository** and open in Android Studio (latest stable version).
2. **Install prerequisites**:
   - JDK 17
   - Android SDKs as required by `gradle.properties`
   - NDK if working on native code
3. **Configure Firebase**: Download and place `google-services.json` in `app/` directory.
4. **Sync Gradle**: Allow Android Studio to sync dependencies automatically.
5. **Run the app**: Build and run in debug mode to ensure everything works.

See [developer-onboarding.md](docs/developer-onboarding.md) for comprehensive onboarding. For a fast start see [QUICK_START.md](QUICK_START.md) and handy commands in [CHEAT_SHEET.md](CHEAT_SHEET.md).

---

## Code of Conduct (Summary)
We are committed to a **welcoming, inclusive, harassment-free** community.

- Be respectful and constructive
- No harassment or discrimination
- Report unacceptable behavior privately (see [SECURITY.md](SECURITY.md))

By contributing you agree to follow these guidelines. The Code of Conduct is maintained here as a concise summary to reduce redundancy.

---

## Development Workflow

### Branching Strategy

We use GitHub Flow with feature branches:

- **main**: Always stable and deployable
- **Feature branches**: Create from main with descriptive names:
  - `feat/add-payment-gateway` - New features
  - `fix/sync-crash` - Bug fixes
  - `docs/update-architecture` - Documentation updates
  - `refactor/simplify-viewmodel` - Code refactoring
  - `test/add-repository-tests` - Test additions

### Commit Messages

Write clear, descriptive commit messages:

```
feat: add multi-step wizard for product creation

- Implement 4-step wizard (BASICS → DETAILS → MEDIA → REVIEW)
- Add progress indicator and navigation controls
- Include inline validation for each step

Closes #123
```

Format: `<type>: <subject>` followed by optional body and footer.

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `test`, `chore`.

## Code Style

Follow `CODE_STYLE.md` for detailed conventions.

### Key Principles

- **Kotlin idioms**: Use data classes, sealed classes, and extension functions appropriately.
- **Null safety**: Avoid `!!`; use safe calls (`?.`) and require checks.
- **Feature organization**: Package by feature, not by layer.
- **Immutability**: Prefer `val` over `var`; use immutable data structures.
- **Compose best practices**: Stateless composables, proper state hoisting.

### Examples

**Good**:
```kotlin
@Composable
fun ProductCard(
    product: Product,
    onProductClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.clickable { onProductClick(product.id) }) {
        // ... content
    }
}
```

**Bad**:
```kotlin
@Composable
fun ProductCard(product: Product) {
    var clicked by remember { mutableStateOf(false) } // State should be hoisted
    Card(modifier = Modifier.clickable { clicked = true }) {
        // ...
    }
}
```

## Testing Requirements

### Test Coverage

- **Unit tests**: Required for ViewModels, repositories, and utility functions.
- **Instrumentation tests**: Required for critical user flows and database migrations.
- **Test naming**: Use descriptive names that explain what is being tested.

See `docs/testing-strategy.md` for comprehensive testing approach.

### Example Test Names

```kotlin
@Test
fun `createProduct with valid data should return Success`() { }

@Test
fun `applyFilters with empty query should return all products`() { }

@Test
fun `wizard nextStep with invalid data should show validation errors`() { }
```

### Running Tests

```bash
# Unit tests
./gradlew test

# Instrumented tests
./gradlew connectedAndroidTest

# Specific test class
./gradlew test --tests "ProductRepositoryTest"
```

---

## Documentation Requirements

All code changes must include appropriate documentation.

### When to Document

**Required**:
- All public classes, interfaces, and functions
- Complex algorithms or non-obvious logic
- All parameters and return values
- Exceptions that can be thrown

**Optional**:
- Private functions (unless complex)
- Self-explanatory code

### KDoc Standards

Use **KDoc** (Kotlin Documentation) for inline API documentation:

```kotlin
/**
 * Creates a new product and stores it locally and remotely.
 *
 * Validates product data, assigns timestamps, stores in Room database,
 * and queues for Firestore sync.
 *
 * @param product The product to create (with ID and sellerId set)
 * @return Result.Success with product ID, or Result.Error with message
 * @throws IllegalArgumentException if validation fails
 * @see Product
 * @see getProducts
 */
suspend fun createProduct(product: Product): Result<String>
```

### Documentation Quality

- **Be clear**: Write for readers unfamiliar with implementation
- **Be concise**: Don't repeat what's obvious from signatures
- **Be specific**: Provide concrete examples for complex APIs
- **Be accurate**: Keep docs in sync with code

### Generating Documentation

```bash
# Generate HTML documentation
./gradlew :app:dokkaHtml

# View generated docs
open app/build/dokka/html/index.html
```

**Complete KDoc Guide**: See [docs/api-documentation.md](docs/api-documentation.md). Also follow our [Documentation Standards](docs/DOCUMENTATION_STANDARDS.md).

---

## Pull Request Process

### Before Submitting

- [ ] Code follows `CODE_STYLE.md` conventions
- [ ] All tests pass locally
- [ ] New tests added for new functionality
- [ ] Documentation updated (README, relevant docs/)
- [ ] No merge conflicts with main
- [ ] Commit messages are clear and descriptive

### PR Description Template

```markdown
## Summary
Brief description of changes

## Type of Change
- [ ] Bug fix
- [ ] New feature
- [ ] Breaking change
- [ ] Documentation update

## Changes Made
- Bullet point list of key changes

## Testing Done
- How you tested these changes

## Screenshots (if UI changes)
[Add screenshots here]

## Checklist
- [ ] Tests added/updated
- [ ] Documentation updated
- [ ] No breaking changes (or migration guide provided)
```

### Required CI Checks

All PRs must pass:
- Lint checks (ktlint, detekt)
- Unit tests
- Build verification
- Code coverage threshold

## Code Review

### What to Expect

- Reviews typically completed within 2 business days
- Constructive feedback focused on code quality and maintainability
- May request changes before approval
- At least 1 approval required before merge

### Reviewer Guidelines

- Focus on logic, architecture, and maintainability
- Suggest improvements, don't demand perfection
- Approve when code meets quality standards

---

## CI/CD Pipeline

ROSTRY uses GitHub Actions for continuous integration and deployment.

### Automated Checks

Every pull request triggers:
- **Linting**: ktlint, detekt, Android lint
- **Build**: Debug and release variants
- **Tests**: Unit tests with coverage reports
- **Security**: Dependency vulnerability scanning

### Pipeline Stages

1. **Code Quality** (<3 min): Linting and static analysis
2. **Build** (<5 min): Compile and generate APKs
3. **Test** (<10 min): Run unit and instrumentation tests
4. **Deploy** (main branch only): Firebase App Distribution

### Viewing Pipeline Results

- Check the **Actions** tab in GitHub
- PR checks must pass before merging
- Failed checks block merge until fixed

### Local Pipeline Testing

Run checks locally before pushing:

```bash
# Lint
./gradlew ktlintCheck detekt

# Build
./gradlew assembleDebug

# Test
./gradlew test

# All checks
./gradlew check
```

**Complete CI/CD Documentation**: See [docs/ci-cd.md](docs/ci-cd.md)

---

## Documentation

### When to Update Docs

Update documentation when:
- Adding new features or APIs
- Changing existing behavior
- Adding new architectural patterns
- Modifying database schema

### Documentation Files

- `README.md`: High-level project overview
- `docs/`: Feature-specific documentation
- Code comments: Complex logic and non-obvious decisions
- KDoc: Public APIs and interfaces

## Issue Reporting

### Bug Reports

Use this template for bug reports:

```markdown
**Describe the bug**
Clear description of the issue

**To Reproduce**
1. Go to '...'
2. Click on '...'
3. See error

**Expected behavior**
What should happen

**Screenshots**
If applicable

**Environment:**
- Device: [e.g. Pixel 6]
- Android version: [e.g. 13]
- App version: [e.g. 1.2.0]
```

### Feature Requests

Use this template for feature requests:

```markdown
**Is your feature request related to a problem?**
Clear description of the problem

**Proposed solution**
How you envision this working

**Alternatives considered**
Other approaches you've thought about

**Additional context**
Any other relevant information
```

---

## Security

Security is critical for ROSTRY. Please follow these guidelines.

### Reporting Security Vulnerabilities

**CRITICAL**: Do NOT create public GitHub issues for security vulnerabilities!

**Private Reporting Process**:
1. Go to the **Security** tab in GitHub
2. Click "Report a vulnerability"
3. Provide detailed description of the vulnerability
4. Include steps to reproduce if possible
5. Suggest a fix if you have one

**Response Timeline**:
- Initial acknowledgment: Within 48 hours
- Status update: Within 7 days
- Fix and disclosure: Coordinated with reporter

**Full Security Policy**: See [SECURITY.md](SECURITY.md)

### Security Best Practices for Contributors

**Never Commit**:
- API keys or secrets
- Passwords or tokens
- Firebase configuration with sensitive data
- Private keys or certificates
- User data or PII

**Always Use**:
- `local.properties` for API keys (gitignored)
- Environment variables for CI/CD secrets
- Encrypted storage for sensitive data
- Secure coding practices from [docs/security-encryption.md](docs/security-encryption.md)

### Code Security Checklist

When contributing code:
- [ ] No hardcoded secrets or credentials
- [ ] Input validation for user data
- [ ] Proper authentication and authorization checks
- [ ] SQL injection prevention (use parameterized queries)
- [ ] XSS prevention (sanitize user input)
- [ ] HTTPS for all network calls
- [ ] Encrypted local storage for sensitive data
- [ ] Follow principle of least privilege

**Security Resources**:
- [Security & Encryption Guide](docs/security-encryption.md)
- [Firebase Security Rules](docs/firebase-setup.md#security-rules)
- [API Keys Setup](docs/api-keys-setup.md)

---

## Release Process

### Versioning

We follow [Semantic Versioning](https://semver.org/):
- **Major** (X.0.0): Breaking changes
- **Minor** (1.X.0): New features, backwards compatible
- **Patch** (1.0.X): Bug fixes, backwards compatible

### Release Steps

1. Update `CHANGELOG.md` with release notes
2. Bump version in `build.gradle.kts`
3. Create release tag: `git tag v1.2.0`
4. Push tag: `git push origin v1.2.0`
5. Create GitHub release with changelog

## Contributing Guidelines

- Be respectful and constructive
- Welcome newcomers and help them get started
- Focus on the code, not the person
- Celebrate successes and learn from failures
## Getting Help

- Review `docs/developer-onboarding.md` for setup help
- Check `docs/troubleshooting.md` for common issues
- Ask questions in GitHub Discussions
- Review existing issues and PRs for similar problems

Please read this document before contributing to ensure a smooth collaboration.

Quick references:
- Code style quick reference: [docs/code-style-quick-reference.md](docs/code-style-quick-reference.md)
- Full code style: [CODE_STYLE.md](CODE_STYLE.md)
