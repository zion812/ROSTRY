package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.HatchingBatchEntity
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
public class HatchingBatchDao_Impl(
  __db: RoomDatabase,
) : HatchingBatchDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfHatchingBatchEntity: EntityInsertAdapter<HatchingBatchEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfHatchingBatchEntity = object : EntityInsertAdapter<HatchingBatchEntity>()
        {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `hatching_batches` (`batchId`,`name`,`farmerId`,`startedAt`,`expectedHatchAt`,`temperatureC`,`humidityPct`,`eggsCount`,`sourceCollectionId`,`notes`,`status`,`hatchedAt`,`updatedAt`,`dirty`,`syncedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: HatchingBatchEntity) {
        statement.bindText(1, entity.batchId)
        statement.bindText(2, entity.name)
        statement.bindText(3, entity.farmerId)
        statement.bindLong(4, entity.startedAt)
        val _tmpExpectedHatchAt: Long? = entity.expectedHatchAt
        if (_tmpExpectedHatchAt == null) {
          statement.bindNull(5)
        } else {
          statement.bindLong(5, _tmpExpectedHatchAt)
        }
        val _tmpTemperatureC: Double? = entity.temperatureC
        if (_tmpTemperatureC == null) {
          statement.bindNull(6)
        } else {
          statement.bindDouble(6, _tmpTemperatureC)
        }
        val _tmpHumidityPct: Double? = entity.humidityPct
        if (_tmpHumidityPct == null) {
          statement.bindNull(7)
        } else {
          statement.bindDouble(7, _tmpHumidityPct)
        }
        val _tmpEggsCount: Int? = entity.eggsCount
        if (_tmpEggsCount == null) {
          statement.bindNull(8)
        } else {
          statement.bindLong(8, _tmpEggsCount.toLong())
        }
        val _tmpSourceCollectionId: String? = entity.sourceCollectionId
        if (_tmpSourceCollectionId == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpSourceCollectionId)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpNotes)
        }
        statement.bindText(11, entity.status)
        val _tmpHatchedAt: Long? = entity.hatchedAt
        if (_tmpHatchedAt == null) {
          statement.bindNull(12)
        } else {
          statement.bindLong(12, _tmpHatchedAt)
        }
        statement.bindLong(13, entity.updatedAt)
        val _tmp: Int = if (entity.dirty) 1 else 0
        statement.bindLong(14, _tmp.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpSyncedAt)
        }
      }
    }
  }

  public override suspend fun upsert(batch: HatchingBatchEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfHatchingBatchEntity.insert(_connection, batch)
  }

  public override fun observeBatches(): Flow<List<HatchingBatchEntity>> {
    val _sql: String = "SELECT * FROM hatching_batches ORDER BY startedAt DESC"
    return createFlow(__db, false, arrayOf("hatching_batches")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _columnIndexOfExpectedHatchAt: Int = getColumnIndexOrThrow(_stmt, "expectedHatchAt")
        val _columnIndexOfTemperatureC: Int = getColumnIndexOrThrow(_stmt, "temperatureC")
        val _columnIndexOfHumidityPct: Int = getColumnIndexOrThrow(_stmt, "humidityPct")
        val _columnIndexOfEggsCount: Int = getColumnIndexOrThrow(_stmt, "eggsCount")
        val _columnIndexOfSourceCollectionId: Int = getColumnIndexOrThrow(_stmt,
            "sourceCollectionId")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfHatchedAt: Int = getColumnIndexOrThrow(_stmt, "hatchedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<HatchingBatchEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: HatchingBatchEntity
          val _tmpBatchId: String
          _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpStartedAt: Long
          _tmpStartedAt = _stmt.getLong(_columnIndexOfStartedAt)
          val _tmpExpectedHatchAt: Long?
          if (_stmt.isNull(_columnIndexOfExpectedHatchAt)) {
            _tmpExpectedHatchAt = null
          } else {
            _tmpExpectedHatchAt = _stmt.getLong(_columnIndexOfExpectedHatchAt)
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
          val _tmpEggsCount: Int?
          if (_stmt.isNull(_columnIndexOfEggsCount)) {
            _tmpEggsCount = null
          } else {
            _tmpEggsCount = _stmt.getLong(_columnIndexOfEggsCount).toInt()
          }
          val _tmpSourceCollectionId: String?
          if (_stmt.isNull(_columnIndexOfSourceCollectionId)) {
            _tmpSourceCollectionId = null
          } else {
            _tmpSourceCollectionId = _stmt.getText(_columnIndexOfSourceCollectionId)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpHatchedAt: Long?
          if (_stmt.isNull(_columnIndexOfHatchedAt)) {
            _tmpHatchedAt = null
          } else {
            _tmpHatchedAt = _stmt.getLong(_columnIndexOfHatchedAt)
          }
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
              HatchingBatchEntity(_tmpBatchId,_tmpName,_tmpFarmerId,_tmpStartedAt,_tmpExpectedHatchAt,_tmpTemperatureC,_tmpHumidityPct,_tmpEggsCount,_tmpSourceCollectionId,_tmpNotes,_tmpStatus,_tmpHatchedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeActiveBatchesForFarmer(farmerId: String, now: Long):
      Flow<List<HatchingBatchEntity>> {
    val _sql: String =
        "SELECT * FROM hatching_batches WHERE farmerId = ? AND status != 'COMPLETED' AND expectedHatchAt IS NOT NULL AND expectedHatchAt > ? ORDER BY expectedHatchAt ASC"
    return createFlow(__db, false, arrayOf("hatching_batches")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _columnIndexOfExpectedHatchAt: Int = getColumnIndexOrThrow(_stmt, "expectedHatchAt")
        val _columnIndexOfTemperatureC: Int = getColumnIndexOrThrow(_stmt, "temperatureC")
        val _columnIndexOfHumidityPct: Int = getColumnIndexOrThrow(_stmt, "humidityPct")
        val _columnIndexOfEggsCount: Int = getColumnIndexOrThrow(_stmt, "eggsCount")
        val _columnIndexOfSourceCollectionId: Int = getColumnIndexOrThrow(_stmt,
            "sourceCollectionId")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfHatchedAt: Int = getColumnIndexOrThrow(_stmt, "hatchedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<HatchingBatchEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: HatchingBatchEntity
          val _tmpBatchId: String
          _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpStartedAt: Long
          _tmpStartedAt = _stmt.getLong(_columnIndexOfStartedAt)
          val _tmpExpectedHatchAt: Long?
          if (_stmt.isNull(_columnIndexOfExpectedHatchAt)) {
            _tmpExpectedHatchAt = null
          } else {
            _tmpExpectedHatchAt = _stmt.getLong(_columnIndexOfExpectedHatchAt)
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
          val _tmpEggsCount: Int?
          if (_stmt.isNull(_columnIndexOfEggsCount)) {
            _tmpEggsCount = null
          } else {
            _tmpEggsCount = _stmt.getLong(_columnIndexOfEggsCount).toInt()
          }
          val _tmpSourceCollectionId: String?
          if (_stmt.isNull(_columnIndexOfSourceCollectionId)) {
            _tmpSourceCollectionId = null
          } else {
            _tmpSourceCollectionId = _stmt.getText(_columnIndexOfSourceCollectionId)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpHatchedAt: Long?
          if (_stmt.isNull(_columnIndexOfHatchedAt)) {
            _tmpHatchedAt = null
          } else {
            _tmpHatchedAt = _stmt.getLong(_columnIndexOfHatchedAt)
          }
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
              HatchingBatchEntity(_tmpBatchId,_tmpName,_tmpFarmerId,_tmpStartedAt,_tmpExpectedHatchAt,_tmpTemperatureC,_tmpHumidityPct,_tmpEggsCount,_tmpSourceCollectionId,_tmpNotes,_tmpStatus,_tmpHatchedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeHatchingDue(
    farmerId: String,
    start: Long,
    end: Long,
  ): Flow<List<HatchingBatchEntity>> {
    val _sql: String =
        "SELECT * FROM hatching_batches WHERE farmerId = ? AND status != 'COMPLETED' AND expectedHatchAt BETWEEN ? AND ? ORDER BY expectedHatchAt ASC"
    return createFlow(__db, false, arrayOf("hatching_batches")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, start)
        _argIndex = 3
        _stmt.bindLong(_argIndex, end)
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _columnIndexOfExpectedHatchAt: Int = getColumnIndexOrThrow(_stmt, "expectedHatchAt")
        val _columnIndexOfTemperatureC: Int = getColumnIndexOrThrow(_stmt, "temperatureC")
        val _columnIndexOfHumidityPct: Int = getColumnIndexOrThrow(_stmt, "humidityPct")
        val _columnIndexOfEggsCount: Int = getColumnIndexOrThrow(_stmt, "eggsCount")
        val _columnIndexOfSourceCollectionId: Int = getColumnIndexOrThrow(_stmt,
            "sourceCollectionId")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfHatchedAt: Int = getColumnIndexOrThrow(_stmt, "hatchedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<HatchingBatchEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: HatchingBatchEntity
          val _tmpBatchId: String
          _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpStartedAt: Long
          _tmpStartedAt = _stmt.getLong(_columnIndexOfStartedAt)
          val _tmpExpectedHatchAt: Long?
          if (_stmt.isNull(_columnIndexOfExpectedHatchAt)) {
            _tmpExpectedHatchAt = null
          } else {
            _tmpExpectedHatchAt = _stmt.getLong(_columnIndexOfExpectedHatchAt)
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
          val _tmpEggsCount: Int?
          if (_stmt.isNull(_columnIndexOfEggsCount)) {
            _tmpEggsCount = null
          } else {
            _tmpEggsCount = _stmt.getLong(_columnIndexOfEggsCount).toInt()
          }
          val _tmpSourceCollectionId: String?
          if (_stmt.isNull(_columnIndexOfSourceCollectionId)) {
            _tmpSourceCollectionId = null
          } else {
            _tmpSourceCollectionId = _stmt.getText(_columnIndexOfSourceCollectionId)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpHatchedAt: Long?
          if (_stmt.isNull(_columnIndexOfHatchedAt)) {
            _tmpHatchedAt = null
          } else {
            _tmpHatchedAt = _stmt.getLong(_columnIndexOfHatchedAt)
          }
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
              HatchingBatchEntity(_tmpBatchId,_tmpName,_tmpFarmerId,_tmpStartedAt,_tmpExpectedHatchAt,_tmpTemperatureC,_tmpHumidityPct,_tmpEggsCount,_tmpSourceCollectionId,_tmpNotes,_tmpStatus,_tmpHatchedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(batchId: String): HatchingBatchEntity? {
    val _sql: String = "SELECT * FROM hatching_batches WHERE batchId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, batchId)
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _columnIndexOfExpectedHatchAt: Int = getColumnIndexOrThrow(_stmt, "expectedHatchAt")
        val _columnIndexOfTemperatureC: Int = getColumnIndexOrThrow(_stmt, "temperatureC")
        val _columnIndexOfHumidityPct: Int = getColumnIndexOrThrow(_stmt, "humidityPct")
        val _columnIndexOfEggsCount: Int = getColumnIndexOrThrow(_stmt, "eggsCount")
        val _columnIndexOfSourceCollectionId: Int = getColumnIndexOrThrow(_stmt,
            "sourceCollectionId")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfHatchedAt: Int = getColumnIndexOrThrow(_stmt, "hatchedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: HatchingBatchEntity?
        if (_stmt.step()) {
          val _tmpBatchId: String
          _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpStartedAt: Long
          _tmpStartedAt = _stmt.getLong(_columnIndexOfStartedAt)
          val _tmpExpectedHatchAt: Long?
          if (_stmt.isNull(_columnIndexOfExpectedHatchAt)) {
            _tmpExpectedHatchAt = null
          } else {
            _tmpExpectedHatchAt = _stmt.getLong(_columnIndexOfExpectedHatchAt)
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
          val _tmpEggsCount: Int?
          if (_stmt.isNull(_columnIndexOfEggsCount)) {
            _tmpEggsCount = null
          } else {
            _tmpEggsCount = _stmt.getLong(_columnIndexOfEggsCount).toInt()
          }
          val _tmpSourceCollectionId: String?
          if (_stmt.isNull(_columnIndexOfSourceCollectionId)) {
            _tmpSourceCollectionId = null
          } else {
            _tmpSourceCollectionId = _stmt.getText(_columnIndexOfSourceCollectionId)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpHatchedAt: Long?
          if (_stmt.isNull(_columnIndexOfHatchedAt)) {
            _tmpHatchedAt = null
          } else {
            _tmpHatchedAt = _stmt.getLong(_columnIndexOfHatchedAt)
          }
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
              HatchingBatchEntity(_tmpBatchId,_tmpName,_tmpFarmerId,_tmpStartedAt,_tmpExpectedHatchAt,_tmpTemperatureC,_tmpHumidityPct,_tmpEggsCount,_tmpSourceCollectionId,_tmpNotes,_tmpStatus,_tmpHatchedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countActiveForFarmer(farmerId: String, now: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM hatching_batches WHERE farmerId = ? AND status != 'COMPLETED' AND expectedHatchAt IS NOT NULL AND expectedHatchAt > ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
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

  public override suspend fun countDueThisWeekForFarmer(
    farmerId: String,
    now: Long,
    weekEnd: Long,
  ): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM hatching_batches WHERE farmerId = ? AND status != 'COMPLETED' AND expectedHatchAt BETWEEN ? AND ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
        _argIndex = 3
        _stmt.bindLong(_argIndex, weekEnd)
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

  public override fun observeActiveForFarmer(farmerId: String, now: Long): Flow<Int> {
    val _sql: String =
        "SELECT COUNT(*) FROM hatching_batches WHERE farmerId = ? AND status != 'COMPLETED' AND expectedHatchAt IS NOT NULL AND expectedHatchAt > ?"
    return createFlow(__db, false, arrayOf("hatching_batches")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
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

  public override fun observeDueThisWeekForFarmer(
    farmerId: String,
    now: Long,
    weekEnd: Long,
  ): Flow<Int> {
    val _sql: String =
        "SELECT COUNT(*) FROM hatching_batches WHERE farmerId = ? AND status != 'COMPLETED' AND expectedHatchAt BETWEEN ? AND ?"
    return createFlow(__db, false, arrayOf("hatching_batches")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
        _argIndex = 3
        _stmt.bindLong(_argIndex, weekEnd)
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

  public override suspend fun getHatchingDueSoon(
    farmerId: String,
    start: Long,
    end: Long,
  ): List<HatchingBatchEntity> {
    val _sql: String =
        "SELECT * FROM hatching_batches WHERE farmerId = ? AND status = 'ACTIVE' AND expectedHatchAt BETWEEN ? AND ? ORDER BY expectedHatchAt ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, start)
        _argIndex = 3
        _stmt.bindLong(_argIndex, end)
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _columnIndexOfExpectedHatchAt: Int = getColumnIndexOrThrow(_stmt, "expectedHatchAt")
        val _columnIndexOfTemperatureC: Int = getColumnIndexOrThrow(_stmt, "temperatureC")
        val _columnIndexOfHumidityPct: Int = getColumnIndexOrThrow(_stmt, "humidityPct")
        val _columnIndexOfEggsCount: Int = getColumnIndexOrThrow(_stmt, "eggsCount")
        val _columnIndexOfSourceCollectionId: Int = getColumnIndexOrThrow(_stmt,
            "sourceCollectionId")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfHatchedAt: Int = getColumnIndexOrThrow(_stmt, "hatchedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<HatchingBatchEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: HatchingBatchEntity
          val _tmpBatchId: String
          _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpStartedAt: Long
          _tmpStartedAt = _stmt.getLong(_columnIndexOfStartedAt)
          val _tmpExpectedHatchAt: Long?
          if (_stmt.isNull(_columnIndexOfExpectedHatchAt)) {
            _tmpExpectedHatchAt = null
          } else {
            _tmpExpectedHatchAt = _stmt.getLong(_columnIndexOfExpectedHatchAt)
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
          val _tmpEggsCount: Int?
          if (_stmt.isNull(_columnIndexOfEggsCount)) {
            _tmpEggsCount = null
          } else {
            _tmpEggsCount = _stmt.getLong(_columnIndexOfEggsCount).toInt()
          }
          val _tmpSourceCollectionId: String?
          if (_stmt.isNull(_columnIndexOfSourceCollectionId)) {
            _tmpSourceCollectionId = null
          } else {
            _tmpSourceCollectionId = _stmt.getText(_columnIndexOfSourceCollectionId)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpHatchedAt: Long?
          if (_stmt.isNull(_columnIndexOfHatchedAt)) {
            _tmpHatchedAt = null
          } else {
            _tmpHatchedAt = _stmt.getLong(_columnIndexOfHatchedAt)
          }
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
              HatchingBatchEntity(_tmpBatchId,_tmpName,_tmpFarmerId,_tmpStartedAt,_tmpExpectedHatchAt,_tmpTemperatureC,_tmpHumidityPct,_tmpEggsCount,_tmpSourceCollectionId,_tmpNotes,_tmpStatus,_tmpHatchedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeTotalActiveEggs(farmerId: String, now: Long): Flow<Int?> {
    val _sql: String =
        "SELECT SUM(eggsCount) FROM hatching_batches WHERE farmerId = ? AND status != 'COMPLETED' AND expectedHatchAt IS NOT NULL AND expectedHatchAt > ?"
    return createFlow(__db, false, arrayOf("hatching_batches")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
        val _result: Int?
        if (_stmt.step()) {
          val _tmp: Int?
          if (_stmt.isNull(0)) {
            _tmp = null
          } else {
            _tmp = _stmt.getLong(0).toInt()
          }
          _result = _tmp
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getBySourceCollectionIds(collectionIds: List<String>):
      List<HatchingBatchEntity> {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT * FROM hatching_batches WHERE sourceCollectionId IN (")
    val _inputSize: Int = collectionIds.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        for (_item: String in collectionIds) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _columnIndexOfExpectedHatchAt: Int = getColumnIndexOrThrow(_stmt, "expectedHatchAt")
        val _columnIndexOfTemperatureC: Int = getColumnIndexOrThrow(_stmt, "temperatureC")
        val _columnIndexOfHumidityPct: Int = getColumnIndexOrThrow(_stmt, "humidityPct")
        val _columnIndexOfEggsCount: Int = getColumnIndexOrThrow(_stmt, "eggsCount")
        val _columnIndexOfSourceCollectionId: Int = getColumnIndexOrThrow(_stmt,
            "sourceCollectionId")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfHatchedAt: Int = getColumnIndexOrThrow(_stmt, "hatchedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<HatchingBatchEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item_1: HatchingBatchEntity
          val _tmpBatchId: String
          _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpStartedAt: Long
          _tmpStartedAt = _stmt.getLong(_columnIndexOfStartedAt)
          val _tmpExpectedHatchAt: Long?
          if (_stmt.isNull(_columnIndexOfExpectedHatchAt)) {
            _tmpExpectedHatchAt = null
          } else {
            _tmpExpectedHatchAt = _stmt.getLong(_columnIndexOfExpectedHatchAt)
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
          val _tmpEggsCount: Int?
          if (_stmt.isNull(_columnIndexOfEggsCount)) {
            _tmpEggsCount = null
          } else {
            _tmpEggsCount = _stmt.getLong(_columnIndexOfEggsCount).toInt()
          }
          val _tmpSourceCollectionId: String?
          if (_stmt.isNull(_columnIndexOfSourceCollectionId)) {
            _tmpSourceCollectionId = null
          } else {
            _tmpSourceCollectionId = _stmt.getText(_columnIndexOfSourceCollectionId)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpHatchedAt: Long?
          if (_stmt.isNull(_columnIndexOfHatchedAt)) {
            _tmpHatchedAt = null
          } else {
            _tmpHatchedAt = _stmt.getLong(_columnIndexOfHatchedAt)
          }
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
          _item_1 =
              HatchingBatchEntity(_tmpBatchId,_tmpName,_tmpFarmerId,_tmpStartedAt,_tmpExpectedHatchAt,_tmpTemperatureC,_tmpHumidityPct,_tmpEggsCount,_tmpSourceCollectionId,_tmpNotes,_tmpStatus,_tmpHatchedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item_1)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDirty(): List<HatchingBatchEntity> {
    val _sql: String = "SELECT * FROM hatching_batches WHERE dirty = 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _columnIndexOfExpectedHatchAt: Int = getColumnIndexOrThrow(_stmt, "expectedHatchAt")
        val _columnIndexOfTemperatureC: Int = getColumnIndexOrThrow(_stmt, "temperatureC")
        val _columnIndexOfHumidityPct: Int = getColumnIndexOrThrow(_stmt, "humidityPct")
        val _columnIndexOfEggsCount: Int = getColumnIndexOrThrow(_stmt, "eggsCount")
        val _columnIndexOfSourceCollectionId: Int = getColumnIndexOrThrow(_stmt,
            "sourceCollectionId")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfHatchedAt: Int = getColumnIndexOrThrow(_stmt, "hatchedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<HatchingBatchEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: HatchingBatchEntity
          val _tmpBatchId: String
          _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpStartedAt: Long
          _tmpStartedAt = _stmt.getLong(_columnIndexOfStartedAt)
          val _tmpExpectedHatchAt: Long?
          if (_stmt.isNull(_columnIndexOfExpectedHatchAt)) {
            _tmpExpectedHatchAt = null
          } else {
            _tmpExpectedHatchAt = _stmt.getLong(_columnIndexOfExpectedHatchAt)
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
          val _tmpEggsCount: Int?
          if (_stmt.isNull(_columnIndexOfEggsCount)) {
            _tmpEggsCount = null
          } else {
            _tmpEggsCount = _stmt.getLong(_columnIndexOfEggsCount).toInt()
          }
          val _tmpSourceCollectionId: String?
          if (_stmt.isNull(_columnIndexOfSourceCollectionId)) {
            _tmpSourceCollectionId = null
          } else {
            _tmpSourceCollectionId = _stmt.getText(_columnIndexOfSourceCollectionId)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpHatchedAt: Long?
          if (_stmt.isNull(_columnIndexOfHatchedAt)) {
            _tmpHatchedAt = null
          } else {
            _tmpHatchedAt = _stmt.getLong(_columnIndexOfHatchedAt)
          }
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
              HatchingBatchEntity(_tmpBatchId,_tmpName,_tmpFarmerId,_tmpStartedAt,_tmpExpectedHatchAt,_tmpTemperatureC,_tmpHumidityPct,_tmpEggsCount,_tmpSourceCollectionId,_tmpNotes,_tmpStatus,_tmpHatchedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countIncubatingForFarmer(farmerId: String, now: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM hatching_batches WHERE farmerId = ? AND status IN ('INCUBATING', 'ACTIVE') AND expectedHatchAt > ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
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

  public override suspend fun clearDirty(batchIds: List<String>, syncedAt: Long) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("UPDATE hatching_batches SET dirty = 0, syncedAt = ")
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

  public override suspend fun updateConditions(
    batchId: String,
    temperature: Double?,
    humidity: Double?,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE hatching_batches SET temperatureC = ?, humidityPct = ?, updatedAt = ? WHERE batchId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        if (temperature == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindDouble(_argIndex, temperature)
        }
        _argIndex = 2
        if (humidity == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindDouble(_argIndex, humidity)
        }
        _argIndex = 3
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 4
        _stmt.bindText(_argIndex, batchId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun decrementEggs(
    batchId: String,
    count: Int,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE hatching_batches SET eggsCount = eggsCount - ?, updatedAt = ? WHERE batchId = ? AND eggsCount >= ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, count.toLong())
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, batchId)
        _argIndex = 4
        _stmt.bindLong(_argIndex, count.toLong())
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
