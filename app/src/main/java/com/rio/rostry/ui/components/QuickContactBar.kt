package com.rio.rostry.ui.components

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Quick Contact Bar - Three-button contact bar for product/seller pages.
 * - Call: Opens phone dialer
 * - WhatsApp: Opens WhatsApp chat
 * - Chat: Navigates to in-app messaging
 */
@Composable
fun QuickContactBar(
    phoneNumber: String?,
    sellerId: String,
    onInAppChat: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Call Button
            ContactButton(
                icon = Icons.Default.Call,
                label = "Call",
                containerColor = Color(0xFF4CAF50),
                enabled = !phoneNumber.isNullOrBlank(),
                onClick = {
                    phoneNumber?.let { phone ->
                        openDialer(context, phone)
                    }
                }
            )

            // WhatsApp Button
            ContactButton(
                icon = Icons.Default.Message,
                label = "WhatsApp",
                containerColor = Color(0xFF25D366),
                enabled = !phoneNumber.isNullOrBlank(),
                onClick = {
                    phoneNumber?.let { phone ->
                        openWhatsApp(context, phone)
                    }
                }
            )

            // In-App Chat Button
            ContactButton(
                icon = Icons.Default.Chat,
                label = "Chat",
                containerColor = MaterialTheme.colorScheme.primary,
                enabled = true,
                onClick = { onInAppChat(sellerId) }
            )
        }
    }
}

@Composable
private fun ContactButton(
    icon: ImageVector,
    label: String,
    containerColor: Color,
    enabled: Boolean,
    onClick: () -> Unit
) {
    FilledTonalButton(
        onClick = onClick,
        enabled = enabled,
        colors = ButtonDefaults.filledTonalButtonColors(
            containerColor = containerColor.copy(alpha = 0.15f),
            contentColor = containerColor
        ),
        shape = RoundedCornerShape(12.dp),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(18.dp)
        )
        Spacer(Modifier.width(6.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}

/**
 * Opens the phone dialer with the given number.
 */
private fun openDialer(context: Context, phoneNumber: String) {
    val cleanNumber = phoneNumber.replace(" ", "").replace("-", "")
    val intent = Intent(Intent.ACTION_DIAL).apply {
        data = Uri.parse("tel:$cleanNumber")
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // Handle error silently
    }
}

/**
 * Opens WhatsApp with the given phone number.
 */
private fun openWhatsApp(context: Context, phoneNumber: String) {
    // Ensure number starts with country code (default to India +91)
    val cleanNumber = phoneNumber.replace(" ", "").replace("-", "")
    val formattedNumber = if (cleanNumber.startsWith("+")) {
        cleanNumber.substring(1)
    } else if (cleanNumber.startsWith("91")) {
        cleanNumber
    } else {
        "91$cleanNumber"
    }

    val intent = Intent(Intent.ACTION_VIEW).apply {
        data = Uri.parse("https://wa.me/$formattedNumber")
    }
    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // Handle error silently - WhatsApp not installed
    }
}
