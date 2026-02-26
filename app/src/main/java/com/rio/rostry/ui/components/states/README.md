# Standard Loading and Empty State Components

This package provides standardized, reusable components for displaying loading, empty, and error states across the ROSTRY application. These components ensure consistent user experience and meet all requirements for loading and empty state consistency.

## Components

### 1. LoadingIndicator

Standard loading indicator with consistent styling.

**Features:**
- Circular progress indicator with Material Design 3 styling
- Optional message display
- Ensures removal within 100ms of data arrival
- Compact variant for inline use
- Full-screen overlay variant

**Usage:**

```kotlin
// Basic loading indicator
LoadingIndicator(message = "Loading products...")

// Compact loading indicator (for buttons, inline use)
CompactLoadingIndicator(message = "Saving...")

// Full-screen overlay
if (isLoading) {
    LoadingOverlay(message = "Processing...")
}
```

**Requirements Validated:** 15.1, 15.3, 15.8

---

### 2. EmptyState

Standard empty state component with contextual illustrations.

**Features:**
- Contextual icon for visual feedback
- Title and description text
- Optional primary and secondary action buttons
- Fade-in animation for smooth transitions
- Predefined variants for common scenarios

**Usage:**

```kotlin
// Custom empty state
EmptyState(
    icon = Icons.Default.ShoppingCart,
    title = "No products yet",
    description = "Start adding products to see them here",
    actionLabel = "Add Product",
    onAction = { /* navigate to add product */ }
)

// Predefined empty states
EmptyProductsState(onAddProduct = { /* action */ })
EmptySearchState(query = "chicken")
EmptyNotificationsState()
EmptyOrdersState(onBrowseProducts = { /* action */ })
EmptyMessagesState()
EmptyFavoritesState(onBrowseProducts = { /* action */ })
```

**Requirements Validated:** 15.2, 15.4, 15.6, 15.7

---

### 3. ErrorState

Standard error state component with retry functionality.

**Features:**
- Error icon with Material Design 3 styling
- Clear error title and message
- Retry button with icon
- Optional secondary action
- Predefined variants for common error scenarios

**Usage:**

```kotlin
// Custom error state
ErrorState(
    title = "Failed to load products",
    message = "Unable to connect. Please check your internet connection.",
    onRetry = { viewModel.retryLoadProducts() }
)

// Predefined error states
NetworkErrorState(onRetry = { viewModel.retry() })
ServiceUnavailableState(
    serviceName = "Product service",
    onRetry = { viewModel.retry() }
)
PermissionDeniedState(
    onRetry = { viewModel.requestPermission() },
    onGoToSettings = { /* navigate to settings */ }
)
DataLoadErrorState(
    dataType = "products",
    onRetry = { viewModel.retry() }
)
TimeoutErrorState(onRetry = { viewModel.retry() })
ServerErrorState(onRetry = { viewModel.retry() })
```

**Requirements Validated:** 15.5

---

## Integration Pattern

### ViewModel State Management

```kotlin
data class ProductsUiState(
    val isLoading: Boolean = false,
    val products: List<Product> = emptyList(),
    val error: String? = null
)

class ProductsViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ProductsUiState())
    val uiState = _uiState.asStateFlow()
    
    fun loadProducts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val products = repository.getProducts()
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        products = products
                    )
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }
}
```

### Screen Implementation

```kotlin
@Composable
fun ProductsScreen(
    viewModel: ProductsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold(
        topBar = { /* app bar */ }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when {
                // Show loading state
                uiState.isLoading -> {
                    LoadingIndicator(message = "Loading products...")
                }
                
                // Show error state
                uiState.error != null -> {
                    ErrorState(
                        title = "Failed to load products",
                        message = uiState.error!!,
                        onRetry = { viewModel.loadProducts() }
                    )
                }
                
                // Show empty state
                uiState.products.isEmpty() -> {
                    EmptyProductsState(
                        onAddProduct = { /* navigate to add product */ }
                    )
                }
                
                // Show content
                else -> {
                    ProductsList(products = uiState.products)
                }
            }
        }
    }
}
```

---

## Best Practices

### 1. State Transitions

Always ensure smooth transitions between states:

```kotlin
// ✅ Good: Clear state transitions
_uiState.update { it.copy(isLoading = true, error = null) }
// ... load data ...
_uiState.update { it.copy(isLoading = false, products = data) }

// ❌ Bad: Unclear state
_uiState.value = ProductsUiState(products = data)
```

### 2. Error Messages

Provide specific, actionable error messages:

