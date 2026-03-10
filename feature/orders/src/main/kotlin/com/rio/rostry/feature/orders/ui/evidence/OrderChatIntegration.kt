package com.rio.rostry.ui.order.evidence

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rio.rostry.data.database.entity.OrderQuoteEntity

/**
 * Order Chat Integration Components.
 * Links orders to chat threads for buyer-seller communication.
 */

/**
 * Floating chat button for order screens.
 */
@Composable
fun OrderChatButton(
    quote: OrderQuoteEntity,
    userIsBuilder: Boolean,
    onChatClick: (threadId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val otherPartyName = if (userIsBuilder) "Seller" else "Buyer"
    
    FloatingActionButton(
        onClick = {
            // Generate or fetch existing thread ID for this order
            val threadId = "order_${quote.orderId}"
            onChatClick(threadId)
        },
        modifier = modifier,
        containerColor = Color(0xFF6366F1)
    ) {
        Icon(
            imageVector = Icons.Default.Chat,
            contentDescription = "Chat with $otherPartyName",
            tint = Color.White
        )
    }
}

/**
 * Chat prompt card that appears on order screens.
 */
@Composable
fun OrderChatPromptCard(
    quote: OrderQuoteEntity,
    currentUserId: String,
    unreadMessageCount: Int = 0,
    onChatClick: (threadId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    val isBuyer = quote.buyerId == currentUserId
    val otherPartyName = if (isBuyer) "seller" else "buyer"

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                val threadId = "order_${quote.orderId}"
                onChatClick(threadId)
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (unreadMessageCount > 0) Color(0xFFE0E7FF) else Color(0xFFF9FAFB)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF6366F1)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Forum,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = if (unreadMessageCount > 0) 
                            "$unreadMessageCount new message${if (unreadMessageCount > 1) "s" else ""}" 
                            else "Message the $otherPartyName",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = if (unreadMessageCount > 0) Color(0xFF3730A3) else Color(0xFF374151)
                    )
                    Text(
                        text = getChatSubtitle(quote),
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF6B7280)
                    )
                }
            }
            
            if (unreadMessageCount > 0) {
                Badge(containerColor = Color(0xFFDC2626)) {
                    Text(unreadMessageCount.toString())
                }
            } else {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color(0xFF9CA3AF)
                )
            }
        }
    }
}

private fun getChatSubtitle(quote: OrderQuoteEntity): String {
    return when (quote.status) {
        "DRAFT" -> "Discuss requirements before quoting"
        "SENT", "NEGOTIATING" -> "Negotiate price and terms"
        "BUYER_AGREED", "SELLER_AGREED" -> "Finalize agreement details"
        "LOCKED", "ADVANCE_PENDING" -> "Coordinate payment"
        "PAYMENT_PROOF_SUBMITTED" -> "Confirm payment verification"
        "DISPATCHED", "READY_FOR_PICKUP" -> "Coordinate delivery"
        "DELIVERED" -> "Confirm receipt"
        else -> "Chat about this order"
    }
}

/**
 * Order context header for chat screen.
 * Shows order summary in the chat thread.
 */
@Composable
fun OrderChatHeader(
    quote: OrderQuoteEntity,
    onOrderClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onOrderClick(quote.orderId) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF3F4F6)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Storefront,
                    contentDescription = null,
                    tint = Color(0xFF6366F1),
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = quote.productName,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium
                    )
                    Text(
                        text = "${quote.quantity.toInt()} ${quote.unit}",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF6B7280)
                    )
                }
            }
            
            OrderStatusBadge(
                status = com.rio.rostry.domain.model.EvidenceOrderStatus.fromString(quote.status)
            )
        }
    }
}

/**
 * Quick action messages for order-related chats.
 */
@Composable
fun QuickChatActions(
    status: String,
    isBuyer: Boolean,
    onSendMessage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val suggestions = getQuickMessages(status, isBuyer)
    
    if (suggestions.isEmpty()) return

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        suggestions.take(3).forEach { message ->
            SuggestionChip(
                onClick = { onSendMessage(message) },
                label = { Text(message, maxLines = 1) }
            )
        }
    }
}

private fun getQuickMessages(status: String, isBuyer: Boolean): List<String> {
    return if (isBuyer) {
        when (status) {
            "DRAFT" -> listOf(
                "Is this available?",
                "What's your best price?",
                "Can you deliver to my location?"
            )
            "SENT" -> listOf(
                "Can you reduce the price?",
                "When can you deliver?",
                "I accept the quote"
            )
            "ADVANCE_PENDING" -> listOf(
                "I've made the payment",
                "Sending payment shortly",
                "Issue with payment"
            )
            "DISPATCHED" -> listOf(
                "When will it arrive?",
                "I'm ready to receive",
                "Change delivery address"
            )
            else -> emptyList()
        }
    } else {
        when (status) {
            "DRAFT" -> listOf(
                "Yes, it's available!",
                "Let me prepare a quote",
                "Need more details"
            )
            "NEGOTIATING" -> listOf(
                "That's my best price",
                "Can do at ₹X",
                "Deal!"
            )
            "PAYMENT_PROOF_SUBMITTED" -> listOf(
                "Checking payment now",
                "Payment verified!",
                "Need clearer screenshot"
            )
            "PREPARING" -> listOf(
                "Order is being prepared",
                "Will dispatch tomorrow",
                "Ready for pickup"
            )
            else -> emptyList()
        }
    }
}

/**
 * Extension to generate order thread ID.
 */
fun generateOrderThreadId(orderId: String): String = "order_$orderId"

/**
 * Extension to check if a thread is order-related.
 */
fun isOrderThread(threadId: String): Boolean = threadId.startsWith("order_")

/**
 * Extension to extract order ID from thread.
 */
fun extractOrderId(threadId: String): String? = 
    if (isOrderThread(threadId)) threadId.removePrefix("order_") else null
