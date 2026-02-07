package com.rio.rostry.ui.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rio.rostry.domain.model.UserType
import kotlinx.coroutines.launch

/**
 * Multi-step profile setup wizard shown after onboarding tour.
 * 
 * Steps:
 * 1. Display Name & Avatar
 * 2. Location (Farmers/Enthusiasts)
 * 3. Interests/Goals (Multi-select chips)
 * 4. Notifications (Smart defaults)
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ProfileSetupWizard(
    userRole: UserType,
    onComplete: (ProfileData) -> Unit,
    onSkip: () -> Unit
) {
    val steps = getSetupStepsForRole(userRole)
    val pagerState = rememberPagerState(pageCount = { steps.size })
    val scope = rememberCoroutineScope()
    
    // Collect profile data
    var displayName by rememberSaveable { mutableStateOf("") }
    var location by rememberSaveable { mutableStateOf("") }
    var selectedInterests by rememberSaveable { mutableStateOf(setOf<String>()) }
    var enableNotifications by rememberSaveable { mutableStateOf(true) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header with skip
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Set up your profile",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                TextButton(onClick = onSkip) {
                    Text("Skip")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Progress indicator
            LinearProgressIndicator(
                progress = { (pagerState.currentPage + 1).toFloat() / steps.size },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Pager for steps
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                userScrollEnabled = false // Disable manual scrolling
            ) { page ->
                when (page) {
                    0 -> DisplayNameStep(
                        displayName = displayName,
                        onNameChange = { displayName = it }
                    )
                    1 -> LocationStep(
                        location = location,
                        onLocationChange = { location = it },
                        userRole = userRole
                    )
                    2 -> InterestsStep(
                        userRole = userRole,
                        selectedInterests = selectedInterests,
                        onInterestsChange = { selectedInterests = it }
                    )
                    3 -> NotificationsStep(
                        enabled = enableNotifications,
                        onToggle = { enableNotifications = it }
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Navigation buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (pagerState.currentPage > 0) {
                    OutlinedButton(
                        onClick = {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage - 1)
                            }
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Back")
                    }
                } else {
                    Spacer(modifier = Modifier.weight(1f))
                }

                Spacer(modifier = Modifier.width(16.dp))

                Button(
                    onClick = {
                        if (pagerState.currentPage < steps.size - 1) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            onComplete(
                                ProfileData(
                                    displayName = displayName,
                                    location = location,
                                    interests = selectedInterests.toList(),
                                    notificationsEnabled = enableNotifications
                                )
                            )
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = when (pagerState.currentPage) {
                        0 -> displayName.isNotBlank()
                        else -> true
                    }
                ) {
                    Text(
                        if (pagerState.currentPage == steps.size - 1)
                            "Complete"
                        else
                            "Next"
                    )
                }
            }
        }
    }
}

@Composable
private fun DisplayNameStep(
    displayName: String,
    onNameChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Person,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "What should we call you?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Your display name will be visible to other users",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = displayName,
            onValueChange = onNameChange,
            label = { Text("Display Name") },
            placeholder = { Text("e.g., John Smith") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun LocationStep(
    location: String,
    onLocationChange: (String) -> Unit,
    userRole: UserType
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Where are you located?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = when (userRole) {
                UserType.FARMER -> "Help buyers find your farm and products nearby"
                UserType.ENTHUSIAST -> "Connect with local breeders and buyers"
                UserType.GENERAL, UserType.ADMIN, UserType.SUPPORT, UserType.MODERATOR -> "Discover products and farmers near you"
            },
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        OutlinedTextField(
            value = location,
            onValueChange = onLocationChange,
            label = { Text("City or Region") },
            placeholder = { Text("e.g., Bangalore, Karnataka") },
            leadingIcon = {
                Icon(Icons.Default.LocationOn, contentDescription = null)
            },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = { /* TODO: Implement auto-detect */ },
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(Icons.Default.MyLocation, contentDescription = null)
            Spacer(Modifier.width(8.dp))
            Text("Auto-detect my location")
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun InterestsStep(
    userRole: UserType,
    selectedInterests: Set<String>,
    onInterestsChange: (Set<String>) -> Unit
) {
    val interests = getInterestsForRole(userRole)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Star,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "What interests you?",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Select all that apply. We'll personalize your experience.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            interests.forEach { interest ->
                val isSelected = interest in selectedInterests
                FilterChip(
                    selected = isSelected,
                    onClick = {
                        onInterestsChange(
                            if (isSelected) selectedInterests - interest
                            else selectedInterests + interest
                        )
                    },
                    label = { Text(interest) },
                    leadingIcon = if (isSelected) {
                        { Icon(Icons.Default.Check, contentDescription = null, Modifier.size(18.dp)) }
                    } else null
                )
            }
        }
    }
}

@Composable
private fun NotificationsStep(
    enabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "Stay updated",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Get notified about important updates and opportunities",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Push Notifications",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            text = "Messages, orders, and important alerts",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Switch(
                        checked = enabled,
                        onCheckedChange = onToggle
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "You can change this anytime in settings",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )
    }
}

data class ProfileData(
    val displayName: String,
    val location: String,
    val interests: List<String>,
    val notificationsEnabled: Boolean
)

private fun getSetupStepsForRole(role: UserType): List<String> {
    return when (role) {
        UserType.FARMER, UserType.ENTHUSIAST -> listOf(
            "Name", "Location", "Interests", "Notifications"
        )
        UserType.GENERAL, UserType.ADMIN, UserType.SUPPORT, UserType.MODERATOR -> listOf(
            "Name", "Location", "Interests", "Notifications"
        )
    }
}

private fun getInterestsForRole(role: UserType): List<String> {
    return when (role) {
        UserType.FARMER -> listOf(
            "Layers", "Broilers", "Ducks", "Quails",
            "Feed Management", "Disease Prevention",
            "Farm Analytics", "Selling Online"
        )
        UserType.ENTHUSIAST -> listOf(
            "Ornamental Birds", "Show Birds", "Breeding",
            "Genetics", "Competitions", "Lineage Tracking",
            "Trading", "Photography"
        )
        UserType.GENERAL, UserType.ADMIN, UserType.SUPPORT, UserType.MODERATOR -> listOf(
            "Fresh Eggs", "Chicken Meat", "Organic Products",
            "Learning", "Local Farmers", "Urban Farming",
            "Recipes", "Sustainability"
        )
    }
}
