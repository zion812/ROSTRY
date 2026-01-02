package com.rio.rostry.ui.auction

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rio.rostry.data.database.entity.AuctionEntity
import com.rio.rostry.data.database.entity.BidEntity
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit
import java.text.SimpleDateFormat // Added manually since formatter logic is custom
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuctionScreen(
    auctionId: String,
    onBack: () -> Unit,
    viewModel: AuctionViewModel = hiltViewModel()
) {
    val uiState by viewModel.auctionState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(auctionId) {
        viewModel.loadAuction(auctionId)
    }

    LaunchedEffect(Unit) {
        viewModel.event.collect { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Live Auction") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.canCancel) {
                        IconButton(onClick = { viewModel.cancelAuction() }) {
                            Icon(Icons.Default.Close, contentDescription = "Cancel Auction", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            if (uiState.canBuyNow) {
                BottomAppBar {
                    Button(
                        onClick = { viewModel.buyNow() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary)
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Buy Now for ₹${uiState.auction?.buyNowPrice}")
                    }
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.padding(padding).fillMaxSize()) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.error != null) {
                Text(
                    text = uiState.error!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (uiState.auction != null) {
                AuctionContent(
                    auction = uiState.auction!!,
                    bids = uiState.bids,
                    isWinning = uiState.isWinning,
                    isSeller = uiState.isSeller,
                    onPlaceBid = { amount -> viewModel.placeBid(amount) }
                )
            }
        }
    }
}

@Composable
fun AuctionContent(
    auction: AuctionEntity,
    bids: List<BidEntity>,
    isWinning: Boolean,
    isSeller: Boolean,
    onPlaceBid: (Double) -> Unit
) {
    var timeRemaining by remember { mutableStateOf(auction.endsAt - System.currentTimeMillis()) }

    LaunchedEffect(auction.endsAt) {
        while (true) {
            timeRemaining = auction.endsAt - System.currentTimeMillis()
            delay(1000)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Timer
        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (timeRemaining < 300_000 && auction.isActive) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (auction.isActive) "Time Remaining" else if (auction.status == "SOLD") "Assets Sold" else "Auction Ended",
                        style = MaterialTheme.typography.labelMedium
                    )
                    Text(
                        text = if (auction.isActive) formatTime(timeRemaining) else auction.status,
                        style = MaterialTheme.typography.displayMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (auction.extensionCount > 0 && auction.isActive) {
                        Text(
                            "Extended ${auction.extensionCount} times (Anti-Sniping)",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // Current Price
        item {
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("Current Bid", style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = "₹${String.format("%.0f", auction.currentPrice)}",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    if (isSeller) {
                         Text(
                            "You are the seller",
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                        if (auction.reservePrice != null) {
                             Text(
                                if (auction.isReserveMet) "Reserve Met" else "Reserve Not Met (${auction.reservePrice})",
                                style = MaterialTheme.typography.bodySmall,
                                color = if (auction.isReserveMet) Color(0xFF4CAF50) else MaterialTheme.colorScheme.error
                            )
                        }
                    } else if (isWinning) {
                        Text(
                            "You are winning!",
                            color = Color(0xFF4CAF50),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    } else if (auction.winnerId != null) {
                        Text(
                            "Highest Bidder: ${auction.winnerId.take(4)}...",
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }

        // Bidding Controls
        if (auction.isActive && timeRemaining > 0 && !isSeller) {
            item {
                QuickBidChips(
                    currentBid = auction.currentPrice,
                    onBidSelected = onPlaceBid,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
        
        // Recent Bids History
        item {
            Text(
                "Recent Bids (${auction.bidCount})",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )
        }
        
        if (bids.isEmpty()) {
            item {
                Text(
                    "No bids yet. Be the first!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(8.dp)
                )
            }
        } else {
            items(bids) { bid ->
                BidItem(bid = bid, isMyBid = false) // user context check for list items can be added later
            }
        }
    }
}

@Composable
fun BidItem(bid: BidEntity, isMyBid: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = "${bid.userId.take(4)}...",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = java.text.SimpleDateFormat("HH:mm:ss", java.util.Locale.getDefault()).format(java.util.Date(bid.placedAt)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Text(
            text = "₹${String.format("%.0f", bid.amount)}",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = if (isMyBid) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

// Assuming QuickBidChips is defined elsewhere or I should include it if it was local.
// Scanning previous file content... line 146 used QuickBidChips. 
// It was referenced but not defined in the viewed lines? 
// No, steps 697 showed specific lines. line 170 ended file.
// Wait, Step 697 shows QuickBidChips usage at line 146 but definitions:
// fun formatTime



fun formatTime(millis: Long): String {
    if (millis <= 0) return "00:00:00"
    val hours = TimeUnit.MILLISECONDS.toHours(millis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}


