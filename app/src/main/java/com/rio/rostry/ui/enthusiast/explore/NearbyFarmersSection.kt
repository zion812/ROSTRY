package com.rio.rostry.ui.enthusiast.explore

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.rio.rostry.data.database.entity.UserEntity
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.material.icons.filled.Person

@Composable
fun NearbyFarmersSection(
    farmers: List<UserEntity>,
    onFarmerClick: (String) -> Unit
) {
    if (farmers.isEmpty()) return

    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Nearby Farmers",
                style = MaterialTheme.typography.titleMedium
            )
            TextButton(onClick = { /* View all map view */ }) {
                Text("View Map")
            }
        }
        
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(farmers) { farmer ->
                FarmerAvatarCard(farmer, onFarmerClick)
            }
        }
    }
}

@Composable
fun FarmerAvatarCard(farmer: UserEntity, onClick: (String) -> Unit) {
    Column(
        modifier = Modifier
            .width(80.dp)
            .clickable { onClick(farmer.userId) },
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AsyncImage(
            model = farmer.profilePictureUrl ?: "",
            contentDescription = farmer.fullName,
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop,
            error = rememberVectorPainter(Icons.Default.Person) // Placeholder
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = farmer.fullName ?: "Unknown",
            style = MaterialTheme.typography.labelMedium,
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
        val location = farmer.farmCity
        if (location != null) {
            Text(
                text = location,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )
        }
    }
}
