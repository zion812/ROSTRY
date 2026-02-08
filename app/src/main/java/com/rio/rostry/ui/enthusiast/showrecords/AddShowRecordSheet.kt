package com.rio.rostry.ui.enthusiast.showrecords

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.PhotoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.rio.rostry.ui.components.MediaThumbnailRow
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddShowRecordSheet(
    onDismiss: () -> Unit,
    onSave: (String, Long, String, String, String?, String?, String?) -> Unit,
    onAddPhoto: (Uri) -> Unit,
    photos: List<String>,
    onRemovePhoto: (Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val scrollState = rememberScrollState()

    var eventName by remember { mutableStateOf("") }
    var eventDate by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }

    var typeExpanded by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf("SHOW") }
    val recordTypes = listOf("SHOW", "EXHIBITION", "SPARRING", "COMPETITION")

    var resultExpanded by remember { mutableStateOf(false) }
    var selectedResult by remember { mutableStateOf("PARTICIPATED") }
    val resultTypes = listOf("WIN", "LOSS", "DRAW", "1ST", "2ND", "3RD", "PARTICIPATED")

    var judge by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val context = LocalContext.current
    val pickImageLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri != null) {
            try {
                context.contentResolver.takePersistableUriPermission(uri, android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (e: Exception) { }
            onAddPhoto(uri)
        }
    }

    val captureTargetUri = remember { mutableStateOf<Uri?>(null) }
    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            captureTargetUri.value?.let { onAddPhoto(it) }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Add Show Record", style = MaterialTheme.typography.headlineSmall)

            // Event Name
            OutlinedTextField(
                value = eventName,
                onValueChange = { eventName = it },
                label = { Text("Event Name *") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Date Picker
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Date: ${SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(Date(eventDate))}",
                    style = MaterialTheme.typography.bodyLarge
                )
                IconButton(onClick = { showDatePicker = true }) {
                    Icon(Icons.Default.CalendarToday, contentDescription = "Select Date")
                }
            }

            if (showDatePicker) {
                val datePickerState = rememberDatePickerState(initialSelectedDateMillis = eventDate)
                DatePickerDialog(
                    onDismissRequest = { showDatePicker = false },
                    confirmButton = {
                        TextButton(onClick = {
                            datePickerState.selectedDateMillis?.let { eventDate = it }
                            showDatePicker = false
                        }) {
                            Text("OK")
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDatePicker = false }) {
                            Text("Cancel")
                        }
                    }
                ) {
                    DatePicker(state = datePickerState)
                }
            }

            // Record Type Dropdown
            ExposedDropdownMenuBox(
                expanded = typeExpanded,
                onExpandedChange = { typeExpanded = !typeExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedType,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Type") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = typeExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = typeExpanded,
                    onDismissRequest = { typeExpanded = false }
                ) {
                    recordTypes.forEach { type ->
                        DropdownMenuItem(
                            text = { Text(type) },
                            onClick = {
                                selectedType = type
                                typeExpanded = false
                            }
                        )
                    }
                }
            }

            // Result Dropdown
            ExposedDropdownMenuBox(
                expanded = resultExpanded,
                onExpandedChange = { resultExpanded = !resultExpanded },
                modifier = Modifier.fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = selectedResult,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Result") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = resultExpanded) },
                    colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                    modifier = Modifier.menuAnchor().fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = resultExpanded,
                    onDismissRequest = { resultExpanded = false }
                ) {
                    resultTypes.forEach { res ->
                        DropdownMenuItem(
                            text = { Text(res) },
                            onClick = {
                                selectedResult = res
                                resultExpanded = false
                            }
                        )
                    }
                }
            }

            // Judge Name
            OutlinedTextField(
                value = judge,
                onValueChange = { judge = it },
                label = { Text("Judge Name (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            // Notes
            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            // Media
            Text("Photos & Awards", style = MaterialTheme.typography.titleMedium)
            
            if (photos.isNotEmpty()) {
                MediaThumbnailRow(
                    urls = photos,
                    onViewGallery = { /* TODO: Implement Open Gallery if needed */ },
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(
                    onClick = { pickImageLauncher.launch(arrayOf("image/*")) },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.PhotoLibrary, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Gallery")
                }

                OutlinedButton(
                    onClick = {
                        val tmp = kotlin.runCatching { java.io.File.createTempFile("show_rec_", ".jpg", context.cacheDir) }.getOrNull()
                        if (tmp != null) {
                            val uri = FileProvider.getUriForFile(context, context.packageName + ".fileprovider", tmp)
                            captureTargetUri.value = uri
                            takePictureLauncher.launch(uri)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(Icons.Default.CameraAlt, null, modifier = Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Camera")
                }
            }

            Spacer(Modifier.height(16.dp))

            // Save Button
            Button(
                onClick = {
                    onSave(eventName, eventDate, selectedType, selectedResult, null, judge, notes)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = eventName.isNotBlank()
            ) {
                Text("Save Record")
            }
            Spacer(Modifier.height(32.dp))
        }
    }
}
