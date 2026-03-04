package com.rio.rostry.ui.components.states

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Standard error state component with retry functionality.
 * 
 * This component displays when an error occurs, providing clear feedback
 * with an error icon, title, message, and a retry button. It transitions
 * from loading state to error state when data loading fails.
 * 
 * Usage:
 * ```
 * ErrorState(
 *     title = "Failed to load products",
 *     message = "Unable to connect. Please check your internet connection.",
 *     onRetry = { viewModel.retryLoadProducts() }
 * )
 * ```
 * 
 * @param title Main error title
 * @param message Detailed error message
 * @param icon Error icon (defaults to error outline)
 * @param retryLabel Label for the retry button
 * @param onRetry Callback for retry action
 * @param secondaryActionLabel Optional label for a secondary action
 * @param onSecondaryAction Optional callback for the secondary action
 * @param modifier Modifier for customizing the layout
 * 
 * **Validates: Requirements 15.5**
 */
@Composable
fun ErrorState(
    title: String,
    message: String,
    icon: ImageVector = Icons.Default.ErrorOutline,
    retryLabel: String = "Try Again",
    onRetry: () -> Unit,
    secondaryActionLabel: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(32.dp)
        ) {
            // Error icon
            Icon(
                imageVector = icon,
                contentDescription = title,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Error title
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Error message
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // Retry button
            Button(
                onClick = onRetry,
                modifier = Modifier.height(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Retry",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(retryLabel)
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
 * Predefined error state for network connectivity issues.
 * 
 * Usage:
 * ```
 * NetworkErrorState(onRetry = { viewModel.retry() })
 * ```
 */
@Composable
fun NetworkErrorState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    ErrorState(
        title = "No Connection",
        message = "Unable to connect. Please check your internet connection and try again.",
        icon = Icons.Default.WifiOff,
        onRetry = onRetry,
        modifier = modifier
    )
}

/**
 * Predefined error state for service unavailability.
 * 
 * Usage:
 * ```
 * ServiceUnavailableState(
 *     serviceName = "Product service",
 *     onRetry = { viewModel.retry() }
 * )
 * ```
 */
@Composable
fun ServiceUnavailableState(
    serviceName: String = "This service",
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    ErrorState(
        title = "Service Unavailable",
        message = "$serviceName is temporarily unavailable. Please try again in a few minutes.",
        icon = Icons.Default.CloudOff,
        onRetry = onRetry,
        modifier = modifier
    )
}

/**
 * Predefined error state for permission denied errors.
 * 
 * Usage:
 * ```
 * PermissionDeniedState(
 *     onRetry = { viewModel.retry() },
 *     onGoToSettings = { /* navigate to settings */ }
 * )
 * ```
 */
@Composable
fun PermissionDeniedState(
    onRetry: () -> Unit,
    onGoToSettings: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    ErrorState(
        title = "Permission Required",
        message = "This feature requires additional permissions to work properly.",
        icon = Icons.Default.Lock,
        retryLabel = "Grant Permission",
        onRetry = onRetry,
        secondaryActionLabel = if (onGoToSettings != null) "Go to Settings" else null,
        onSecondaryAction = onGoToSettings,
        modifier = modifier
    )
}

/**
 * Predefined error state for data loading failures.
 * 
 * Usage:
 * ```
 * DataLoadErrorState(
 *     dataType = "products",
 *     onRetry = { viewModel.retry() }
 * )
 * ```
 */
@Composable
fun DataLoadErrorState(
    dataType: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    ErrorState(
        title = "Failed to Load $dataType",
        message = "Something went wrong while loading $dataType. Please try again.",
        onRetry = onRetry,
        modifier = modifier
    )
}

/**
 * Predefined error state for timeout errors.
 * 
 * Usage:
 * ```
 * TimeoutErrorState(onRetry = { viewModel.retry() })
 * ```
 */
@Composable
fun TimeoutErrorState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    ErrorState(
        title = "Request Timed Out",
        message = "The request took too long to complete. Please check your connection and try again.",
        icon = Icons.Default.AccessTime,
        onRetry = onRetry,
        modifier = modifier
    )
}

/**
 * Predefined error state for server errors (5xx).
 * 
 * Usage:
 * ```
 * ServerErrorState(onRetry = { viewModel.retry() })
 * ```
 */
@Composable
fun ServerErrorState(
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    ErrorState(
        title = "Server Error",
        message = "Our servers are experiencing issues. Please try again later.",
        icon = Icons.Default.CloudOff,
        onRetry = onRetry,
        modifier = modifier
    )
}
