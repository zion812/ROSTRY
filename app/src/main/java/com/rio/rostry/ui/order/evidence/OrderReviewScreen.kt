package com.rio.rostry.ui.order.evidence

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.text.SimpleDateFormat
import java.util.*

/**
 * Order Review Screen - Allows buyer to rate and review the order after completion.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderReviewScreen(
    orderId: String,
    productName: String,
    sellerName: String,
    onNavigateBack: () -> Unit,
    onReviewSubmitted: () -> Unit,
    viewModel: EvidenceOrderViewModel = hiltViewModel()
) {
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val successMessage by viewModel.successMessage.collectAsState()

    var overallRating by remember { mutableStateOf(0) }
    var qualityRating by remember { mutableStateOf(0) }
    var deliveryRating by remember { mutableStateOf(0) }
    var communicationRating by remember { mutableStateOf(0) }
    var reviewText by remember { mutableStateOf("") }
    var wouldRecommend by remember { mutableStateOf<Boolean?>(null) }

    LaunchedEffect(successMessage) {
        if (successMessage?.contains("Review submitted") == true) {
            kotlinx.coroutines.delay(1500)
            onReviewSubmitted()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Rate & Review", fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            // Order Summary Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(Color(0xFF10B981), Color(0xFF059669))
                                ),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(20.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(56.dp)
                                    .clip(CircleShape)
                                    .background(Color.White.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.White,
                                    modifier = Modifier.size(32.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(16.dp))
                            Column {
                                Text(
                                    text = "Order Completed!",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                                Text(
                                    text = productName,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                                Text(
                                    text = "from $sellerName",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }

            // Overall Rating
            item {
                Text(
                    text = "How was your experience?",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    repeat(5) { index ->
                        IconButton(
                            onClick = { overallRating = index + 1 }
                        ) {
                            Icon(
                                imageVector = if (index < overallRating) Icons.Default.Star else Icons.Default.StarBorder,
                                contentDescription = null,
                                tint = if (index < overallRating) Color(0xFFFBBF24) else Color(0xFFD1D5DB),
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                }
                Text(
                    text = when (overallRating) {
                        1 -> "Poor"
                        2 -> "Fair"
                        3 -> "Good"
                        4 -> "Very Good"
                        5 -> "Excellent!"
                        else -> "Tap to rate"
                    },
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center,
                    color = if (overallRating > 0) Color(0xFF374151) else Color(0xFF9CA3AF),
                    modifier = Modifier.fillMaxWidth()
                )
            }

            // Detailed Ratings
            item {
                Text(
                    text = "Rate specific aspects",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                RatingRow(
                    label = "Product Quality",
                    icon = Icons.Default.VerifiedUser,
                    rating = qualityRating,
                    onRatingChange = { qualityRating = it }
                )
            }

            item {
                RatingRow(
                    label = "Delivery Experience",
                    icon = Icons.Default.LocalShipping,
                    rating = deliveryRating,
                    onRatingChange = { deliveryRating = it }
                )
            }

            item {
                RatingRow(
                    label = "Communication",
                    icon = Icons.Default.Chat,
                    rating = communicationRating,
                    onRatingChange = { communicationRating = it }
                )
            }

            // Would Recommend
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Would you recommend this seller?",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            RecommendButton(
                                isSelected = wouldRecommend == true,
                                isPositive = true,
                                onClick = { wouldRecommend = true }
                            )
                            RecommendButton(
                                isSelected = wouldRecommend == false,
                                isPositive = false,
                                onClick = { wouldRecommend = false }
                            )
                        }
                    }
                }
            }

            // Review Text
            item {
                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    label = { Text("Share your experience (optional)") },
                    placeholder = { Text("Tell others about your experience with this seller...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 5
                )
            }

            // Submit Button
            item {
                Button(
                    onClick = {
                        viewModel.submitReview(
                            orderId = orderId,
                            overallRating = overallRating,
                            qualityRating = qualityRating,
                            deliveryRating = deliveryRating,
                            communicationRating = communicationRating,
                            reviewText = reviewText,
                            wouldRecommend = wouldRecommend
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    enabled = overallRating > 0 && !isLoading,
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6366F1)
                    )
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White
                        )
                    } else {
                        Icon(Icons.Default.RateReview, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Submit Review", fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Skip Option
            item {
                TextButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Maybe Later", color = Color(0xFF6B7280))
                }
            }

            item { Spacer(modifier = Modifier.height(32.dp)) }
        }
    }
}

@Composable
private fun RatingRow(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    rating: Int,
    onRatingChange: (Int) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
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
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = Color(0xFF6366F1)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            
            Row {
                repeat(5) { index ->
                    Icon(
                        imageVector = if (index < rating) Icons.Default.Star else Icons.Default.StarBorder,
                        contentDescription = null,
                        tint = if (index < rating) Color(0xFFFBBF24) else Color(0xFFD1D5DB),
                        modifier = Modifier
                            .size(28.dp)
                            .clickable { onRatingChange(index + 1) }
                    )
                }
            }
        }
    }
}

@Composable
private fun RecommendButton(
    isSelected: Boolean,
    isPositive: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isSelected && isPositive -> Color(0xFFDCFCE7)
        isSelected && !isPositive -> Color(0xFFFEE2E2)
        else -> Color(0xFFF3F4F6)
    }
    
    val contentColor = when {
        isSelected && isPositive -> Color(0xFF059669)
        isSelected && !isPositive -> Color(0xFFDC2626)
        else -> Color(0xFF6B7280)
    }
    
    val borderColor = when {
        isSelected && isPositive -> Color(0xFF10B981)
        isSelected && !isPositive -> Color(0xFFDC2626)
        else -> Color(0xFFE5E7EB)
    }

    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .border(2.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(onClick = onClick),
        color = backgroundColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = if (isPositive) Icons.Default.ThumbUp else Icons.Default.ThumbDown,
                contentDescription = null,
                tint = contentColor
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isPositive) "Yes" else "No",
                fontWeight = FontWeight.Medium,
                color = contentColor
            )
        }
    }
}

/**
 * Compact review prompt that appears on Order Tracking for completed orders.
 */
@Composable
fun ReviewPromptCard(
    productName: String,
    onReviewClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onReviewClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFFEF3C7)
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
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Rate your order",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF92400E)
                    )
                    Text(
                        text = "Help others by sharing your experience",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFFB45309)
                    )
                }
            }
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = null,
                tint = Color(0xFFD97706)
            )
        }
    }
}

/**
 * Seller ratings summary card (for product details page).
 */
@Composable
fun SellerRatingsCard(
    averageRating: Float,
    totalReviews: Int,
    recommendationPercentage: Int,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Rating
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = String.format("%.1f", averageRating),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFBBF24),
                        modifier = Modifier.size(24.dp)
                    )
                }
                Text(
                    text = "$totalReviews reviews",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6B7280)
                )
            }

            Divider(
                modifier = Modifier
                    .height(48.dp)
                    .width(1.dp)
            )

            // Recommendation
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "$recommendationPercentage%",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF10B981)
                )
                Text(
                    text = "Would recommend",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF6B7280)
                )
            }
        }
    }
}
