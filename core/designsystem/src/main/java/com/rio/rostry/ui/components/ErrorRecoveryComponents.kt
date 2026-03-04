package com.rio.rostry.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import androidx.core.content.ContextCompat
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

enum class ErrorCategory {
    NETWORK,
    VALIDATION,
    PERMISSION,
    SERVER,
    UNKNOWN
}

data class ErrorDetails(
    val message: String,
    val category: ErrorCategory,
    val canRetry: Boolean = false,
    val canNavigateToFix: Boolean = false,
    val canContactSupport: Boolean = false,
    val fixNavigation: (() -> Unit)? = null,
    val retryAction: (() -> Unit)? = null,
    val contactSupportAction: (() -> Unit)? = null
)

@Composable
fun ErrorRecoveryBanner(
    errorDetails: ErrorDetails,
    onDismiss: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var isVisible by remember { mutableStateOf(true) }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
        exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
    ) {
        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
            modifier = modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Error banner: ${errorDetails.message}" }
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = getErrorIcon(errorDetails.category),
                        contentDescription = "Error icon",
                        tint = MaterialTheme.colorScheme.onErrorContainer
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = errorDetails.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.weight(1f)
                    )
                }
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    if (errorDetails.canContactSupport) {
                        TextButton(onClick = {
                            errorDetails.contactSupportAction?.invoke()
                            scope.launch {
                                delay(500)
                                isVisible = false
                                onDismiss?.invoke()
                            }
                        }) {
                            Text("Contact Support")
                        }
                        Spacer(Modifier.width(8.dp))
                    }
                    if (errorDetails.canNavigateToFix) {
                        TextButton(onClick = {
                            errorDetails.fixNavigation?.invoke()
                            scope.launch {
                                delay(500)
                                isVisible = false
                                onDismiss?.invoke()
                            }
                        }) {
                            Text("Fix Now")
                        }
                        Spacer(Modifier.width(8.dp))
                    }
                    if (errorDetails.canRetry) {
                        Button(onClick = {
                            errorDetails.retryAction?.invoke()
                            scope.launch {
                                delay(500)
                                isVisible = false
                                onDismiss?.invoke()
                            }
                        }) {
                            Text("Retry")
                        }
                    }
                    onDismiss?.let {
                        Spacer(Modifier.width(8.dp))
                        TextButton(onClick = {
                            isVisible = false
                            it()
                        }) {
                            Text("Dismiss")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RetryButton(
    onRetry: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier,
    buttonText: String = "Retry"
) {
    Button(
        onClick = { if (!isLoading) onRetry() },
        enabled = !isLoading,
        modifier = modifier.semantics { contentDescription = "Retry button" }
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = MaterialTheme.colorScheme.onPrimary
            )
            Spacer(Modifier.width(8.dp))
        }
        Text(buttonText)
    }
}

@Composable
fun OfflineErrorState(
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Filled.Warning,
            contentDescription = "Offline error icon",
            modifier = Modifier
                .size(64.dp)
                .graphicsLayer(alpha = alpha),
            tint = MaterialTheme.colorScheme.error
        )
        Spacer(Modifier.height(16.dp))
        Text(
            text = "You're offline",
            style = MaterialTheme.typography.titleLarge,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = "Please check your internet connection and try again.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
        onRetry?.let { action ->
            Spacer(Modifier.height(24.dp))
            RetryButton(onRetry = action, isLoading = false)
        }
    }
}

@Composable
fun ErrorSnackbar(
    errorDetails: ErrorDetails,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var isVisible by remember { mutableStateOf(true) }

    if (isVisible) {
        Popup(
            alignment = Alignment.BottomCenter,
            properties = PopupProperties(focusable = false)
        ) {
            AnimatedVisibility(
                visible = true,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .semantics { contentDescription = "Error snackbar: ${errorDetails.message}" }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = errorDetails.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            if (errorDetails.canRetry) {
                                TextButton(onClick = {
                                    errorDetails.retryAction?.invoke()
                                    scope.launch {
                                        delay(500)
                                        isVisible = false
                                        onDismiss()
                                    }
                                }) {
                                    Text("Retry")
                                }
                                Spacer(Modifier.width(8.dp))
                            }
                            TextButton(onClick = {
                                isVisible = false
                                onDismiss()
                            }) {
                                Text("Dismiss")
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ErrorAnimation(
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Filled.Error
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shake")
    val shakeOffset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 10f,
        animationSpec = infiniteRepeatable(
            animation = keyframes {
                durationMillis = 500
                0f at 0
                -10f at 100
                10f at 200
                -10f at 300
                10f at 400
                0f at 500
            },
            repeatMode = RepeatMode.Restart
        ),
        label = "shake_offset"
    )

    Icon(
        imageVector = icon,
        contentDescription = "Error animation",
        modifier = modifier
            .size(48.dp)
            .offset(x = shakeOffset.dp),
        tint = MaterialTheme.colorScheme.error
    )
}

fun reportErrorToSupport(
    context: android.content.Context,
    errorDetails: ErrorDetails,
    additionalInfo: String = ""
) {
    // Implementation for sending error logs to support
    // This could integrate with Firebase Crashlytics or a custom logging service
    val intent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(android.content.Intent.EXTRA_EMAIL, arrayOf("support@rostry.com"))
        putExtra(android.content.Intent.EXTRA_SUBJECT, "Error Report: ${errorDetails.category}")
        putExtra(
            android.content.Intent.EXTRA_TEXT,
            """
            Error Category: ${errorDetails.category}
            Message: ${errorDetails.message}
            Additional Info: $additionalInfo
            Device: ${android.os.Build.MODEL}
            Android Version: ${android.os.Build.VERSION.RELEASE}
            App Version: ${context.packageManager.getPackageInfo(context.packageName, 0).versionName}
            """.trimIndent()
        )
    }
    ContextCompat.startActivity(context, intent, null)
}

private fun getErrorIcon(category: ErrorCategory): ImageVector {
    return when (category) {
        ErrorCategory.NETWORK -> Icons.Filled.Warning
        ErrorCategory.VALIDATION -> Icons.Filled.Error
        ErrorCategory.PERMISSION -> Icons.Filled.Warning
        ErrorCategory.SERVER -> Icons.Filled.Error
        ErrorCategory.UNKNOWN -> Icons.Filled.Error
    }
}