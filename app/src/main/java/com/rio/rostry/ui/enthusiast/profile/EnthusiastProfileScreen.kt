package com.rio.rostry.ui.enthusiast.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
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
import com.rio.rostry.domain.model.VerificationStatus
import kotlinx.coroutines.launch

// Enthusiast brand colors
private val EnthusiastPurple = Color(0xFF673AB7)
private val EnthusiastDeepPurple = Color(0xFF512DA8)
private val EnthusiastGold = Color(0xFFFFD700)
private val EnthusiastCyan = Color(0xFF00E5FF)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EnthusiastProfileScreen(
    viewModel: EnthusiastProfileViewModel = hiltViewModel(),
    onEditProfile: () -> Unit = {},
    onVerifyKyc: () -> Unit = {},
    onContactSupport: () -> Unit = {},
    onNavigateToStorageQuota: () -> Unit = {},
    onNavigateToBird: (String) -> Unit = {}
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showEditSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scope = rememberCoroutineScope()

    LaunchedEffect(state.saveSuccess) {
        if (state.saveSuccess) {
            snackbarHostState.showSnackbar("Profile updated!")
            viewModel.clearSaveSuccess()
        }
    }

    LaunchedEffect(state.error) {
        state.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.surface
    ) { padding ->
        if (state.isLoading && state.user == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = EnthusiastPurple)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                // 1. Premium Gradient Header
                item {
                    EnthusiastProfileHeader(
                        user = state.user,
                        reputation = state.reputation?.score ?: 0,
                        onEdit = { showEditSheet = true }
                    )
                }

                // 2. Champion Stats Row
                item {
                    ChampionStatsRow(
                        totalBirds = state.totalBirds,
                        showWins = state.totalShowWins,
                        totalShows = state.totalShows,
                        activePairs = state.activePairs
                    )
                }

                // 3. Verification & Badges
                item {
                    EnthusiastVerificationCard(
                        user = state.user,
                        onVerifyKyc = onVerifyKyc
                    )
                }

                // 4. Breeder Details
                item {
                    BreederDetailsSection(user = state.user)
                }

                // 5. Contact Info
                item {
                    ContactInfoSection(user = state.user)
                }

                // 6. Bird Portfolio
                item {
                    SectionHeader(
                        title = "My Birds",
                        icon = Icons.Default.EggAlt,
                        count = state.birds.size
                    )
                }

                if (state.birds.isEmpty()) {
                    item {
                        EmptyPortfolioCard()
                    }
                } else {
                    item {
                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(state.birds) { bird ->
                                BirdPortfolioCard(
                                    bird = bird,
                                    onClick = { onNavigateToBird(bird.productId) }
                                )
                            }
                        }
                    }
                }

                // 7. Quick Actions
                item {
                    QuickActionsSection(
                        onStorageQuota = onNavigateToStorageQuota,
                        onContactSupport = onContactSupport
                    )
                }
            }
        }
    }

    // Edit Profile Bottom Sheet
    if (showEditSheet && state.user != null) {
        EnthusiastEditProfileSheet(
            sheetState = sheetState,
            currentProfile = state.user!!,
            onDismissRequest = { showEditSheet = false },
            onSave = { updatedUser ->
                viewModel.updateProfile(updatedUser)
                scope.launch { sheetState.hide() }.invokeOnCompletion {
                    showEditSheet = false
                }
            },
            onProfileImagePicked = { uri ->
                viewModel.uploadProfileImage(uri)
            }
        )
    }
}

// ============ HEADER ============

