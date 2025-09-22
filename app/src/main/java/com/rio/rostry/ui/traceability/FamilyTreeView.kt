package com.rio.rostry.ui.traceability

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Simple visualizer for family tree layers.
 * Expects a map of depth -> list of productIds. You can feed ancestors/descendants from TraceabilityRepository.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun FamilyTreeView(
    rootId: String,
    layersUp: Map<Int, List<String>>,
    layersDown: Map<Int, List<String>>,
    edges: List<Pair<String, String>> = emptyList(), // pair of (fromId -> toId)
    resetKey: Int = 0,
    modifier: Modifier = Modifier
) {
    val scroll = rememberScrollState()

    // Pan/zoom state
    var scale by remember(resetKey) { mutableFloatStateOf(1f) }
    var offsetX by remember(resetKey) { mutableFloatStateOf(0f) }
    var offsetY by remember(resetKey) { mutableFloatStateOf(0f) }
    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.5f, 3f)
        offsetX += panChange.x
        offsetY += panChange.y
    }

    Column(
        modifier = modifier
            .horizontalScroll(scroll)
            .padding(16.dp)
            ,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Track node centers for connectors
        val nodeCenters = remember(resetKey) { mutableMapOf<String, Offset>() }

        // Ancestors (upwards)
        if (layersUp.isNotEmpty()) {
            Text("Ancestors", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            Box(
                modifier = Modifier
                    .transformable(transformState)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offsetX,
                        translationY = offsetY,
                        transformOrigin = TransformOrigin(0f, 0f)
                    )
            ) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    items(layersUp.toSortedMap().entries.toList(), key = { it.key }) { entry ->
                        val depth = entry.key
                        val ids = entry.value
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("Gen -$depth:", fontWeight = FontWeight.SemiBold)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                items(ids, key = { it }) { id ->
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFE3F2FD))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                            .onGloballyPositioned { c ->
                                                val center = c.positionInRoot() + Offset(c.size.width / 2f, c.size.height / 2f)
                                                nodeCenters[id] = center
                                            }
                                    ) {
                                        Text(id, color = Color(0xFF0D47A1))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        // Root
        Text("Root", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
        Box(
            modifier = Modifier
                .background(Color(0xFFF1F8E9))
                .padding(horizontal = 8.dp, vertical = 4.dp)
                .onGloballyPositioned { c ->
                    val center = c.positionInRoot() + Offset(c.size.width / 2f, c.size.height / 2f)
                    nodeCenters[rootId] = center
                }
        ) {
            Text(rootId, color = Color(0xFF1B5E20))
        }
        // Descendants (downwards)
        if (layersDown.isNotEmpty()) {
            Text("Descendants", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
            Box(
                modifier = Modifier
                    .transformable(transformState)
                    .graphicsLayer(
                        scaleX = scale,
                        scaleY = scale,
                        translationX = offsetX,
                        translationY = offsetY,
                        transformOrigin = TransformOrigin(0f, 0f)
                    )
            ) {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    items(layersDown.toSortedMap().entries.toList(), key = { it.key }) { entry ->
                        val depth = entry.key
                        val ids = entry.value
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                            Text("Gen +$depth:", fontWeight = FontWeight.SemiBold)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                items(ids, key = { it }) { id ->
                                    Box(
                                        modifier = Modifier
                                            .background(Color(0xFFFFF3E0))
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                            .onGloballyPositioned { c ->
                                                val center = c.positionInRoot() + Offset(c.size.width / 2f, c.size.height / 2f)
                                                nodeCenters[id] = center
                                            }
                                    ) {
                                        Text(id, color = Color(0xFFE65100))
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Connectors overlay
        if (edges.isNotEmpty()) {
            Canvas(modifier = Modifier) {
                edges.forEach { (from, to) ->
                    val p1 = nodeCenters[from]
                    val p2 = nodeCenters[to]
                    if (p1 != null && p2 != null) {
                        drawLine(
                            color = Color(0xFF9E9E9E),
                            start = p1,
                            end = p2,
                            strokeWidth = 3f
                        )
                    }
                }
            }
        }
    }
}
