package com.rio.rostry.ui.showcase.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.rio.rostry.ui.theme.Dimens

@Composable
fun CardShowcase() {
    Column(verticalArrangement = Arrangement.spacedBy(Dimens.space_large)) {
        Text("Cards", style = MaterialTheme.typography.titleLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(Dimens.space_medium)) {
            ElevatedCard { Box(Modifier.size(140.dp, 100.dp).padding(Dimens.space_medium)) { Text("Elevated Card") } }
            Card { Box(Modifier.size(140.dp, 100.dp).padding(Dimens.space_medium)) { Text("Filled Card") } }
            OutlinedCard { Box(Modifier.size(140.dp, 100.dp).padding(Dimens.space_medium)) { Text("Outlined Card") } }
        }
        Card { Column(Modifier.fillMaxWidth().padding(Dimens.space_large)) {
            Text("Product Card", style = MaterialTheme.typography.titleMedium)
            Spacer(Modifier.height(Dimens.space_small))
            Text("Healthy Broilers - 2kg avg", style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(Dimens.space_medium))
            Row(horizontalArrangement = Arrangement.spacedBy(Dimens.space_medium)) {
                TextButton(onClick = {}) { Text("Details") }
                Button(onClick = {}) { Text("Add to Cart") }
            }
        } }
    }
}