@Composable
private fun EnthusiastProfileHeader(
    user: com.rio.rostry.data.database.entity.UserEntity?,
    reputation: Int,
    onEdit: () -> Unit
) {
    Box(modifier = Modifier.fillMaxWidth()) {
        // Cover Gradient
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            EnthusiastDeepPurple,
                            EnthusiastPurple,
                            EnthusiastPurple.copy(alpha = 0.7f)
                        )
                    )
                )
        ) {
            // Subtle decorative pattern
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(16.dp)
                    .size(80.dp)
                    .background(
                        Color.White.copy(alpha = 0.06f),
                        CircleShape
                    )
            )
            Box(
                modifier = Modifier
                    .align(Alignment.BottomStart)
                    .padding(24.dp)
                    .size(40.dp)
                    .background(
                        EnthusiastGold.copy(alpha = 0.1f),
                        CircleShape
                    )
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 110.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Avatar
            Surface(
                shape = CircleShape,
                border = androidx.compose.foundation.BorderStroke(
                    4.dp,
                    Brush.linearGradient(listOf(EnthusiastGold, EnthusiastCyan))
                ),
                modifier = Modifier.size(120.dp),
                color = MaterialTheme.colorScheme.surfaceVariant
            ) {
                if (user?.profilePictureUrl != null) {
                    AsyncImage(
                        model = user.profilePictureUrl,
                        contentDescription = "Profile",
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            user?.fullName?.take(1)?.uppercase() ?: "E",
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = EnthusiastPurple
                        )
                    }
                }
            }

            Spacer(Modifier.height(12.dp))

            // Name
            Text(
                user?.fullName ?: "Enthusiast",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            // Location
            if (!user?.address.isNullOrBlank()) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocationOn,
                        null,
                        modifier = Modifier.size(14.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        user?.address ?: "",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Reputation + Edit
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Reputation Chip
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = EnthusiastGold.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.Star,
                            null,
                            modifier = Modifier.size(16.dp),
                            tint = EnthusiastGold
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "$reputation Rep",
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.SemiBold,
                            color = EnthusiastGold
                        )
                    }
                }

                // Role Badge
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = EnthusiastPurple.copy(alpha = 0.12f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            null,
                            modifier = Modifier.size(16.dp),
                            tint = EnthusiastPurple
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Enthusiast",
                            style = MaterialTheme.typography.labelLarge,
                            color = EnthusiastPurple
                        )
                    }
                }

                // Edit Button
                Button(
                    onClick = onEdit,
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = EnthusiastPurple)
                ) {
                    Text("Edit")
                }
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

// ============ CHAMPION STATS ============

@Composable
private fun ChampionStatsRow(
    totalBirds: Int,
    showWins: Int,
    totalShows: Int,
    activePairs: Int
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            StatItem(value = "$totalBirds", label = "Birds", icon = Icons.Default.EggAlt)
            StatItem(value = "$showWins", label = "Wins", icon = Icons.Default.EmojiEvents)
            StatItem(value = "$totalShows", label = "Shows", icon = Icons.Default.Leaderboard)
            StatItem(value = "$activePairs", label = "Pairs", icon = Icons.Default.Favorite)
        }
    }
}

@Composable
private fun StatItem(value: String, label: String, icon: ImageVector) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(
            icon,
            null,
            modifier = Modifier.size(20.dp),
            tint = EnthusiastPurple
        )
        Spacer(Modifier.height(4.dp))
        Text(
            value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

// ============ VERIFICATION CARD ============

@Composable
private fun EnthusiastVerificationCard(
    user: com.rio.rostry.data.database.entity.UserEntity?,
    onVerifyKyc: () -> Unit
) {
    val status = user?.verificationStatus ?: VerificationStatus.UNVERIFIED
    val isVerified = status == VerificationStatus.VERIFIED

    val (icon, color, text) = when (status) {
        VerificationStatus.VERIFIED -> Triple(Icons.Default.Verified, Color(0xFF4CAF50), "Verified Enthusiast")
        VerificationStatus.PENDING -> Triple(Icons.Default.HourglassEmpty, Color(0xFFFF9800), "Verification Pending")
        VerificationStatus.PENDING_UPGRADE -> Triple(Icons.Default.HourglassEmpty, Color(0xFFFF9800), "Upgrade Pending")
        VerificationStatus.REJECTED -> Triple(Icons.Default.Error, MaterialTheme.colorScheme.error, "Verification Rejected")
        else -> Triple(Icons.Default.GppBad, MaterialTheme.colorScheme.onSurfaceVariant, "Unverified")
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isVerified) Color(0xFF4CAF50).copy(alpha = 0.08f)
            else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = color)
                Spacer(Modifier.width(8.dp))
                Text(
                    text,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = color
                )
            }

            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                VerificationBadge("KYC", isVerified)
                VerificationBadge("ID", user?.kycLevel != null && user.kycLevel!! > 0)
            }

            if (!isVerified && status != VerificationStatus.PENDING && status != VerificationStatus.PENDING_UPGRADE) {
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onVerifyKyc,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = EnthusiastPurple)
                ) {
                    Text(if (status == VerificationStatus.REJECTED) "Resubmit Verification" else "Start Verification")
                }
            }
        }
    }
}

@Composable
private fun VerificationBadge(label: String, active: Boolean) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (active) EnthusiastPurple.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surface,
        border = if (!active) androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        ) else null
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (active) {
                Icon(
                    Icons.Default.CheckCircle,
                    null,
                    modifier = Modifier.size(12.dp),
                    tint = EnthusiastPurple
                )
                Spacer(Modifier.width(4.dp))
            }
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = if (active) EnthusiastPurple else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

