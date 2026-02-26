# Error Handler — Usage Guide

## Overview

The Error Handler (`ErrorHandler.kt`) provides centralized, categorized error handling with structured logging, user-friendly error messages, and recovery strategies.

## Error Categories

| Category | Description | Recovery Strategy |
|----------|-------------|-------------------|
| `NetworkError` | Connectivity or timeout issues | Retry with exponential backoff |
| `DatabaseError` | Room/SQLite failures | Validate and retry, or clear cache |
| `AuthenticationError` | Firebase Auth failures | Re-authenticate user |
| `ValidationError` | Invalid user input | Show inline field errors |
| `ServerError` | Backend/API errors | Retry or show degraded state |
| `UnknownError` | Unexpected exceptions | Log and show generic message |

## Usage in ViewModels

```kotlin
class MyViewModel @Inject constructor(
    private val errorHandler: ErrorHandler
) : ViewModel() {
    
    fun performAction() {
        viewModelScope.launch {
            try {
                // Business logic
                repository.doSomething()
            } catch (e: Exception) {
                // Categorize and handle the error
                val result = errorHandler.handle(e, context = "MyViewModel.performAction")
                _errorState.value = result.userMessage
                
                // Optionally attempt recovery
                if (result.isRetryable) {
                    delay(result.retryDelayMs)
                    performAction() // Retry
                }
            }
        }
    }
}
```

## Usage in Workers

```kotlin
override suspend fun doWork(): Result {
    return try {
        // Worker logic
        Result.success()
    } catch (e: Exception) {
        val result = errorHandler.handle(e, context = "MyWorker.doWork")
        if (result.isRetryable && runAttemptCount < 3) {
            Result.retry()
        } else {
            Result.failure()
        }
    }
}
```

## User-Facing Error Messages

Error messages shown to users are always friendly and actionable:

- ❌ "SQLiteConstraintException: UNIQUE constraint failed"  
- ✅ "This item already exists. Please try a different name."

- ❌ "SocketTimeoutException: failed to connect"  
- ✅ "Unable to connect. Please check your internet connection and try again."

## Logging

All errors are logged with:
- Error category and severity
- Context (calling function)
- Stack trace (debug builds only)
- Timestamp and user ID (anonymized)

---

# Configuration Manager — Usage Guide

## Overview

`ConfigurationManager` provides centralized, type-safe configuration loaded from Firebase Remote Config with local cache fallback.

## Configuration Structure

```kotlin
AppConfiguration(
    security = SecurityConfig(
        adminIdentifiers = listOf(...),
        moderationBlocklist = listOf(...),
        allowedFileTypes = listOf("image/jpeg", "image/png", "video/mp4")
    ),
    thresholds = ThresholdConfig(
        storageQuotaMB = 500,
        maxBatchSize = 100,
        circuitBreakerFailureRate = 0.5,
        hubCapacity = 1000,
        deliveryRadiusKm = 50.0
    ),
    timeouts = TimeoutConfig(
        networkRequestSeconds = 30,
        circuitBreakerOpenSeconds = 30,
        retryDelaysSeconds = listOf(1, 2, 4)
    ),
    features = FeatureConfig(
        enableRecommendations = true,
        enableDisputes = true,
        enableBreedingCompatibility = true
    )
)
```

## Reading Configuration

```kotlin
class MyService @Inject constructor(
    private val configManager: ConfigurationManager
) {
    suspend fun doSomething() {
        val config = configManager.get()
        val maxRadius = config.thresholds.deliveryRadiusKm
        val timeout = config.timeouts.networkRequestSeconds
    }
}
```

## Adding New Configuration Values

1. Add the field to the appropriate config data class (`ThresholdConfig`, `TimeoutConfig`, etc.)
2. Set a sensible default value
3. Add the mapping in `ConfigurationManager.parseRemoteConfig()`
4. Use `configManager.get()` to access the value

---

# Validation Framework — Usage Guide

## Available Validators

| Validator | Purpose | Example |
|-----------|---------|---------|
| `ProductValidator` | Product fields | Name, price, breed, category |
| `FileUploadValidator` | Media uploads | File size, type, dimensions |
| `CoordinateValidator` | GPS coordinates | Farm location, hub location |
| `InputValidator` | General text | Email, phone, non-blank fields |

## Usage

```kotlin
val result = ProductValidator.validate(
    name = "Rhode Island Red",
    price = 450.0,
    breed = "RIR",
    category = "Rooster"
)

when (result) {
    is InputValidationResult.Valid -> { /* proceed */ }
    is InputValidationResult.Invalid -> {
        result.errors.forEach { error ->
            showFieldError(error.field, error.message)
        }
    }
}
```

## Custom Validators

```kotlin
class MyCustomValidator {
    fun validate(input: MyInput): InputValidationResult {
        val errors = mutableListOf<InputValidationError>()
        
        if (input.name.isBlank()) {
            errors.add(InputValidationError("name", "Name is required", "REQUIRED"))
        }
        
        return if (errors.isEmpty()) 
            InputValidationResult.Valid 
        else 
            InputValidationResult.Invalid(errors)
    }
}
```

---

# Circuit Breaker — Usage Guide

## States

```
CLOSED → OPEN → HALF_OPEN → CLOSED (or back to OPEN)
```

| State | Behavior |
|-------|----------|
| `CLOSED` | Normal operation, requests pass through |
| `OPEN` | All requests fail fast, fallback used |
| `HALF_OPEN` | One test request allowed to probe recovery |

## Configuration

- **Failure Rate Threshold**: 50% (configurable via `ThresholdConfig.circuitBreakerFailureRate`)
- **Open Duration**: 30 seconds (configurable via `TimeoutConfig.circuitBreakerOpenSeconds`)
- **Sliding Window**: Last 10 requests

## Usage with External Services

```kotlin
val result = circuitBreaker.execute("firebase-storage") {
    // External call
    firebaseStorage.uploadFile(file)
} ?: run {
    // Fallback when circuit is open
    cacheManager.getCachedResult()
}
```
