package com.rio.rostry.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.rio.rostry.data.database.entity.AuditLogEntity
import com.rio.rostry.data.database.entity.DisputeEntity
import com.rio.rostry.data.database.entity.TransferVerificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransferVerificationDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: TransferVerificationEntity)

    @Query("SELECT * FROM transfer_verifications WHERE transferId = :transferId ORDER BY createdAt ASC")
    fun streamByTransfer(transferId: String): Flow<List<TransferVerificationEntity>>

    @Query("SELECT * FROM transfer_verifications WHERE transferId = :transferId ORDER BY createdAt ASC")
    suspend fun getByTransfer(transferId: String): List<TransferVerificationEntity>

    @Update
    suspend fun update(entity: TransferVerificationEntity)
}

@Dao
interface DisputeDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: DisputeEntity)

    @Query("SELECT * FROM transfer_disputes WHERE transferId = :transferId ORDER BY createdAt ASC")
    suspend fun getByTransfer(transferId: String): List<DisputeEntity>

    @Query("SELECT * FROM transfer_disputes WHERE disputeId = :disputeId LIMIT 1")
    suspend fun getById(disputeId: String): DisputeEntity?

    @Query("SELECT * FROM transfer_disputes WHERE transferId = :transferId ORDER BY createdAt ASC")
    fun streamByTransfer(transferId: String): Flow<List<DisputeEntity>>

    @Query("SELECT * FROM transfer_disputes WHERE status = :status ORDER BY createdAt ASC")
    fun streamByStatus(status: String): Flow<List<DisputeEntity>>

    @Update
    suspend fun update(entity: DisputeEntity)
}

@Dao
interface AuditLogDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: AuditLogEntity)

    @Query("SELECT * FROM audit_logs WHERE refId = :refId ORDER BY createdAt ASC")
    suspend fun getByRef(refId: String): List<AuditLogEntity>

    @Query("SELECT * FROM audit_logs WHERE refId = :refId ORDER BY createdAt ASC")
    fun streamByRef(refId: String): Flow<List<AuditLogEntity>>
}
