package com.rio.rostry.ui.screens.fowl

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.rio.rostry.data.models.Fowl
import com.rio.rostry.viewmodel.FowlError
import com.rio.rostry.repository.DataErrorType
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun FowlListScreen(
    fowls: List<Fowl>,
    loading: Boolean,
    error: FowlError?,
    onAddFowlClick: () -> Unit,
    onEditFowl: (Fowl) -> Unit,
    onDeleteFowl: (String) -> Unit,
    onViewDetails: (Fowl) -> Unit,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "My Fowls",
                style = MaterialTheme.typography.headlineMedium
            )
            FloatingActionButton(onClick = onAddFowlClick) {
                Icon(Icons.Default.Add, contentDescription = "Add Fowl")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (error != null) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = getFriendlyErrorMessage(error),
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        style = MaterialTheme.typography.bodyMedium
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        TextButton(
                            onClick = { onRefresh() }
                        ) {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Retry")
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }

        if (loading) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("loadingIndicator")
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Loading fowls...")
                }
            }
        } else if (fowls.isEmpty()) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "No fowls added yet.\nTap the + button to add your first fowl!",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        } else {
            LazyColumn {
                items(fowls) { fowl ->
                    FowlItem(
                        fowl = fowl,
                        onEdit = { onEditFowl(fowl) },
                        onDelete = { onDeleteFowl(fowl.fowlId) },
                        onViewDetails = { onViewDetails(fowl) }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
fun FowlItem(
    fowl: Fowl,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onViewDetails: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = fowl.name,
                    style = MaterialTheme.typography.headlineSmall
                )
                Row {
                    IconButton(onClick = onViewDetails) {
                        Icon(Icons.Default.Info, contentDescription = "View Details")
                    }
                    IconButton(onClick = onEdit) {
                        Icon(Icons.Default.Edit, contentDescription = "Edit")
                    }
                    IconButton(onClick = onDelete) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete")
                    }
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Breed: ${fowl.breed}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Birth Date: ${formatDate(fowl.birthDate)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Status: ${fowl.status}",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Health Records: ${fowl.healthRecords.size}",
                style = MaterialTheme.typography.bodyMedium
            )
            if (fowl.parentIds.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Parents: ${fowl.parentIds.size}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

private fun formatDate(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        "Unknown"
    }
}

private fun getFriendlyErrorMessage(error: FowlError): String {
    return com.rio.rostry.utils.ErrorUtils.getFriendlyFowlErrorMessage(error)
}