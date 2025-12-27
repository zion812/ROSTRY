package com.rio.rostry.ui.order

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

/**
 * Quick Order Sheet - One-screen bottom sheet for fast ordering.
 * Features: Quantity, Delivery/Pickup, COD payment with advance proof option.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuickOrderSheet(
    productName: String,
    productPrice: Double,
    sellerName: String,
    onDismiss: () -> Unit,
    onConfirmOrder: (quantity: Int, isDelivery: Boolean, advanceProofUri: String?) -> Unit,
    modifier: Modifier = Modifier
) {
    var quantity by remember { mutableIntStateOf(1) }
    var isDelivery by remember { mutableStateOf(true) }
    var advanceProofUri by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }

    val totalPrice = productPrice * quantity

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Quick Order",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Spacer(Modifier.height(16.dp))

            // Product Summary
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(productName, fontWeight = FontWeight.SemiBold)
                        Text("From $sellerName", style = MaterialTheme.typography.bodySmall)
                    }
                    Text(
                        "₹${productPrice.toInt()}",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            // Quantity Selector
            Text("Quantity", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                FilledIconButton(
                    onClick = { if (quantity > 1) quantity-- },
                    enabled = quantity > 1
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease")
                }
                Text(
                    text = quantity.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                FilledIconButton(
                    onClick = { if (quantity < 99) quantity++ }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase")
                }
            }

            Spacer(Modifier.height(20.dp))

            // Delivery Method
            Text("Delivery Method", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                DeliveryOptionCard(
                    icon = Icons.Default.LocalShipping,
                    label = "Delivery",
                    isSelected = isDelivery,
                    onClick = { isDelivery = true },
                    modifier = Modifier.weight(1f)
                )
                DeliveryOptionCard(
                    icon = Icons.Default.Store,
                    label = "Pickup",
                    isSelected = !isDelivery,
                    onClick = { isDelivery = false },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(20.dp))

            // Payment Method
            Text("Payment", style = MaterialTheme.typography.labelLarge)
            Spacer(Modifier.height(8.dp))
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Money, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Column {
                            Text("Cash on Delivery", fontWeight = FontWeight.Medium)
                            Text(
                                "Pay when you receive",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            // Optional: Advance Payment Proof Upload
            // Can be expanded with camera/gallery picker

            Spacer(Modifier.height(24.dp))

            // Total and Confirm
            HorizontalDivider()
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Total", style = MaterialTheme.typography.bodySmall)
                    Text(
                        "₹${totalPrice.toInt()}",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Button(
                    onClick = {
                        isLoading = true
                        onConfirmOrder(quantity, isDelivery, advanceProofUri)
                    },
                    enabled = !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 32.dp, vertical = 16.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Confirm Order", fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DeliveryOptionCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primaryContainer 
            else 
                MaterialTheme.colorScheme.surfaceVariant
        ),
        border = if (isSelected) 
            CardDefaults.outlinedCardBorder() 
        else 
            null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = label,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
