package com.rio.rostry.core.model

import com.rio.rostry.domain.model.LifecycleStage

/**
 * Graph data for ancestry/descendancy visualization.
 */
data class GraphData(
    val nodes: List<NodeMetadata>,
    val edges: List<Pair<String, String>>
)

/**
 * Metadata for a graph node (product).
 */
data class NodeMetadata(
    val productId: String,
    val name: String,
    val breed: String?,
    val stage: LifecycleStage?,
    val ageWeeks: Int?,
    val healthScore: Int,
    val lifecycleStatus: String?
)
