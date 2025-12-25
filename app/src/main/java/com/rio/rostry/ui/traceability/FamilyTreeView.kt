package com.rio.rostry.ui.traceability

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
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
import androidx.compose.foundation.layout.size
import com.rio.rostry.domain.model.LifecycleStage
import com.rio.rostry.ui.traceability.TraceabilityViewModel.NodeMetadata

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
    modifier: Modifier = Modifier,
    onNodeClick: ((String) -> Unit)? = null,
    onScan: (() -> Unit)? = null,
    onLoadMoreUp: (() -> Unit)? = null,
    onLoadMoreDown: (() -> Unit)? = null,
    nodeMetadata: Map<String, NodeMetadata> = emptyMap(),
) {
    // Pan/zoom state
    var scale by remember(resetKey) { mutableFloatStateOf(1f) }
    var offsetX by remember(resetKey) { mutableFloatStateOf(0f) }
    var offsetY by remember(resetKey) { mutableFloatStateOf(0f) }
    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.5f, 3f)
        offsetX += panChange.x
        offsetY += panChange.y
    }

    Box(modifier = modifier.fillMaxSize()) {
    Column(
        modifier = Modifier
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Track node centers relative to the transformed container
        val nodeCenters = remember(resetKey) { mutableStateMapOf<String, Offset>() }
        // Container origin in root coordinates to compute local positions
        var containerOrigin by remember(resetKey) { mutableStateOf(Offset.Zero) }
        var containerSize by remember(resetKey) { mutableStateOf(androidx.compose.ui.unit.IntSize.Zero) }

        // One unified transformed container for nodes and connectors
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
                .onGloballyPositioned { coords ->
                    containerOrigin = coords.positionInRoot()
                    containerSize = coords.size
                }
        ) {
            // Content column inside transformed space
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // Ancestors
                if (layersUp.isNotEmpty()) {
                    Text("Ancestors", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        items(layersUp.toSortedMap().entries.toList(), key = { it.key }) { entry ->
                            val depth = entry.key
                            val ids = entry.value
                            // Viewport in model space for culling
                            val w = containerSize.width.toFloat().coerceAtLeast(1f)
                            val h = containerSize.height.toFloat().coerceAtLeast(1f)
                            val invScale = if (scale != 0f) 1f / scale else 1f
                            val viewLeft = -offsetX * invScale
                            val viewTop = -offsetY * invScale
                            val viewRight = viewLeft + w * invScale
                            val viewBottom = viewTop + h * invScale
                            val pad = 96f * invScale // inflate to avoid pop-in
                            val l = viewLeft - pad
                            val t = viewTop - pad
                            val r = viewRight + pad
                            val b = viewBottom + pad
                            val totalNodes = layersUp.values.sumOf { it.size } + 1 + layersDown.values.sumOf { it.size }
                            val visibleIds = if (totalNodes > 60 && nodeCenters.isNotEmpty()) {
                                ids.filter { id ->
                                    nodeCenters[id]?.let { c -> c.x in l..r && c.y in t..b } ?: true
                                }
                            } else ids
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("Gen -$depth:", fontWeight = FontWeight.SemiBold)
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    items(visibleIds, key = { it }) { id ->
                                        val meta = nodeMetadata[id]
                                        val bgColor = when (meta?.stage) {
                                            LifecycleStage.CHICK -> Color(0xFFE3F2FD)
                                            LifecycleStage.GROWER -> Color(0xFFE8F5E9)
                                            LifecycleStage.LAYER -> Color(0xFFFFF9C4)
                                            LifecycleStage.BREEDER -> Color(0xFFF3E5F5)
                                            else -> Color(0xFFE3F2FD)
                                        }
                                        val healthColor = when {
                                            (meta?.healthScore ?: 0) > 70 -> Color.Green
                                            (meta?.healthScore ?: 0) > 50 -> Color.Yellow
                                            else -> Color.Red
                                        }
                                        val statusIcon = when (meta?.lifecycleStatus) {
                                            "QUARANTINE" -> "⚠️"
                                            "ACTIVE" -> "✓"
                                            else -> ""
                                        }
                                        Box(
                                            modifier = Modifier
                                                .background(bgColor)
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                                .let { base -> if (onNodeClick != null) base.clickable { onNodeClick(id) } else base }
                                                .onGloballyPositioned { c ->
                                                    // store centers relative to container
                                                    val centerInRoot = c.positionInRoot() + Offset(c.size.width / 2f, c.size.height / 2f)
                                                    nodeCenters[id] = centerInRoot - containerOrigin
                                                }
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(meta?.name ?: id, color = Color(0xFF0D47A1))
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(meta?.stage?.name ?: "", style = MaterialTheme.typography.bodySmall)
                                                    Box(modifier = Modifier.size(8.dp).background(healthColor))
                                                    meta?.ageWeeks?.let { Text("${it}w", style = MaterialTheme.typography.bodySmall) }
                                                    Text(statusIcon, style = MaterialTheme.typography.bodySmall)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Root node
                Text("Root", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
                val rootMeta = nodeMetadata[rootId]
                val rootBgColor = when (rootMeta?.stage) {
                    LifecycleStage.CHICK -> Color(0xFFE3F2FD)
                    LifecycleStage.GROWER -> Color(0xFFE8F5E9)
                    LifecycleStage.LAYER -> Color(0xFFFFF9C4)
                    LifecycleStage.BREEDER -> Color(0xFFF3E5F5)
                    else -> Color(0xFFF1F8E9)
                }
                val rootHealthColor = when {
                    (rootMeta?.healthScore ?: 0) > 70 -> Color.Green
                    (rootMeta?.healthScore ?: 0) > 50 -> Color.Yellow
                    else -> Color.Red
                }
                val rootStatusIcon = when (rootMeta?.lifecycleStatus) {
                    "QUARANTINE" -> "⚠️"
                    "ACTIVE" -> "✓"
                    else -> ""
                }
                Box(
                    modifier = Modifier
                        .background(rootBgColor)
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .onGloballyPositioned { c ->
                            val centerInRoot = c.positionInRoot() + Offset(c.size.width / 2f, c.size.height / 2f)
                            nodeCenters[rootId] = centerInRoot - containerOrigin
                        }
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(rootMeta?.name ?: rootId, color = Color(0xFF1B5E20), fontWeight = FontWeight.Bold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(rootMeta?.stage?.name ?: "", style = MaterialTheme.typography.bodySmall)
                            Box(modifier = Modifier.size(8.dp).background(rootHealthColor))
                            rootMeta?.ageWeeks?.let { Text("${it}w", style = MaterialTheme.typography.bodySmall) }
                            Text(rootStatusIcon, style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }

                // Descendants
                if (layersDown.isNotEmpty()) {
                    Text("Descendants", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 12.dp))
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        items(layersDown.toSortedMap().entries.toList(), key = { it.key }) { entry ->
                            val depth = entry.key
                            val ids = entry.value
                            // Viewport in model space for culling
                            val w = containerSize.width.toFloat().coerceAtLeast(1f)
                            val h = containerSize.height.toFloat().coerceAtLeast(1f)
                            val invScale = if (scale != 0f) 1f / scale else 1f
                            val viewLeft = -offsetX * invScale
                            val viewTop = -offsetY * invScale
                            val viewRight = viewLeft + w * invScale
                            val viewBottom = viewTop + h * invScale
                            val pad = 96f * invScale
                            val l = viewLeft - pad
                            val t = viewTop - pad
                            val r = viewRight + pad
                            val b = viewBottom + pad
                            val totalNodes = layersUp.values.sumOf { it.size } + 1 + layersDown.values.sumOf { it.size }
                            val visibleIds = if (totalNodes > 60 && nodeCenters.isNotEmpty()) {
                                ids.filter { id ->
                                    nodeCenters[id]?.let { c -> c.x in l..r && c.y in t..b } ?: true
                                }
                            } else ids
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("Gen +$depth:", fontWeight = FontWeight.SemiBold)
                                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically) {
                                    items(visibleIds, key = { it }) { id ->
                                        val meta = nodeMetadata[id]
                                        val bgColor = when (meta?.stage) {
                                            LifecycleStage.CHICK -> Color(0xFFE3F2FD)
                                            LifecycleStage.GROWER -> Color(0xFFE8F5E9)
                                            LifecycleStage.LAYER -> Color(0xFFFFF9C4)
                                            LifecycleStage.BREEDER -> Color(0xFFF3E5F5)
                                            else -> Color(0xFFFFF3E0)
                                        }
                                        val healthColor = when {
                                            (meta?.healthScore ?: 0) > 70 -> Color.Green
                                            (meta?.healthScore ?: 0) > 50 -> Color.Yellow
                                            else -> Color.Red
                                        }
                                        val statusIcon = when (meta?.lifecycleStatus) {
                                            "QUARANTINE" -> "⚠️"
                                            "ACTIVE" -> "✓"
                                            else -> ""
                                        }
                                        Box(
                                            modifier = Modifier
                                                .background(bgColor)
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                                .let { base -> if (onNodeClick != null) base.clickable { onNodeClick(id) } else base }
                                                .onGloballyPositioned { c ->
                                                    val centerInRoot = c.positionInRoot() + Offset(c.size.width / 2f, c.size.height / 2f)
                                                    nodeCenters[id] = centerInRoot - containerOrigin
                                                }
                                        ) {
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Text(meta?.name ?: id, color = Color(0xFFE65100))
                                                Row(verticalAlignment = Alignment.CenterVertically) {
                                                    Text(meta?.stage?.name ?: "", style = MaterialTheme.typography.bodySmall)
                                                    Box(modifier = Modifier.size(8.dp).background(healthColor))
                                                    meta?.ageWeeks?.let { Text("${it}w", style = MaterialTheme.typography.bodySmall) }
                                                    Text(statusIcon, style = MaterialTheme.typography.bodySmall)
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Connectors drawn within the same transformed space
            if (edges.isNotEmpty()) {
                Canvas(modifier = Modifier.matchParentSize()) {
                    clipRect {
                        val w = containerSize.width.toFloat().coerceAtLeast(1f)
                        val h = containerSize.height.toFloat().coerceAtLeast(1f)
                        val invScale = if (scale != 0f) 1f / scale else 1f
                        val viewLeft = -offsetX * invScale
                        val viewTop = -offsetY * invScale
                        val viewRight = viewLeft + w * invScale
                        val viewBottom = viewTop + h * invScale
                        val pad = 48f * invScale
                        val l = viewLeft - pad
                        val t = viewTop - pad
                        val r = viewRight + pad
                        val b = viewBottom + pad
                        edges.forEach { (from, to) ->
                            val p1 = nodeCenters[from]
                            val p2 = nodeCenters[to]
                            if (p1 != null && p2 != null) {
                                val p1In = p1.x in l..r && p1.y in t..b
                                val p2In = p2.x in l..r && p2.y in t..b
                                if (p1In || p2In) {
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
        }

        // Progressive loading controls
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.padding(top = 12.dp)) {
            if (onLoadMoreUp != null) {
                androidx.compose.material3.OutlinedButton(onClick = onLoadMoreUp) { Text("Load more ancestors") }
            }
            if (onLoadMoreDown != null) {
                androidx.compose.material3.OutlinedButton(onClick = onLoadMoreDown) { Text("Load more descendants") }
            }
        }
    }

    // Scan FAB
    if (onScan != null) {
        Box(modifier = Modifier.fillMaxSize()) {
            FloatingActionButton(
                onClick = onScan,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                Icon(Icons.Filled.QrCodeScanner, contentDescription = "Scan QR")
            }
        }
    }
    }
}