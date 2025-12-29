package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.rio.rostry.data.database.entity.ShowRecordEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for ShowRecordEntity - Competition and exhibition records for Enthusiast birds.
 */
@Dao
interface ShowRecordDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(record: ShowRecordEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(records: List<ShowRecordEntity>)
    
    @Query("SELECT * FROM show_records WHERE recordId = :recordId LIMIT 1")
    suspend fun findById(recordId: String): ShowRecordEntity?
    
    @Query("SELECT * FROM show_records WHERE productId = :productId AND isDeleted = 0 ORDER BY eventDate DESC")
    suspend fun getByProduct(productId: String): List<ShowRecordEntity>
    
    @Query("SELECT * FROM show_records WHERE productId = :productId AND isDeleted = 0 ORDER BY eventDate DESC")
    fun observeByProduct(productId: String): Flow<List<ShowRecordEntity>>
    
    @Query("SELECT * FROM show_records WHERE ownerId = :ownerId AND isDeleted = 0 ORDER BY eventDate DESC")
    suspend fun getByOwner(ownerId: String): List<ShowRecordEntity>
    
    @Query("SELECT * FROM show_records WHERE ownerId = :ownerId AND isDeleted = 0 ORDER BY eventDate DESC")
    fun observeByOwner(ownerId: String): Flow<List<ShowRecordEntity>>
    
    @Query("SELECT * FROM show_records WHERE productId = :productId AND recordType = :type AND isDeleted = 0 ORDER BY eventDate DESC")
    suspend fun getByProductAndType(productId: String, type: String): List<ShowRecordEntity>
    
    @Query("SELECT COUNT(*) FROM show_records WHERE productId = :productId AND result IN ('WIN', '1ST') AND isDeleted = 0")
    suspend fun countWins(productId: String): Int
    
    @Query("SELECT COUNT(*) FROM show_records WHERE productId = :productId AND result IN ('1ST', '2ND', '3RD') AND isDeleted = 0")
    suspend fun countPodiums(productId: String): Int
    
    @Query("SELECT COUNT(*) FROM show_records WHERE productId = :productId AND isDeleted = 0")
    suspend fun countTotal(productId: String): Int
    
    @Query("SELECT * FROM show_records WHERE productId = :productId AND recordType = 'SPARRING' AND isDeleted = 0 ORDER BY eventDate DESC")
    suspend fun getSparringRecords(productId: String): List<ShowRecordEntity>
    
    @Query("SELECT * FROM show_records WHERE productId = :productId AND recordType IN ('SHOW', 'EXHIBITION', 'COMPETITION') AND isDeleted = 0 ORDER BY eventDate DESC")
    suspend fun getCompetitionRecords(productId: String): List<ShowRecordEntity>
    
    // Stats aggregation
    @Query("""
        SELECT recordType, 
               COUNT(*) as total,
               SUM(CASE WHEN result IN ('WIN', '1ST') THEN 1 ELSE 0 END) as wins,
               SUM(CASE WHEN result IN ('1ST', '2ND', '3RD') THEN 1 ELSE 0 END) as podiums
        FROM show_records 
        WHERE productId = :productId AND isDeleted = 0 
        GROUP BY recordType
    """)
    suspend fun getStatsByProduct(productId: String): List<ShowRecordStats>
    
    // Soft delete
    @Query("UPDATE show_records SET isDeleted = 1, deletedAt = :deletedAt, dirty = 1 WHERE recordId = :recordId")
    suspend fun softDelete(recordId: String, deletedAt: Long = System.currentTimeMillis())
    
    // Sync helpers
    @Query("SELECT * FROM show_records WHERE dirty = 1 ORDER BY updatedAt ASC LIMIT :limit")
    suspend fun getDirty(limit: Int = 500): List<ShowRecordEntity>
    
    @Query("UPDATE show_records SET dirty = 0, syncedAt = :syncedAt WHERE recordId IN (:ids)")
    suspend fun clearDirty(ids: List<String>, syncedAt: Long)
    
    @Query("DELETE FROM show_records WHERE isDeleted = 1")
    suspend fun purgeDeleted()
}

/**
 * Statistics result for show records.
 */
data class ShowRecordStats(
    val recordType: String,
    val total: Int,
    val wins: Int,
    val podiums: Int
)
