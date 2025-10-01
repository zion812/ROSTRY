# Contributing to ROSTRY

Thank you for your interest in contributing! This guide explains how to set up your environment, follow coding standards, and submit changes.

## Table of Contents

- [Getting Started](#getting-started)
- [Development Workflow](#development-workflow)
- [Code Style](#code-style)
- [Testing Requirements](#testing-requirements)
- [Pull Request Process](#pull-request-process)
- [Code Review](#code-review)
- [Documentation](#documentation)
- [Issue Reporting](#issue-reporting)
- [Security](#security)
- [Release Process](#release-process)

## Getting Started

1. **Clone the repository** and open in Android Studio (latest stable version).
2. **Install prerequisites**:
   - JDK 17
   - Android SDKs as required by `gradle.properties`
   - NDK if working on native code
3. **Configure Firebase**: Download and place `google-services.json` in `app/` directory.
4. **Sync Gradle**: Allow Android Studio to sync dependencies automatically.
5. **Run the app**: Build and run in debug mode to ensure everything works.

See `docs/developer-onboarding.md` for comprehensive onboarding guide.

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

## Security

- **Never commit secrets**: API keys, passwords, tokens belong in environment variables or secure storage.
- **Report vulnerabilities privately**: Contact maintainers directly for security issues.
- **Follow security best practices**: See `docs/security-encryption.md`.

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

## Community Guidelines

- Be respectful and constructive
- Welcome newcomers and help them get started
- Focus on the code, not the person
- Celebrate successes and learn from failures

## Communication Channels

- **GitHub Issues**: Bug reports, feature requests, discussions
- **Pull Requests**: Code review and technical discussions

## Getting Help

- Review `docs/developer-onboarding.md` for setup help
- Check `docs/troubleshooting.md` for common issues
- Ask questions in GitHub Discussions
- Review existing issues and PRs for similar problems

---

Thank you for contributing to ROSTRY! Your efforts help build a better platform for the poultry farming community.
