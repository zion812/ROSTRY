package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.HatchingLogEntity
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
public class HatchingLogDao_Impl(
  __db: RoomDatabase,
) : HatchingLogDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfHatchingLogEntity: EntityInsertAdapter<HatchingLogEntity>

  private val __insertAdapterOfHatchingLogEntity_1: EntityInsertAdapter<HatchingLogEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfHatchingLogEntity = object : EntityInsertAdapter<HatchingLogEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `hatching_logs` (`logId`,`batchId`,`farmerId`,`productId`,`eventType`,`qualityScore`,`temperatureC`,`humidityPct`,`notes`,`createdAt`,`updatedAt`,`dirty`,`syncedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: HatchingLogEntity) {
        statement.bindText(1, entity.logId)
        statement.bindText(2, entity.batchId)
        statement.bindText(3, entity.farmerId)
        val _tmpProductId: String? = entity.productId
        if (_tmpProductId == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpProductId)
        }
        statement.bindText(5, entity.eventType)
        val _tmpQualityScore: Int? = entity.qualityScore
        if (_tmpQualityScore == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpQualityScore.toLong())
        }
        val _tmpTemperatureC: Double? = entity.temperatureC
        if (_tmpTemperatureC == null) {
          statement.bindNull(7)
        } else {
          statement.bindDouble(7, _tmpTemperatureC)
        }
        val _tmpHumidityPct: Double? = entity.humidityPct
        if (_tmpHumidityPct == null) {
          statement.bindNull(8)
        } else {
          statement.bindDouble(8, _tmpHumidityPct)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpNotes)
        }
        statement.bindLong(10, entity.createdAt)
        statement.bindLong(11, entity.updatedAt)
        val _tmp: Int = if (entity.dirty) 1 else 0
        statement.bindLong(12, _tmp.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpSyncedAt)
        }
      }
    }
    this.__insertAdapterOfHatchingLogEntity_1 = object : EntityInsertAdapter<HatchingLogEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `hatching_logs` (`logId`,`batchId`,`farmerId`,`productId`,`eventType`,`qualityScore`,`temperatureC`,`humidityPct`,`notes`,`createdAt`,`updatedAt`,`dirty`,`syncedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: HatchingLogEntity) {
        statement.bindText(1, entity.logId)
        statement.bindText(2, entity.batchId)
        statement.bindText(3, entity.farmerId)
        val _tmpProductId: String? = entity.productId
        if (_tmpProductId == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpProductId)
        }
        statement.bindText(5, entity.eventType)
        val _tmpQualityScore: Int? = entity.qualityScore
        if (_tmpQualityScore == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpQualityScore.toLong())
        }
        val _tmpTemperatureC: Double? = entity.temperatureC
        if (_tmpTemperatureC == null) {
          statement.bindNull(7)
        } else {
          statement.bindDouble(7, _tmpTemperatureC)
        }
        val _tmpHumidityPct: Double? = entity.humidityPct
        if (_tmpHumidityPct == null) {
          statement.bindNull(8)
        } else {
          statement.bindDouble(8, _tmpHumidityPct)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpNotes)
        }
        statement.bindLong(10, entity.createdAt)
        statement.bindLong(11, entity.updatedAt)
        val _tmp: Int = if (entity.dirty) 1 else 0
        statement.bindLong(12, _tmp.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpSyncedAt)
        }
      }
    }
  }

  public override suspend fun insert(log: HatchingLogEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfHatchingLogEntity.insert(_connection, log)
  }

  public override suspend fun upsert(log: HatchingLogEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfHatchingLogEntity_1.insert(_connection, log)
  }

  public override fun observeForBatch(batchId: String): Flow<List<HatchingLogEntity>> {
    val _sql: String = "SELECT * FROM hatching_logs WHERE batchId = ? ORDER BY createdAt ASC"
    return createFlow(__db, false, arrayOf("hatching_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, batchId)
        val _columnIndexOfLogId: Int = getColumnIndexOrThrow(_stmt, "logId")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfQualityScore: Int = getColumnIndexOrThrow(_stmt, "qualityScore")
        val _columnIndexOfTemperatureC: Int = getColumnIndexOrThrow(_stmt, "temperatureC")
        val _columnIndexOfHumidityPct: Int = getColumnIndexOrThrow(_stmt, "humidityPct")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<HatchingLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: HatchingLogEntity
          val _tmpLogId: String
          _tmpLogId = _stmt.getText(_columnIndexOfLogId)
          val _tmpBatchId: String
          _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpQualityScore: Int?
          if (_stmt.isNull(_columnIndexOfQualityScore)) {
            _tmpQualityScore = null
          } else {
            _tmpQualityScore = _stmt.getLong(_columnIndexOfQualityScore).toInt()
          }
          val _tmpTemperatureC: Double?
          if (_stmt.isNull(_columnIndexOfTemperatureC)) {
            _tmpTemperatureC = null
          } else {
            _tmpTemperatureC = _stmt.getDouble(_columnIndexOfTemperatureC)
          }
          val _tmpHumidityPct: Double?
          if (_stmt.isNull(_columnIndexOfHumidityPct)) {
            _tmpHumidityPct = null
          } else {
            _tmpHumidityPct = _stmt.getDouble(_columnIndexOfHumidityPct)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
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
              HatchingLogEntity(_tmpLogId,_tmpBatchId,_tmpFarmerId,_tmpProductId,_tmpEventType,_tmpQualityScore,_tmpTemperatureC,_tmpHumidityPct,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countByBatchAndType(batchId: String, type: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM hatching_logs WHERE batchId = ? AND eventType = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, batchId)
        _argIndex = 2
        _stmt.bindText(_argIndex, type)
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

  public override suspend fun countHatchedBetweenForFarmer(
    farmerId: String,
    start: Long,
    end: Long,
  ): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM hatching_logs WHERE farmerId = ? AND eventType = 'HATCHED' AND createdAt BETWEEN ? AND ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, start)
        _argIndex = 3
        _stmt.bindLong(_argIndex, end)
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

  public override suspend fun countEggsSetBetweenForFarmer(
    farmerId: String,
    start: Long,
    end: Long,
  ): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM hatching_logs WHERE farmerId = ? AND eventType = 'SET' AND createdAt BETWEEN ? AND ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, start)
        _argIndex = 3
        _stmt.bindLong(_argIndex, end)
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

  public override suspend fun getDirty(): List<HatchingLogEntity> {
    val _sql: String = "SELECT * FROM hatching_logs WHERE dirty = 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfLogId: Int = getColumnIndexOrThrow(_stmt, "logId")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfEventType: Int = getColumnIndexOrThrow(_stmt, "eventType")
        val _columnIndexOfQualityScore: Int = getColumnIndexOrThrow(_stmt, "qualityScore")
        val _columnIndexOfTemperatureC: Int = getColumnIndexOrThrow(_stmt, "temperatureC")
        val _columnIndexOfHumidityPct: Int = getColumnIndexOrThrow(_stmt, "humidityPct")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<HatchingLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: HatchingLogEntity
          val _tmpLogId: String
          _tmpLogId = _stmt.getText(_columnIndexOfLogId)
          val _tmpBatchId: String
          _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpEventType: String
          _tmpEventType = _stmt.getText(_columnIndexOfEventType)
          val _tmpQualityScore: Int?
          if (_stmt.isNull(_columnIndexOfQualityScore)) {
            _tmpQualityScore = null
          } else {
            _tmpQualityScore = _stmt.getLong(_columnIndexOfQualityScore).toInt()
          }
          val _tmpTemperatureC: Double?
          if (_stmt.isNull(_columnIndexOfTemperatureC)) {
            _tmpTemperatureC = null
          } else {
            _tmpTemperatureC = _stmt.getDouble(_columnIndexOfTemperatureC)
          }
          val _tmpHumidityPct: Double?
          if (_stmt.isNull(_columnIndexOfHumidityPct)) {
            _tmpHumidityPct = null
          } else {
            _tmpHumidityPct = _stmt.getDouble(_columnIndexOfHumidityPct)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
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
              HatchingLogEntity(_tmpLogId,_tmpBatchId,_tmpFarmerId,_tmpProductId,_tmpEventType,_tmpQualityScore,_tmpTemperatureC,_tmpHumidityPct,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearDirty(logIds: List<String>, syncedAt: Long) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("UPDATE hatching_logs SET dirty = 0, syncedAt = ")
    _stringBuilder.append("?")
    _stringBuilder.append(" WHERE logId IN (")
    val _inputSize: Int = logIds.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, syncedAt)
        _argIndex = 2
        for (_item: String in logIds) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
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
