package com.rio.rostry.data.repository

import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.ReviewDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject
import javax.inject.Singleton

enum class ContentType { PRODUCT, REVIEW }

data class ModerationItem(
    val id: String,
    val type: ContentType,
    val title: String,
    val content: String,
    val flaggedAt: Long,
    val reason: String?
)

interface ModerationRepository {
    fun getModerationQueue(): Flow<List<ModerationItem>>
    suspend fun approve(id: String, type: ContentType)
    suspend fun reject(id: String, type: ContentType, reason: String)
}

@Singleton
class ModerationRepositoryImpl @Inject constructor(
    private val productDao: ProductDao,
    private val reviewDao: ReviewDao
) : ModerationRepository {

    override fun getModerationQueue(): Flow<List<ModerationItem>> {
        return combine(
            productDao.getFlaggedProducts(),
            reviewDao.getFlaggedReviews()
        ) { products, reviews ->
            val productItems = products.map { p ->
                ModerationItem(
                    id = p.productId,
                    type = ContentType.PRODUCT,
                    title = p.name,
                    content = p.description,
                    flaggedAt = p.updatedAt, // Using update time as flag time for now
                    reason = p.moderationNote
                )
            }
            val reviewItems = reviews.map { r ->
                ModerationItem(
                    id = r.reviewId,
                    type = ContentType.REVIEW,
                    title = "Review for ${r.productId ?: "Seller"}",
                    content = r.content ?: "No content",
                    flaggedAt = r.createdAt, // Or update time
                    reason = r.moderationNote
                )
            }
            (productItems + reviewItems).sortedByDescending { it.flaggedAt }
        }
    }

    override suspend fun approve(id: String, type: ContentType) {
        when (type) {
            ContentType.PRODUCT -> {
                // Clear flagged status
                // Need a DAO method to clear flag. 
                // Currently only getFlaggedProducts exists.
                // Assuming I can update the entity. But direct update requires full object.
                // I'll add clearProductFlag(id) to DAO or fetch-modify-save.
                // Fetch-modify-save is safer for now without extra DAO methods.
                val product = productDao.findById(id)
                product?.let {
                    productDao.updateProduct(it.copy(adminFlagged = false, moderationNote = null))
                }
            }
            ContentType.REVIEW -> {
                val review = reviewDao.findById(id)
                review?.let {
                    reviewDao.upsert(it.copy(adminFlagged = false, moderationNote = null))
                }
            }
        }
    }

    override suspend fun reject(id: String, type: ContentType, reason: String) {
         when (type) {
            ContentType.PRODUCT -> {
                val product = productDao.findById(id)
                product?.let {
                    // Rejecting might mean hiding it + keeping flag, or deleting.
                    // Ideally: status = "rejected", flagged = false (handled).
                    productDao.updateProduct(it.copy(adminFlagged = false, status = "rejected", moderationNote = reason))
                }
            }
            ContentType.REVIEW -> {
                val review = reviewDao.findById(id)
                review?.let {
                     // specific rejected state? Or just delete?
                     // Soft delete for review rejection is common.
                     reviewDao.upsert(it.copy(adminFlagged = false, isDeleted = true, moderationNote = reason))
                }
            }
        }
    }
}
