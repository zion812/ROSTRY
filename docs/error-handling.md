# Error Handling Guide

Version: 1.0
Last Updated: 2025-01-15
Audience: Android developers

---

## Result Type Pattern
Use a sealed `Result` for success/loading/error and extension helpers.

```kotlin
sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val exception: Exception? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
}
```

## Repository Error Handling
- Validate inputs early; return `Result.Error` with actionable messages
- Catch network exceptions; prefer offline-first behavior when safe

```kotlin
suspend fun createProduct(p: Product): Result<String> = try {
    dao.insert(p.toEntity())
    api.create(p)
    Result.Success(p.id)
} catch (e: FirebaseNetworkException) {
    // Local success, remote will sync later
    Result.Success(p.id)
} catch (e: Exception) {
    Timber.e(e, "createProduct failed")
    Result.Error("Failed to create product: ${e.localizedMessage}", e)
}
```

## ViewModel Error Mapping
- Convert technical messages to user-friendly text
- Provide retry actions where possible

```kotlin
private fun handleError(msg: String) {
    _ui.update {
        it.copy(
            isLoading = false,
            error = when {
                msg.contains("network", true) -> "No internet. Changes saved locally."
                msg.contains("permission", true) -> "You don't have permission."
                else -> "Something went wrong. Please try again."
            }
        )
    }
}
```

## Logging Strategy
- Use Timber in debug; Crashlytics in release
- Avoid logging PII; redact sensitive fields

## Error Monitoring
- Enable Firebase Crashlytics with custom keys
- Track error categories for analytics

## Testing Error Scenarios
- Mock network failures, DAO exceptions
- Assert UI state transitions (loading → error, with retry)

See also: `architecture.md`, `security-encryption.md`, `testing-strategy.md`.
