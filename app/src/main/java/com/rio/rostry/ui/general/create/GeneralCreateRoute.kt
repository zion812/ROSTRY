package com.rio.rostry.ui.general.create

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.rio.rostry.ui.general.create.GeneralCreateViewModel.MediaAttachment
import com.rio.rostry.ui.general.create.GeneralCreateViewModel.Privacy
import com.rio.rostry.ui.marketplace.LocationPickerScreen
import com.rio.rostry.marketplace.location.LocationSearchService
import com.rio.rostry.marketplace.location.LocationService
import com.google.android.libraries.places.api.Places
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GeneralCreateRoute(
    onPostCreated: () -> Unit,
    viewModel: GeneralCreateViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    LaunchedEffect(uiState.error) {
        uiState.error?.let { message ->
            scope.launch { snackbarHostState.showSnackbar(message) }
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.successMessage) {
        uiState.successMessage?.let { message ->
            scope.launch { snackbarHostState.showSnackbar(message) }
            viewModel.clearSuccess()
            onPostCreated()
        }
    }

    var showLocationDialog by rememberSaveable { mutableStateOf(false) }
    var locationInput by rememberSaveable { mutableStateOf(uiState.locationTag.orEmpty()) }
    var showPlacesPicker by rememberSaveable { mutableStateOf(false) }

    val context = LocalContext.current
    val mediaPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        uris.forEach { uri ->
            try {
                context.contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) { }
            
            val isVideo = uri.toString().contains("video") || uri.toString().endsWith(".mp4", ignoreCase = true)
            viewModel.addMedia(uri, isVideo)
        }
    }

    if (showLocationDialog) {
        AlertDialog(
            onDismissRequest = { showLocationDialog = false },
            title = { Text("Tag a location") },
            text = {
                OutlinedTextField(
                    value = locationInput,
                    onValueChange = { locationInput = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Location description") }
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.setLocationTag(locationInput.trim().ifBlank { null })
                    showLocationDialog = false
                }) {
                    Text("Save")
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = {
                        // Open Places-powered picker
                        showLocationDialog = false
                        showPlacesPicker = true
                    }) { Text("Use Places Picker") }
                    TextButton(onClick = {
                        showLocationDialog = false
                        locationInput = uiState.locationTag.orEmpty()
                    }) {
                        Text("Cancel")
                    }
                }
            }
        )
    }

    if (showPlacesPicker) {
        val context = LocalContext.current
        // Ensure Places SDK is ready; app initializes in RostryApp
        val placesClient = remember { Places.createClient(context) }
        val locationService = remember { LocationService }
        Dialog(onDismissRequest = { showPlacesPicker = false }) {
            Surface(shape = MaterialTheme.shapes.medium) {
                Column(Modifier.padding(16.dp)) {
                    LocationPickerScreen(
                        locationSearchService = remember { LocationSearchService(placesClient, locationService) },
                        locationService = locationService,
                        onLocationPicked = { lat, lng, address ->
                            // Prefer formatted address when available; fallback to coordinates
                            val tag = address ?: locationService.formatAddressFallback(lat, lng)
                            viewModel.setLocationTag(tag)
                            showPlacesPicker = false
                        }
                    )
                    Spacer(Modifier.height(8.dp))
                    TextButton(onClick = { showPlacesPicker = false }) { Text("Close") }
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Create post", maxLines = 1, overflow = TextOverflow.Ellipsis)
                }
            )
        },
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.text,
                onValueChange = viewModel::updateText,
                modifier = Modifier.fillMaxWidth().height(200.dp),
                placeholder = { Text("Share your thoughts, mention @friends, add #hashtags...") }
            )

            PrivacySelector(
                selected = uiState.privacy,
                onSelect = viewModel::setPrivacy
            )

            LocationRow(
                location = uiState.locationTag,
                onChange = {
                    locationInput = uiState.locationTag.orEmpty()
                    showLocationDialog = true
                },
                onClear = { viewModel.setLocationTag(null) }
            )

            MediaSection(
                attachments = uiState.attachments,
                onAddMedia = {
                    mediaPicker.launch(arrayOf("image/*", "video/*"))
                },
                onRemove = viewModel::removeMedia
            )

            if (uiState.hashtags.isNotEmpty() || uiState.mentions.isNotEmpty()) {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    uiState.hashtags.forEach { tag ->
                        AssistChip(onClick = {}, label = { Text(tag) })
                    }
                    uiState.mentions.forEach { mention ->
                        AssistChip(onClick = {}, label = { Text(mention) })
                    }
                }
            }

            Button(
                onClick = { viewModel.submitPost() },
                enabled = uiState.canPost && !uiState.isPosting,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isPosting) {
                    CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                    Spacer(Modifier.width(12.dp))
                } else {
                    Icon(Icons.Filled.Send, contentDescription = "Send")
                    Spacer(Modifier.width(8.dp))
                }
                Text(if (uiState.isPosting) "Sharing..." else "Share with community")
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun PrivacySelector(selected: Privacy, onSelect: (Privacy) -> Unit) {
    Column {
        Text("Privacy", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        FlowRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            PrivacyChip(
                icon = Icons.Filled.Public,
                label = "Public",
                selected = selected == Privacy.PUBLIC,
                onClick = { onSelect(Privacy.PUBLIC) }
            )
            PrivacyChip(
                icon = Icons.Filled.Group,
                label = "Followers",
                selected = selected == Privacy.FOLLOWERS_ONLY,
                onClick = { onSelect(Privacy.FOLLOWERS_ONLY) }
            )
            PrivacyChip(
                icon = Icons.Filled.Lock,
                label = "Verified buyers",
                selected = selected == Privacy.VERIFIED_BUYERS,
                onClick = { onSelect(Privacy.VERIFIED_BUYERS) }
            )
        }
    }
}

@Composable
private fun PrivacyChip(icon: ImageVector, label: String, selected: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label) },
        leadingIcon = { Icon(icon, contentDescription = label) }
    )
}

@Composable
private fun LocationRow(location: String?, onChange: () -> Unit, onClear: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (location.isNullOrBlank()) {
            Text("No location tagged", style = MaterialTheme.typography.bodyMedium)
        } else {
            Text("📍 $location", style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            TextButton(onClick = onChange) {
                Icon(Icons.Filled.LocationOn, contentDescription = "Location")
                Spacer(Modifier.width(4.dp))
                Text(if (location.isNullOrBlank()) "Add" else "Edit")
            }
            if (!location.isNullOrBlank()) {
                TextButton(onClick = onClear) { Text("Remove") }
            }
        }
    }
}

@Composable
private fun MediaSection(
    attachments: List<MediaAttachment>,
    onAddMedia: () -> Unit,
    onRemove: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Media", style = MaterialTheme.typography.titleMedium)
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            IconButton(onClick = onAddMedia) {
                Icon(Icons.Filled.CameraAlt, contentDescription = "Add media")
            }
            Text("Add up to 6 photos or videos", style = MaterialTheme.typography.labelMedium)
        }
        attachments.forEach { attachment ->
            MediaAttachmentRow(attachment = attachment, onRemove = onRemove)
        }
    }
}

@Composable
private fun MediaAttachmentRow(attachment: MediaAttachment, onRemove: (String) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = attachment.uri,
            contentDescription = "Attachment",
            modifier = Modifier.height(80.dp).fillMaxWidth(0.8f)
        )
        IconButton(onClick = { onRemove(attachment.uriString) }) {
            Icon(Icons.Outlined.Delete, contentDescription = "Remove media")
        }
    }
}
