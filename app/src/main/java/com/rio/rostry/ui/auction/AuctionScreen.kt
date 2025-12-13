package com.rio.rostry.ui.auction

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import kotlinx.coroutines.delay
import java.util.concurrent.TimeUnit

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
        viewModel.bidEvent.collect { message ->
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
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
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
                    isWinning = uiState.isWinning,
                    onPlaceBid = { amount -> viewModel.placeBid(amount) }
                )
            }
        }
    }
}

@Composable
fun AuctionContent(
    auction: AuctionEntity,
    isWinning: Boolean,
    onPlaceBid: (Double) -> Unit
) {
    var timeRemaining by remember { mutableStateOf(auction.endsAt - System.currentTimeMillis()) }

    LaunchedEffect(auction.endsAt) {
        while (true) {
            timeRemaining = auction.endsAt - System.currentTimeMillis()
            delay(1000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Timer
        Card(
            colors = CardDefaults.cardColors(
                containerColor = if (timeRemaining < 300_000) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Time Remaining", style = MaterialTheme.typography.labelMedium)
                Text(
                    text = formatTime(timeRemaining),
                    style = MaterialTheme.typography.displayMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Current Price
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
                if (isWinning) {
                    Text(
                        "You are winning!",
                        color = Color(0xFF4CAF50),
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 8.dp)
                    )
                } else if (auction.winnerId != null) {
                    Text(
                        "Winning: ${auction.winnerId.take(4)}...",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bidding Controls
        if (auction.isActive && timeRemaining > 0) {
            QuickBidChips(
                currentBid = auction.currentPrice,
                onBidSelected = onPlaceBid,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(
                text = if (auction.status == "ENDED") "Auction Ended" else "Auction Not Active",
                style = MaterialTheme.typography.headlineSmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

fun formatTime(millis: Long): String {
    if (millis <= 0) return "00:00:00"
    val hours = TimeUnit.MILLISECONDS.toHours(millis)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(millis) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(millis) % 60
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}