```kotlin
// ✅ Good: Specific error
ErrorState(
    title = "Failed to load products",
    message = "Unable to connect. Please check your internet connection.",
    onRetry = { viewModel.retry() }
)

// ❌ Bad: Generic error
ErrorState(
    title = "Error",
    message = "Something went wrong",
    onRetry = { viewModel.retry() }
)
```

### 3. Loading Indicators

Remove loading indicators promptly (within 100ms):

```kotlin
// ✅ Good: Immediate state update
viewModelScope.launch {
    _uiState.update { it.copy(isLoading = true) }
    val data = repository.getData()
    _uiState.update { it.copy(isLoading = false, data = data) }
}

// ❌ Bad: Delayed state update
viewModelScope.launch {
    _uiState.update { it.copy(isLoading = true) }
    val data = repository.getData()
    delay(500) // Don't add artificial delays!
    _uiState.update { it.copy(isLoading = false, data = data) }
}
```

### 4. Empty State Actions

Always provide actionable buttons when appropriate:

```kotlin
// ✅ Good: Actionable empty state
EmptyState(
    icon = Icons.Default.ShoppingCart,
    title = "No products yet",
    description = "Start adding products to build your inventory",
    actionLabel = "Add Product",
    onAction = { /* navigate to add product */ }
)

// ❌ Bad: No action when one is appropriate
EmptyState(
    icon = Icons.Default.ShoppingCart,
    title = "No products yet",
    description = "Start adding products to build your inventory"
    // Missing actionable button!
)
```

---

## Testing

### Unit Tests

```kotlin
@Test
fun `LoadingIndicator displays message correctly`() {
    composeTestRule.setContent {
        LoadingIndicator(message = "Loading...")
    }
    
    composeTestRule.onNodeWithText("Loading...").assertIsDisplayed()
}

@Test
fun `EmptyState displays action button when provided`() {
    var actionClicked = false
    
    composeTestRule.setContent {
        EmptyState(
            icon = Icons.Default.ShoppingCart,
            title = "No products",
            actionLabel = "Add Product",
            onAction = { actionClicked = true }
        )
    }
    
    composeTestRule.onNodeWithText("Add Product").performClick()
    assertTrue(actionClicked)
}

@Test
fun `ErrorState calls retry callback`() {
    var retryClicked = false
    
    composeTestRule.setContent {
        ErrorState(
            title = "Error",
            message = "Failed to load",
            onRetry = { retryClicked = true }
        )
    }
    
    composeTestRule.onNodeWithText("Try Again").performClick()
    assertTrue(retryClicked)
}
```

---

## Accessibility

All components are designed with accessibility in mind:

- **Content descriptions** for all icons
- **Minimum touch target sizes** (48dp) for all buttons
- **Sufficient color contrast** (4.5:1) for all text
- **Screen reader support** with semantic descriptions
- **Dynamic text sizing** support

---

## Migration Guide

### Migrating from Old Components

If you're using old loading/empty state components, here's how to migrate:

**Old:**
```kotlin
// Old LoadingState
LoadingState(message = "Loading...")

// Old EmptyState
EmptyState(
    title = "No products",
    subtitle = "Add products to see them here",
    icon = Icons.Default.ShoppingCart
)

// Old ErrorState
ErrorState(
    error = "Failed to load",
    retryAction = { viewModel.retry() }
)
```

**New:**
```kotlin
// New LoadingIndicator
LoadingIndicator(message = "Loading...")

// New EmptyState
EmptyState(
    icon = Icons.Default.ShoppingCart,
    title = "No products",
    description = "Add products to see them here"
)

// New ErrorState
ErrorState(
    title = "Failed to load products",
    message = "Unable to connect. Please check your internet connection.",
    onRetry = { viewModel.retry() }
)
```

---

## Requirements Coverage

| Requirement | Component | Status |
|-------------|-----------|--------|
| 15.1 - Display loading indicator when fetching data | LoadingIndicator | ✅ |
| 15.2 - Display empty state when no data available | EmptyState | ✅ |
| 15.3 - Consistent loading indicator styling | LoadingIndicator | ✅ |
| 15.4 - Consistent empty state styling | EmptyState | ✅ |
| 15.5 - Transition from loading to error state | ErrorState | ✅ |
| 15.6 - Contextual illustrations in empty states | EmptyState | ✅ |
| 15.7 - Actionable buttons in empty states | EmptyState | ✅ |
| 15.8 - Remove loading indicators within 100ms | LoadingIndicator | ✅ |

---

## Support

For questions or issues with these components, please refer to:
- Design document: `.kiro/specs/production-readiness-gap-filling/design.md`
- Requirements: `.kiro/specs/production-readiness-gap-filling/requirements.md`
- Task list: `.kiro/specs/production-readiness-gap-filling/tasks.md`
