package com.rio.rostry.ui.shared.gallery.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rio.rostry.domain.model.media.MediaFilter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GalleryFilterRow(
    filter: MediaFilter,
    availableAgeGroups: List<String>,
    availableSourceTypes: List<String>,
    onFilterChanged: (MediaFilter) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.FilterList,
            contentDescription = "Filters",
            modifier = Modifier.padding(end = 4.dp)
        )

        // Mocking age group selection for simplicity
        availableAgeGroups.forEach { ageGroup ->
            val isSelected = filter.ageGroups.any { it.name == ageGroup }
            FilterChip(
                selected = isSelected,
                onClick = {
                    // Logic to toggle age group in filter
                },
                label = { Text(ageGroup) }
            )
        }

        availableSourceTypes.forEach { sourceType ->
            val isSelected = filter.sourceTypes.any { it.name == sourceType }
            FilterChip(
                selected = isSelected,
                onClick = {
                    // Logic to toggle source type in filter
                },
                label = { Text(sourceType) }
            )
        }
    }
}
