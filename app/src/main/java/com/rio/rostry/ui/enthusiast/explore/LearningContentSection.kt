package com.rio.rostry.ui.enthusiast.explore

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

data class LearningModule(
    val id: String,
    val title: String,
    val type: String, // VIDEO, ARTICLE
    val durationOrReadTime: String,
    val thumbnailUrl: String? = null
)

@Composable
fun LearningContentSection(
    modules: List<LearningModule>,
    onModuleClick: (String) -> Unit
) {
    if (modules.isEmpty()) return

    Column(modifier = Modifier.padding(vertical = 16.dp)) {
        Text(
            text = "Learn & Grow",
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )
        
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(modules) { module ->
                LearningCard(module, onModuleClick)
            }
        }
    }
}

@Composable
fun LearningCard(module: LearningModule, onClick: (String) -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .width(200.dp)
            .clickable { onClick(module.id) },
        shape = MaterialTheme.shapes.medium
    ) {
        Column {
            Box(Modifier.height(110.dp).fillMaxWidth()) {
                AsyncImage(
                    model = module.thumbnailUrl,
                    contentDescription = module.title,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    error = rememberVectorPainter(Icons.Default.PlayArrow)
                )
                Surface(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(4.dp),
                    color = MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = module.durationOrReadTime,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                    )
                }
            }
            Column(Modifier.padding(12.dp)) {
                Text(
                    text = module.type,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = module.title,
                    style = MaterialTheme.typography.titleSmall,
                    maxLines = 2
                )
            }
        }
    }
}
