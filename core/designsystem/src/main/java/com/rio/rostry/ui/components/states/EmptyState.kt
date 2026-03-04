package com.rio.rostry.ui.components.states

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Standard empty state component with contextual illustrations.
 * 
 * This component displays when no data is available, providing clear feedback
 * to users with an icon, title, description, and optional action button.
 * 
 * Usage:
 * ```
 * EmptyState(
 *     icon = Icons.Default.ShoppingCart,
 *     title = "No products yet",
 *     description = "Start adding products to see them here",
 *     actionLabel = "Add Product",
 *     onAction = { /* navigate to add product */ }
 * )
 * ```
 * 
 * @param icon Contextual icon representing the empty state
 * @param title Main title describing the empty state
 * @param description Optional detailed description
 * @param actionLabel Optional label for the primary action button
 * @param onAction Optional callback for the primary action
 * @param secondaryActionLabel Optional label for a secondary action
 * @param onSecondaryAction Optional callback for the secondary action
 * @param modifier Modifier for customizing the layout
 * 
 * **Validates: Requirements 15.2, 15.4, 15.6, 15.7**
 */
@Composable
fun EmptyState(
    icon: ImageVector,
    title: String,
    description: String? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    secondaryActionLabel: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    // Fade-in animation for better UX
    val alphaAnim by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 400),
        label = "empty_state_fade_in"
    )
    
    Box(
        modifier = modifier
            .fillMaxSize()
            .alpha(alphaAnim),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Contextual illustration
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            // Description
            if (description != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
            
            // Primary action button
            if (actionLabel != null && onAction != null) {
                Spacer(modifier = Modifier.height(24.dp))
                Button(
                    onClick = onAction,
                    modifier = Modifier.height(48.dp)
                ) {
                    Text(actionLabel)
                }
            }
            
            // Secondary action button
            if (secondaryActionLabel != null && onSecondaryAction != null) {
                Spacer(modifier = Modifier.height(8.dp))
                TextButton(onClick = onSecondaryAction) {
                    Text(secondaryActionLabel)
                }
            }
        }
    }
}

/**
 * Predefined empty state for products/items lists.
 */
@Composable
fun EmptyProductsState(
    onAddProduct: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Default.ShoppingCart,
        title = "No products yet",
        description = "Start adding products to build your inventory",
        actionLabel = if (onAddProduct != null) "Add Product" else null,
        onAction = onAddProduct,
        modifier = modifier
    )
}

/**
 * Predefined empty state for search results.
 */
@Composable
fun EmptySearchState(
    query: String,
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Default.SearchOff,
        title = "No results found",
        description = "We couldn't find anything matching \"$query\". Try a different search term.",
        modifier = modifier
    )
}

/**
 * Predefined empty state for notifications.
 */
@Composable
fun EmptyNotificationsState(
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Default.Notifications,
        title = "No notifications",
        description = "You're all caught up! Check back later for updates.",
        modifier = modifier
    )
}

/**
 * Predefined empty state for orders.
 */
@Composable
fun EmptyOrdersState(
    onBrowseProducts: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Default.ShoppingBag,
        title = "No orders yet",
        description = "Browse products and place your first order",
        actionLabel = if (onBrowseProducts != null) "Browse Products" else null,
        onAction = onBrowseProducts,
        modifier = modifier
    )
}

/**
 * Predefined empty state for messages/inbox.
 */
@Composable
fun EmptyMessagesState(
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Default.MailOutline,
        title = "No messages",
        description = "Your inbox is empty. Messages will appear here.",
        modifier = modifier
    )
}

/**
 * Predefined empty state for favorites/wishlist.
 */
@Composable
fun EmptyFavoritesState(
    onBrowseProducts: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    EmptyState(
        icon = Icons.Default.FavoriteBorder,
        title = "No favorites yet",
        description = "Save products you like to find them easily later",
        actionLabel = if (onBrowseProducts != null) "Browse Products" else null,
        onAction = onBrowseProducts,
        modifier = modifier
    )
}