// ============ BREEDER DETAILS ============

@Composable
private fun BreederDetailsSection(user: com.rio.rostry.data.database.entity.UserEntity?) {
    val details = listOfNotNull(
        user?.chickenCount?.let { "Flock Size" to "$it Birds" },
        user?.favoriteBreed?.let { "Favorite Breed" to it },
        user?.raisingSince?.let { "Raising Since" to "$it" },
        user?.bio?.takeIf { it.isNotBlank() }?.let { "About" to it }
    )

    if (details.isNotEmpty()) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            SectionHeader(title = "Breeder Info", icon = Icons.Default.Agriculture)
            Spacer(Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                )
            ) {
                Column(Modifier.padding(16.dp)) {
                    details.forEachIndexed { index, (label, value) ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                label,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                value,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f, fill = false),
                                textAlign = androidx.compose.ui.text.style.TextAlign.End
                            )
                        }
                        if (index < details.lastIndex) {
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 2.dp),
                                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.15f)
                            )
                        }
                    }
                }
            }
        }
    }
}

// ============ CONTACT INFO ============

@Composable
private fun ContactInfoSection(user: com.rio.rostry.data.database.entity.UserEntity?) {
    Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
        SectionHeader(title = "Contact", icon = Icons.Default.ContactMail)
        Spacer(Modifier.height(8.dp))

        ContactRow(Icons.Default.Phone, user?.phoneNumber ?: "Add phone number")
        ContactRow(Icons.Default.Email, user?.email ?: "Add email address")
        if (!user?.address.isNullOrBlank()) {
            ContactRow(Icons.Default.LocationOn, user?.address ?: "")
        }
    }
}

@Composable
private fun ContactRow(icon: ImageVector, text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        Spacer(Modifier.width(16.dp))
        Text(text, style = MaterialTheme.typography.bodyMedium)
    }
}

// ============ BIRD PORTFOLIO ============

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun BirdPortfolioCard(
    bird: ProductEntity,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .width(150.dp)
            .height(200.dp),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(3.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column {
            // Bird image area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                EnthusiastPurple.copy(alpha = 0.1f),
                                EnthusiastPurple.copy(alpha = 0.05f)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (!bird.imageUrls.isEmpty()) {
                    AsyncImage(
                        model = bird.imageUrls.first(),
                        contentDescription = bird.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Icon(
                        Icons.Default.EggAlt,
                        null,
                        modifier = Modifier.size(36.dp),
                        tint = EnthusiastPurple.copy(alpha = 0.4f)
                    )
                }
            }

            Column(Modifier.padding(10.dp)) {
                Text(
                    bird.name,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    bird.breed ?: "Unknown breed",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.weight(1f))
                Text(
                    bird.gender?.replaceFirstChar { it.uppercase() } ?: "",
                    style = MaterialTheme.typography.labelSmall,
                    color = EnthusiastPurple
                )
            }
        }
    }
}

@Composable
private fun EmptyPortfolioCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(100.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
        )
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(
                    Icons.Default.EggAlt,
                    null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "No birds added yet",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

// ============ QUICK ACTIONS ============

@Composable
private fun QuickActionsSection(
    onStorageQuota: () -> Unit,
    onContactSupport: () -> Unit
) {
    Column(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SectionHeader(title = "Settings", icon = Icons.Default.Settings)
        Spacer(Modifier.height(4.dp))

        OutlinedButton(
            onClick = onStorageQuota,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
        ) {
            Icon(Icons.Default.Cloud, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Storage & Quota")
        }

        OutlinedButton(
            onClick = onContactSupport,
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.onSurface)
        ) {
            Icon(Icons.Default.SupportAgent, null, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Contact Support")
        }
    }
}

// ============ SHARED COMPONENTS ============

@Composable
private fun SectionHeader(
    title: String,
    icon: ImageVector,
    count: Int? = null
) {
    Row(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            null,
            modifier = Modifier.size(20.dp),
            tint = EnthusiastPurple
        )
        Spacer(Modifier.width(8.dp))
        Text(
            title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        if (count != null) {
            Spacer(Modifier.width(8.dp))
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = EnthusiastPurple.copy(alpha = 0.1f),
                modifier = Modifier.padding(start = 4.dp)
            ) {
                Text(
                    "$count",
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    style = MaterialTheme.typography.labelSmall,
                    color = EnthusiastPurple
                )
            }
        }
    }
}
