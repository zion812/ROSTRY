package com.rio.rostry.ui.enthusiast.showrecords

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.rio.rostry.data.database.entity.ShowRecordEntity
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowEntryScreen(
    productId: String,
    navController: NavController,
    viewModel: ShowRecordsViewModel = hiltViewModel()
) {
    var eventName by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var dateMillis by remember { mutableStateOf(System.currentTimeMillis()) }
    var recordType by remember { mutableStateOf(ShowRecordEntity.TYPE_SHOW) }
    var result by remember { mutableStateOf("") }
    var placement by remember { mutableStateOf("") }
    var judgeName by remember { mutableStateOf("") }
    var points by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    
    // UI State for Date Picker
    val datePickerState = rememberDatePickerState(initialSelectedDateMillis = dateMillis)
    var showDatePicker by remember { mutableStateOf(false) }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    dateMillis = datePickerState.selectedDateMillis ?: System.currentTimeMillis()
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Show Record") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            OutlinedTextField(
                value = eventName,
                onValueChange = { eventName = it },
                label = { Text("Event Name") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text("Location") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Words)
            )
            
            // Record Type Dropdown (Simplification: just a text field or radio buttons for now)
            // Ideally should be a dropdown
            Text("Type: $recordType") // Placeholder for selector

            OutlinedTextField(
                value = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date(dateMillis)),
                onValueChange = { },
                label = { Text("Date") },
                modifier = Modifier.fillMaxWidth(),
                readOnly = true,
                trailingIcon = {
                    IconButton(onClick = { showDatePicker = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Select Date")
                    }
                }
            )

            OutlinedTextField(
                value = result,
                onValueChange = { result = it },
                label = { Text("Result (e.g., 1st, Win)") },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = placement,
                onValueChange = { placement = it },
                label = { Text("Placement (Numeric, Optional)") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = points,
                onValueChange = { points = it },
                label = { Text("Points/Score") },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
            )

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                label = { Text("Notes/Judge's Comments") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences)
            )

            Button(
                onClick = {
                    viewModel.addRecord(
                        productId = productId,
                        eventName = eventName,
                        recordType = recordType,
                        result = result,
                        eventDate = dateMillis,
                        placement = placement.toIntOrNull(),
                        notes = notes
                        // Add other fields as needed in ViewModel
                    )
                    navController.popBackStack()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = eventName.isNotBlank() && result.isNotBlank()
            ) {
                Text("Save Record")
            }
        }
    }
}
