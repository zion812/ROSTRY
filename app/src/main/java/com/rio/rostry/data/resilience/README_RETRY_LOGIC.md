# Retry Logic Implementation

## Overview

The retry logic implementation provides automatic retry with exponential backoff for transient failures. It integrates with the Error Handler and Circuit Breaker to provide robust failure recovery.

## Components

### 1. RetryPolicyManager

**Location**: `app/src/main/java/com/rio/rostry/data/resilience/RetryPolicyManager.kt`

**Features**:
- Exponential backoff with configurable delays (default: 1s, 2s, 4s)
- Retry on transient HTTP status codes: 408, 429, 500, 502, 503, 504
- No retry on client errors (400-499) except 408 and 429
- Logs each retry attempt with attempt number and delay
- Logs successful retries
- Circuit breaker integration
- Retry budget to prevent thundering herd

**Usage**:
```kotlin
val retryPolicyManager = RetryPolicyManager()

val result = retryPolicyManager.executeWithRetry(
    key = "fetch-products",
    policy = RetryPolicy.DEFAULT
) {
    productRepository.fetchProducts()
}
```

### 2. TransientErrorClassifier

**Location**: `app/src/main/java/com/rio/rostry/data/resilience/TransientErrorClassifier.kt`

**Features**:
- Classifies errors as transient (retryable) or permanent
- Handles HTTP status codes correctly
- Supports network errors (SocketTimeoutException, UnknownHostException, etc.)
- Respects Retry-After header for 429 responses

**Retryable Status Codes**:
- 408 (Request Timeout)
- 429 (Too Many Requests)
- 500 (Internal Server Error)
- 502 (Bad Gateway)
- 503 (Service Unavailable)
- 504 (Gateway Timeout)

**Non-Retryable Status Codes**:
- 400 (Bad Request)
- 401 (Unauthorized)
- 403 (Forbidden)
- 404 (Not Found)
- 405 (Method Not Allowed)
- 409 (Conflict)
- 422 (Unprocessable Entity)

### 3. RetryRecoveryStrategy

**Location**: `app/src/main/java/com/rio/rostry/domain/error/RecoveryStrategy.kt`

**Features**:
- Integrates retry logic with Error Handler
- Uses exponential backoff with 1s, 2s, 4s delays
- Automatically retries failed operations

**Usage**:
```kotlin
val recoveryStrategy = RetryRecoveryStrategy(
    retryPolicyManager = retryPolicyManager,
    maxAttempts = 3,
    retryAction = { productRepository.fetchProducts() }
)

errorHandler.handle(
    error = exception,
    operationName = "FetchProducts",
    recoveryStrategy = recoveryStrategy
)
```

### 4. UI Retry Components

**Location**: `app/src/main/java/com/rio/rostry/ui/components/states/ErrorState.kt`

**Features**:
- Standard error state component with retry button
- Predefined error states for common scenarios
- Consistent UI across all screens

**Usage**:
```kotlin
ErrorState(
    title = "Failed to load products",
    message = "Unable to connect. Please check your internet connection.",
    onRetry = { viewModel.retryLoadProducts() }
)
```

## Retry Policy Configuration

### Default Policy
```kotlin
RetryPolicy(
    maxRetries = 3,
    baseDelayMs = 1000L,  // 1 second
    maxDelayMs = 4000L,   // 4 seconds
    retryBudget = 10,
    useJitter = false     // Predictable delays: 1s, 2s, 4s
)
```

### Exponential Backoff Calculation

For attempt `n` (0-indexed):
- Attempt 0: 1000ms * 2^0 = 1000ms (1 second)
- Attempt 1: 1000ms * 2^1 = 2000ms (2 seconds)
- Attempt 2: 1000ms * 2^2 = 4000ms (4 seconds)

## Logging

### Retry Attempt Logging
```
D/RetryPolicyManager: Retry attempt 1/3 for fetch-products - delaying 1000ms - error: Network timeout
D/RetryPolicyManager: Retry attempt 2/3 for fetch-products - delaying 2000ms - error: Network timeout
D/RetryPolicyManager: Retry attempt 3/3 for fetch-products - delaying 4000ms - error: Network timeout
```

