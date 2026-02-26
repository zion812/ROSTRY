# Migration Guide: Standardized State Components

This guide helps you migrate existing screens to use the new standardized loading, empty, and error state components.

## Overview

The new state components are located in `com.rio.rostry.ui.components.states` and provide:
- **LoadingIndicator**: Consistent loading states
- **EmptyState**: Consistent empty states with contextual illustrations
- **ErrorState**: Consistent error states with retry functionality

## Quick Migration Checklist

- [ ] Replace `CircularProgressIndicator` with `LoadingIndicator`
- [ ] Replace custom empty state UI with `EmptyState` or predefined variants
- [ ] Replace custom error UI with `ErrorState` or predefined variants
- [ ] Ensure loading indicators are removed within 100ms of data arrival
- [ ] Add actionable buttons to empty states where appropriate
- [ ] Use specific error messages instead of generic ones

## Migration Patterns

### Pattern 1: Simple Loading State

**Before:**
```kotlin
if (uiState.isLoading) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}
```

**After:**
```kotlin
if (uiState.isLoading) {
    LoadingIndicator(message = "Loading products...")
}
```

---

### Pattern 2: Inline Loading (Buttons)

**Before:**
```kotlin
Button(onClick = onSave, enabled = !isSaving) {
    if (isSaving) {
        CircularProgressIndicator(Modifier.size(20.dp), strokeWidth = 2.dp)
        Spacer(Modifier.width(8.dp))
        Text("Saving...")
    } else {
        Text("Save")
    }
}
```

**After:**
```kotlin
Button(onClick = onSave, enabled = !isSaving) {
    if (isSaving) {
        CompactLoadingIndicator(message = "Saving...")
    } else {
        Text("Save")
    }
}
```

---

### Pattern 3: Empty State

**Before:**
```kotlin
if (uiState.items.isEmpty() && !uiState.isLoading) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(Icons.Default.Inbox, contentDescription = null)
            Text("No items found")
        }
    }
}
```

**After:**
```kotlin
if (uiState.items.isEmpty() && !uiState.isLoading) {
    EmptyState(
        icon = Icons.Default.Inbox,
        title = "No items yet",
        description = "Items will appear here once added",
        actionLabel = "Add Item",
        onAction = { /* navigate to add item */ }
    )
}
```

---

### Pattern 4: Error State

**Before:**
```kotlin
if (uiState.error != null) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Error: ${uiState.error}")
            Button(onClick = onRetry) {
                Text("Retry")
            }
        }
    }
}
```

**After:**
```kotlin
if (uiState.error != null) {
    ErrorState(
        title = "Failed to load items",
        message = uiState.error,
        onRetry = onRetry
    )
}
```

---

### Pattern 5: Network Error

**Before:**
```kotlin
if (uiState.error is NetworkException) {
    // Custom network error UI
}
```

**After:**
```kotlin
if (uiState.error is NetworkException) {
    NetworkErrorState(onRetry = onRetry)
}
```

---

### Pattern 6: Complete Screen Pattern

**Before:**
```kotlin
@Composable
fun ProductsScreen(viewModel: ProductsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold { padding ->
        Box(Modifier.padding(padding)) {
            when {
                uiState.isLoading -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                uiState.error != null -> {
                    Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Error: ${uiState.error}")
                        Button(onClick = { viewModel.retry() }) {
                            Text("Retry")
                        }
                    }
                }
                uiState.products.isEmpty() -> {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("No products")
                    }
                }
                else -> {
                    LazyColumn {
                        items(uiState.products) { product ->
                            ProductCard(product)
                        }
                    }
                }
            }
        }
    }
}
```

**After:**
```kotlin
@Composable
fun ProductsScreen(viewModel: ProductsViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    
    Scaffold { padding ->
        Box(Modifier.padding(padding)) {
            when {
                uiState.isLoading -> {
                    LoadingIndicator(message = "Loading products...")
                }
                uiState.error != null -> {
                    ErrorState(
                        title = "Failed to load products",
                        message = uiState.error,
                        onRetry = { viewModel.retry() }
                    )
                }
                uiState.products.isEmpty() -> {
                    EmptyProductsState(
                        onAddProduct = { /* navigate to add product */ }
                    )
                }
                else -> {
                    LazyColumn {
                        items(uiState.products) { product ->
                            ProductCard(product)
                        }
                    }
                }
            }
        }
    }
}
```

---

## Predefined Components Reference

### Empty State Variants

```kotlin
// Products
EmptyProductsState(onAddProduct = { /* action */ })

// Search results
EmptySearchState(query = "search term")

// Notifications
EmptyNotificationsState()

// Orders
EmptyOrdersState(onBrowseProducts = { /* action */ })

// Messages
EmptyMessagesState()

// Favorites
EmptyFavoritesState(onBrowseProducts = { /* action */ })
```

### Error State Variants

