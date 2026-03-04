package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.StorageQuotaEntity
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.reflect.KClass
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class StorageQuotaDao_Impl(
  __db: RoomDatabase,
) : StorageQuotaDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfStorageQuotaEntity: EntityInsertAdapter<StorageQuotaEntity>

  private val __deleteAdapterOfStorageQuotaEntity: EntityDeleteOrUpdateAdapter<StorageQuotaEntity>

  private val __updateAdapterOfStorageQuotaEntity: EntityDeleteOrUpdateAdapter<StorageQuotaEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfStorageQuotaEntity = object : EntityInsertAdapter<StorageQuotaEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `storage_quota` (`userId`,`quotaBytes`,`publicLimitBytes`,`privateLimitBytes`,`usedBytes`,`publicUsedBytes`,`privateUsedBytes`,`imageBytes`,`documentBytes`,`dataBytes`,`warningLevel`,`lastCalculatedAt`,`lastSyncedAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: StorageQuotaEntity) {
        statement.bindText(1, entity.userId)
        statement.bindLong(2, entity.quotaBytes)
        statement.bindLong(3, entity.publicLimitBytes)
        statement.bindLong(4, entity.privateLimitBytes)
        statement.bindLong(5, entity.usedBytes)
        statement.bindLong(6, entity.publicUsedBytes)
        statement.bindLong(7, entity.privateUsedBytes)
        statement.bindLong(8, entity.imageBytes)
        statement.bindLong(9, entity.documentBytes)
        statement.bindLong(10, entity.dataBytes)
        statement.bindText(11, entity.warningLevel)
        statement.bindLong(12, entity.lastCalculatedAt)
        val _tmpLastSyncedAt: Long? = entity.lastSyncedAt
        if (_tmpLastSyncedAt == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpLastSyncedAt)
        }
        statement.bindLong(14, entity.updatedAt)
      }
    }
    this.__deleteAdapterOfStorageQuotaEntity = object :
        EntityDeleteOrUpdateAdapter<StorageQuotaEntity>() {
      protected override fun createQuery(): String =
          "DELETE FROM `storage_quota` WHERE `userId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: StorageQuotaEntity) {
        statement.bindText(1, entity.userId)
      }
    }
    this.__updateAdapterOfStorageQuotaEntity = object :
        EntityDeleteOrUpdateAdapter<StorageQuotaEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `storage_quota` SET `userId` = ?,`quotaBytes` = ?,`publicLimitBytes` = ?,`privateLimitBytes` = ?,`usedBytes` = ?,`publicUsedBytes` = ?,`privateUsedBytes` = ?,`imageBytes` = ?,`documentBytes` = ?,`dataBytes` = ?,`warningLevel` = ?,`lastCalculatedAt` = ?,`lastSyncedAt` = ?,`updatedAt` = ? WHERE `userId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: StorageQuotaEntity) {
        statement.bindText(1, entity.userId)
        statement.bindLong(2, entity.quotaBytes)
        statement.bindLong(3, entity.publicLimitBytes)
        statement.bindLong(4, entity.privateLimitBytes)
        statement.bindLong(5, entity.usedBytes)
        statement.bindLong(6, entity.publicUsedBytes)
        statement.bindLong(7, entity.privateUsedBytes)
        statement.bindLong(8, entity.imageBytes)
        statement.bindLong(9, entity.documentBytes)
        statement.bindLong(10, entity.dataBytes)
        statement.bindText(11, entity.warningLevel)
        statement.bindLong(12, entity.lastCalculatedAt)
        val _tmpLastSyncedAt: Long? = entity.lastSyncedAt
        if (_tmpLastSyncedAt == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpLastSyncedAt)
        }
        statement.bindLong(14, entity.updatedAt)
        statement.bindText(15, entity.userId)
      }
    }
  }

  public override suspend fun insert(quota: StorageQuotaEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfStorageQuotaEntity.insert(_connection, quota)
  }

  public override suspend fun delete(quota: StorageQuotaEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __deleteAdapterOfStorageQuotaEntity.handle(_connection, quota)
  }

  public override suspend fun update(quota: StorageQuotaEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __updateAdapterOfStorageQuotaEntity.handle(_connection, quota)
  }

  public override suspend fun getByUserId(userId: String): StorageQuotaEntity? {
    val _sql: String = "SELECT * FROM storage_quota WHERE userId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfQuotaBytes: Int = getColumnIndexOrThrow(_stmt, "quotaBytes")
        val _columnIndexOfPublicLimitBytes: Int = getColumnIndexOrThrow(_stmt, "publicLimitBytes")
        val _columnIndexOfPrivateLimitBytes: Int = getColumnIndexOrThrow(_stmt, "privateLimitBytes")
        val _columnIndexOfUsedBytes: Int = getColumnIndexOrThrow(_stmt, "usedBytes")
        val _columnIndexOfPublicUsedBytes: Int = getColumnIndexOrThrow(_stmt, "publicUsedBytes")
        val _columnIndexOfPrivateUsedBytes: Int = getColumnIndexOrThrow(_stmt, "privateUsedBytes")
        val _columnIndexOfImageBytes: Int = getColumnIndexOrThrow(_stmt, "imageBytes")
        val _columnIndexOfDocumentBytes: Int = getColumnIndexOrThrow(_stmt, "documentBytes")
        val _columnIndexOfDataBytes: Int = getColumnIndexOrThrow(_stmt, "dataBytes")
        val _columnIndexOfWarningLevel: Int = getColumnIndexOrThrow(_stmt, "warningLevel")
        val _columnIndexOfLastCalculatedAt: Int = getColumnIndexOrThrow(_stmt, "lastCalculatedAt")
        val _columnIndexOfLastSyncedAt: Int = getColumnIndexOrThrow(_stmt, "lastSyncedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: StorageQuotaEntity?
        if (_stmt.step()) {
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpQuotaBytes: Long
          _tmpQuotaBytes = _stmt.getLong(_columnIndexOfQuotaBytes)
          val _tmpPublicLimitBytes: Long
          _tmpPublicLimitBytes = _stmt.getLong(_columnIndexOfPublicLimitBytes)
          val _tmpPrivateLimitBytes: Long
          _tmpPrivateLimitBytes = _stmt.getLong(_columnIndexOfPrivateLimitBytes)
          val _tmpUsedBytes: Long
          _tmpUsedBytes = _stmt.getLong(_columnIndexOfUsedBytes)
          val _tmpPublicUsedBytes: Long
          _tmpPublicUsedBytes = _stmt.getLong(_columnIndexOfPublicUsedBytes)
          val _tmpPrivateUsedBytes: Long
          _tmpPrivateUsedBytes = _stmt.getLong(_columnIndexOfPrivateUsedBytes)
          val _tmpImageBytes: Long
          _tmpImageBytes = _stmt.getLong(_columnIndexOfImageBytes)
          val _tmpDocumentBytes: Long
          _tmpDocumentBytes = _stmt.getLong(_columnIndexOfDocumentBytes)
          val _tmpDataBytes: Long
          _tmpDataBytes = _stmt.getLong(_columnIndexOfDataBytes)
          val _tmpWarningLevel: String
          _tmpWarningLevel = _stmt.getText(_columnIndexOfWarningLevel)
          val _tmpLastCalculatedAt: Long
          _tmpLastCalculatedAt = _stmt.getLong(_columnIndexOfLastCalculatedAt)
          val _tmpLastSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfLastSyncedAt)) {
            _tmpLastSyncedAt = null
          } else {
            _tmpLastSyncedAt = _stmt.getLong(_columnIndexOfLastSyncedAt)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              StorageQuotaEntity(_tmpUserId,_tmpQuotaBytes,_tmpPublicLimitBytes,_tmpPrivateLimitBytes,_tmpUsedBytes,_tmpPublicUsedBytes,_tmpPrivateUsedBytes,_tmpImageBytes,_tmpDocumentBytes,_tmpDataBytes,_tmpWarningLevel,_tmpLastCalculatedAt,_tmpLastSyncedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByUserId(userId: String): Flow<StorageQuotaEntity?> {
    val _sql: String = "SELECT * FROM storage_quota WHERE userId = ?"
    return createFlow(__db, false, arrayOf("storage_quota")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfQuotaBytes: Int = getColumnIndexOrThrow(_stmt, "quotaBytes")
        val _columnIndexOfPublicLimitBytes: Int = getColumnIndexOrThrow(_stmt, "publicLimitBytes")
        val _columnIndexOfPrivateLimitBytes: Int = getColumnIndexOrThrow(_stmt, "privateLimitBytes")
        val _columnIndexOfUsedBytes: Int = getColumnIndexOrThrow(_stmt, "usedBytes")
        val _columnIndexOfPublicUsedBytes: Int = getColumnIndexOrThrow(_stmt, "publicUsedBytes")
        val _columnIndexOfPrivateUsedBytes: Int = getColumnIndexOrThrow(_stmt, "privateUsedBytes")
        val _columnIndexOfImageBytes: Int = getColumnIndexOrThrow(_stmt, "imageBytes")
        val _columnIndexOfDocumentBytes: Int = getColumnIndexOrThrow(_stmt, "documentBytes")
        val _columnIndexOfDataBytes: Int = getColumnIndexOrThrow(_stmt, "dataBytes")
        val _columnIndexOfWarningLevel: Int = getColumnIndexOrThrow(_stmt, "warningLevel")
        val _columnIndexOfLastCalculatedAt: Int = getColumnIndexOrThrow(_stmt, "lastCalculatedAt")
        val _columnIndexOfLastSyncedAt: Int = getColumnIndexOrThrow(_stmt, "lastSyncedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: StorageQuotaEntity?
        if (_stmt.step()) {
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpQuotaBytes: Long
          _tmpQuotaBytes = _stmt.getLong(_columnIndexOfQuotaBytes)
          val _tmpPublicLimitBytes: Long
          _tmpPublicLimitBytes = _stmt.getLong(_columnIndexOfPublicLimitBytes)
          val _tmpPrivateLimitBytes: Long
          _tmpPrivateLimitBytes = _stmt.getLong(_columnIndexOfPrivateLimitBytes)
          val _tmpUsedBytes: Long
          _tmpUsedBytes = _stmt.getLong(_columnIndexOfUsedBytes)
          val _tmpPublicUsedBytes: Long
          _tmpPublicUsedBytes = _stmt.getLong(_columnIndexOfPublicUsedBytes)
          val _tmpPrivateUsedBytes: Long
          _tmpPrivateUsedBytes = _stmt.getLong(_columnIndexOfPrivateUsedBytes)
          val _tmpImageBytes: Long
          _tmpImageBytes = _stmt.getLong(_columnIndexOfImageBytes)
          val _tmpDocumentBytes: Long
          _tmpDocumentBytes = _stmt.getLong(_columnIndexOfDocumentBytes)
          val _tmpDataBytes: Long
          _tmpDataBytes = _stmt.getLong(_columnIndexOfDataBytes)
          val _tmpWarningLevel: String
          _tmpWarningLevel = _stmt.getText(_columnIndexOfWarningLevel)
          val _tmpLastCalculatedAt: Long
          _tmpLastCalculatedAt = _stmt.getLong(_columnIndexOfLastCalculatedAt)
          val _tmpLastSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfLastSyncedAt)) {
            _tmpLastSyncedAt = null
          } else {
            _tmpLastSyncedAt = _stmt.getLong(_columnIndexOfLastSyncedAt)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              StorageQuotaEntity(_tmpUserId,_tmpQuotaBytes,_tmpPublicLimitBytes,_tmpPrivateLimitBytes,_tmpUsedBytes,_tmpPublicUsedBytes,_tmpPrivateUsedBytes,_tmpImageBytes,_tmpDocumentBytes,_tmpDataBytes,_tmpWarningLevel,_tmpLastCalculatedAt,_tmpLastSyncedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getWarningLevel(userId: String): String? {
    val _sql: String = "SELECT warningLevel FROM storage_quota WHERE userId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _result: String?
        if (_stmt.step()) {
          if (_stmt.isNull(0)) {
            _result = null
          } else {
            _result = _stmt.getText(0)
          }
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getUsedBytes(userId: String): Long? {
    val _sql: String = "SELECT usedBytes FROM storage_quota WHERE userId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _result: Long?
        if (_stmt.step()) {
          if (_stmt.isNull(0)) {
            _result = null
          } else {
            _result = _stmt.getLong(0)
          }
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateUsage(
    userId: String,
    usedBytes: Long,
    publicUsedBytes: Long,
    privateUsedBytes: Long,
    imageBytes: Long,
    documentBytes: Long,
    dataBytes: Long,
    warningLevel: String,
    timestamp: Long,
  ) {
    val _sql: String = """
        |
        |        UPDATE storage_quota 
        |        SET usedBytes = ?,
        |            publicUsedBytes = ?,
        |            privateUsedBytes = ?,
        |            imageBytes = ?,
        |            documentBytes = ?,
        |            dataBytes = ?,
        |            warningLevel = ?,
        |            lastCalculatedAt = ?,
        |            updatedAt = ?
        |        WHERE userId = ?
        |    
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, usedBytes)
        _argIndex = 2
        _stmt.bindLong(_argIndex, publicUsedBytes)
        _argIndex = 3
        _stmt.bindLong(_argIndex, privateUsedBytes)
        _argIndex = 4
        _stmt.bindLong(_argIndex, imageBytes)
        _argIndex = 5
        _stmt.bindLong(_argIndex, documentBytes)
        _argIndex = 6
        _stmt.bindLong(_argIndex, dataBytes)
        _argIndex = 7
        _stmt.bindText(_argIndex, warningLevel)
        _argIndex = 8
        _stmt.bindLong(_argIndex, timestamp)
        _argIndex = 9
        _stmt.bindLong(_argIndex, timestamp)
        _argIndex = 10
        _stmt.bindText(_argIndex, userId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateQuotaLimits(
    userId: String,
    quotaBytes: Long,
    publicLimitBytes: Long,
    privateLimitBytes: Long,
    timestamp: Long,
  ) {
    val _sql: String = """
        |
        |        UPDATE storage_quota 
        |        SET quotaBytes = ?,
        |            publicLimitBytes = ?,
        |            privateLimitBytes = ?,
        |            updatedAt = ?
        |        WHERE userId = ?
        |    
        """.trimMargin()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, quotaBytes)
        _argIndex = 2
        _stmt.bindLong(_argIndex, publicLimitBytes)
        _argIndex = 3
        _stmt.bindLong(_argIndex, privateLimitBytes)
        _argIndex = 4
        _stmt.bindLong(_argIndex, timestamp)
        _argIndex = 5
        _stmt.bindText(_argIndex, userId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateSyncTime(userId: String, timestamp: Long) {
    val _sql: String = "UPDATE storage_quota SET lastSyncedAt = ?, updatedAt = ? WHERE userId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, timestamp)
        _argIndex = 2
        _stmt.bindLong(_argIndex, timestamp)
        _argIndex = 3
        _stmt.bindText(_argIndex, userId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteByUserId(userId: String) {
    val _sql: String = "DELETE FROM storage_quota WHERE userId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
