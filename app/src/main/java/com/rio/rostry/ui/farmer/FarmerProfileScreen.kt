package com.rio.rostry.ui.farmer

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.rio.rostry.data.database.entity.ProductEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerProfileScreen(
    viewModel: FarmerProfileViewModel = hiltViewModel(),
    onEditProfile: () -> Unit,
    onManageCertifications: () -> Unit,
    onContactSupport: () -> Unit,
) {
    val state by viewModel.uiState.collectAsState()
    
    var showEditSheet by remember { androidx.compose.runtime.mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface
    ) { padding ->
        if (state.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                // 1. Header Profile Section
                item {
                    ProfileHeader(
                        user = state.user,
                        reputation = state.reputation?.score ?: 0,
                        onEdit = { showEditSheet = true }
                    )
                }

                // 2. Verification & Badges
                item {
                    VerificationSection(
                        user = state.user,
                        onManageCertifications = onManageCertifications
                    )
                }

                // 3. About / Contact
                item {
                    ContactSection(state.user)
                }
                
                // 3.5 Farm Details
                if (state.user?.role == com.rio.rostry.domain.model.UserType.FARMER) {
                    item {
                        FarmDetailsSection(state.user)
                    }
                }

                // 4. Portfolio / Products
                item {
                    SectionTitle("Farm Portfolio")
                }
                
                if (state.products.isEmpty()) {
                    item {
                        Box(
                            Modifier
                                .fillMaxWidth()
                                .padding(16.dp)
                                .height(100.dp)
                                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No products listed yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                    }
                } else {
                    // Using a fixed height grid for simplicity in LazyColumn, or just rows
                    // Here we'll use a LazyRow for horizontal scrolling portfolio
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.products) { product ->
                                PortfolioItem(product)
                            }
                        }
                    }
                }

                // 5. Sales History Summary
                item {
                    SectionTitle("Recent Activity")
                }
                
                if (state.salesHistory.isEmpty()) {
                    item {
                        Text(
                            "No recent sales activity.",
                            modifier = Modifier.padding(horizontal = 16.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                } else {
                    items(state.salesHistory.take(5)) { order ->
                        ListItem(
                            headlineContent = { Text("Order #${order.orderId.takeLast(6).uppercase()}") },
                            supportingContent = { Text(SimpleDateFormat("dd MMM, yyyy", Locale.getDefault()).format(Date(order.orderDate))) },
                            trailingContent = { Text("₹${order.totalAmount}", fontWeight = FontWeight.Bold) },
                            leadingContent = {
                                Icon(Icons.Outlined.ShoppingBag, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                            },
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }

                item {
                    Spacer(Modifier.height(24.dp))
                    OutlinedButton(
                        onClick = onContactSupport,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    ) {
                        Text("Contact Support")
                    }
                }
            }
        }
    }

    if (showEditSheet && state.user != null) {
        FarmerEditProfileSheet(
            sheetState = sheetState,
            currentProfile = state.user!!,
            onDismissRequest = { showEditSheet = false },
            onSave = { updatedUser ->
                viewModel.updateProfile(updatedUser)
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    showEditSheet = false
                }
            }
        )
    }
}

@Composable
private fun ProfileHeader(
    user: com.rio.rostry.data.database.entity.UserEntity?,
    reputation: Int,
    onEdit: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        // Cover Image
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    Brush.horizontalGradient(
                        listOf(MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.secondary)
                    )
                )
        )
        
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 100.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Picture
            Surface(
                shape = CircleShape,
                border = androidx.compose.foundation.BorderStroke(4.dp, MaterialTheme.colorScheme.surface),
                modifier = Modifier.size(120.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                if (user?.profilePictureUrl != null) {
                    AsyncImage(
                        model = androidx.compose.ui.platform.LocalContext.current.let {
                            coil.request.ImageRequest.Builder(it)
                                .data(user.profilePictureUrl)
                                .crossfade(true)
                                .build()
                        },
                        contentDescription = null,
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            user?.fullName?.take(1)?.uppercase() ?: "F",
                            style = MaterialTheme.typography.displayMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(Modifier.height(12.dp))
            
            // Name & Bio
            Text(
                user?.fullName ?: "Farm Name",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                user?.address ?: "Location not set",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(Modifier.height(16.dp))
            
            // Reputation & Edit
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = MaterialTheme.colorScheme.primaryContainer,
                    onClick = {}
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Star, null, modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "$reputation Reputation",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
                
                Button(onClick = onEdit, contentPadding = PaddingValues(horizontal = 24.dp)) {
                    Text("Edit Profile")
                }
            }
        }
    }
}

@Composable
private fun VerificationSection(user: com.rio.rostry.data.database.entity.UserEntity?, onManageCertifications: () -> Unit) {
    val status = user?.verificationStatus ?: com.rio.rostry.domain.model.VerificationStatus.UNVERIFIED
    val isVerified = status == com.rio.rostry.domain.model.VerificationStatus.VERIFIED
    val isLocationVerified = user?.locationVerified == true

    val (icon, color, text) = when (status) {
        com.rio.rostry.domain.model.VerificationStatus.VERIFIED -> Triple(Icons.Default.Verified, Color(0xFF4CAF50), "Verified Farmer")
        com.rio.rostry.domain.model.VerificationStatus.PENDING -> Triple(Icons.Default.HourglassEmpty, Color(0xFFFF9800), "Verification Pending")
        com.rio.rostry.domain.model.VerificationStatus.REJECTED -> Triple(Icons.Default.Error, MaterialTheme.colorScheme.error, "Verification Rejected")
        else -> Triple(Icons.Default.GppBad, MaterialTheme.colorScheme.onSurfaceVariant, "Unverified")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                BadgeItem("KYC", isVerified)
                BadgeItem("Location", isLocationVerified)
            }
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = onManageCertifications) {
                Text(if (status == com.rio.rostry.domain.model.VerificationStatus.UNVERIFIED || status == com.rio.rostry.domain.model.VerificationStatus.REJECTED) "Verify Now" else "Manage Certifications")
            }
        }
    }
}

