package com.rio.rostry.domain.serialization

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rio.rostry.data.database.entity.FarmAssetEntity
import javax.inject.Inject

class AssetParser @Inject constructor() {
    private val gson = Gson()
    
    fun serializeAsset(asset: FarmAssetEntity): String {
        return gson.toJson(asset)
    }

    fun deserializeAsset(json: String): FarmAssetEntity? {
        return try {
            gson.fromJson(json, FarmAssetEntity::class.java)
        } catch (e: Exception) {
            null
        }
    }
    
    fun extractMetadata(metadataJson: String?): Map<String, String> {
        if (metadataJson.isNullOrBlank()) return emptyMap()
        val type = object : TypeToken<Map<String, String>>() {}.type
        return try {
            gson.fromJson(metadataJson, type) ?: emptyMap()
        } catch (e: Exception) {
            emptyMap()
        }
    }
}
