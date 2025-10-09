# API Documentation

Version: 1.0  
Last Updated: 2025-01-15  
Audience: All developers

---

## Overview
This directory hosts generated API documentation produced by Dokka. It is safe to clean and regenerate at any time.

## Generate Docs
```bash
# Per-module HTML
./gradlew :app:dokkaHtml

# Multi-module (if configured)
./gradlew dokkaHtmlMultiModule

# Open locally (example for macOS)
open app/build/dokka/html/index.html
```

> CI/CD can publish HTML artifacts; avoid committing generated docs to VCS.

## KDoc Guidelines
- Document all public classes, functions, parameters, and return types.
- Prefer concise descriptions; add examples for complex behavior.
- Reference related APIs with `@see`.

Example:
```kotlin
/**
 * Creates a new product and persists it locally and remotely.
 *
 * @param product Validated product to create
 * @return Result with product ID on success
 * @throws IllegalArgumentException when validation fails
 * @see getProducts
 */
 suspend fun createProduct(product: Product): Result<String>
```

## When to Update
- Public API changes (new/removed/changed signatures)
- Significant behavior or contract changes
- New modules or packages introduced

## Links
- API documentation standards: `../api-documentation.md`
- Code style guide: `../../CODE_STYLE.md`
- Documentation standards: `../DOCUMENTATION_STANDARDS.md`
