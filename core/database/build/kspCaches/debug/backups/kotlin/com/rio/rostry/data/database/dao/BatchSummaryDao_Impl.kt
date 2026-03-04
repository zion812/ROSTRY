package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.BatchSummaryEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Double
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass
import kotlin.text.StringBuilder
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class BatchSummaryDao_Impl(
  __db: RoomDatabase,
) : BatchSummaryDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfBatchSummaryEntity: EntityInsertAdapter<BatchSummaryEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfBatchSummaryEntity = object : EntityInsertAdapter<BatchSummaryEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `batch_summaries` (`batchId`,`farmerId`,`batchName`,`currentCount`,`avgWeightGrams`,`totalFeedKg`,`fcr`,`ageWeeks`,`hatchDate`,`status`,`createdAt`,`updatedAt`,`dirty`,`syncedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: BatchSummaryEntity) {
        statement.bindText(1, entity.batchId)
        statement.bindText(2, entity.farmerId)
        statement.bindText(3, entity.batchName)
        statement.bindLong(4, entity.currentCount.toLong())
        statement.bindDouble(5, entity.avgWeightGrams)
        statement.bindDouble(6, entity.totalFeedKg)
        statement.bindDouble(7, entity.fcr)
        statement.bindLong(8, entity.ageWeeks.toLong())
        val _tmpHatchDate: Long? = entity.hatchDate
        if (_tmpHatchDate == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpHatchDate)
        }
        statement.bindText(10, entity.status)
        statement.bindLong(11, entity.createdAt)
        statement.bindLong(12, entity.updatedAt)
        val _tmp: Int = if (entity.dirty) 1 else 0
        statement.bindLong(13, _tmp.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(14)
        } else {
          statement.bindLong(14, _tmpSyncedAt)
        }
      }
    }
  }

  public override suspend fun upsert(summary: BatchSummaryEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfBatchSummaryEntity.insert(_connection, summary)
  }

  public override suspend fun upsertAll(summaries: List<BatchSummaryEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfBatchSummaryEntity.insert(_connection, summaries)
  }

  public override fun observeForFarmer(farmerId: String): Flow<List<BatchSummaryEntity>> {
    val _sql: String = "SELECT * FROM batch_summaries WHERE farmerId = ? ORDER BY updatedAt DESC"
    return createFlow(__db, false, arrayOf("batch_summaries")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfBatchName: Int = getColumnIndexOrThrow(_stmt, "batchName")
        val _columnIndexOfCurrentCount: Int = getColumnIndexOrThrow(_stmt, "currentCount")
        val _columnIndexOfAvgWeightGrams: Int = getColumnIndexOrThrow(_stmt, "avgWeightGrams")
        val _columnIndexOfTotalFeedKg: Int = getColumnIndexOrThrow(_stmt, "totalFeedKg")
        val _columnIndexOfFcr: Int = getColumnIndexOrThrow(_stmt, "fcr")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfHatchDate: Int = getColumnIndexOrThrow(_stmt, "hatchDate")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<BatchSummaryEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BatchSummaryEntity
          val _tmpBatchId: String
          _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpBatchName: String
          _tmpBatchName = _stmt.getText(_columnIndexOfBatchName)
          val _tmpCurrentCount: Int
          _tmpCurrentCount = _stmt.getLong(_columnIndexOfCurrentCount).toInt()
          val _tmpAvgWeightGrams: Double
          _tmpAvgWeightGrams = _stmt.getDouble(_columnIndexOfAvgWeightGrams)
          val _tmpTotalFeedKg: Double
          _tmpTotalFeedKg = _stmt.getDouble(_columnIndexOfTotalFeedKg)
          val _tmpFcr: Double
          _tmpFcr = _stmt.getDouble(_columnIndexOfFcr)
          val _tmpAgeWeeks: Int
          _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          val _tmpHatchDate: Long?
          if (_stmt.isNull(_columnIndexOfHatchDate)) {
            _tmpHatchDate = null
          } else {
            _tmpHatchDate = _stmt.getLong(_columnIndexOfHatchDate)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              BatchSummaryEntity(_tmpBatchId,_tmpFarmerId,_tmpBatchName,_tmpCurrentCount,_tmpAvgWeightGrams,_tmpTotalFeedKg,_tmpFcr,_tmpAgeWeeks,_tmpHatchDate,_tmpStatus,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByStatus(farmerId: String, status: String):
      Flow<List<BatchSummaryEntity>> {
    val _sql: String =
        "SELECT * FROM batch_summaries WHERE farmerId = ? AND status = ? ORDER BY updatedAt DESC"
    return createFlow(__db, false, arrayOf("batch_summaries")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindText(_argIndex, status)
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfBatchName: Int = getColumnIndexOrThrow(_stmt, "batchName")
        val _columnIndexOfCurrentCount: Int = getColumnIndexOrThrow(_stmt, "currentCount")
        val _columnIndexOfAvgWeightGrams: Int = getColumnIndexOrThrow(_stmt, "avgWeightGrams")
        val _columnIndexOfTotalFeedKg: Int = getColumnIndexOrThrow(_stmt, "totalFeedKg")
        val _columnIndexOfFcr: Int = getColumnIndexOrThrow(_stmt, "fcr")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfHatchDate: Int = getColumnIndexOrThrow(_stmt, "hatchDate")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<BatchSummaryEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BatchSummaryEntity
          val _tmpBatchId: String
          _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpBatchName: String
          _tmpBatchName = _stmt.getText(_columnIndexOfBatchName)
          val _tmpCurrentCount: Int
          _tmpCurrentCount = _stmt.getLong(_columnIndexOfCurrentCount).toInt()
          val _tmpAvgWeightGrams: Double
          _tmpAvgWeightGrams = _stmt.getDouble(_columnIndexOfAvgWeightGrams)
          val _tmpTotalFeedKg: Double
          _tmpTotalFeedKg = _stmt.getDouble(_columnIndexOfTotalFeedKg)
          val _tmpFcr: Double
          _tmpFcr = _stmt.getDouble(_columnIndexOfFcr)
          val _tmpAgeWeeks: Int
          _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          val _tmpHatchDate: Long?
          if (_stmt.isNull(_columnIndexOfHatchDate)) {
            _tmpHatchDate = null
          } else {
            _tmpHatchDate = _stmt.getLong(_columnIndexOfHatchDate)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              BatchSummaryEntity(_tmpBatchId,_tmpFarmerId,_tmpBatchName,_tmpCurrentCount,_tmpAvgWeightGrams,_tmpTotalFeedKg,_tmpFcr,_tmpAgeWeeks,_tmpHatchDate,_tmpStatus,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(batchId: String): BatchSummaryEntity? {
    val _sql: String = "SELECT * FROM batch_summaries WHERE batchId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, batchId)
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfBatchName: Int = getColumnIndexOrThrow(_stmt, "batchName")
        val _columnIndexOfCurrentCount: Int = getColumnIndexOrThrow(_stmt, "currentCount")
        val _columnIndexOfAvgWeightGrams: Int = getColumnIndexOrThrow(_stmt, "avgWeightGrams")
        val _columnIndexOfTotalFeedKg: Int = getColumnIndexOrThrow(_stmt, "totalFeedKg")
        val _columnIndexOfFcr: Int = getColumnIndexOrThrow(_stmt, "fcr")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfHatchDate: Int = getColumnIndexOrThrow(_stmt, "hatchDate")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: BatchSummaryEntity?
        if (_stmt.step()) {
          val _tmpBatchId: String
          _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpBatchName: String
          _tmpBatchName = _stmt.getText(_columnIndexOfBatchName)
          val _tmpCurrentCount: Int
          _tmpCurrentCount = _stmt.getLong(_columnIndexOfCurrentCount).toInt()
          val _tmpAvgWeightGrams: Double
          _tmpAvgWeightGrams = _stmt.getDouble(_columnIndexOfAvgWeightGrams)
          val _tmpTotalFeedKg: Double
          _tmpTotalFeedKg = _stmt.getDouble(_columnIndexOfTotalFeedKg)
          val _tmpFcr: Double
          _tmpFcr = _stmt.getDouble(_columnIndexOfFcr)
          val _tmpAgeWeeks: Int
          _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          val _tmpHatchDate: Long?
          if (_stmt.isNull(_columnIndexOfHatchDate)) {
            _tmpHatchDate = null
          } else {
            _tmpHatchDate = _stmt.getLong(_columnIndexOfHatchDate)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _result =
              BatchSummaryEntity(_tmpBatchId,_tmpFarmerId,_tmpBatchName,_tmpCurrentCount,_tmpAvgWeightGrams,_tmpTotalFeedKg,_tmpFcr,_tmpAgeWeeks,_tmpHatchDate,_tmpStatus,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllForFarmer(farmerId: String): List<BatchSummaryEntity> {
    val _sql: String = "SELECT * FROM batch_summaries WHERE farmerId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfBatchName: Int = getColumnIndexOrThrow(_stmt, "batchName")
        val _columnIndexOfCurrentCount: Int = getColumnIndexOrThrow(_stmt, "currentCount")
        val _columnIndexOfAvgWeightGrams: Int = getColumnIndexOrThrow(_stmt, "avgWeightGrams")
        val _columnIndexOfTotalFeedKg: Int = getColumnIndexOrThrow(_stmt, "totalFeedKg")
        val _columnIndexOfFcr: Int = getColumnIndexOrThrow(_stmt, "fcr")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfHatchDate: Int = getColumnIndexOrThrow(_stmt, "hatchDate")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<BatchSummaryEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BatchSummaryEntity
          val _tmpBatchId: String
          _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpBatchName: String
          _tmpBatchName = _stmt.getText(_columnIndexOfBatchName)
          val _tmpCurrentCount: Int
          _tmpCurrentCount = _stmt.getLong(_columnIndexOfCurrentCount).toInt()
          val _tmpAvgWeightGrams: Double
          _tmpAvgWeightGrams = _stmt.getDouble(_columnIndexOfAvgWeightGrams)
          val _tmpTotalFeedKg: Double
          _tmpTotalFeedKg = _stmt.getDouble(_columnIndexOfTotalFeedKg)
          val _tmpFcr: Double
          _tmpFcr = _stmt.getDouble(_columnIndexOfFcr)
          val _tmpAgeWeeks: Int
          _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          val _tmpHatchDate: Long?
          if (_stmt.isNull(_columnIndexOfHatchDate)) {
            _tmpHatchDate = null
          } else {
            _tmpHatchDate = _stmt.getLong(_columnIndexOfHatchDate)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              BatchSummaryEntity(_tmpBatchId,_tmpFarmerId,_tmpBatchName,_tmpCurrentCount,_tmpAvgWeightGrams,_tmpTotalFeedKg,_tmpFcr,_tmpAgeWeeks,_tmpHatchDate,_tmpStatus,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDirty(): List<BatchSummaryEntity> {
    val _sql: String = "SELECT * FROM batch_summaries WHERE dirty = 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfBatchName: Int = getColumnIndexOrThrow(_stmt, "batchName")
        val _columnIndexOfCurrentCount: Int = getColumnIndexOrThrow(_stmt, "currentCount")
        val _columnIndexOfAvgWeightGrams: Int = getColumnIndexOrThrow(_stmt, "avgWeightGrams")
        val _columnIndexOfTotalFeedKg: Int = getColumnIndexOrThrow(_stmt, "totalFeedKg")
        val _columnIndexOfFcr: Int = getColumnIndexOrThrow(_stmt, "fcr")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfHatchDate: Int = getColumnIndexOrThrow(_stmt, "hatchDate")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<BatchSummaryEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BatchSummaryEntity
          val _tmpBatchId: String
          _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpBatchName: String
          _tmpBatchName = _stmt.getText(_columnIndexOfBatchName)
          val _tmpCurrentCount: Int
          _tmpCurrentCount = _stmt.getLong(_columnIndexOfCurrentCount).toInt()
          val _tmpAvgWeightGrams: Double
          _tmpAvgWeightGrams = _stmt.getDouble(_columnIndexOfAvgWeightGrams)
          val _tmpTotalFeedKg: Double
          _tmpTotalFeedKg = _stmt.getDouble(_columnIndexOfTotalFeedKg)
          val _tmpFcr: Double
          _tmpFcr = _stmt.getDouble(_columnIndexOfFcr)
          val _tmpAgeWeeks: Int
          _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          val _tmpHatchDate: Long?
          if (_stmt.isNull(_columnIndexOfHatchDate)) {
            _tmpHatchDate = null
          } else {
            _tmpHatchDate = _stmt.getLong(_columnIndexOfHatchDate)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              BatchSummaryEntity(_tmpBatchId,_tmpFarmerId,_tmpBatchName,_tmpCurrentCount,_tmpAvgWeightGrams,_tmpTotalFeedKg,_tmpFcr,_tmpAgeWeeks,_tmpHatchDate,_tmpStatus,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getActiveBatchCount(farmerId: String): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM batch_summaries WHERE farmerId = ? AND status = 'ACTIVE'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTotalBirdCount(farmerId: String): Int {
    val _sql: String =
        "SELECT COALESCE(SUM(currentCount), 0) FROM batch_summaries WHERE farmerId = ? AND status = 'ACTIVE'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAverageFcr(farmerId: String): Double {
    val _sql: String =
        "SELECT COALESCE(AVG(fcr), 0.0) FROM batch_summaries WHERE farmerId = ? AND status = 'ACTIVE' AND fcr > 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _result: Double
        if (_stmt.step()) {
          val _tmp: Double
          _tmp = _stmt.getDouble(0)
          _result = _tmp
        } else {
          _result = 0.0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearDirty(batchIds: List<String>, syncedAt: Long) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("UPDATE batch_summaries SET dirty = 0, syncedAt = ")
    _stringBuilder.append("?")
    _stringBuilder.append(" WHERE batchId IN (")
    val _inputSize: Int = batchIds.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, syncedAt)
        _argIndex = 2
        for (_item: String in batchIds) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun delete(batchId: String) {
    val _sql: String = "DELETE FROM batch_summaries WHERE batchId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, batchId)
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
