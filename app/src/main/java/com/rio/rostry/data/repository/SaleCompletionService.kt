package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.OrderDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.FarmAssetDao
import com.rio.rostry.data.database.entity.FarmAssetEntity
import com.rio.rostry.utils.Resource
import timber.log.Timber
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Handles post-sale asset transfer and record keeping.
 * 
 * When an order is delivered:
 * 1. Source FarmAsset is marked as SOLD
 * 2. Buyer receives a new FarmAsset (copy with lineage link)
 */
@Singleton
class SaleCompletionService @Inject constructor(
    private val orderDao: OrderDao,
    private val productDao: ProductDao,
    private val farmAssetDao: FarmAssetDao
) {
    
    /**
     * Complete the sale for a delivered order.
     * Supports partial sales: decrements source asset quantity and only marks as SOLD if remaining is 0.
     * Buyer receives a new FarmAsset with the purchased quantity.
     */
    suspend fun completeOrderSale(orderId: String, buyerId: String): Resource<Int> {
        return try {
            val now = System.currentTimeMillis()
            var assetsTransferred = 0
            
            // 1. Get order items
            val orderItems = orderDao.getOrderItemsList(orderId)
            Timber.d("SaleCompletionService: Processing ${orderItems.size} items for order $orderId")
            
            for (item in orderItems) {
                // 2. Find the product
                val product = productDao.findById(item.productId) ?: continue
                
                // 3. Skip if no source asset
                val sourceAssetId = product.sourceAssetId ?: continue
                val sourceAsset = farmAssetDao.findById(sourceAssetId) ?: continue
                
                val quantitySold = item.quantity.toDouble()
                val currentQty = sourceAsset.quantity ?: 1.0
                val remainingQty = (currentQty - quantitySold).coerceAtLeast(0.0)
                
                Timber.d("SaleCompletionService: Processing asset $sourceAssetId - selling $quantitySold of $currentQty, remaining: $remainingQty")
                
                // 4. Handle based on remaining quantity
                if (remainingQty <= 0) {
                    // Full sale - mark as SOLD
                    farmAssetDao.markAsSold(
                        assetId = sourceAssetId,
                        buyerId = buyerId,
                        price = item.priceAtPurchase * item.quantity,
                        soldAt = now,
                        updatedAt = now
                    )
                } else {
                    // Partial sale - only decrement quantity
                    farmAssetDao.updateQuantity(sourceAssetId, remainingQty, now)
                }
                
                // 5. Create buyer's copy of the asset with sold quantity
                val buyerAsset = createBuyerAsset(sourceAsset, buyerId, quantitySold, now)
                farmAssetDao.insertAsset(buyerAsset)
                
                Timber.d("SaleCompletionService: Created buyer asset ${buyerAsset.assetId} with qty $quantitySold from ${sourceAsset.assetId}")
                assetsTransferred++
            }
            
            Timber.d("SaleCompletionService: Completed $assetsTransferred asset transfers for order $orderId")
            Resource.Success(assetsTransferred)
        } catch (e: Exception) {
            Timber.e(e, "SaleCompletionService: Failed to complete sale for order $orderId")
            Resource.Error(e.message ?: "Failed to complete sale")
        }
    }
    
    /**
     * Create a buyer's copy of the asset with lineage tracking.
     * @param quantitySold The quantity being transferred to the buyer (for partial sales)
     */
    private fun createBuyerAsset(
        sourceAsset: FarmAssetEntity,
        buyerId: String,
        quantitySold: Double,
        now: Long
    ): FarmAssetEntity {
        return sourceAsset.copy(
            assetId = UUID.randomUUID().toString(),
            farmerId = buyerId,
            status = "ACTIVE",
            quantity = quantitySold, // Set to sold quantity for partial sales
            
            // Lineage tracking
            previousOwnerId = sourceAsset.farmerId,
            transferredAt = now,
            
            // Reset marketplace fields
            listedAt = null,
            listingId = null,
            soldAt = null,
            soldToUserId = null,
            soldPrice = null,
            
            // Metadata
            createdAt = now,
            updatedAt = now,
            dirty = true
        )
    }
    
    /**
     * Check if an order has assets that need transfer.
     */
    suspend fun hasTransferableAssets(orderId: String): Boolean {
        val orderItems = orderDao.getOrderItemsList(orderId)
        for (item in orderItems) {
            val product = productDao.findById(item.productId) ?: continue
            if (product.sourceAssetId != null) {
                return true
            }
        }
        return false
    }
}
