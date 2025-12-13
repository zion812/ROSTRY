package com.rio.rostry.ui.traceability

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TraceabilityScreen(
    vm: TraceabilityViewModel,
    productId: String,
    onBack: () -> Unit,
    onScanQr: () -> Unit
) {
    val state by vm.state.collectAsState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    LaunchedEffect(productId) {
        vm.load(productId)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Traceability & Lineage") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        scope.launch {
                            val uri = vm.generateLineageProof(productId)
                            val shareIntent = android.content.Intent(android.content.Intent.ACTION_SEND).apply {
                                type = "application/pdf"
                                putExtra(android.content.Intent.EXTRA_STREAM, uri)
                                addFlags(android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(android.content.Intent.createChooser(shareIntent, "Share Lineage Proof"))
                        }
                    }) {
                        Icon(Icons.Filled.Share, contentDescription = "Export PDF")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (state.loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.error != null) {
                Text(
                    text = state.error ?: "Unknown error",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                FamilyTreeView(
                    rootId = state.rootId,
                    layersUp = state.layersUp,
                    layersDown = state.layersDown,
                    edges = state.edges,
                    onNodeClick = { nodeId -> vm.selectNode(nodeId) },
                    onScan = onScanQr,
                    onLoadMoreUp = { vm.loadMoreAncestors() },
                    onLoadMoreDown = { vm.loadMoreDescendants() },
                    nodeMetadata = state.nodeMetadata
                )
            }

            // Node Details Sheet
            if (state.selectedNodeId != null && state.selectedNodeEvents != null) {
                val meta = state.nodeMetadata[state.selectedNodeId] ?: TraceabilityViewModel.NodeMetadata(
                    productId = state.selectedNodeId!!,
                    name = "Unknown",
                    breed = null,
                    stage = null,
                    ageWeeks = null,
                    healthScore = 0,
                    lifecycleStatus = null
                )
                
                NodeEventTimelineSheet(
                    nodeMetadata = meta,
                    events = state.selectedNodeEvents!!,
                    onDismiss = { vm.clearSelection() }
                )
            }
        }
    }
}
