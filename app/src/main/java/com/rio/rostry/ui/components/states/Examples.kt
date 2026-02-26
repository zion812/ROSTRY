@file:OptIn(ExperimentalMaterial3Api::class)

package com.rio.rostry.ui.components.states

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Example implementations showing how to use the standard state components.
 * 
 * These examples demonstrate best practices for integrating loading, empty,
 * and error states into your screens.
 */

// ============================================================================
// Example 1: Simple List Screen with Loading, Empty, and Error States
// ============================================================================

/**
 * Example UI state for a products list screen.
 */
data class ProductsUiState(
    val isLoading: Boolean = false,
    val products: List<String> = emptyList(),
    val error: String? = null
)

/**
 * Example screen showing products with proper state handling.
 * 
 * This demonstrates the recommended pattern for handling loading,
 * empty, and error states in a list screen.
 */
@Composable
fun ExampleProductsScreen(
    uiState: ProductsUiState,
    onRetry: () -> Unit,
    onAddProduct: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Products") }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when {
                // Loading state - show while fetching data
                uiState.isLoading -> {
                    LoadingIndicator(message = "Loading products...")
                }
                
                // Error state - show when loading fails
                uiState.error != null -> {
                    ErrorState(
                        title = "Failed to load products",
                        message = uiState.error,
                        onRetry = onRetry
                    )
                }
                
                // Empty state - show when no data available
                uiState.products.isEmpty() -> {
                    EmptyProductsState(onAddProduct = onAddProduct)
                }
                
                // Content state - show the actual data
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.products) { product ->
                            Card {
                                Text(
                                    text = product,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// Example 2: Search Screen with Different Empty States
// ============================================================================

/**
 * Example UI state for a search screen.
 */
data class SearchUiState(
    val isLoading: Boolean = false,
    val query: String = "",
    val results: List<String> = emptyList(),
    val error: String? = null,
    val hasSearched: Boolean = false
)

/**
 * Example search screen with different empty states.
 * 
 * This demonstrates how to handle different empty state scenarios:
 * - Initial state (no search performed)
 * - No results found (search performed but empty)
 */
@Composable
fun ExampleSearchScreen(
    uiState: SearchUiState,
    onSearch: (String) -> Unit,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Search") }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Search bar
            OutlinedTextField(
                value = uiState.query,
                onValueChange = onSearch,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                placeholder = { Text("Search products...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                }
            )
            
            // Content area
            Box(modifier = Modifier.weight(1f)) {
                when {
                    // Loading state
                    uiState.isLoading -> {
                        LoadingIndicator(message = "Searching...")
                    }
                    
                    // Error state
                    uiState.error != null -> {
                        ErrorState(
                            title = "Search failed",
                            message = uiState.error,
                            onRetry = onRetry
                        )
                    }
                    
                    // Empty state - no search performed yet
                    !uiState.hasSearched -> {
                        EmptyState(
                            icon = Icons.Default.Search,
                            title = "Start searching",
                            description = "Enter a search term to find products"
                        )
                    }
                    
                    // Empty state - no results found
                    uiState.results.isEmpty() -> {
                        EmptySearchState(query = uiState.query)
                    }
                    
                    // Content state - show results
                    else -> {
                        LazyColumn(
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(uiState.results) { result ->
                                Card {
                                    Text(
                                        text = result,
                                        modifier = Modifier.padding(16.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// Example 3: Network-Dependent Screen with Specific Error States
// ============================================================================

/**
 * Example UI state for a network-dependent screen.
 */
data class NetworkDataUiState(
    val isLoading: Boolean = false,
    val data: List<String> = emptyList(),
    val errorType: ErrorType? = null
)

enum class ErrorType {
    NETWORK, SERVER, TIMEOUT, UNKNOWN
}

/**
 * Example screen with specific error states for different error types.
 * 
 * This demonstrates how to use predefined error state components
 * for common error scenarios.
 */
@Composable
fun ExampleNetworkDataScreen(
    uiState: NetworkDataUiState,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Network Data") }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding)) {
            when {
                // Loading state
                uiState.isLoading -> {
                    LoadingIndicator(message = "Loading data...")
                }
                
                // Specific error states based on error type
                uiState.errorType != null -> {
                    when (uiState.errorType) {
                        ErrorType.NETWORK -> {
                            NetworkErrorState(onRetry = onRetry)
                        }
                        ErrorType.SERVER -> {
                            ServerErrorState(onRetry = onRetry)
                        }
                        ErrorType.TIMEOUT -> {
                            TimeoutErrorState(onRetry = onRetry)
                        }
                        ErrorType.UNKNOWN -> {
                            ErrorState(
                                title = "Something went wrong",
                                message = "An unexpected error occurred. Please try again.",
                                onRetry = onRetry
                            )
                        }
                    }
                }
                
                // Empty state
                uiState.data.isEmpty() -> {
                    EmptyState(
                        icon = Icons.Default.CloudOff,
                        title = "No data available",
                        description = "Data will appear here once available"
                    )
                }
                
                // Content state
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.data) { item ->
                            Card {
                                Text(
                                    text = item,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ============================================================================
// Example 4: Inline Loading States
// ============================================================================

/**
 * Example showing inline loading states for buttons and forms.
 * 
 * This demonstrates how to use CompactLoadingIndicator for
 * inline loading feedback.
 */
@Composable
fun ExampleInlineLoadingScreen(
    isSaving: Boolean,
    onSave: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Form fields
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth()
            )
            
            OutlinedTextField(
                value = "",
                onValueChange = {},
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Save button with inline loading
            Button(
                onClick = onSave,
                enabled = !isSaving,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isSaving) {
                    CompactLoadingIndicator(message = "Saving...")
                } else {
                    Text("Save Changes")
                }
            }
        }
    }
}

// ============================================================================
// Example 5: Multiple Empty States in Tabs
// ============================================================================

/**
 * Example showing different empty states in a tabbed interface.
 * 
 * This demonstrates how to use predefined empty state components
 * for different content types.
 */
@Composable
fun ExampleTabbedScreen(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Activity") }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            // Tabs
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { onTabSelected(0) },
                    text = { Text("Orders") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { onTabSelected(1) },
                    text = { Text("Favorites") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { onTabSelected(2) },
                    text = { Text("Messages") }
                )
            }
            
            // Tab content
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    0 -> EmptyOrdersState(
                        onBrowseProducts = { /* navigate */ }
                    )
                    1 -> EmptyFavoritesState(
                        onBrowseProducts = { /* navigate */ }
                    )
                    2 -> EmptyMessagesState()
                }
            }
        }
    }
}
