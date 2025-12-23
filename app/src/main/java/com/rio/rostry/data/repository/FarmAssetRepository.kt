package com.rio.rostry.data.repository

import com.rio.rostry.data.database.entity.FarmAssetEntity
import com.rio.rostry.utils.Resource
import kotlinx.coroutines.flow.Flow

interface FarmAssetRepository {
    
    fun getAssetsByFarmer(farmerId: String): Flow<Resource<List<FarmAssetEntity>>>
    
    fun getAssetById(assetId: String): Flow<Resource<FarmAssetEntity?>>
    
    fun getAssetsByType(farmerId: String, type: String): Flow<Resource<List<FarmAssetEntity>>>
    
    fun getShowcaseAssets(farmerId: String): Flow<Resource<List<FarmAssetEntity>>>
    
    suspend fun addAsset(asset: FarmAssetEntity): Resource<String>
    
    suspend fun updateAsset(asset: FarmAssetEntity): Resource<Unit>
    
    suspend fun deleteAsset(assetId: String): Resource<Unit>
    
    suspend fun updateQuantity(assetId: String, quantity: Double): Resource<Unit>
    
    suspend fun updateHealthStatus(assetId: String, status: String): Resource<Unit>
    
    suspend fun syncAssets(): Resource<Unit>
}
