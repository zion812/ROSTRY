package com.rio.rostry.ui.screens.fowl

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rio.rostry.data.models.Fowl
import com.rio.rostry.utils.ImagePickerUtil
import com.rio.rostry.utils.rememberImagePickerLauncher
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditFowlScreen(
    fowl: Fowl? = null,
    onAddEditFowl: (Fowl) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var name by remember { mutableStateOf(fowl?.name ?: "") }
    var breed by remember { mutableStateOf(fowl?.breed ?: "") }
    var birthDate by remember { mutableStateOf(fowl?.birthDate ?: System.currentTimeMillis()) }
    var status by remember { mutableStateOf(fowl?.status ?: "growing") }
    var lineageNotes by remember { mutableStateOf(fowl?.lineageNotes ?: "") }
    var parentIds by remember { mutableStateOf(fowl?.parentIds?.joinToString(", ") ?: "") }
    var photoUrl by remember { mutableStateOf(fowl?.photoUrl ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
    // Loading state for image upload
    var isUploading by remember { mutableStateOf(false) }
    
    // Validation states
    var nameError by remember { mutableStateOf(false) }
    var breedError by remember { mutableStateOf(false) }
    var birthDateError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    
    // Date picker state
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = birthDate)
    
    // Image picker launcher
    val imagePickerLauncher = rememberImagePickerLauncher { uri ->
        selectedImageUri = uri
        // Store the local URI for immediate display
        photoUrl = uri.toString()
    }
    
    // For simplicity, we're not implementing health records in this UI yet
    // In a full implementation, you would have a separate screen for health records

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (fowl == null) "Add Fowl" else "Edit Fowl") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(scrollState)
        ) {
            // Name input
            OutlinedTextField(
                value = name,
                onValueChange = { 
                    name = it
                    nameError = false
                    errorMessage = null
                },
                label = { Text("Name") },
                modifier = Modifier.fillMaxWidth(),
                isError = nameError,
                supportingText = {
                    if (nameError) {
                        Text("Name is required")
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Breed input
            OutlinedTextField(
                value = breed,
                onValueChange = { 
                    breed = it
                    breedError = false
                    errorMessage = null
                },
                label = { Text("Breed") },
                modifier = Modifier.fillMaxWidth(),
                isError = breedError,
                supportingText = {
                    if (breedError) {
                        Text("Breed is required")
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Birth date picker
            OutlinedTextField(
                value = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(birthDate)),
                onValueChange = { },
                label = { Text("Birth Date") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                readOnly = true,
                isError = birthDateError,
                supportingText = {
                    if (birthDateError) {
                        Text("Birth date is required")
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Status dropdown
            var expanded by remember { mutableStateOf(false) }
            val statusOptions = listOf("growing", "breeder", "for sale", "sold", "deceased")
            
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    readOnly = true,
                    value = status,
                    onValueChange = { },
                    label = { Text("Status") },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    statusOptions.forEach { option ->
                        DropdownMenuItem(
                            text = { Text(option) },
                            onClick = {
                                status = option
                                expanded = false
                            }
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Lineage notes
            OutlinedTextField(
                value = lineageNotes,
                onValueChange = { lineageNotes = it },
                label = { Text("Lineage Notes") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 3
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Parent IDs
            OutlinedTextField(
                value = parentIds,
                onValueChange = { parentIds = it },
                label = { Text("Parent IDs (comma separated)") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Photo section
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Fowl Photo",
                        style = MaterialTheme.typography.headlineSmall,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    // Display selected image or placeholder
                    if (selectedImageUri != null) {
                        AsyncImage(
                            model = selectedImageUri,
                            contentDescription = "Selected fowl photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    } else if (photoUrl.isNotBlank()) {
                        AsyncImage(
                            model = photoUrl,
                            contentDescription = "Current fowl photo",
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    } else {
                        // Placeholder for photo
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clickable { imagePickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Icon(
                                    Icons.Default.Add,
                                    contentDescription = "Add photo",
                                    modifier = Modifier.size(48.dp)
                                )
                                Text("Tap to add photo")
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Button(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isUploading
                    ) {
                        if (isUploading) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.onPrimary,
                                strokeWidth = 2.dp,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                        }
                        Text(if (selectedImageUri != null) "Change Photo" else "Add Photo")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Display error message if there is one
            errorMessage?.let { error ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Save button
            Button(
                onClick = {
                    // Validate inputs
                    nameError = name.isBlank()
                    breedError = breed.isBlank()
                    birthDateError = birthDate <= 0
                    
                    if (nameError || breedError || birthDateError) {
                        errorMessage = "Please fill in all required fields"
                        return@Button
                    }
                    
                    // If there's a selected image, upload it first
                    if (selectedImageUri != null) {
                        coroutineScope.launch {
                            isUploading = true
                            errorMessage = null
                            
                            val result = ImagePickerUtil.getInstance().uploadImage(context, selectedImageUri!!)
                            if (result.isSuccess) {
                                val downloadUrl = result.getOrNull()
                                if (downloadUrl != null) {
                                    photoUrl = downloadUrl
                                    // Create fowl object with the uploaded image URL
                                    val fowlToSave = fowl?.copy(
                                        name = name,
                                        breed = breed,
                                        birthDate = birthDate,
                                        status = status,
                                        lineageNotes = lineageNotes,
                                        parentIds = if (parentIds.isBlank()) emptyList() else parentIds.split(",").map { it.trim() },
                                        photoUrl = photoUrl,
                                        updatedAt = System.currentTimeMillis()
                                    ) ?: Fowl(
                                        name = name,
                                        breed = breed,
                                        birthDate = birthDate,
                                        status = status,
                                        lineageNotes = lineageNotes,
                                        parentIds = if (parentIds.isBlank()) emptyList() else parentIds.split(",").map { it.trim() },
                                        photoUrl = photoUrl,
                                        createdAt = System.currentTimeMillis(),
                                        updatedAt = System.currentTimeMillis()
                                    )
                                    
                                    onAddEditFowl(fowlToSave)
                                } else {
                                    errorMessage = "Failed to get image URL"
                                }
                            } else {
                                errorMessage = result.exceptionOrNull()?.message ?: "Failed to upload image"
                            }
                            
                            isUploading = false
                        }
                    } else {
                        // No image to upload, just save the fowl
                        val fowlToSave = fowl?.copy(
                            name = name,
                            breed = breed,
                            birthDate = birthDate,
                            status = status,
                            lineageNotes = lineageNotes,
                            parentIds = if (parentIds.isBlank()) emptyList() else parentIds.split(",").map { it.trim() },
                            photoUrl = photoUrl,
                            updatedAt = System.currentTimeMillis()
                        ) ?: Fowl(
                            name = name,
                            breed = breed,
                            birthDate = birthDate,
                            status = status,
                            lineageNotes = lineageNotes,
                            parentIds = if (parentIds.isBlank()) emptyList() else parentIds.split(",").map { it.trim() },
                            photoUrl = photoUrl,
                            createdAt = System.currentTimeMillis(),
                            updatedAt = System.currentTimeMillis()
                        )
                        
                        onAddEditFowl(fowlToSave)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isUploading
            ) {
                if (isUploading) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(if (fowl == null) "Add Fowl" else "Save Changes")
            }
        }
        
        // Date picker dialog
        if (showDatePicker) {
            DatePickerDialog(
                onDismissRequest = { showDatePicker = false },
                confirmButton = {
                    TextButton(
                        onClick = {
                            datePickerState.selectedDateMillis?.let { selectedDate ->
                                birthDate = selectedDate
                                birthDateError = false
                                errorMessage = null
                            }
                            showDatePicker = false
                        }
                    ) {
                        Text("OK")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDatePicker = false }) {
                        Text("Cancel")
                    }
                }
            ) {
                DatePicker(
                    state = datePickerState
                    // Note: dateValidator is not available in this version of Compose
                    // We'll handle date validation in the business logic instead
                )
            }
        }
    }
}