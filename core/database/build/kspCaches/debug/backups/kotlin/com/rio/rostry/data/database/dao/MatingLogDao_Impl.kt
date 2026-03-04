package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.EntityUpsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.MatingLogEntity
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
import kotlin.text.StringBuilder
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class MatingLogDao_Impl(
  __db: RoomDatabase,
) : MatingLogDao {
  private val __db: RoomDatabase

  private val __upsertAdapterOfMatingLogEntity: EntityUpsertAdapter<MatingLogEntity>
  init {
    this.__db = __db
    this.__upsertAdapterOfMatingLogEntity = EntityUpsertAdapter<MatingLogEntity>(object :
        EntityInsertAdapter<MatingLogEntity>() {
      protected override fun createQuery(): String =
          "INSERT INTO `mating_logs` (`logId`,`pairId`,`farmerId`,`matedAt`,`observedBehavior`,`environmentalConditions`,`notes`,`createdAt`,`updatedAt`,`dirty`,`syncedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: MatingLogEntity) {
        statement.bindText(1, entity.logId)
        statement.bindText(2, entity.pairId)
        statement.bindText(3, entity.farmerId)
        statement.bindLong(4, entity.matedAt)
        val _tmpObservedBehavior: String? = entity.observedBehavior
        if (_tmpObservedBehavior == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpObservedBehavior)
        }
        val _tmpEnvironmentalConditions: String? = entity.environmentalConditions
        if (_tmpEnvironmentalConditions == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpEnvironmentalConditions)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpNotes)
        }
        statement.bindLong(8, entity.createdAt)
        statement.bindLong(9, entity.updatedAt)
        val _tmp: Int = if (entity.dirty) 1 else 0
        statement.bindLong(10, _tmp.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpSyncedAt)
        }
      }
    }, object : EntityDeleteOrUpdateAdapter<MatingLogEntity>() {
      protected override fun createQuery(): String =
          "UPDATE `mating_logs` SET `logId` = ?,`pairId` = ?,`farmerId` = ?,`matedAt` = ?,`observedBehavior` = ?,`environmentalConditions` = ?,`notes` = ?,`createdAt` = ?,`updatedAt` = ?,`dirty` = ?,`syncedAt` = ? WHERE `logId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: MatingLogEntity) {
        statement.bindText(1, entity.logId)
        statement.bindText(2, entity.pairId)
        statement.bindText(3, entity.farmerId)
        statement.bindLong(4, entity.matedAt)
        val _tmpObservedBehavior: String? = entity.observedBehavior
        if (_tmpObservedBehavior == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpObservedBehavior)
        }
        val _tmpEnvironmentalConditions: String? = entity.environmentalConditions
        if (_tmpEnvironmentalConditions == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpEnvironmentalConditions)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpNotes)
        }
        statement.bindLong(8, entity.createdAt)
        statement.bindLong(9, entity.updatedAt)
        val _tmp: Int = if (entity.dirty) 1 else 0
        statement.bindLong(10, _tmp.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpSyncedAt)
        }
        statement.bindText(12, entity.logId)
      }
    })
  }

  public override suspend fun upsert(log: MatingLogEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __upsertAdapterOfMatingLogEntity.upsert(_connection, log)
  }

  public override fun observeByPair(pairId: String): Flow<List<MatingLogEntity>> {
    val _sql: String = "SELECT * FROM mating_logs WHERE pairId = ? ORDER BY matedAt DESC"
    return createFlow(__db, false, arrayOf("mating_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, pairId)
        val _columnIndexOfLogId: Int = getColumnIndexOrThrow(_stmt, "logId")
        val _columnIndexOfPairId: Int = getColumnIndexOrThrow(_stmt, "pairId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfMatedAt: Int = getColumnIndexOrThrow(_stmt, "matedAt")
        val _columnIndexOfObservedBehavior: Int = getColumnIndexOrThrow(_stmt, "observedBehavior")
        val _columnIndexOfEnvironmentalConditions: Int = getColumnIndexOrThrow(_stmt,
            "environmentalConditions")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<MatingLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MatingLogEntity
          val _tmpLogId: String
          _tmpLogId = _stmt.getText(_columnIndexOfLogId)
          val _tmpPairId: String
          _tmpPairId = _stmt.getText(_columnIndexOfPairId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpMatedAt: Long
          _tmpMatedAt = _stmt.getLong(_columnIndexOfMatedAt)
          val _tmpObservedBehavior: String?
          if (_stmt.isNull(_columnIndexOfObservedBehavior)) {
            _tmpObservedBehavior = null
          } else {
            _tmpObservedBehavior = _stmt.getText(_columnIndexOfObservedBehavior)
          }
          val _tmpEnvironmentalConditions: String?
          if (_stmt.isNull(_columnIndexOfEnvironmentalConditions)) {
            _tmpEnvironmentalConditions = null
          } else {
            _tmpEnvironmentalConditions = _stmt.getText(_columnIndexOfEnvironmentalConditions)
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
              MatingLogEntity(_tmpLogId,_tmpPairId,_tmpFarmerId,_tmpMatedAt,_tmpObservedBehavior,_tmpEnvironmentalConditions,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getRecentByFarmer(farmerId: String, limit: Int):
      List<MatingLogEntity> {
    val _sql: String = "SELECT * FROM mating_logs WHERE farmerId = ? ORDER BY matedAt DESC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfLogId: Int = getColumnIndexOrThrow(_stmt, "logId")
        val _columnIndexOfPairId: Int = getColumnIndexOrThrow(_stmt, "pairId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfMatedAt: Int = getColumnIndexOrThrow(_stmt, "matedAt")
        val _columnIndexOfObservedBehavior: Int = getColumnIndexOrThrow(_stmt, "observedBehavior")
        val _columnIndexOfEnvironmentalConditions: Int = getColumnIndexOrThrow(_stmt,
            "environmentalConditions")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<MatingLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MatingLogEntity
          val _tmpLogId: String
          _tmpLogId = _stmt.getText(_columnIndexOfLogId)
          val _tmpPairId: String
          _tmpPairId = _stmt.getText(_columnIndexOfPairId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpMatedAt: Long
          _tmpMatedAt = _stmt.getLong(_columnIndexOfMatedAt)
          val _tmpObservedBehavior: String?
          if (_stmt.isNull(_columnIndexOfObservedBehavior)) {
            _tmpObservedBehavior = null
          } else {
            _tmpObservedBehavior = _stmt.getText(_columnIndexOfObservedBehavior)
          }
          val _tmpEnvironmentalConditions: String?
          if (_stmt.isNull(_columnIndexOfEnvironmentalConditions)) {
            _tmpEnvironmentalConditions = null
          } else {
            _tmpEnvironmentalConditions = _stmt.getText(_columnIndexOfEnvironmentalConditions)
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
              MatingLogEntity(_tmpLogId,_tmpPairId,_tmpFarmerId,_tmpMatedAt,_tmpObservedBehavior,_tmpEnvironmentalConditions,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countByPairBetween(
    pairId: String,
    start: Long,
    end: Long,
  ): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM mating_logs WHERE pairId = ? AND matedAt BETWEEN ? AND ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, pairId)
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

  public override fun observeLastMatedByFarmer(farmerId: String):
      Flow<List<MatingLogDao.MatingLast>> {
    val _sql: String =
        "SELECT pairId AS pairId, MAX(matedAt) AS lastMated FROM mating_logs WHERE farmerId = ? GROUP BY pairId"
    return createFlow(__db, false, arrayOf("mating_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfPairId: Int = 0
        val _columnIndexOfLastMated: Int = 1
        val _result: MutableList<MatingLogDao.MatingLast> = mutableListOf()
        while (_stmt.step()) {
          val _item: MatingLogDao.MatingLast
          val _tmpPairId: String
          _tmpPairId = _stmt.getText(_columnIndexOfPairId)
          val _tmpLastMated: Long?
          if (_stmt.isNull(_columnIndexOfLastMated)) {
            _tmpLastMated = null
          } else {
            _tmpLastMated = _stmt.getLong(_columnIndexOfLastMated)
          }
          _item = MatingLogDao.MatingLast(_tmpPairId,_tmpLastMated)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDirty(): List<MatingLogEntity> {
    val _sql: String = "SELECT * FROM mating_logs WHERE dirty = 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfLogId: Int = getColumnIndexOrThrow(_stmt, "logId")
        val _columnIndexOfPairId: Int = getColumnIndexOrThrow(_stmt, "pairId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfMatedAt: Int = getColumnIndexOrThrow(_stmt, "matedAt")
        val _columnIndexOfObservedBehavior: Int = getColumnIndexOrThrow(_stmt, "observedBehavior")
        val _columnIndexOfEnvironmentalConditions: Int = getColumnIndexOrThrow(_stmt,
            "environmentalConditions")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<MatingLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MatingLogEntity
          val _tmpLogId: String
          _tmpLogId = _stmt.getText(_columnIndexOfLogId)
          val _tmpPairId: String
          _tmpPairId = _stmt.getText(_columnIndexOfPairId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpMatedAt: Long
          _tmpMatedAt = _stmt.getLong(_columnIndexOfMatedAt)
          val _tmpObservedBehavior: String?
          if (_stmt.isNull(_columnIndexOfObservedBehavior)) {
            _tmpObservedBehavior = null
          } else {
            _tmpObservedBehavior = _stmt.getText(_columnIndexOfObservedBehavior)
          }
          val _tmpEnvironmentalConditions: String?
          if (_stmt.isNull(_columnIndexOfEnvironmentalConditions)) {
            _tmpEnvironmentalConditions = null
          } else {
            _tmpEnvironmentalConditions = _stmt.getText(_columnIndexOfEnvironmentalConditions)
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
              MatingLogEntity(_tmpLogId,_tmpPairId,_tmpFarmerId,_tmpMatedAt,_tmpObservedBehavior,_tmpEnvironmentalConditions,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
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
    _stringBuilder.append("UPDATE mating_logs SET dirty = 0, syncedAt = ")
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
