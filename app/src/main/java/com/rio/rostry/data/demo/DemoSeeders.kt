package com.rio.rostry.data.demo

import com.rio.rostry.data.database.dao.PostsDao
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.data.database.dao.TransferDao
import com.rio.rostry.data.database.dao.UserDao
import com.rio.rostry.data.database.entity.PostEntity
import com.rio.rostry.data.database.entity.ProductEntity
import com.rio.rostry.data.database.entity.TransferEntity
import com.rio.rostry.data.database.entity.UserEntity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DemoSeeders @Inject constructor(
    private val userDao: UserDao,
    private val productDao: ProductDao,
    private val transferDao: TransferDao,
    private val postsDao: PostsDao
) {

    suspend fun seedProfile(profile: DemoUserProfile) {
        val now = System.currentTimeMillis()

        val ownerEntity = profile.toUserEntity(now)
        userDao.insertUser(ownerEntity)

        val productEntities: List<ProductEntity> = profile.productListings.mapIndexed { index, demoProduct ->
            demoProduct.toProductEntity(owner = profile, index = index, now = now)
        }
        if (productEntities.isNotEmpty()) {
            productDao.insertProducts(productEntities)
        }

        val counterpartUsers = mutableMapOf<String, UserEntity>().apply {
            profile.transactions.forEach { txn ->
                val counterpart = txn.toCounterpartUser(now)
                putIfAbsent(counterpart.userId, counterpart)
            }
            profile.socialConnections.forEach { conn ->
                val counterpart = conn.toCounterpartUser(now)
                putIfAbsent(counterpart.userId, counterpart)
            }
        }

        if (counterpartUsers.isNotEmpty()) {
            userDao.insertUsers(counterpartUsers.values.toList())
        }

        val transfers: List<TransferEntity> = profile.transactions.mapIndexed { index, demoTxn ->
            val productId = if (productEntities.isNotEmpty()) {
                productEntities[index % productEntities.size].productId
            } else {
                null
            }
            val counterpart = demoTxn.toCounterpartUser(now)
            val counterpartUserId = counterpartUsers[counterpart.userId]?.userId ?: counterpart.userId
            demoTxn.toTransferEntity(
                owner = profile,
                productId = productId,
                counterpartUserId = counterpartUserId,
                index = index,
                now = now
            )
        }
        transfers.forEach { transferDao.upsert(it) }

        profile.socialConnections.forEachIndexed { index, connection ->
            val post: PostEntity = connection.toPostEntity(profile, index, now)
            postsDao.upsert(post)
        }
    }
}
