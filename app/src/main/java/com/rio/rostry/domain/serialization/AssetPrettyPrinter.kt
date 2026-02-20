package com.rio.rostry.domain.serialization

import com.rio.rostry.data.database.entity.FarmAssetEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class AssetPrettyPrinter @Inject constructor(
    private val parser: AssetParser
) {
    private val dateFormat = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())

    fun formatAssetSummary(asset: FarmAssetEntity): String {
        return buildString {
            appendLine("Asset ID: ${asset.assetId}")
            appendLine("Type: ${asset.assetType} - ${asset.breed}")
            appendLine("Status: ${asset.status}")
            appendLine("Created: ${dateFormat.format(Date(asset.createdAt))}")
            if (asset.birthDate != null) {
                appendLine("DOB/Hatch Date: ${dateFormat.format(Date(asset.birthDate))}")
            }
            appendLine("Current Location: ${asset.locationName ?: "Unknown"}")
            
            val metadata = parser.extractMetadata(asset.metadataJson)
            if (metadata.isNotEmpty()) {
                appendLine("Metadata:")
                metadata.forEach { (key, value) ->
                    appendLine("  - $key: $value")
                }
            }
        }
    }
}
