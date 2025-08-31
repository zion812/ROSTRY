package com.rio.rostry.ui.fowl

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.SyncProblem
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.work.WorkInfo
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.rio.rostry.R
import com.rio.rostry.data.models.Fowl

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FowlListScreen(
    viewModel: FowlViewModel = hiltViewModel(),
    onFowlClick: (String) -> Unit,
    onNavigateToRegistration: () -> Unit
) {
    val fowls by viewModel.fowls.collectAsState()
    val syncWorkInfo by viewModel.syncWorkInfo.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Fowls") },
                actions = {
                    SyncStatusIcon(workInfo = syncWorkInfo)
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToRegistration) {
                Icon(Icons.Filled.Add, contentDescription = "Add Fowl")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            items(fowls) { fowl ->
                FowlListItem(fowl = fowl, onClick = { onFowlClick(fowl.id) })
            }
        }
    }
}

@Composable
fun SyncStatusIcon(workInfo: WorkInfo?) {
    val infiniteTransition = rememberInfiniteTransition(label = "sync_rotation")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(animation = tween(1000)), label = "sync_rotation_angle"
    )

    when (workInfo?.state) {
        WorkInfo.State.RUNNING -> {
            Icon(
                imageVector = Icons.Default.Sync,
                contentDescription = "Syncing",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.rotate(angle).padding(end = 8.dp)
            )
        }
        WorkInfo.State.SUCCEEDED -> {
            Icon(
                imageVector = Icons.Default.CloudDone,
                contentDescription = "Synced",
                tint = Color.Green,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        WorkInfo.State.FAILED -> {
            Icon(
                imageVector = Icons.Default.SyncProblem,
                contentDescription = "Sync Failed",
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        WorkInfo.State.ENQUEUED, WorkInfo.State.BLOCKED -> {
            Icon(
                imageVector = Icons.Default.Sync,
                contentDescription = "Sync Pending",
                tint = Color.Gray,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        WorkInfo.State.CANCELLED -> {
            Icon(
                imageVector = Icons.Default.CloudOff,
                contentDescription = "Sync Cancelled",
                tint = Color.Gray,
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        null -> {
            // Don't show anything if there's no work info
        }
    }
}


@Composable
fun FowlListItem(fowl: Fowl, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(fowl.imageUrl)
                    .crossfade(true)
                    .build(),
                placeholder = painterResource(R.drawable.ic_placeholder_image),
                error = painterResource(R.drawable.ic_placeholder_image),
                contentDescription = fowl.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = fowl.name, style = MaterialTheme.typography.titleMedium)
                Text(text = fowl.status, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
