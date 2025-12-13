package com.rio.rostry.ui.enthusiast.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.rio.rostry.ui.theme.Dimens
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.compose.runtime.LaunchedEffect

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun TransferFilterCard(
    statusFilter: String,
    typeFilter: String,
    startDate: Long?,
    endDate: Long?,
    onStatusFilterChange: (String) -> Unit,
    onTypeFilterChange: (String) -> Unit,
    onDateRangeChange: (Long?, Long?) -> Unit,
    modifier: Modifier = Modifier
) {
    val isExpanded = rememberSaveable { mutableStateOf(false) }
    val rotation = animateFloatAsState(
        targetValue = if (isExpanded.value) 180f else 0f,
        label = "filter_rotation"
    )

    // Local state for date text fields
    val startDateText = remember { mutableStateOf("") }
    val endDateText = remember { mutableStateOf("") }
    val dateError = remember { mutableStateOf<String?>(null) }

    // Initialize and update text fields when dates change
    LaunchedEffect(startDate, endDate) {
        startDateText.value = startDate?.let {
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        } ?: ""
        endDateText.value = endDate?.let {
            Instant.ofEpochMilli(it).atZone(ZoneId.systemDefault()).toLocalDate()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
        } ?: ""
    }

    // Calculate active filter count
    val activeCount =
        (if (statusFilter != "ALL") 1 else 0) +
                (if (typeFilter != "ALL") 1 else 0) +
                (if (startDate != null || endDate != null) 1 else 0)

    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(Dimens.space_large)) {
            // Header Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Filled.FilterList,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(Dimens.space_medium))
                Text(
                    "Filters",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                if (activeCount > 0) {
                    Badge { Text("$activeCount active") }
                }
                Spacer(Modifier.width(Dimens.space_medium))
                IconButton(
                    onClick = { isExpanded.value = !isExpanded.value },
                    modifier = Modifier.semantics {
                        contentDescription = if (isExpanded.value) "Collapse filters" else "Expand filters"
                    }
                ) {
                    Icon(
                        Icons.Filled.ExpandMore,
                        contentDescription = null,
                        modifier = Modifier.rotate(rotation.value)
                    )
                }
            }

            // Collapsible Content
            AnimatedVisibility(
                visible = isExpanded.value,
                enter = expandVertically(),
                exit = shrinkVertically()
            ) {
                Column {
                    Spacer(Modifier.height(Dimens.space_xl))

                    // Status Section
                    Text("Status", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(Dimens.space_medium))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(Dimens.space_small)) {
                        val statuses = listOf("ALL", "PENDING", "VERIFIED", "COMPLETED", "DISPUTED")
                        statuses.forEach { status ->
                            FilterChip(
                                selected = statusFilter == status,
                                onClick = { onStatusFilterChange(status) },
                                label = { Text(status.lowercase().replaceFirstChar { it.uppercase() }) }
                            )
                        }
                    }

                    Spacer(Modifier.height(Dimens.space_xl))

                    // Type Section
                    Text("Transfer Type", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(Dimens.space_medium))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(Dimens.space_small)) {
                        val types = listOf(
                            "ALL" to null,
                            "INCOMING" to Icons.Filled.ArrowDownward,
                            "OUTGOING" to Icons.Filled.ArrowUpward
                        )
                        types.forEach { (type, icon) ->
                            FilterChip(
                                selected = typeFilter == type,
                                onClick = { onTypeFilterChange(type) },
                                label = { Text(type.lowercase().replaceFirstChar { it.uppercase() }) },
                                leadingIcon = icon?.let { { Icon(it, contentDescription = null) } }
                            )
                        }
                    }

                    Spacer(Modifier.height(Dimens.space_xl))

                    // Date Range Section
                    Text("Date Range", style = MaterialTheme.typography.labelLarge)
                    Spacer(Modifier.height(Dimens.space_medium))
                    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.space_medium)) {
                        OutlinedTextField(
                            value = startDateText.value,
                            onValueChange = { startDateText.value = it; dateError.value = null },
                            label = { Text("Start Date") },
                            modifier = Modifier.weight(1f),
                            isError = dateError.value != null
                        )
                        OutlinedTextField(
                            value = endDateText.value,
                            onValueChange = { endDateText.value = it; dateError.value = null },
                            label = { Text("End Date") },
                            modifier = Modifier.weight(1f),
                            isError = dateError.value != null
                        )
                    }
                    dateError.value?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(Modifier.height(Dimens.space_medium))
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(Dimens.space_small)) {
                        val quicks = listOf("Today", "Last 7 days", "Last 30 days", "Clear")
                        quicks.forEach { quick ->
                            FilterChip(
                                selected = false,
                                onClick = {
                                    val today = LocalDate.now()
                                    when (quick) {
                                        "Today" -> {
                                            startDateText.value = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                            endDateText.value = startDateText.value
                                        }
                                        "Last 7 days" -> {
                                            val start = today.minusDays(7)
                                            startDateText.value = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                            endDateText.value = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                        }
                                        "Last 30 days" -> {
                                            val start = today.minusDays(30)
                                            startDateText.value = start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                            endDateText.value = today.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                        }
                                        "Clear" -> {
                                            startDateText.value = ""
                                            endDateText.value = ""
                                        }
                                    }
                                    dateError.value = null
                                },
                                label = { Text(quick) }
                            )
                        }
                    }
                    Spacer(Modifier.height(Dimens.space_medium))
                    Row {
                        OutlinedButton(onClick = {
                            try {
                                val start = startDateText.value.takeIf { it.isNotEmpty() }?.let {
                                    LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                        .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                                }
                                val end = endDateText.value.takeIf { it.isNotEmpty() }?.let {
                                    LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                        .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                                }
                                if (start != null && end != null && end < start) {
                                    dateError.value = "End date must be after start date"
                                } else {
                                    onDateRangeChange(start, end)
                                }
                            } catch (e: Exception) {
                                dateError.value = "Invalid date format"
                            }
                        }) {
                            Text("Apply")
                        }
                        Spacer(Modifier.width(Dimens.space_medium))
                        TextButton(onClick = {
                            startDateText.value = ""
                            endDateText.value = ""
                            dateError.value = null
                            onDateRangeChange(null, null)
                        }) {
                            Text("Clear All")
                        }
                    }

                    Spacer(Modifier.height(Dimens.space_xl))

                    // Footer Row
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                        TextButton(onClick = {
                            onStatusFilterChange("ALL")
                            onTypeFilterChange("ALL")
                            onDateRangeChange(null, null)
                            startDateText.value = ""
                            endDateText.value = ""
                            dateError.value = null
                        }) {
                            Text("Reset")
                        }
                        Spacer(Modifier.width(Dimens.space_medium))
                        Button(onClick = {
                            try {
                                val start = startDateText.value.takeIf { it.isNotEmpty() }?.let {
                                    LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                        .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                                }
                                val end = endDateText.value.takeIf { it.isNotEmpty() }?.let {
                                    LocalDate.parse(it, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                        .atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli()
                                }
                                if (start != null && end != null && end < start) {
                                    dateError.value = "End date must be after start date"
                                } else {
                                    onDateRangeChange(start, end)
                                }
                            } catch (e: Exception) {
                                dateError.value = "Invalid date format"
                            }
                        }) {
                            Text("Apply Filters")
                        }
                    }
                }
            }
        }
    }
}
