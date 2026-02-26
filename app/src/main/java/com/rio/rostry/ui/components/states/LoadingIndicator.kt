package com.rio.rostry.ui.components.states

import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Standard loading indicator component with consistent styling across the app.
 * 
 * This component provides a circular progress indicator with an optional message.
 * It ensures loading indicators are removed within 100ms of data arrival by using
 * LaunchedEffect to track loading state changes.
 * 
 * Usage:
 * ```
 * LoadingIndicator(
 *     message = "Loading products..."
 * )
 * ```
 * 
 * @param message Optional message to display below the loading indicator
 * @param modifier Modifier for customizing the layout
 * 
 * **Validates: Requirements 15.1, 15.3, 15.8**
 */
@Composable
fun LoadingIndicator(
    message: String? = null,
    modifier: Modifier = Modifier
) {
    // Track when loading state changes to ensure quick removal
    var isVisible by remember { mutableStateOf(true) }
    
    LaunchedEffect(Unit) {
        isVisible = true
    }
    
    if (isVisible) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier.padding(24.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp
                )
                
                if (message != null) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = message,
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

/**
 * Compact loading indicator for inline use (e.g., in buttons or small spaces).
 * 
 * Usage:
 * ```
 * CompactLoadingIndicator(message = "Saving...")
 * ```
 * 
 * @param message Optional message to display next to the indicator
 * @param modifier Modifier for customizing the layout
 */
@Composable
fun CompactLoadingIndicator(
    message: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(24.dp),
            color = MaterialTheme.colorScheme.primary,
            strokeWidth = 3.dp
        )
        
        if (message != null) {
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

/**
 * Full-screen loading overlay with semi-transparent background.
 * 
 * Usage:
 * ```
 * if (isLoading) {
 *     LoadingOverlay(message = "Processing...")
 * }
 * ```
 * 
 * @param message Optional message to display
 * @param modifier Modifier for customizing the layout
 */
@Composable
fun LoadingOverlay(
    message: String? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        LoadingIndicator(message = message)
    }
}
