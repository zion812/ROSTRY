package com.rio.rostry.ui.enthusiast.showrecords

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.rio.rostry.data.database.entity.ShowRecordEntity
import com.rio.rostry.ui.components.MediaThumbnailRow
import org.json.JSONArray
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowRecordsScreen(
    birdId: String,
    onNavigateBack: () -> Unit
) {
    val viewModel: ShowRecordsViewModel = hiltViewModel()
    val state by viewModel.uiState.collectAsState()
    val inputPhotos by viewModel.inputPhotos.collectAsState()

    LaunchedEffect(birdId) {
        viewModel.loadRecords(birdId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Show Records") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.openAddSheet() },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Record")
            }
        }
    ) { padding ->
        if (state.isLoading && state.records.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Stats Header
                ShowStatsHeader(
                    totalShows = state.totalShows,
                    totalWins = state.totalWins,
                    winRate = state.winRate
                )

                if (state.records.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .weight(1f),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.EmojiEvents, 
                                contentDescription = null, 
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.outline
                            )
                            Spacer(Modifier.height(16.dp))
                            Text(
                                "No show records yet", 
                                style = MaterialTheme.typography.titleMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                            Text(
                                "Add your first competition result", 
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.outline
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(state.records) { record ->
                            ShowRecordCard(record)
                        }
                    }
                }
            }
        }
        
        if (state.isAddSheetOpen) {
            AddShowRecordSheet(
                onDismiss = { viewModel.closeAddSheet() },
                onSave = { name, date, type, result, placement, judge, notes ->
                    viewModel.saveRecord(name, date, type, result, placement, judge, notes)
                },
                onAddPhoto = { uri -> viewModel.addPhoto(uri) },
                photos = inputPhotos,
                onRemovePhoto = { index -> viewModel.removePhoto(index) }
            )
        }
    }
}

@Composable
fun ShowStatsHeader(
    totalShows: Int,
    totalWins: Int,
    winRate: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        StatCard(
            label = "Total Shows", 
            value = totalShows.toString(), 
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.primaryContainer
        )
        StatCard(
            label = "Total Wins", 
            value = totalWins.toString(), 
            modifier = Modifier.weight(1f),
            color = Color(0xFFFFD700).copy(alpha = 0.3f) // Goldish
        )
        StatCard(
            label = "Win Rate", 
            value = "$winRate%", 
            modifier = Modifier.weight(1f),
            color = Color(0xFF4CAF50).copy(alpha = 0.3f) // Greenish
        )
    }
}

@Composable
fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    color: Color
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = color)
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text(label, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun ShowRecordCard(record: ShowRecordEntity) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = record.eventName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = record.recordType,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                // Result Badge
                val badgeColor = when {
                    record.isWin -> Color(0xFFFFD700) // Gold
                    record.isPodium -> Color(0xFFC0C0C0) // Silver/Gray
                    else -> MaterialTheme.colorScheme.surfaceVariant
                }
                
                Box(
                    modifier = Modifier
                        .background(badgeColor, RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = record.result,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            
            val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
            Text(
                text = "Date: ${dateFormat.format(Date(record.eventDate))}",
                style = MaterialTheme.typography.bodySmall
            )
            
            if (!record.judgesNotes.isNullOrBlank()) {
                 Text(
                    text = "Judge: ${record.judgesNotes}",
                    style = MaterialTheme.typography.bodySmall,
                     color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            if (!record.notes.isNullOrBlank()) {
                Text(
                    text = record.notes,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            
            // Photos
             val photoList = try {
                 val jsonArray = JSONArray(record.photoUrls)
                 val list = mutableListOf<String>()
                 for (i in 0 until jsonArray.length()) {
                     list.add(jsonArray.getString(i))
                 }
                 list
             } catch (e: Exception) {
                 emptyList()
             }
             
             if (photoList.isNotEmpty()) {
                 Spacer(Modifier.height(8.dp))
                 MediaThumbnailRow(
                     urls = photoList,
                     onViewGallery = { /* TODO: Implement Gallery View */ },
                     modifier = Modifier.fillMaxWidth()
                 )
             }
        }
    }
}
