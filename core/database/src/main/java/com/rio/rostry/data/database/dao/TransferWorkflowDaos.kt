package com.rio.rostry.data.database.dao

import androidx.room.*
import com.rio.rostry.data.database.entity.AuditLogEntity
import com.rio.rostry.data.database.entity.TransferVerificationEntity
import com.rio.rostry.data.database.entity.DisputeEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for audit logs. Audit logs are immutable and write-once to ensure integrity of the audit trail.
 * They cannot be modified or deleted after creation.
 */
@Dao
interface AuditLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: AuditLogEntity)

    /**
     * Retrieves all audit logs ordered by creation time descending.
     * Warning: This method should only be used in tests or admin tools, as retrieving all audit logs in production could be expensive.
     */
    @Query("SELECT * FROM audit_logs ORDER BY createdAt DESC")
    suspend fun getAll(): List<AuditLogEntity>

    @Query("SELECT * FROM audit_logs WHERE refId = :refId ORDER BY createdAt ASC")
    suspend fun getByRef(refId: String): List<AuditLogEntity>

    @Query("SELECT * FROM audit_logs WHERE refId = :refId ORDER BY createdAt ASC")
    fun streamByRef(refId: String): Flow<List<AuditLogEntity>>

    @Query("SELECT * FROM audit_logs WHERE type = :type ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getByType(type: String, limit: Int): List<AuditLogEntity>

    @Query("SELECT * FROM audit_logs WHERE actorUserId = :userId ORDER BY createdAt DESC LIMIT :limit")
    suspend fun getByActor(userId: String, limit: Int): List<AuditLogEntity>
}

@Dao
interface TransferVerificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: TransferVerificationEntity)

    @Upsert
    suspend fun upsert(entity: TransferVerificationEntity)

    @Query("SELECT * FROM transfer_verifications WHERE transferId = :transferId")
    suspend fun getByTransferId(transferId: String): List<TransferVerificationEntity>

    @Query("SELECT * FROM transfer_verifications WHERE transferId = :transferId")
    fun observeByTransferId(transferId: String): Flow<List<TransferVerificationEntity>>
}

@Dao
interface DisputeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: DisputeEntity)

    @Upsert
    suspend fun upsert(entity: DisputeEntity)

    @Query("SELECT * FROM disputes WHERE transferId = :transferId")
    suspend fun getByTransferId(transferId: String): List<DisputeEntity>

    @Query("SELECT * FROM disputes WHERE disputeId = :disputeId")
    suspend fun getById(disputeId: String): DisputeEntity?

    @Query("SELECT * FROM disputes WHERE transferId = :transferId")
    fun observeByTransferId(transferId: String): Flow<List<DisputeEntity>>
}