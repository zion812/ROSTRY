package com.rio.rostry.data.database.dao

import androidx.room.*
import com.rio.rostry.data.database.entity.ExpenseEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for expense tracking operations.
 */
@Dao
interface ExpenseDao {

    // ==================== Observe Operations ====================
    
    @Query("SELECT * FROM expenses WHERE farmerId = :farmerId ORDER BY expenseDate DESC")
    fun observeForFarmer(farmerId: String): Flow<List<ExpenseEntity>>
    
    @Query("SELECT * FROM expenses WHERE farmerId = :farmerId AND category = :category ORDER BY expenseDate DESC")
    fun observeForFarmerByCategory(farmerId: String, category: String): Flow<List<ExpenseEntity>>
    
    @Query("SELECT * FROM expenses WHERE assetId = :assetId ORDER BY expenseDate DESC")
    fun observeForAsset(assetId: String): Flow<List<ExpenseEntity>>
    
    @Query("""
        SELECT * FROM expenses 
        WHERE farmerId = :farmerId 
        AND expenseDate >= :startDate 
        AND expenseDate <= :endDate 
        ORDER BY expenseDate DESC
    """)
    fun observeForDateRange(farmerId: String, startDate: Long, endDate: Long): Flow<List<ExpenseEntity>>
    
    // ==================== Aggregation Queries ====================
    
    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expenses WHERE farmerId = :farmerId")
    suspend fun getTotalForFarmer(farmerId: String): Double
    
    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expenses WHERE farmerId = :farmerId AND category = :category")
    suspend fun getTotalByCategory(farmerId: String, category: String): Double
    
    @Query("""
        SELECT COALESCE(SUM(amount), 0.0) FROM expenses 
        WHERE farmerId = :farmerId 
        AND category = :category 
        AND expenseDate >= :startDate 
        AND expenseDate <= :endDate
    """)
    suspend fun getTotalByCategoryInRange(farmerId: String, category: String, startDate: Long, endDate: Long): Double
    
    @Query("""
        SELECT COALESCE(SUM(amount), 0.0) FROM expenses 
        WHERE farmerId = :farmerId 
        AND expenseDate >= :startDate 
        AND expenseDate <= :endDate
    """)
    suspend fun getTotalInRange(farmerId: String, startDate: Long, endDate: Long): Double
    
    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expenses WHERE assetId = :assetId")
    suspend fun getTotalForAsset(assetId: String): Double
    
    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM expenses WHERE assetId = :assetId AND category = :category")
    suspend fun getTotalForAssetByCategory(assetId: String, category: String): Double
    
    @Query("SELECT COUNT(*) FROM expenses WHERE farmerId = :farmerId")
    suspend fun countForFarmer(farmerId: String): Int
    
    // ==================== CRUD Operations ====================
    
    @Query("SELECT * FROM expenses WHERE expenseId = :expenseId")
    suspend fun getById(expenseId: String): ExpenseEntity?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(expense: ExpenseEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(expenses: List<ExpenseEntity>)
    
    @Update
    suspend fun update(expense: ExpenseEntity)
    
    @Delete
    suspend fun delete(expense: ExpenseEntity)
    
    @Query("DELETE FROM expenses WHERE expenseId = :expenseId")
    suspend fun deleteById(expenseId: String)
    
    @Query("DELETE FROM expenses WHERE farmerId = :farmerId")
    suspend fun deleteAllForFarmer(farmerId: String)
    
    // ==================== Sync Support ====================
    
    @Query("SELECT * FROM expenses WHERE dirty = 1")
    suspend fun getDirty(): List<ExpenseEntity>
    
    @Query("UPDATE expenses SET dirty = 0, syncedAt = :syncedAt WHERE expenseId = :expenseId")
    suspend fun markSynced(expenseId: String, syncedAt: Long)
}
