package com.rio.rostry.data.database.dao

import androidx.room.*
import com.rio.rostry.data.database.entity.BreedingRecordEntity
import com.rio.rostry.data.database.entity.TraitEntity
import com.rio.rostry.data.database.entity.ProductTraitCrossRef
import com.rio.rostry.data.database.entity.LifecycleEventEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BreedingRecordDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(record: BreedingRecordEntity)

    @Query("SELECT * FROM breeding_records WHERE childId = :childId")
    suspend fun recordsByChild(childId: String): List<BreedingRecordEntity>

    @Query("SELECT * FROM breeding_records WHERE parentId = :parentId OR partnerId = :parentId")
    suspend fun recordsByParent(parentId: String): List<BreedingRecordEntity>

    @Query("SELECT COUNT(*) FROM breeding_records WHERE parentId = :parent AND partnerId = :partner AND success = 1")
    suspend fun successfulBreedings(parent: String, partner: String): Int

    @Query("SELECT COUNT(*) FROM breeding_records WHERE parentId = :parent AND partnerId = :partner")
    suspend fun totalBreedings(parent: String, partner: String): Int
}

@Dao
interface TraitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(trait: TraitEntity)

    @Query("SELECT * FROM traits WHERE traitId = :traitId")
    suspend fun findById(traitId: String): TraitEntity?
}

@Dao
interface ProductTraitDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTrait(ref: ProductTraitCrossRef)

    @Query("DELETE FROM product_traits WHERE productId = :productId AND traitId = :traitId")
    suspend fun removeTrait(productId: String, traitId: String)

    @Query("SELECT t.* FROM traits t INNER JOIN product_traits pt ON t.traitId = pt.traitId WHERE pt.productId = :productId")
    suspend fun traitsForProduct(productId: String): List<TraitEntity>
}

@Dao
interface LifecycleEventDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(event: LifecycleEventEntity)

    @Query("SELECT * FROM lifecycle_events WHERE productId = :productId ORDER BY week ASC, timestamp ASC")
    fun observeForProduct(productId: String): Flow<List<LifecycleEventEntity>>

    @Query("SELECT * FROM lifecycle_events WHERE productId = :productId AND week BETWEEN :startWeek AND :endWeek ORDER BY week ASC")
    suspend fun range(productId: String, startWeek: Int, endWeek: Int): List<LifecycleEventEntity>

    @Query("SELECT EXISTS(SELECT 1 FROM lifecycle_events WHERE productId = :productId AND type = :type AND week = :week)")
    suspend fun existsEvent(productId: String, type: String, week: Int): Boolean
}

