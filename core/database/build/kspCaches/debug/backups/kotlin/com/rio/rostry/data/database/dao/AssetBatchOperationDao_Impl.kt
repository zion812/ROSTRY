package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.AssetBatchOperationEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AssetBatchOperationDao_Impl(
  __db: RoomDatabase,
) : AssetBatchOperationDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfAssetBatchOperationEntity:
      EntityInsertAdapter<AssetBatchOperationEntity>

  private val __updateAdapterOfAssetBatchOperationEntity:
      EntityDeleteOrUpdateAdapter<AssetBatchOperationEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfAssetBatchOperationEntity = object :
        EntityInsertAdapter<AssetBatchOperationEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `asset_batch_operations` (`operationId`,`farmerId`,`operationType`,`selectionCriteria`,`operationData`,`status`,`totalItems`,`processedItems`,`successfulItems`,`failedItems`,`errorLog`,`canRollback`,`rollbackData`,`startedAt`,`completedAt`,`estimatedDuration`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: AssetBatchOperationEntity) {
        statement.bindText(1, entity.operationId)
        statement.bindText(2, entity.farmerId)
        statement.bindText(3, entity.operationType)
        statement.bindText(4, entity.selectionCriteria)
        statement.bindText(5, entity.operationData)
        statement.bindText(6, entity.status)
        statement.bindLong(7, entity.totalItems.toLong())
        statement.bindLong(8, entity.processedItems.toLong())
        statement.bindLong(9, entity.successfulItems.toLong())
        statement.bindLong(10, entity.failedItems.toLong())
        val _tmpErrorLog: String? = entity.errorLog
        if (_tmpErrorLog == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpErrorLog)
        }
        val _tmp: Int = if (entity.canRollback) 1 else 0
        statement.bindLong(12, _tmp.toLong())
        val _tmpRollbackData: String? = entity.rollbackData
        if (_tmpRollbackData == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpRollbackData)
        }
        val _tmpStartedAt: Long? = entity.startedAt
        if (_tmpStartedAt == null) {
          statement.bindNull(14)
        } else {
          statement.bindLong(14, _tmpStartedAt)
        }
        val _tmpCompletedAt: Long? = entity.completedAt
        if (_tmpCompletedAt == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpCompletedAt)
        }
        val _tmpEstimatedDuration: Long? = entity.estimatedDuration
        if (_tmpEstimatedDuration == null) {
          statement.bindNull(16)
        } else {
          statement.bindLong(16, _tmpEstimatedDuration)
        }
        statement.bindLong(17, entity.createdAt)
        statement.bindLong(18, entity.updatedAt)
      }
    }
    this.__updateAdapterOfAssetBatchOperationEntity = object :
        EntityDeleteOrUpdateAdapter<AssetBatchOperationEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `asset_batch_operations` SET `operationId` = ?,`farmerId` = ?,`operationType` = ?,`selectionCriteria` = ?,`operationData` = ?,`status` = ?,`totalItems` = ?,`processedItems` = ?,`successfulItems` = ?,`failedItems` = ?,`errorLog` = ?,`canRollback` = ?,`rollbackData` = ?,`startedAt` = ?,`completedAt` = ?,`estimatedDuration` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `operationId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: AssetBatchOperationEntity) {
        statement.bindText(1, entity.operationId)
        statement.bindText(2, entity.farmerId)
        statement.bindText(3, entity.operationType)
        statement.bindText(4, entity.selectionCriteria)
        statement.bindText(5, entity.operationData)
        statement.bindText(6, entity.status)
        statement.bindLong(7, entity.totalItems.toLong())
        statement.bindLong(8, entity.processedItems.toLong())
        statement.bindLong(9, entity.successfulItems.toLong())
        statement.bindLong(10, entity.failedItems.toLong())
        val _tmpErrorLog: String? = entity.errorLog
        if (_tmpErrorLog == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpErrorLog)
        }
        val _tmp: Int = if (entity.canRollback) 1 else 0
        statement.bindLong(12, _tmp.toLong())
        val _tmpRollbackData: String? = entity.rollbackData
        if (_tmpRollbackData == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpRollbackData)
        }
        val _tmpStartedAt: Long? = entity.startedAt
        if (_tmpStartedAt == null) {
          statement.bindNull(14)
        } else {
          statement.bindLong(14, _tmpStartedAt)
        }
        val _tmpCompletedAt: Long? = entity.completedAt
        if (_tmpCompletedAt == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpCompletedAt)
        }
        val _tmpEstimatedDuration: Long? = entity.estimatedDuration
        if (_tmpEstimatedDuration == null) {
          statement.bindNull(16)
        } else {
          statement.bindLong(16, _tmpEstimatedDuration)
        }
        statement.bindLong(17, entity.createdAt)
        statement.bindLong(18, entity.updatedAt)
        statement.bindText(19, entity.operationId)
      }
    }
  }

  public override suspend fun insert(operation: AssetBatchOperationEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfAssetBatchOperationEntity.insert(_connection, operation)
  }

  public override suspend fun update(operation: AssetBatchOperationEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfAssetBatchOperationEntity.handle(_connection, operation)
  }

  public override suspend fun getOperationById(operationId: String): AssetBatchOperationEntity? {
    val _sql: String = "SELECT * FROM asset_batch_operations WHERE operationId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, operationId)
        val _columnIndexOfOperationId: Int = getColumnIndexOrThrow(_stmt, "operationId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfOperationType: Int = getColumnIndexOrThrow(_stmt, "operationType")
        val _columnIndexOfSelectionCriteria: Int = getColumnIndexOrThrow(_stmt, "selectionCriteria")
        val _columnIndexOfOperationData: Int = getColumnIndexOrThrow(_stmt, "operationData")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTotalItems: Int = getColumnIndexOrThrow(_stmt, "totalItems")
        val _columnIndexOfProcessedItems: Int = getColumnIndexOrThrow(_stmt, "processedItems")
        val _columnIndexOfSuccessfulItems: Int = getColumnIndexOrThrow(_stmt, "successfulItems")
        val _columnIndexOfFailedItems: Int = getColumnIndexOrThrow(_stmt, "failedItems")
        val _columnIndexOfErrorLog: Int = getColumnIndexOrThrow(_stmt, "errorLog")
        val _columnIndexOfCanRollback: Int = getColumnIndexOrThrow(_stmt, "canRollback")
        val _columnIndexOfRollbackData: Int = getColumnIndexOrThrow(_stmt, "rollbackData")
        val _columnIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfEstimatedDuration: Int = getColumnIndexOrThrow(_stmt, "estimatedDuration")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: AssetBatchOperationEntity?
        if (_stmt.step()) {
          val _tmpOperationId: String
          _tmpOperationId = _stmt.getText(_columnIndexOfOperationId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpOperationType: String
          _tmpOperationType = _stmt.getText(_columnIndexOfOperationType)
          val _tmpSelectionCriteria: String
          _tmpSelectionCriteria = _stmt.getText(_columnIndexOfSelectionCriteria)
          val _tmpOperationData: String
          _tmpOperationData = _stmt.getText(_columnIndexOfOperationData)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTotalItems: Int
          _tmpTotalItems = _stmt.getLong(_columnIndexOfTotalItems).toInt()
          val _tmpProcessedItems: Int
          _tmpProcessedItems = _stmt.getLong(_columnIndexOfProcessedItems).toInt()
          val _tmpSuccessfulItems: Int
          _tmpSuccessfulItems = _stmt.getLong(_columnIndexOfSuccessfulItems).toInt()
          val _tmpFailedItems: Int
          _tmpFailedItems = _stmt.getLong(_columnIndexOfFailedItems).toInt()
          val _tmpErrorLog: String?
          if (_stmt.isNull(_columnIndexOfErrorLog)) {
            _tmpErrorLog = null
          } else {
            _tmpErrorLog = _stmt.getText(_columnIndexOfErrorLog)
          }
          val _tmpCanRollback: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfCanRollback).toInt()
          _tmpCanRollback = _tmp != 0
          val _tmpRollbackData: String?
          if (_stmt.isNull(_columnIndexOfRollbackData)) {
            _tmpRollbackData = null
          } else {
            _tmpRollbackData = _stmt.getText(_columnIndexOfRollbackData)
          }
          val _tmpStartedAt: Long?
          if (_stmt.isNull(_columnIndexOfStartedAt)) {
            _tmpStartedAt = null
          } else {
            _tmpStartedAt = _stmt.getLong(_columnIndexOfStartedAt)
          }
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpEstimatedDuration: Long?
          if (_stmt.isNull(_columnIndexOfEstimatedDuration)) {
            _tmpEstimatedDuration = null
          } else {
            _tmpEstimatedDuration = _stmt.getLong(_columnIndexOfEstimatedDuration)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              AssetBatchOperationEntity(_tmpOperationId,_tmpFarmerId,_tmpOperationType,_tmpSelectionCriteria,_tmpOperationData,_tmpStatus,_tmpTotalItems,_tmpProcessedItems,_tmpSuccessfulItems,_tmpFailedItems,_tmpErrorLog,_tmpCanRollback,_tmpRollbackData,_tmpStartedAt,_tmpCompletedAt,_tmpEstimatedDuration,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getOperationsByFarmer(farmerId: String):
      List<AssetBatchOperationEntity> {
    val _sql: String =
        "SELECT * FROM asset_batch_operations WHERE farmerId = ? ORDER BY createdAt DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfOperationId: Int = getColumnIndexOrThrow(_stmt, "operationId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfOperationType: Int = getColumnIndexOrThrow(_stmt, "operationType")
        val _columnIndexOfSelectionCriteria: Int = getColumnIndexOrThrow(_stmt, "selectionCriteria")
        val _columnIndexOfOperationData: Int = getColumnIndexOrThrow(_stmt, "operationData")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfTotalItems: Int = getColumnIndexOrThrow(_stmt, "totalItems")
        val _columnIndexOfProcessedItems: Int = getColumnIndexOrThrow(_stmt, "processedItems")
        val _columnIndexOfSuccessfulItems: Int = getColumnIndexOrThrow(_stmt, "successfulItems")
        val _columnIndexOfFailedItems: Int = getColumnIndexOrThrow(_stmt, "failedItems")
        val _columnIndexOfErrorLog: Int = getColumnIndexOrThrow(_stmt, "errorLog")
        val _columnIndexOfCanRollback: Int = getColumnIndexOrThrow(_stmt, "canRollback")
        val _columnIndexOfRollbackData: Int = getColumnIndexOrThrow(_stmt, "rollbackData")
        val _columnIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfEstimatedDuration: Int = getColumnIndexOrThrow(_stmt, "estimatedDuration")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<AssetBatchOperationEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AssetBatchOperationEntity
          val _tmpOperationId: String
          _tmpOperationId = _stmt.getText(_columnIndexOfOperationId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpOperationType: String
          _tmpOperationType = _stmt.getText(_columnIndexOfOperationType)
          val _tmpSelectionCriteria: String
          _tmpSelectionCriteria = _stmt.getText(_columnIndexOfSelectionCriteria)
          val _tmpOperationData: String
          _tmpOperationData = _stmt.getText(_columnIndexOfOperationData)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpTotalItems: Int
          _tmpTotalItems = _stmt.getLong(_columnIndexOfTotalItems).toInt()
          val _tmpProcessedItems: Int
          _tmpProcessedItems = _stmt.getLong(_columnIndexOfProcessedItems).toInt()
          val _tmpSuccessfulItems: Int
          _tmpSuccessfulItems = _stmt.getLong(_columnIndexOfSuccessfulItems).toInt()
          val _tmpFailedItems: Int
          _tmpFailedItems = _stmt.getLong(_columnIndexOfFailedItems).toInt()
          val _tmpErrorLog: String?
          if (_stmt.isNull(_columnIndexOfErrorLog)) {
            _tmpErrorLog = null
          } else {
            _tmpErrorLog = _stmt.getText(_columnIndexOfErrorLog)
          }
          val _tmpCanRollback: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfCanRollback).toInt()
          _tmpCanRollback = _tmp != 0
          val _tmpRollbackData: String?
          if (_stmt.isNull(_columnIndexOfRollbackData)) {
            _tmpRollbackData = null
          } else {
            _tmpRollbackData = _stmt.getText(_columnIndexOfRollbackData)
          }
          val _tmpStartedAt: Long?
          if (_stmt.isNull(_columnIndexOfStartedAt)) {
            _tmpStartedAt = null
          } else {
            _tmpStartedAt = _stmt.getLong(_columnIndexOfStartedAt)
          }
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpEstimatedDuration: Long?
          if (_stmt.isNull(_columnIndexOfEstimatedDuration)) {
            _tmpEstimatedDuration = null
          } else {
            _tmpEstimatedDuration = _stmt.getLong(_columnIndexOfEstimatedDuration)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              AssetBatchOperationEntity(_tmpOperationId,_tmpFarmerId,_tmpOperationType,_tmpSelectionCriteria,_tmpOperationData,_tmpStatus,_tmpTotalItems,_tmpProcessedItems,_tmpSuccessfulItems,_tmpFailedItems,_tmpErrorLog,_tmpCanRollback,_tmpRollbackData,_tmpStartedAt,_tmpCompletedAt,_tmpEstimatedDuration,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
