package com.rio.rostry.ui.showcase.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.rio.rostry.ui.theme.Dimens
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.ShoppingCart

@Composable
fun ButtonShowcase() {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.space_large)) {
        Text("Buttons", style = MaterialTheme.typography.titleLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.space_medium)) {
            Button(onClick = {}) { Text("Primary") }
            Button(onClick = {}, enabled = false) { Text("Disabled") }
            OutlinedButton(onClick = {}) { Text("Secondary") }
            TextButton(onClick = {}) { Text("Tertiary") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.space_medium), verticalAlignment = Alignment.CenterVertically) {
            SmallFloatingActionButton(onClick = {}) { Icon(Icons.Default.Add, contentDescription = "Add") }
            FloatingActionButton(onClick = {}) { Icon(Icons.Default.Edit, contentDescription = "Edit") }
            LargeFloatingActionButton(onClick = {}) { Icon(Icons.Default.ShoppingCart, contentDescription = "Cart") }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.space_medium)) {
            IconButton(onClick = {}) { Icon(Icons.Default.Favorite, contentDescription = "Fav") }
            FilledIconButton(onClick = {}) { Icon(Icons.Default.Check, contentDescription = "Check") }
            OutlinedIconButton(onClick = {}) { Icon(Icons.Default.Close, contentDescription = "Close") }
        }
    }
}
