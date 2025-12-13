package com.rio.rostry.ui.profile

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import android.content.Intent
import java.io.File
import java.util.UUID
import coil.compose.AsyncImage
import com.rio.rostry.domain.model.UserType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    onNavigateBack: () -> Unit,
    viewModel: ProfileEditViewModel = hiltViewModel()
) {
    val uiState by viewModel.ui.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current
    val context = LocalContext.current

    var showUnsavedDialog by remember { mutableStateOf(false) }
    var showPhotoSourceDialog by remember { mutableStateOf(false) }

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) { }
            viewModel.uploadProfilePhoto(it.toString())
        }
    }

    var pendingCameraUri by remember { mutableStateOf<android.net.Uri?>(null) }
    val takePicture = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            pendingCameraUri?.let { viewModel.uploadProfilePhoto(it.toString()) }
        }
        pendingCameraUri = null
    }

    LaunchedEffect(uiState.message) {
        uiState.message?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearMessage()
        }
    }

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearError()
        }
    }

    LaunchedEffect(Unit) {
        viewModel.navigationEvents.collect { event ->
            when (event) {
                is ProfileEditViewModel.NavigationEvent.ProfileUpdated -> onNavigateBack()
            }
        }
    }

    BackHandler {
        if (uiState.isDirty) {
            showUnsavedDialog = true
        } else {
            onNavigateBack()
        }
    }

    if (showUnsavedDialog) {
        AlertDialog(
            onDismissRequest = { showUnsavedDialog = false },
            title = { Text("Unsaved changes") },
            text = { Text("You have unsaved changes. Do you want to discard them?") },
            confirmButton = {
                TextButton(onClick = {
                    showUnsavedDialog = false
                    onNavigateBack()
                }) {
                    Text("Discard")
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextButton(onClick = { showUnsavedDialog = false }) {
                        Text("Keep Editing")
                    }
                    TextButton(onClick = {
                        viewModel.updateProfile()
                        showUnsavedDialog = false
                    }) {
                        Text("Save")
                    }
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Edit Profile") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            if (uiState.isDirty) {
                                showUnsavedDialog = true
                            } else {
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier.semantics { contentDescription = "Navigate back" }
                    ) {
                        Icon(Icons.Default.ArrowBack, contentDescription = null)
                    }
                },
                actions = {
                    OutlinedButton(
                        onClick = { 
                            if (uiState.isDirty) {
                                showUnsavedDialog = true
                            } else {
                                onNavigateBack()
                            }
                        },
                        modifier = Modifier.semantics { contentDescription = "Cancel" }
                    ) { Text("Cancel") }
                    TextButton(
                        onClick = {
                            viewModel.updateProfile()
                            focusManager.clearFocus()
                        },
                        enabled = !uiState.isSaving,
                        modifier = Modifier.semantics { contentDescription = "Save profile" }
                    ) {
                        if (uiState.isSaving) {
                            CircularProgressIndicator(modifier = Modifier.size(18.dp), strokeWidth = 2.dp)
                        } else {
                            Text("Save")
                        }
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Photo Section
            Text(
                "Profile Photo",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.semantics { contentDescription = "Profile photo section" }
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                AsyncImage(
                    model = uiState.photoUri ?: "https://via.placeholder.com/80", // Placeholder
                    contentDescription = "Current profile photo",
                    modifier = Modifier.size(80.dp)
                )
                OutlinedButton(
                    onClick = { showPhotoSourceDialog = true },
                    modifier = Modifier.semantics { contentDescription = "Change profile photo" }
                ) {
                    Text("Change Photo")
                }
            }
            if (uiState.uploadProgress > 0) {
                CircularProgressIndicator(
                    progress = uiState.uploadProgress / 100f,
                    modifier = Modifier.align(Alignment.Start)
                )
                Text("${uiState.uploadProgress}%")
            }

            // Form Fields (common)
            ProfileTextField(
                value = uiState.name,
                onValueChange = { viewModel.updateName(it) },
                label = "Name",
                error = uiState.validationErrors["name"],
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier
                    .semantics { contentDescription = "Name field" }
                    .testTag("profile_edit_name")
            )

            ProfileTextField(
                value = uiState.email,
                onValueChange = { viewModel.updateEmail(it) },
                label = "Email",
                error = uiState.validationErrors["email"],
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .semantics { contentDescription = "Email field" }
                    .testTag("profile_edit_email")
            )

            ProfileTextField(
                value = uiState.phone,
                onValueChange = { viewModel.updatePhone(it) },
                label = "Phone",
                error = uiState.validationErrors["phone"],
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Phone,
                    imeAction = ImeAction.Next
                ),
                modifier = Modifier
                    .semantics { contentDescription = "Phone field" }
                    .testTag("profile_edit_phone")
            )

            // Role-specific sections
            when (uiState.user?.role) {
                UserType.FARMER -> {
                    ProfileTextField(
                        value = uiState.farmLocation,
                        onValueChange = { viewModel.updateFarmLocation(it) },
                        label = "Farm Location",
                        error = uiState.validationErrors["farmLocation"],
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        modifier = Modifier.semantics { contentDescription = "Farm Location field" }
                    )

                    ProfileTextField(
                        value = uiState.location,
                        onValueChange = { viewModel.updateLocation(it) },
                        label = "Farm Address",
                        error = uiState.validationErrors["location"],
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        modifier = Modifier.semantics { contentDescription = "Farm Address field" }
                    )
                }
                UserType.ENTHUSIAST -> {
                    ReadOnlyField(label = "KYC Level", value = (uiState.user?.kycLevel ?: 0).toString())
                    ReadOnlyField(label = "Verification Status", value = uiState.user?.verificationStatus?.name ?: "UNVERIFIED")
                }
                else -> {
                    ProfileTextField(
                        value = uiState.location,
                        onValueChange = { viewModel.updateLocation(it) },
                        label = "Location",
                        error = uiState.validationErrors["location"],
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        modifier = Modifier.semantics { contentDescription = "Location field" }
                    )
                }
            }

            ProfileTextField(
                value = uiState.bio,
                onValueChange = { viewModel.updateBio(it) },
                label = "Bio",
                error = null,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                singleLine = false,
                maxLines = 5,
                modifier = Modifier
                    .semantics { contentDescription = "Bio field" }
                    .testTag("profile_edit_bio")
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    if (showPhotoSourceDialog) {
        AlertDialog(
            onDismissRequest = { showPhotoSourceDialog = false },
            title = { Text("Select Photo Source") },
            text = { Text("Choose how to update your profile photo") },
            confirmButton = {
                TextButton(onClick = {
                    // Camera
                    val file = File(context.cacheDir, "profile_${UUID.randomUUID()}.jpg")
                    val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
                    pendingCameraUri = uri
                    takePicture.launch(uri)
                    showPhotoSourceDialog = false
                }) { Text("Camera") }
            },
            dismissButton = {
                TextButton(onClick = {
                    photoPicker.launch(arrayOf("image/*"))
                    showPhotoSourceDialog = false
                }) { Text("Gallery") }
            }
        )
    }
}

@Composable
private fun ReadOnlyField(label: String, value: String) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(label, style = MaterialTheme.typography.labelLarge)
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = {},
            enabled = false,
            label = { Text(label) },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@Composable
private fun ProfileTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    error: String?,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = 1,
    modifier: Modifier = Modifier
) {
    var focused by remember { mutableStateOf(false) }
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        isError = error != null && !focused,
        supportingText = { error?.let { Text(it) } },
        singleLine = singleLine,
        maxLines = maxLines,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        modifier = modifier
            .fillMaxWidth()
            .onFocusChanged { focused = it.isFocused }
    )
}