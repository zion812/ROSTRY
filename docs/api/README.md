# API Documentation

Version: 1.0  
Last Updated: 2025-01-15  
Audience: All developers

---

## Overview
This directory contains generated API documentation from Dokka. This README provides guidance on generating, viewing, and maintaining API docs.

## Generated Docs Location
Dokka generates HTML documentation in the following location:
- **Output Directory**: `app/build/dokka/html/`
- **Why Not Committed to VCS**: Generated docs are not committed to version control as they can be regenerated from source code. This avoids bloating the repository and ensures docs stay in sync with the latest code.
- **Viewing Locally**: After generation, open `app/build/dokka/html/index.html` in your browser to view the docs.

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

### Basic Example
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

### Additional Examples
- **Data Classes**: Document properties and validation logic.
  ```kotlin
  /**
   * Domain model representing a marketplace product.
   *
   * @property id Unique product identifier
   * @property name Product display name (max 100 chars)
   * @property price Price in local currency (must be positive)
   */
  data class Product(
      val id: String,
      val name: String,
      val price: Double
  )
  ```
- **Sealed Classes**: Explain states and usage.
  ```kotlin
  /**
   * Result wrapper for asynchronous operations.
   *
   * @param T Type of successful result data
   * @see Success
   * @see Error
   */
  sealed class Result<out T> {
      data class Success<T>(val data: T) : Result<T>()
      data class Error(val message: String) : Result<Nothing>()
  }
  ```
- **Suspend Functions**: Note async behavior and threading.
  ```kotlin
  /**
   * Fetches products from the repository asynchronously.
   *
   * Runs on IO dispatcher; safe to call from main thread.
   *
   * @return Flow of product lists
   */
  suspend fun getProducts(): Flow<List<Product>>
  ```
- **ViewModels**: Document state management and lifecycle.
  ```kotlin
  /**
   * ViewModel for product creation.
   *
   * Manages UI state and handles product creation logic.
   *
   * @property repository Repository for data operations
   */
  class ProductCreateViewModel @Inject constructor(
      private val repository: ProductRepository
  ) : ViewModel()
  ```
- **Repositories**: Explain data flow and offline support.
  ```kotlin
  /**
   * Repository for product data operations.
   *
   * Handles local and remote data with offline-first caching.
   *
   * @see ProductDao
   */
  interface ProductRepository
  ```
- **Composables**: Document UI responsibilities and parameters.
  ```kotlin
  /**
   * Displays a product card.
   *
   * @param product The product to display
   * @param onClick Callback when clicked
   */
  @Composable
  fun ProductCard(product: Product, onClick: () -> Unit)
  ```

For more details, see the [KDoc style guide](https://kotlinlang.org/docs/kotlin-doc.html) and [Dokka documentation](https://kotlinlang.org/docs/dokka-introduction.html).

## CI/CD Integration
- **Generation in CI/CD**: Add `./gradlew dokkaHtml` to your CI pipeline to generate docs on each build.
- **Hosting**: Publish generated HTML to a static site host (e.g., GitHub Pages, Netlify) or as CI artifacts.
- **Configuration in build.gradle.kts**:
  ```kotlin
  plugins {
      id("org.jetbrains.dokka")
  }

  tasks.withType<org.jetbrains.dokka.gradle.DokkaTaskPartial>().configureEach {
      dokkaSourceSets.configureEach {
          externalDocumentationLink {
              url.set(URL("https://kotlinlang.org/api/kotlin/"))
          }
      }
  }
  ```

## Maintenance
- **When to Regenerate**: After public API changes, significant behavior updates, or new modules/packages.
- **Handling Breaking Changes**: Update KDoc to reflect new contracts; regenerate and review docs for accuracy.
- **Versioning Strategy**: Docs are versioned with the app; tag releases include corresponding doc snapshots.

## Examples from Codebase
- **ProductRepository**: See `app/src/main/java/com/rio/rostry/data/repository/ProductRepository.kt` for repository documentation examples.
- **SyncManager**: See `app/src/main/java/com/rio/rostry/data/sync/SyncManager.kt` for sync-related API docs.

## When to Update
- Public API changes (new/removed/changed signatures)
- Significant behavior or contract changes
- New modules or packages introduced

## Links
- API documentation standards: `../api-documentation.md`
- Code style guide: `../../CODE_STYLE.md`
- Documentation standards: `../DOCUMENTATION_STANDARDS.md`
- Dokka introduction: https://kotlinlang.org/docs/dokka-introduction.html
- KDoc syntax: https://kotlinlang.org/docs/kotlin-doc.html