@Composable
private fun BadgeItem(label: String, active: Boolean) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (active) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
        border = if (!active) androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)) else null
    ) {
        Text(
            label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = if (active) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun ContactSection(user: com.rio.rostry.data.database.entity.UserEntity?) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        SectionTitle("Contact Information", modifier = Modifier.padding(bottom = 8.dp))
        ContactRow(Icons.Default.Phone, user?.phoneNumber ?: "Add phone number")
        ContactRow(Icons.Default.Email, user?.email ?: "Add email address")
        ContactRow(Icons.Default.LocationOn, user?.address ?: "Add farm location")
    }
}

@Composable
private fun ContactRow(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(16.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
private fun PortfolioItem(product: ProductEntity) {
    Card(
        modifier = Modifier
            .width(140.dp)
            .height(180.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .background(MaterialTheme.colorScheme.secondaryContainer)
            ) {
                // Placeholder for product image
                Icon(
                    Icons.Default.Image,
                    contentDescription = null,
                    modifier = Modifier.align(Alignment.Center),
                    tint = MaterialTheme.colorScheme.onSecondaryContainer
                )
            }
            Column(Modifier.padding(8.dp)) {
                Text(
                    product.name,
                    style = MaterialTheme.typography.labelLarge,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    "₹${product.price}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.weight(1f))
                Text(
                    "${product.quantity} in stock",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun SectionTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        modifier = modifier.padding(horizontal = 16.dp, vertical = 12.dp)
    )
}

@Composable
private fun FarmDetailsSection(user: com.rio.rostry.data.database.entity.UserEntity?) {
    Column(Modifier.padding(horizontal = 16.dp)) {
        SectionTitle("Farm Details", modifier = Modifier.padding(bottom = 8.dp))
        
        val fullAddress = listOfNotNull(
            user?.farmAddressLine1,
            user?.farmAddressLine2,
            user?.farmCity,
            user?.farmState,
            user?.farmPostalCode,
            user?.farmCountry
        ).filter { !it.isNullOrBlank() }.joinToString(", ")

        val details = listOfNotNull(
            user?.farmerType?.let { "Type" to it },
            user?.chickenCount?.let { "Flock Size" to "$it Birds" },
            user?.favoriteBreed?.let { "Favorite Breed" to it },
            user?.raisingSince?.let { "Raising Since" to "$it" },
            user?.bio?.takeIf { it.isNotBlank() }?.let { "Bio" to it },
            if (fullAddress.isNotBlank()) "Farm Address" to fullAddress else null
        )
        
        if (details.isEmpty()) {
            Text("No farm details added.", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            details.forEach { (label, value) ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(label, style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text(
                        value, 
                        style = MaterialTheme.typography.bodyMedium, 
                        fontWeight = FontWeight.SemiBold, 
                        modifier = Modifier.weight(1f, fill = false),
                        textAlign = androidx.compose.ui.text.style.TextAlign.End
                    )
                }
            }
        }
    }
}