```kotlin
// Network error
NetworkErrorState(onRetry = { /* retry */ })

// Service unavailable
ServiceUnavailableState(
    serviceName = "Product service",
    onRetry = { /* retry */ }
)

// Permission denied
PermissionDeniedState(
    onRetry = { /* request permission */ },
    onGoToSettings = { /* navigate to settings */ }
)

// Data load error
DataLoadErrorState(
    dataType = "products",
    onRetry = { /* retry */ }
)

// Timeout error
TimeoutErrorState(onRetry = { /* retry */ })

// Server error
ServerErrorState(onRetry = { /* retry */ })
```

---

## Screen-by-Screen Migration Status

### High Priority Screens (User-Facing)

- [ ] `ProductsScreen` - Product listings
- [ ] `SearchScreen` - Search functionality
- [ ] `OrdersScreen` - Order history
- [ ] `NotificationsScreen` - Notifications
- [ ] `ProfileScreen` - User profile
- [ ] `FavoritesScreen` - Wishlist/favorites
- [ ] `MessagesScreen` - Messaging

### Medium Priority Screens

- [ ] `TransferCreateScreen` - Transfer creation
- [ ] `TransferVerificationScreen` - Transfer verification
- [ ] `SyncConflictScreen` - Sync conflicts
- [ ] `TraceabilityScreen` - Product traceability
- [ ] `GalleryScreen` - Media gallery
- [ ] `BackupRestoreScreen` - Backup/restore
- [ ] `AddressManagementScreen` - Address management

### Admin Screens

- [ ] `UserManagementScreen` - User management
- [ ] `AdminUserDetailScreen` - User details
- [ ] `InvoicesScreen` - Invoice management
- [ ] `ModerationScreen` - Content moderation

---

## Testing After Migration

After migrating a screen, verify:

1. **Loading State**
   - [ ] Loading indicator appears when fetching data
   - [ ] Loading indicator disappears within 100ms of data arrival
   - [ ] Loading message is contextual and clear

2. **Empty State**
   - [ ] Empty state appears when no data is available
   - [ ] Icon is contextual and appropriate
   - [ ] Title and description are clear
   - [ ] Action button is present when appropriate
   - [ ] Action button works correctly

3. **Error State**
   - [ ] Error state appears when loading fails
   - [ ] Error message is specific and actionable
   - [ ] Retry button works correctly
   - [ ] Error transitions from loading state smoothly

4. **Content State**
   - [ ] Content displays correctly after loading
   - [ ] Transitions between states are smooth
   - [ ] No flickering or layout shifts

---

## Common Issues and Solutions

### Issue 1: Loading indicator doesn't disappear quickly

**Problem:**
```kotlin
viewModelScope.launch {
    _uiState.update { it.copy(isLoading = true) }
    val data = repository.getData()
    delay(500) // ❌ Artificial delay
    _uiState.update { it.copy(isLoading = false, data = data) }
}
```

**Solution:**
```kotlin
viewModelScope.launch {
    _uiState.update { it.copy(isLoading = true) }
    val data = repository.getData()
    _uiState.update { it.copy(isLoading = false, data = data) } // ✅ Immediate update
}
```

### Issue 2: Empty state missing action button

**Problem:**
```kotlin
EmptyState(
    icon = Icons.Default.ShoppingCart,
    title = "No products yet",
    description = "Add products to see them here"
    // ❌ Missing action button
)
```

**Solution:**
```kotlin
EmptyState(
    icon = Icons.Default.ShoppingCart,
    title = "No products yet",
    description = "Add products to see them here",
    actionLabel = "Add Product", // ✅ Action button added
    onAction = { /* navigate to add product */ }
)
```

### Issue 3: Generic error message

**Problem:**
```kotlin
ErrorState(
    title = "Error", // ❌ Generic
    message = "Something went wrong", // ❌ Not actionable
    onRetry = onRetry
)
```

**Solution:**
```kotlin
ErrorState(
    title = "Failed to load products", // ✅ Specific
    message = "Unable to connect. Please check your internet connection.", // ✅ Actionable
    onRetry = onRetry
)
```

---

## Import Statements

Add these imports to your screen files:

```kotlin
import com.rio.rostry.ui.components.states.LoadingIndicator
import com.rio.rostry.ui.components.states.CompactLoadingIndicator
import com.rio.rostry.ui.components.states.EmptyState
import com.rio.rostry.ui.components.states.EmptyProductsState
import com.rio.rostry.ui.components.states.EmptySearchState
import com.rio.rostry.ui.components.states.ErrorState
import com.rio.rostry.ui.components.states.NetworkErrorState
import com.rio.rostry.ui.components.states.ServiceUnavailableState
```

---

## Questions?

For more information, see:
- `README.md` - Component documentation and usage
- `Examples.kt` - Complete example implementations
- Design document: `.kiro/specs/production-readiness-gap-filling/design.md`
