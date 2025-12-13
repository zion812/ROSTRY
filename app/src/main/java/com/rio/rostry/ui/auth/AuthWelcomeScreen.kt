package com.rio.rostry.ui.auth

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rio.rostry.R
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.ui.components.ShimmerAuthCard

/**
 * Role-first welcome screen - asks users to select their role before authentication
 *
 * Shows three role selection cards with options for guest preview or immediate sign-in.
 */
@Composable
fun AuthWelcomeScreen(
    onPreviewAsRole: (UserType) -> Unit,
    onSignInAsRole: (UserType) -> Unit,
    isLoading: Boolean = false
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(24.dp)
            .windowInsetsPadding(WindowInsets.safeDrawing),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))

        // Animated App Logo
        val logoScale by animateFloatAsState(
            targetValue = 1f,
            animationSpec = spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow
            ),
            label = "logo_scale"
        )

        Surface(
            modifier = Modifier
                .size(120.dp)
                .scale(logoScale),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            shadowElevation = 8.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                // ROSTRY Logo - custom drawable
                Image(
                    painter = painterResource(id = R.mipmap.ic_launcher_foreground),
                    contentDescription = "ROSTRY Logo",
                    modifier = Modifier.size(100.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Welcome to ROSTRY - Your Poultry Management Platform",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Choose your role to get started",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.weight(1f))

        // Role Selection Cards with staggered animations
        AnimatedVisibility(
            visible = !isLoading,
            enter = fadeIn() + slideInVertically(
                initialOffsetY = { it / 2 },
                animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing)
            ),
            exit = fadeOut() + slideOutVertically(
                targetOffsetY = { it / 2 },
                animationSpec = tween(durationMillis = 300)
            )
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Farmer Role Card
                RoleSelectionCard(
                    role = UserType.FARMER,
                    icon = Icons.Default.Agriculture,
                    title = "Farmer",
                    description = "Manage your poultry farm, track production, and sell products",
                    onPreviewClick = {
                        onPreviewAsRole(UserType.FARMER)
                    },
                    onSignInClick = {
                        onSignInAsRole(UserType.FARMER)
                    },
                    delayMillis = 100
                )

                // Enthusiast Role Card
                RoleSelectionCard(
                    role = UserType.ENTHUSIAST,
                    icon = Icons.Default.Pets,
                    title = "Enthusiast",
                    description = "Breed, monitor, and showcase your birds",
                    onPreviewClick = {
                        onPreviewAsRole(UserType.ENTHUSIAST)
                    },
                    onSignInClick = {
                        onSignInAsRole(UserType.ENTHUSIAST)
                    },
                    delayMillis = 200
                )

                // General User Role Card
                RoleSelectionCard(
                    role = UserType.GENERAL,
                    icon = Icons.Default.Person,
                    title = "General User",
                    description = "Browse marketplace, learn about poultry, and connect with farmers",
                    onPreviewClick = {
                        onPreviewAsRole(UserType.GENERAL)
                    },
                    onSignInClick = {
                        onSignInAsRole(UserType.GENERAL)
                    },
                    delayMillis = 200
                )
            }
        }

        // Shimmer Loading
        AnimatedVisibility(
            visible = isLoading,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            ShimmerAuthCard()
        }

        Spacer(modifier = Modifier.height(24.dp))

        // "Why we ask" help text
        Text(
            text = "Why we ask? We customize your experience based on your role to show relevant features and content.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Terms and Privacy
        Text(
            text = "By continuing, you agree to our Terms of Service\nand Privacy Policy",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(bottom = 16.dp)
        )
    }
}

/**
 * Role selection card with icon, title, description, and action buttons
 */
@Composable
private fun RoleSelectionCard(
    role: UserType,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onPreviewClick: () -> Unit,
    onSignInClick: () -> Unit,
    delayMillis: Int = 0
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        kotlinx.coroutines.delay(delayMillis.toLong())
        visible = true
    }

    val offsetY by animateFloatAsState(
        targetValue = if (visible) 0f else 50f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_slide"
    )

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(durationMillis = 400),
        label = "card_fade"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .graphicsLayer {
                translationY = offsetY
                this.alpha = alpha
            }
            .clickable(onClick = onPreviewClick),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onPreviewClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Preview as $title")
                }
                Button(
                    onClick = onSignInClick,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Sign in as $title")
                }
            }
        }
    }
}