### Successful Retry Logging
```
I/RetryPolicyManager: Retry successful for fetch-products after 2 attempt(s)
```

### Failure Logging
```
W/RetryPolicyManager: All retry attempts exhausted for fetch-products after 4 attempts - error: Network timeout
```

### Circuit Breaker Logging
```
W/RetryPolicyManager: Circuit breaker open for fetch-products - rejecting request
```

## Integration Examples

### Repository Layer
```kotlin
class ProductRepository @Inject constructor(
    private val retryPolicyManager: RetryPolicyManager,
    private val api: ProductApi
) {
    suspend fun fetchProducts(): List<Product> {
        return retryPolicyManager.executeWithRetry(
            key = "fetch-products",
            policy = RetryPolicy.DEFAULT
        ) {
            api.getProducts()
        }.getOrThrow()
    }
}
```

### ViewModel Layer
```kotlin
class ProductViewModel @Inject constructor(
    private val repository: ProductRepository,
    private val errorHandler: ErrorHandler
) : ViewModel() {
    
    fun loadProducts() {
        viewModelScope.launch {
            try {
                val products = repository.fetchProducts()
                _state.value = ProductState.Success(products)
            } catch (e: Exception) {
                val result = errorHandler.handle(
                    error = e,
                    operationName = "LoadProducts",
                    recoveryStrategy = RetryRecoveryStrategy(
                        retryPolicyManager = retryPolicyManager,
                        maxAttempts = 3,
                        retryAction = { repository.fetchProducts() }
                    )
                )
                
                if (!result.recovered) {
                    _state.value = ProductState.Error(result.userMessage)
                }
            }
        }
    }
    
    fun retryLoadProducts() {
        loadProducts()
    }
}
```

### UI Layer
```kotlin
@Composable
fun ProductScreen(viewModel: ProductViewModel) {
    val state by viewModel.state.collectAsState()
    
    when (state) {
        is ProductState.Loading -> LoadingIndicator()
        is ProductState.Success -> ProductList(state.products)
        is ProductState.Error -> ErrorState(
            title = "Failed to load products",
            message = state.message,
            onRetry = { viewModel.retryLoadProducts() }
        )
    }
}
```

## Testing

Comprehensive unit tests are provided in:
- `app/src/test/java/com/rio/rostry/data/resilience/RetryPolicyManagerTest.kt`

Tests cover:
- Retry with transient errors
- No retry with client errors
- Exponential backoff timing (1s, 2s, 4s)
- Max retry limit
- Successful retry after failures
- Circuit breaker integration
- Retry budget exhaustion

## Requirements Validation

**Validates Requirements**:
- 16.1: Retry up to 3 times on transient errors ✓
- 16.2: Exponential backoff with 1s, 2s, 4s delays ✓
- 16.3: Retry on HTTP 408, 429, 500, 502, 503, 504 ✓
- 16.4: No retry on client errors 400-499 (except 408, 429) ✓
- 16.5: Display error message after exhausting retries ✓
- 16.6: Log each retry attempt with attempt number and delay ✓
- 16.7: Log successful retries ✓
- 16.8: Manual retry via UI button ✓

## Performance Considerations

1. **Circuit Breaker**: Prevents cascading failures by opening after 5 consecutive failures
2. **Retry Budget**: Limits total retries per endpoint to prevent resource exhaustion
3. **Jitter**: Optional jitter can be enabled to prevent thundering herd (disabled by default for predictable delays)
4. **Timeout**: Each retry respects the underlying operation timeout

## Best Practices

1. **Use appropriate retry policies**: Choose the right policy for your use case (DEFAULT, CRITICAL, ANALYTICS, BACKGROUND)
2. **Log retry attempts**: All retry attempts are automatically logged for debugging
3. **Provide user feedback**: Use ErrorState components to show retry buttons
4. **Handle circuit breaker**: Check circuit breaker status before critical operations
5. **Monitor retry metrics**: Track retry success rates and circuit breaker state
