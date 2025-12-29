package com.rio.rostry.ui.onboarding

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.rio.rostry.domain.model.UserType
import kotlinx.coroutines.launch

/**
 * Instagram Stories-style onboarding tour showcasing key features per user role.
 * 
 * Shows 4 swipeable cards with:
 * - Large icon
 * - Bold title
 * - Brief description
 * - Progress dots
 * - Skip/Next buttons
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun UserOnboardingTourScreen(
    userRole: UserType,
    onComplete: () -> Unit,
    onSkip: () -> Unit
) {
    val tourPages = getTourPagesForRole(userRole)
    val pagerState = rememberPagerState(pageCount = { tourPages.size })
    val scope = rememberCoroutineScope()

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
            // Skip button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onSkip) {
                    Text("Skip", style = MaterialTheme.typography.labelLarge)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Horizontal Pager
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { page ->
                TourPageContent(tourPage = tourPages[page])
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Progress Dots
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(tourPages.size) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (pagerState.currentPage == index) 10.dp else 8.dp)
                            .clip(CircleShape)
                            .background(
                                if (pagerState.currentPage == index)
                                    MaterialTheme.colorScheme.primary
                                else
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Navigation Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Back button (hidden on first page)
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

                // Next/Get Started button
                Button(
                    onClick = {
                        if (pagerState.currentPage < tourPages.size - 1) {
                            scope.launch {
                                pagerState.animateScrollToPage(pagerState.currentPage + 1)
                            }
                        } else {
                            onComplete()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        if (pagerState.currentPage == tourPages.size - 1)
                            "Get Started"
                        else
                            "Next"
                    )
                }
            }
        }
    }
}

@Composable
private fun TourPageContent(tourPage: TourPage) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Feature Icon
        Surface(
            modifier = Modifier.size(120.dp),
            shape = RoundedCornerShape(24.dp),
            color = MaterialTheme.colorScheme.primaryContainer,
            shadowElevation = 4.dp
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = tourPage.icon,
                    contentDescription = null,
                    modifier = Modifier.size(64.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Title
        Text(
            text = tourPage.title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Description
        Text(
            text = tourPage.description,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight.times(1.5f)
        )

        if (tourPage.highlights.isNotEmpty()) {
            Spacer(modifier = Modifier.height(24.dp))

            // Feature highlights
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.Start,
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                tourPage.highlights.forEach { highlight ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = highlight,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }
}

private data class TourPage(
    val icon: ImageVector,
    val title: String,
    val description: String,
    val highlights: List<String> = emptyList()
)

private fun getTourPagesForRole(role: UserType): List<TourPage> {
    return when (role) {
        UserType.FARMER -> listOf(
            TourPage(
                icon = Icons.Default.Dashboard,
                title = "Your Farm Dashboard",
                description = "Monitor your entire farm operation from one place. Track health, production, and inventory in real-time.",
                highlights = listOf(
                    "Live data on all birds and batches",
                    "Quick actions for daily tasks",
                    "Smart alerts and reminders"
                )
            ),
            TourPage(
                icon = Icons.Default.Storefront,
                title = "Marketplace Listings",
                description = "Sell directly to buyers with zero commission. Create beautiful listings in under a minute.",
                highlights = listOf(
                    "Professional product photos",
                    "Built-in pricing guidance",
                    "Chat with interested buyers"
                )
            ),
            TourPage(
                icon = Icons.Default.Analytics,
                title = "Farm Analytics",
                description = "Make data-driven decisions with powerful analytics. Understand your profitability and optimize operations.",
                highlights = listOf(
                    "Revenue and expense tracking",
                    "Production trends",
                    "Performance benchmarks"
                )
            ),
            TourPage(
                icon = Icons.Default.Groups,
                title = "Farmer Community",
                description = "Connect with other farmers, share knowledge, and learn best practices from experienced professionals.",
                highlights = listOf(
                    "Ask questions and get answers",
                    "Join local farmer groups",
                    "Expert tips and guides"
                )
            )
        )

        UserType.ENTHUSIAST -> listOf(
            TourPage(
                icon = Icons.Default.Pets,
                title = "Breeding Tools",
                description = "Track lineage, manage pairings, and maintain comprehensive breeding records for your birds.",
                highlights = listOf(
                    "Visual family trees",
                    "Pairing recommendations",
                    "Genetic trait tracking"
                )
            ),
            TourPage(
                icon = Icons.Default.QrCode,
                title = "Full Traceability",
                description = "Every bird has a story. Record and showcase complete lineage with blockchain-verified authenticity.",
                highlights = listOf(
                    "QR code for each bird",
                    "Shareable family trees",
                    "Show records and achievements"
                )
            ),
            TourPage(
                icon = Icons.Default.SwapHoriz,
                title = "Safe Transfers",
                description = "Buy and sell birds with confidence. Secure escrow system protects both buyers and sellers.",
                highlights = listOf(
                    "Verified bird authenticity",
                    "Dispute resolution",
                    "Transfer history tracking"
                )
            ),
            TourPage(
                icon = Icons.Default.Share,
                title = "Showcase Your Birds",
                description = "Build your reputation in the community. Share photos, achievements, and breeding success stories.",
                highlights = listOf(
                    "Create stunning profiles",
                    "Connect with breeders",
                    "Participate in competitions"
                )
            )
        )

        UserType.GENERAL -> listOf(
            TourPage(
                icon = Icons.Default.Search,
                title = "Browse Marketplace",
                description = "Discover quality poultry products from verified farmers. Filter by location, price, and certification.",
                highlights = listOf(
                    "Wide selection of products",
                    "Read reviews and ratings",
                    "Secure payment options"
                )
            ),
            TourPage(
                icon = Icons.Default.School,
                title = "Learn About Poultry",
                description = "Access expert guides, videos, and articles. From beginner tips to advanced breeding techniques.",
                highlights = listOf(
                    "Step-by-step guides",
                    "Video tutorials",
                    "Ask expert questions"
                )
            ),
            TourPage(
                icon = Icons.Default.GroupAdd,
                title = "Connect with Farmers",
                description = "Chat directly with sellers, join community discussions, and build relationships with trusted farmers.",
                highlights = listOf(
                    "Direct messaging",
                    "Community forums",
                    "Local events and meetups"
                )
            ),
            TourPage(
                icon = Icons.Default.ShoppingCart,
                title = "Buy with Confidence",
                description = "Secure transactions, verified products, and buyer protection. Your satisfaction is guaranteed.",
                highlights = listOf(
                    "Escrow payments",
                    "Product authenticity",
                    "Easy returns and refunds"
                )
                )
            )
            UserType.ADMIN -> emptyList()
        }
}
