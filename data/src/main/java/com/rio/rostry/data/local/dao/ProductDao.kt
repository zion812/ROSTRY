package com.rio.rostry.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rio.rostry.data.local.entities.ProductEntity
import com.rio.rostry.data.local.entities.ProductBasic
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(product: ProductEntity)

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getById(id: String): ProductEntity?

    @Query("SELECT * FROM products WHERE farmerId = :farmerId ORDER BY createdAt DESC")
    fun streamByFarmer(farmerId: String): Flow<List<ProductEntity>>

    // Advanced filtering with optional parameters. When a param is NULL, it is ignored.
    @Query(
        """
        SELECT * FROM products
        WHERE (:breed IS NULL OR breed = :breed)
          AND (:minPrice IS NULL OR price >= :minPrice)
          AND (:maxPrice IS NULL OR price <= :maxPrice)
          AND (:verifiedSeller IS NULL OR verifiedSeller = :verifiedSeller)
          AND (:minLat IS NULL OR locationLat >= :minLat)
          AND (:maxLat IS NULL OR locationLat <= :maxLat)
          AND (:minLng IS NULL OR locationLng >= :minLng)
          AND (:maxLng IS NULL OR locationLng <= :maxLng)
          AND (:minBirthDate IS NULL OR birthDate >= :minBirthDate)
          AND (:maxBirthDate IS NULL OR birthDate <= :maxBirthDate)
        ORDER BY createdAt DESC
        """
    )
    fun streamFiltered(
        breed: String?,
        minPrice: Long?,
        maxPrice: Long?,
        verifiedSeller: Int?,
        minLat: Double?,
        maxLat: Double?,
        minLng: Double?,
        maxLng: Double?,
        minBirthDate: Long?,
        maxBirthDate: Long?,
    ): Flow<List<ProductEntity>>

    @Query(
        """
        SELECT * FROM products
        WHERE name LIKE :pattern OR breed LIKE :pattern OR category LIKE :pattern
        ORDER BY createdAt DESC
        LIMIT :limit
        """
    )
    suspend fun searchAutocomplete(pattern: String, limit: Int): List<ProductEntity>

    @Update
    suspend fun update(product: ProductEntity)

    // Projection for lifecycle processing to avoid loading entire entities
    @Query(
        """
        SELECT id, farmerId, birthDate, gender, color, createdAt
        FROM products
        ORDER BY createdAt DESC
        """
    )
    suspend fun listBasics(): List<ProductBasic>

    // For background workers needing full fields like vaccinationRecords
    @Query("SELECT * FROM products")
    suspend fun listAll(): List<ProductEntity>
}

