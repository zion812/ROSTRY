package com.rio.rostry.ui.showcase.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.unit.dp
import com.rio.rostry.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChipsShowcase() {
    var filterA by remember { mutableStateOf(true) }
    var filterB by remember { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.space_large)) {
        Text("Chips", style = MaterialTheme.typography.titleLarge)

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            AssistChip(onClick = { /* assist */ }, label = { Text("Assist") })
            SuggestionChip(onClick = { /* suggest */ }, label = { Text("Suggestion") })
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            FilterChip(selected = filterA, onClick = { filterA = !filterA }, label = { Text("Filter A") })
            FilterChip(selected = filterB, onClick = { filterB = !filterB }, label = { Text("Filter B") })
        }
    }
}
