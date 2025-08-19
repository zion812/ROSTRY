package com.rio.rostry.ui.screens.fowl

import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rio.rostry.data.models.Fowl
import com.rio.rostry.utils.rememberImagePickerLauncher
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
    var name by remember { mutableStateOf(fowl?.name ?: "") }
    var breed by remember { mutableStateOf(fowl?.breed ?: "") }
    var birthDate by remember { mutableStateOf(fowl?.birthDate ?: System.currentTimeMillis()) }
    var status by remember { mutableStateOf(fowl?.status ?: "growing") }
    var lineageNotes by remember { mutableStateOf(fowl?.lineageNotes ?: "") }
    var parentIds by remember { mutableStateOf(fowl?.parentIds?.joinToString(", ") ?: "") }
    var photoUrl by remember { mutableStateOf(fowl?.photoUrl ?: "") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    
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
        // In a real implementation, you would upload the image and get a URL
        // For now, we'll just use a placeholder URL
        photoUrl = "https://example.com/temp-image-${System.currentTimeMillis()}.jpg"
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
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
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
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Error message display
            errorMessage?.let { message ->
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                ) {
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
            
            // Photo section
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp)
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
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Select photo")
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (selectedImageUri != null || photoUrl.isNotBlank()) "Change Photo" else "Select Photo")
                    }
                }
            }
            
            OutlinedTextField(
                value = name,
                onValueChange = { 
                    name = it
                    // Clear error when user starts typing
                    if (nameError) nameError = false
                    if (errorMessage != null) errorMessage = null
                },
                label = { Text("Name *") },
                isError = nameError,
                supportingText = {
                    if (nameError) {
                        Text("Name is required")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = breed,
                onValueChange = { 
                    breed = it
                    // Clear error when user starts typing
                    if (breedError) breedError = false
                    if (errorMessage != null) errorMessage = null
                },
                label = { Text("Breed *") },
                isError = breedError,
                supportingText = {
                    if (breedError) {
                        Text("Breed is required")
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Date picker implementation
            OutlinedTextField(
                value = formatDateInternal(birthDate),
                onValueChange = { /* Handle date change */ },
                label = { Text("Birth Date *") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showDatePicker = true },
                readOnly = true,
                isError = birthDateError,
                supportingText = {
                    if (birthDateError) {
                        Text("Birth date cannot be in the future")
                    }
                }
            )
            
            // Date picker dialog
            if (showDatePicker) {
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                datePickerState.selectedDateMillis?.let { selectedDate ->
                                    birthDate = selectedDate
                                    // Clear error when user selects a date
                                    if (birthDateError) birthDateError = false
                                    if (errorMessage != null) errorMessage = null
                                }
                                showDatePicker = false
                            }
                        ) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                showDatePicker = false
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

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
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = expanded
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    statusOptions.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption) },
                            onClick = {
                                status = selectionOption
                                expanded = false
                                // Clear error when user selects a status
                                if (errorMessage != null) errorMessage = null
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = parentIds,
                onValueChange = { 
                    parentIds = it
                    // Clear error when user starts typing
                    if (errorMessage != null) errorMessage = null
                },
                label = { Text("Parent IDs (comma separated)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = lineageNotes,
                onValueChange = { 
                    lineageNotes = it
                    // Clear error when user starts typing
                    if (errorMessage != null) errorMessage = null
                },
                label = { Text("Lineage Notes") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    // Validate form
                    var hasError = false
                    
                    if (name.isBlank()) {
                        nameError = true
                        hasError = true
                    }
                    
                    if (breed.isBlank()) {
                        breedError = true
                        hasError = true
                    }
                    
                    // Check if birth date is in the future
                    if (birthDate > System.currentTimeMillis()) {
                        birthDateError = true
                        errorMessage = "Birth date cannot be in the future"
                        hasError = true
                    }
                    
                    if (hasError) {
                        return@Button
                    }
                    
                    val fowlToSave = (fowl ?: Fowl()).copy(
                        name = name.trim(),
                        breed = breed.trim(),
                        birthDate = birthDate,
                        status = status,
                        lineageNotes = lineageNotes.trim(),
                        parentIds = parentIds.split(",").map { it.trim() }.filter { it.isNotEmpty() },
                        photoUrl = photoUrl,
                        updatedAt = System.currentTimeMillis()
                    )
                    onAddEditFowl(fowlToSave)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (fowl == null) "Add Fowl" else "Update Fowl")
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "* Required fields",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun formatDateInternal(timestamp: Long): String {
    return try {
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
        sdf.format(Date(timestamp))
    } catch (e: Exception) {
        "Unknown"
    }
}