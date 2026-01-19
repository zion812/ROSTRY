package com.rio.rostry.ui.farmer.calendar

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.rio.rostry.data.database.entity.FarmEventType
import com.rio.rostry.data.database.entity.RecurrenceType
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventDialog(
    initialDate: Long,
    onDismiss: () -> Unit,
    onConfirm: (String, String, Long, FarmEventType, RecurrenceType, Long) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var eventType by remember { mutableStateOf(FarmEventType.VACCINATION) }
    var recurrence by remember { mutableStateOf(RecurrenceType.ONCE) }
    var reminderMinutes by remember { mutableStateOf("0") }
    var scheduledAt by remember { mutableStateOf(initialDate) }
    
    // Simple state for dropdowns
    var typeExpanded by remember { mutableStateOf(false) }
    var recurrenceExpanded by remember { mutableStateOf(false) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = MaterialTheme.shapes.extraLarge,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface,
            )
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Schedule Event",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onSurface
                )

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2
                )

                // Date & Time Selection
                val formattedDateTime = remember(scheduledAt) {
                    Instant.ofEpochMilli(scheduledAt)
                        .atZone(ZoneId.systemDefault())
                        .format(DateTimeFormatter.ofPattern("MMM dd, yyyy HH:mm"))
                }
                
                var showDatePicker by remember { mutableStateOf(false) }
                var showTimePicker by remember { mutableStateOf(false) }
                
                // DatePicker State (Experimental)
                val datePickerState = rememberDatePickerState(
                    initialSelectedDateMillis = scheduledAt
                )
                
                // TimePicker State (Experimental)
                val timePickerState = rememberTimePickerState(
                    initialHour = Instant.ofEpochMilli(scheduledAt).atZone(ZoneId.systemDefault()).hour,
                    initialMinute = Instant.ofEpochMilli(scheduledAt).atZone(ZoneId.systemDefault()).minute
                )

                OutlinedTextField(
                    value = formattedDateTime,
                    onValueChange = {},
                    label = { Text("Date & Time") },
                    readOnly = true,
                    trailingIcon = { Icon(Icons.Default.DateRange, null) },
                    modifier = Modifier
                        .fillMaxWidth(),
                    interactionSource = remember { androidx.compose.foundation.interaction.MutableInteractionSource() }
                        .also { interactionSource ->
                            LaunchedEffect(interactionSource) {
                                interactionSource.interactions.collect {
                                    if (it is androidx.compose.foundation.interaction.PressInteraction.Release) {
                                        showDatePicker = true
                                    }
                                }
                            }
                        }
                )

                if (showDatePicker) {
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(
                                onClick = {
                                    showDatePicker = false
                                    showTimePicker = true
                                }
                            ) {
                                Text("Next")
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
                
                if (showTimePicker) {
                    Dialog(
                        onDismissRequest = { showTimePicker = false }
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.large,
                            tonalElevation = 6.dp,
                            modifier = Modifier.wrapContentSize()
                        ) {
                            Column(
                                modifier = Modifier.padding(24.dp),
                                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                            ) {
                                Text(
                                    "Select Time",
                                    style = MaterialTheme.typography.labelLarge,
                                    modifier = Modifier.padding(bottom = 20.dp)
                                )
                                TimePicker(state = timePickerState)
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(top = 20.dp),
                                    horizontalArrangement = Arrangement.End
                                ) {
                                    TextButton(onClick = { showTimePicker = false }) {
                                        Text("Cancel")
                                    }
                                    TextButton(
                                        onClick = {
                                            showTimePicker = false
                                            val selectedDateMillis = datePickerState.selectedDateMillis ?: scheduledAt
                                            val selectedDate = Instant.ofEpochMilli(selectedDateMillis).atZone(ZoneId.of("UTC")).toLocalDate()
                                            
                                            val finalDateTime = selectedDate.atTime(timePickerState.hour, timePickerState.minute)
                                                .atZone(ZoneId.systemDefault())
                                                .toInstant()
                                                .toEpochMilli()
                                                
                                            scheduledAt = finalDateTime
                                        }
                                    ) {
                                        Text("Confirm")
                                    }
                                }
                            }
                        }
                    }
                }

                // Event Type Dropdown
                ExposedDropdownMenuBox(
                    expanded = typeExpanded,
                    onExpandedChange = { typeExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = eventType.name,
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
                        FarmEventType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name) },
                                onClick = {
                                    eventType = type
                                    typeExpanded = false
                                }
                            )
                        }
                    }
                }

                // Recurrence Dropdown
                ExposedDropdownMenuBox(
                    expanded = recurrenceExpanded,
                    onExpandedChange = { recurrenceExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = recurrence.name,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Recurrence") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = recurrenceExpanded) },
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = recurrenceExpanded,
                        onDismissRequest = { recurrenceExpanded = false }
                    ) {
                        RecurrenceType.values().forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.name) },
                                onClick = {
                                    recurrence = type
                                    recurrenceExpanded = false
                                }
                            )
                        }
                    }
                }

                OutlinedTextField(
                    value = reminderMinutes,
                    onValueChange = { if (it.all { char -> char.isDigit() }) reminderMinutes = it },
                    label = { Text("Reminder (min before)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Cancel")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            if (title.isNotBlank()) {
                                onConfirm(
                                    title,
                                    description,
                                    scheduledAt,
                                    eventType,
                                    recurrence,
                                    reminderMinutes.toLongOrNull() ?: 0L
                                )
                            }
                        }
                    ) {
                        Text("Schedule")
                    }
                }
            }
        }
    }
}
