package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.QuarantineRecordEntity
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
public class QuarantineRecordDao_Impl(
  __db: RoomDatabase,
) : QuarantineRecordDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfQuarantineRecordEntity: EntityInsertAdapter<QuarantineRecordEntity>

  private val __updateAdapterOfQuarantineRecordEntity:
      EntityDeleteOrUpdateAdapter<QuarantineRecordEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfQuarantineRecordEntity = object :
        EntityInsertAdapter<QuarantineRecordEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `quarantine_records` (`quarantineId`,`productId`,`farmerId`,`reason`,`protocol`,`medicationScheduleJson`,`statusHistoryJson`,`vetNotes`,`startedAt`,`lastUpdatedAt`,`updatesCount`,`endedAt`,`status`,`healthScore`,`updatedAt`,`dirty`,`syncedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: QuarantineRecordEntity) {
        statement.bindText(1, entity.quarantineId)
        statement.bindText(2, entity.productId)
        statement.bindText(3, entity.farmerId)
        statement.bindText(4, entity.reason)
        val _tmpProtocol: String? = entity.protocol
        if (_tmpProtocol == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpProtocol)
        }
        val _tmpMedicationScheduleJson: String? = entity.medicationScheduleJson
        if (_tmpMedicationScheduleJson == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpMedicationScheduleJson)
        }
        val _tmpStatusHistoryJson: String? = entity.statusHistoryJson
        if (_tmpStatusHistoryJson == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpStatusHistoryJson)
        }
        val _tmpVetNotes: String? = entity.vetNotes
        if (_tmpVetNotes == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpVetNotes)
        }
        statement.bindLong(9, entity.startedAt)
        statement.bindLong(10, entity.lastUpdatedAt)
        statement.bindLong(11, entity.updatesCount.toLong())
        val _tmpEndedAt: Long? = entity.endedAt
        if (_tmpEndedAt == null) {
          statement.bindNull(12)
        } else {
          statement.bindLong(12, _tmpEndedAt)
        }
        statement.bindText(13, entity.status)
        statement.bindLong(14, entity.healthScore.toLong())
        statement.bindLong(15, entity.updatedAt)
        val _tmp: Int = if (entity.dirty) 1 else 0
        statement.bindLong(16, _tmp.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(17)
        } else {
          statement.bindLong(17, _tmpSyncedAt)
        }
      }
    }
    this.__updateAdapterOfQuarantineRecordEntity = object :
        EntityDeleteOrUpdateAdapter<QuarantineRecordEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `quarantine_records` SET `quarantineId` = ?,`productId` = ?,`farmerId` = ?,`reason` = ?,`protocol` = ?,`medicationScheduleJson` = ?,`statusHistoryJson` = ?,`vetNotes` = ?,`startedAt` = ?,`lastUpdatedAt` = ?,`updatesCount` = ?,`endedAt` = ?,`status` = ?,`healthScore` = ?,`updatedAt` = ?,`dirty` = ?,`syncedAt` = ? WHERE `quarantineId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: QuarantineRecordEntity) {
        statement.bindText(1, entity.quarantineId)
        statement.bindText(2, entity.productId)
        statement.bindText(3, entity.farmerId)
        statement.bindText(4, entity.reason)
        val _tmpProtocol: String? = entity.protocol
        if (_tmpProtocol == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpProtocol)
        }
        val _tmpMedicationScheduleJson: String? = entity.medicationScheduleJson
        if (_tmpMedicationScheduleJson == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpMedicationScheduleJson)
        }
        val _tmpStatusHistoryJson: String? = entity.statusHistoryJson
        if (_tmpStatusHistoryJson == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpStatusHistoryJson)
        }
        val _tmpVetNotes: String? = entity.vetNotes
        if (_tmpVetNotes == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpVetNotes)
        }
        statement.bindLong(9, entity.startedAt)
        statement.bindLong(10, entity.lastUpdatedAt)
        statement.bindLong(11, entity.updatesCount.toLong())
        val _tmpEndedAt: Long? = entity.endedAt
        if (_tmpEndedAt == null) {
          statement.bindNull(12)
        } else {
          statement.bindLong(12, _tmpEndedAt)
        }
        statement.bindText(13, entity.status)
        statement.bindLong(14, entity.healthScore.toLong())
        statement.bindLong(15, entity.updatedAt)
        val _tmp: Int = if (entity.dirty) 1 else 0
        statement.bindLong(16, _tmp.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(17)
        } else {
          statement.bindLong(17, _tmpSyncedAt)
        }
        statement.bindText(18, entity.quarantineId)
      }
    }
  }

  public override suspend fun insert(record: QuarantineRecordEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfQuarantineRecordEntity.insert(_connection, record)
  }

  public override suspend fun upsert(record: QuarantineRecordEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfQuarantineRecordEntity.insert(_connection, record)
  }

  public override suspend fun update(record: QuarantineRecordEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __updateAdapterOfQuarantineRecordEntity.handle(_connection, record)
  }

  public override fun observeForProduct(productId: String): Flow<List<QuarantineRecordEntity>> {
    val _sql: String =
        "SELECT * FROM quarantine_records WHERE productId = ? ORDER BY startedAt DESC"
    return createFlow(__db, false, arrayOf("quarantine_records")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfQuarantineId: Int = getColumnIndexOrThrow(_stmt, "quarantineId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfReason: Int = getColumnIndexOrThrow(_stmt, "reason")
        val _columnIndexOfProtocol: Int = getColumnIndexOrThrow(_stmt, "protocol")
        val _columnIndexOfMedicationScheduleJson: Int = getColumnIndexOrThrow(_stmt,
            "medicationScheduleJson")
        val _columnIndexOfStatusHistoryJson: Int = getColumnIndexOrThrow(_stmt, "statusHistoryJson")
        val _columnIndexOfVetNotes: Int = getColumnIndexOrThrow(_stmt, "vetNotes")
        val _columnIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _columnIndexOfLastUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "lastUpdatedAt")
        val _columnIndexOfUpdatesCount: Int = getColumnIndexOrThrow(_stmt, "updatesCount")
        val _columnIndexOfEndedAt: Int = getColumnIndexOrThrow(_stmt, "endedAt")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfHealthScore: Int = getColumnIndexOrThrow(_stmt, "healthScore")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<QuarantineRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: QuarantineRecordEntity
          val _tmpQuarantineId: String
          _tmpQuarantineId = _stmt.getText(_columnIndexOfQuarantineId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpReason: String
          _tmpReason = _stmt.getText(_columnIndexOfReason)
          val _tmpProtocol: String?
          if (_stmt.isNull(_columnIndexOfProtocol)) {
            _tmpProtocol = null
          } else {
            _tmpProtocol = _stmt.getText(_columnIndexOfProtocol)
          }
          val _tmpMedicationScheduleJson: String?
          if (_stmt.isNull(_columnIndexOfMedicationScheduleJson)) {
            _tmpMedicationScheduleJson = null
          } else {
            _tmpMedicationScheduleJson = _stmt.getText(_columnIndexOfMedicationScheduleJson)
          }
          val _tmpStatusHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfStatusHistoryJson)) {
            _tmpStatusHistoryJson = null
          } else {
            _tmpStatusHistoryJson = _stmt.getText(_columnIndexOfStatusHistoryJson)
          }
          val _tmpVetNotes: String?
          if (_stmt.isNull(_columnIndexOfVetNotes)) {
            _tmpVetNotes = null
          } else {
            _tmpVetNotes = _stmt.getText(_columnIndexOfVetNotes)
          }
          val _tmpStartedAt: Long
          _tmpStartedAt = _stmt.getLong(_columnIndexOfStartedAt)
          val _tmpLastUpdatedAt: Long
          _tmpLastUpdatedAt = _stmt.getLong(_columnIndexOfLastUpdatedAt)
          val _tmpUpdatesCount: Int
          _tmpUpdatesCount = _stmt.getLong(_columnIndexOfUpdatesCount).toInt()
          val _tmpEndedAt: Long?
          if (_stmt.isNull(_columnIndexOfEndedAt)) {
            _tmpEndedAt = null
          } else {
            _tmpEndedAt = _stmt.getLong(_columnIndexOfEndedAt)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpHealthScore: Int
          _tmpHealthScore = _stmt.getLong(_columnIndexOfHealthScore).toInt()
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
              QuarantineRecordEntity(_tmpQuarantineId,_tmpProductId,_tmpFarmerId,_tmpReason,_tmpProtocol,_tmpMedicationScheduleJson,_tmpStatusHistoryJson,_tmpVetNotes,_tmpStartedAt,_tmpLastUpdatedAt,_tmpUpdatesCount,_tmpEndedAt,_tmpStatus,_tmpHealthScore,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByStatus(status: String): Flow<List<QuarantineRecordEntity>> {
    val _sql: String = "SELECT * FROM quarantine_records WHERE status = ? ORDER BY startedAt DESC"
    return createFlow(__db, false, arrayOf("quarantine_records")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        val _columnIndexOfQuarantineId: Int = getColumnIndexOrThrow(_stmt, "quarantineId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfReason: Int = getColumnIndexOrThrow(_stmt, "reason")
        val _columnIndexOfProtocol: Int = getColumnIndexOrThrow(_stmt, "protocol")
        val _columnIndexOfMedicationScheduleJson: Int = getColumnIndexOrThrow(_stmt,
            "medicationScheduleJson")
        val _columnIndexOfStatusHistoryJson: Int = getColumnIndexOrThrow(_stmt, "statusHistoryJson")
        val _columnIndexOfVetNotes: Int = getColumnIndexOrThrow(_stmt, "vetNotes")
        val _columnIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _columnIndexOfLastUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "lastUpdatedAt")
        val _columnIndexOfUpdatesCount: Int = getColumnIndexOrThrow(_stmt, "updatesCount")
        val _columnIndexOfEndedAt: Int = getColumnIndexOrThrow(_stmt, "endedAt")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfHealthScore: Int = getColumnIndexOrThrow(_stmt, "healthScore")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<QuarantineRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: QuarantineRecordEntity
          val _tmpQuarantineId: String
          _tmpQuarantineId = _stmt.getText(_columnIndexOfQuarantineId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpReason: String
          _tmpReason = _stmt.getText(_columnIndexOfReason)
          val _tmpProtocol: String?
          if (_stmt.isNull(_columnIndexOfProtocol)) {
            _tmpProtocol = null
          } else {
            _tmpProtocol = _stmt.getText(_columnIndexOfProtocol)
          }
          val _tmpMedicationScheduleJson: String?
          if (_stmt.isNull(_columnIndexOfMedicationScheduleJson)) {
            _tmpMedicationScheduleJson = null
          } else {
            _tmpMedicationScheduleJson = _stmt.getText(_columnIndexOfMedicationScheduleJson)
          }
          val _tmpStatusHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfStatusHistoryJson)) {
            _tmpStatusHistoryJson = null
          } else {
            _tmpStatusHistoryJson = _stmt.getText(_columnIndexOfStatusHistoryJson)
          }
          val _tmpVetNotes: String?
          if (_stmt.isNull(_columnIndexOfVetNotes)) {
            _tmpVetNotes = null
          } else {
            _tmpVetNotes = _stmt.getText(_columnIndexOfVetNotes)
          }
          val _tmpStartedAt: Long
          _tmpStartedAt = _stmt.getLong(_columnIndexOfStartedAt)
          val _tmpLastUpdatedAt: Long
          _tmpLastUpdatedAt = _stmt.getLong(_columnIndexOfLastUpdatedAt)
          val _tmpUpdatesCount: Int
          _tmpUpdatesCount = _stmt.getLong(_columnIndexOfUpdatesCount).toInt()
          val _tmpEndedAt: Long?
          if (_stmt.isNull(_columnIndexOfEndedAt)) {
            _tmpEndedAt = null
          } else {
            _tmpEndedAt = _stmt.getLong(_columnIndexOfEndedAt)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpHealthScore: Int
          _tmpHealthScore = _stmt.getLong(_columnIndexOfHealthScore).toInt()
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
              QuarantineRecordEntity(_tmpQuarantineId,_tmpProductId,_tmpFarmerId,_tmpReason,_tmpProtocol,_tmpMedicationScheduleJson,_tmpStatusHistoryJson,_tmpVetNotes,_tmpStartedAt,_tmpLastUpdatedAt,_tmpUpdatesCount,_tmpEndedAt,_tmpStatus,_tmpHealthScore,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getRecordsForFarmer(farmerId: String): Flow<List<QuarantineRecordEntity>> {
    val _sql: String = "SELECT * FROM quarantine_records WHERE farmerId = ?"
    return createFlow(__db, false, arrayOf("quarantine_records")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfQuarantineId: Int = getColumnIndexOrThrow(_stmt, "quarantineId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfReason: Int = getColumnIndexOrThrow(_stmt, "reason")
        val _columnIndexOfProtocol: Int = getColumnIndexOrThrow(_stmt, "protocol")
        val _columnIndexOfMedicationScheduleJson: Int = getColumnIndexOrThrow(_stmt,
            "medicationScheduleJson")
        val _columnIndexOfStatusHistoryJson: Int = getColumnIndexOrThrow(_stmt, "statusHistoryJson")
        val _columnIndexOfVetNotes: Int = getColumnIndexOrThrow(_stmt, "vetNotes")
        val _columnIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _columnIndexOfLastUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "lastUpdatedAt")
        val _columnIndexOfUpdatesCount: Int = getColumnIndexOrThrow(_stmt, "updatesCount")
        val _columnIndexOfEndedAt: Int = getColumnIndexOrThrow(_stmt, "endedAt")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfHealthScore: Int = getColumnIndexOrThrow(_stmt, "healthScore")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<QuarantineRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: QuarantineRecordEntity
          val _tmpQuarantineId: String
          _tmpQuarantineId = _stmt.getText(_columnIndexOfQuarantineId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpReason: String
          _tmpReason = _stmt.getText(_columnIndexOfReason)
          val _tmpProtocol: String?
          if (_stmt.isNull(_columnIndexOfProtocol)) {
            _tmpProtocol = null
          } else {
            _tmpProtocol = _stmt.getText(_columnIndexOfProtocol)
          }
          val _tmpMedicationScheduleJson: String?
          if (_stmt.isNull(_columnIndexOfMedicationScheduleJson)) {
            _tmpMedicationScheduleJson = null
          } else {
            _tmpMedicationScheduleJson = _stmt.getText(_columnIndexOfMedicationScheduleJson)
          }
          val _tmpStatusHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfStatusHistoryJson)) {
            _tmpStatusHistoryJson = null
          } else {
            _tmpStatusHistoryJson = _stmt.getText(_columnIndexOfStatusHistoryJson)
          }
          val _tmpVetNotes: String?
          if (_stmt.isNull(_columnIndexOfVetNotes)) {
            _tmpVetNotes = null
          } else {
            _tmpVetNotes = _stmt.getText(_columnIndexOfVetNotes)
          }
          val _tmpStartedAt: Long
          _tmpStartedAt = _stmt.getLong(_columnIndexOfStartedAt)
          val _tmpLastUpdatedAt: Long
          _tmpLastUpdatedAt = _stmt.getLong(_columnIndexOfLastUpdatedAt)
          val _tmpUpdatesCount: Int
          _tmpUpdatesCount = _stmt.getLong(_columnIndexOfUpdatesCount).toInt()
          val _tmpEndedAt: Long?
          if (_stmt.isNull(_columnIndexOfEndedAt)) {
            _tmpEndedAt = null
          } else {
            _tmpEndedAt = _stmt.getLong(_columnIndexOfEndedAt)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpHealthScore: Int
          _tmpHealthScore = _stmt.getLong(_columnIndexOfHealthScore).toInt()
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
              QuarantineRecordEntity(_tmpQuarantineId,_tmpProductId,_tmpFarmerId,_tmpReason,_tmpProtocol,_tmpMedicationScheduleJson,_tmpStatusHistoryJson,_tmpVetNotes,_tmpStartedAt,_tmpLastUpdatedAt,_tmpUpdatesCount,_tmpEndedAt,_tmpStatus,_tmpHealthScore,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeActiveForFarmer(farmerId: String): Flow<List<QuarantineRecordEntity>> {
    val _sql: String = "SELECT * FROM quarantine_records WHERE farmerId = ? AND status = 'ACTIVE'"
    return createFlow(__db, false, arrayOf("quarantine_records")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfQuarantineId: Int = getColumnIndexOrThrow(_stmt, "quarantineId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfReason: Int = getColumnIndexOrThrow(_stmt, "reason")
        val _columnIndexOfProtocol: Int = getColumnIndexOrThrow(_stmt, "protocol")
        val _columnIndexOfMedicationScheduleJson: Int = getColumnIndexOrThrow(_stmt,
            "medicationScheduleJson")
        val _columnIndexOfStatusHistoryJson: Int = getColumnIndexOrThrow(_stmt, "statusHistoryJson")
        val _columnIndexOfVetNotes: Int = getColumnIndexOrThrow(_stmt, "vetNotes")
        val _columnIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _columnIndexOfLastUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "lastUpdatedAt")
        val _columnIndexOfUpdatesCount: Int = getColumnIndexOrThrow(_stmt, "updatesCount")
        val _columnIndexOfEndedAt: Int = getColumnIndexOrThrow(_stmt, "endedAt")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfHealthScore: Int = getColumnIndexOrThrow(_stmt, "healthScore")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<QuarantineRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: QuarantineRecordEntity
          val _tmpQuarantineId: String
          _tmpQuarantineId = _stmt.getText(_columnIndexOfQuarantineId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpReason: String
          _tmpReason = _stmt.getText(_columnIndexOfReason)
          val _tmpProtocol: String?
          if (_stmt.isNull(_columnIndexOfProtocol)) {
            _tmpProtocol = null
          } else {
            _tmpProtocol = _stmt.getText(_columnIndexOfProtocol)
          }
          val _tmpMedicationScheduleJson: String?
          if (_stmt.isNull(_columnIndexOfMedicationScheduleJson)) {
            _tmpMedicationScheduleJson = null
          } else {
            _tmpMedicationScheduleJson = _stmt.getText(_columnIndexOfMedicationScheduleJson)
          }
          val _tmpStatusHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfStatusHistoryJson)) {
            _tmpStatusHistoryJson = null
          } else {
            _tmpStatusHistoryJson = _stmt.getText(_columnIndexOfStatusHistoryJson)
          }
          val _tmpVetNotes: String?
          if (_stmt.isNull(_columnIndexOfVetNotes)) {
            _tmpVetNotes = null
          } else {
            _tmpVetNotes = _stmt.getText(_columnIndexOfVetNotes)
          }
          val _tmpStartedAt: Long
          _tmpStartedAt = _stmt.getLong(_columnIndexOfStartedAt)
          val _tmpLastUpdatedAt: Long
          _tmpLastUpdatedAt = _stmt.getLong(_columnIndexOfLastUpdatedAt)
          val _tmpUpdatesCount: Int
          _tmpUpdatesCount = _stmt.getLong(_columnIndexOfUpdatesCount).toInt()
          val _tmpEndedAt: Long?
          if (_stmt.isNull(_columnIndexOfEndedAt)) {
            _tmpEndedAt = null
          } else {
            _tmpEndedAt = _stmt.getLong(_columnIndexOfEndedAt)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpHealthScore: Int
          _tmpHealthScore = _stmt.getLong(_columnIndexOfHealthScore).toInt()
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
              QuarantineRecordEntity(_tmpQuarantineId,_tmpProductId,_tmpFarmerId,_tmpReason,_tmpProtocol,_tmpMedicationScheduleJson,_tmpStatusHistoryJson,_tmpVetNotes,_tmpStartedAt,_tmpLastUpdatedAt,_tmpUpdatesCount,_tmpEndedAt,_tmpStatus,_tmpHealthScore,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllActiveForFarmer(farmerId: String):
      List<QuarantineRecordEntity> {
    val _sql: String = "SELECT * FROM quarantine_records WHERE farmerId = ? AND status = 'ACTIVE'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfQuarantineId: Int = getColumnIndexOrThrow(_stmt, "quarantineId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfReason: Int = getColumnIndexOrThrow(_stmt, "reason")
        val _columnIndexOfProtocol: Int = getColumnIndexOrThrow(_stmt, "protocol")
        val _columnIndexOfMedicationScheduleJson: Int = getColumnIndexOrThrow(_stmt,
            "medicationScheduleJson")
        val _columnIndexOfStatusHistoryJson: Int = getColumnIndexOrThrow(_stmt, "statusHistoryJson")
        val _columnIndexOfVetNotes: Int = getColumnIndexOrThrow(_stmt, "vetNotes")
        val _columnIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _columnIndexOfLastUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "lastUpdatedAt")
        val _columnIndexOfUpdatesCount: Int = getColumnIndexOrThrow(_stmt, "updatesCount")
        val _columnIndexOfEndedAt: Int = getColumnIndexOrThrow(_stmt, "endedAt")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfHealthScore: Int = getColumnIndexOrThrow(_stmt, "healthScore")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<QuarantineRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: QuarantineRecordEntity
          val _tmpQuarantineId: String
          _tmpQuarantineId = _stmt.getText(_columnIndexOfQuarantineId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpReason: String
          _tmpReason = _stmt.getText(_columnIndexOfReason)
          val _tmpProtocol: String?
          if (_stmt.isNull(_columnIndexOfProtocol)) {
            _tmpProtocol = null
          } else {
            _tmpProtocol = _stmt.getText(_columnIndexOfProtocol)
          }
          val _tmpMedicationScheduleJson: String?
          if (_stmt.isNull(_columnIndexOfMedicationScheduleJson)) {
            _tmpMedicationScheduleJson = null
          } else {
            _tmpMedicationScheduleJson = _stmt.getText(_columnIndexOfMedicationScheduleJson)
          }
          val _tmpStatusHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfStatusHistoryJson)) {
            _tmpStatusHistoryJson = null
          } else {
            _tmpStatusHistoryJson = _stmt.getText(_columnIndexOfStatusHistoryJson)
          }
          val _tmpVetNotes: String?
          if (_stmt.isNull(_columnIndexOfVetNotes)) {
            _tmpVetNotes = null
          } else {
            _tmpVetNotes = _stmt.getText(_columnIndexOfVetNotes)
          }
          val _tmpStartedAt: Long
          _tmpStartedAt = _stmt.getLong(_columnIndexOfStartedAt)
          val _tmpLastUpdatedAt: Long
          _tmpLastUpdatedAt = _stmt.getLong(_columnIndexOfLastUpdatedAt)
          val _tmpUpdatesCount: Int
          _tmpUpdatesCount = _stmt.getLong(_columnIndexOfUpdatesCount).toInt()
          val _tmpEndedAt: Long?
          if (_stmt.isNull(_columnIndexOfEndedAt)) {
            _tmpEndedAt = null
          } else {
            _tmpEndedAt = _stmt.getLong(_columnIndexOfEndedAt)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpHealthScore: Int
          _tmpHealthScore = _stmt.getLong(_columnIndexOfHealthScore).toInt()
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
              QuarantineRecordEntity(_tmpQuarantineId,_tmpProductId,_tmpFarmerId,_tmpReason,_tmpProtocol,_tmpMedicationScheduleJson,_tmpStatusHistoryJson,_tmpVetNotes,_tmpStartedAt,_tmpLastUpdatedAt,_tmpUpdatesCount,_tmpEndedAt,_tmpStatus,_tmpHealthScore,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countActiveForFarmer(farmerId: String): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM quarantine_records WHERE farmerId = ? AND status = 'ACTIVE'"
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

  public override suspend fun getUpdatesOverdueForFarmer(farmerId: String, threshold: Long):
      List<QuarantineRecordEntity> {
    val _sql: String = "SELECT * FROM quarantine_records WHERE farmerId = ? AND updatedAt < ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, threshold)
        val _columnIndexOfQuarantineId: Int = getColumnIndexOrThrow(_stmt, "quarantineId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfReason: Int = getColumnIndexOrThrow(_stmt, "reason")
        val _columnIndexOfProtocol: Int = getColumnIndexOrThrow(_stmt, "protocol")
        val _columnIndexOfMedicationScheduleJson: Int = getColumnIndexOrThrow(_stmt,
            "medicationScheduleJson")
        val _columnIndexOfStatusHistoryJson: Int = getColumnIndexOrThrow(_stmt, "statusHistoryJson")
        val _columnIndexOfVetNotes: Int = getColumnIndexOrThrow(_stmt, "vetNotes")
        val _columnIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _columnIndexOfLastUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "lastUpdatedAt")
        val _columnIndexOfUpdatesCount: Int = getColumnIndexOrThrow(_stmt, "updatesCount")
        val _columnIndexOfEndedAt: Int = getColumnIndexOrThrow(_stmt, "endedAt")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfHealthScore: Int = getColumnIndexOrThrow(_stmt, "healthScore")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<QuarantineRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: QuarantineRecordEntity
          val _tmpQuarantineId: String
          _tmpQuarantineId = _stmt.getText(_columnIndexOfQuarantineId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpReason: String
          _tmpReason = _stmt.getText(_columnIndexOfReason)
          val _tmpProtocol: String?
          if (_stmt.isNull(_columnIndexOfProtocol)) {
            _tmpProtocol = null
          } else {
            _tmpProtocol = _stmt.getText(_columnIndexOfProtocol)
          }
          val _tmpMedicationScheduleJson: String?
          if (_stmt.isNull(_columnIndexOfMedicationScheduleJson)) {
            _tmpMedicationScheduleJson = null
          } else {
            _tmpMedicationScheduleJson = _stmt.getText(_columnIndexOfMedicationScheduleJson)
          }
          val _tmpStatusHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfStatusHistoryJson)) {
            _tmpStatusHistoryJson = null
          } else {
            _tmpStatusHistoryJson = _stmt.getText(_columnIndexOfStatusHistoryJson)
          }
          val _tmpVetNotes: String?
          if (_stmt.isNull(_columnIndexOfVetNotes)) {
            _tmpVetNotes = null
          } else {
            _tmpVetNotes = _stmt.getText(_columnIndexOfVetNotes)
          }
          val _tmpStartedAt: Long
          _tmpStartedAt = _stmt.getLong(_columnIndexOfStartedAt)
          val _tmpLastUpdatedAt: Long
          _tmpLastUpdatedAt = _stmt.getLong(_columnIndexOfLastUpdatedAt)
          val _tmpUpdatesCount: Int
          _tmpUpdatesCount = _stmt.getLong(_columnIndexOfUpdatesCount).toInt()
          val _tmpEndedAt: Long?
          if (_stmt.isNull(_columnIndexOfEndedAt)) {
            _tmpEndedAt = null
          } else {
            _tmpEndedAt = _stmt.getLong(_columnIndexOfEndedAt)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpHealthScore: Int
          _tmpHealthScore = _stmt.getLong(_columnIndexOfHealthScore).toInt()
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
              QuarantineRecordEntity(_tmpQuarantineId,_tmpProductId,_tmpFarmerId,_tmpReason,_tmpProtocol,_tmpMedicationScheduleJson,_tmpStatusHistoryJson,_tmpVetNotes,_tmpStartedAt,_tmpLastUpdatedAt,_tmpUpdatesCount,_tmpEndedAt,_tmpStatus,_tmpHealthScore,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeUpdatesOverdueForFarmer(farmerId: String, threshold: Long):
      Flow<List<QuarantineRecordEntity>> {
    val _sql: String = "SELECT * FROM quarantine_records WHERE farmerId = ? AND updatedAt < ?"
    return createFlow(__db, false, arrayOf("quarantine_records")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, threshold)
        val _columnIndexOfQuarantineId: Int = getColumnIndexOrThrow(_stmt, "quarantineId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfReason: Int = getColumnIndexOrThrow(_stmt, "reason")
        val _columnIndexOfProtocol: Int = getColumnIndexOrThrow(_stmt, "protocol")
        val _columnIndexOfMedicationScheduleJson: Int = getColumnIndexOrThrow(_stmt,
            "medicationScheduleJson")
        val _columnIndexOfStatusHistoryJson: Int = getColumnIndexOrThrow(_stmt, "statusHistoryJson")
        val _columnIndexOfVetNotes: Int = getColumnIndexOrThrow(_stmt, "vetNotes")
        val _columnIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _columnIndexOfLastUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "lastUpdatedAt")
        val _columnIndexOfUpdatesCount: Int = getColumnIndexOrThrow(_stmt, "updatesCount")
        val _columnIndexOfEndedAt: Int = getColumnIndexOrThrow(_stmt, "endedAt")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfHealthScore: Int = getColumnIndexOrThrow(_stmt, "healthScore")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<QuarantineRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: QuarantineRecordEntity
          val _tmpQuarantineId: String
          _tmpQuarantineId = _stmt.getText(_columnIndexOfQuarantineId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpReason: String
          _tmpReason = _stmt.getText(_columnIndexOfReason)
          val _tmpProtocol: String?
          if (_stmt.isNull(_columnIndexOfProtocol)) {
            _tmpProtocol = null
          } else {
            _tmpProtocol = _stmt.getText(_columnIndexOfProtocol)
          }
          val _tmpMedicationScheduleJson: String?
          if (_stmt.isNull(_columnIndexOfMedicationScheduleJson)) {
            _tmpMedicationScheduleJson = null
          } else {
            _tmpMedicationScheduleJson = _stmt.getText(_columnIndexOfMedicationScheduleJson)
          }
          val _tmpStatusHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfStatusHistoryJson)) {
            _tmpStatusHistoryJson = null
          } else {
            _tmpStatusHistoryJson = _stmt.getText(_columnIndexOfStatusHistoryJson)
          }
          val _tmpVetNotes: String?
          if (_stmt.isNull(_columnIndexOfVetNotes)) {
            _tmpVetNotes = null
          } else {
            _tmpVetNotes = _stmt.getText(_columnIndexOfVetNotes)
          }
          val _tmpStartedAt: Long
          _tmpStartedAt = _stmt.getLong(_columnIndexOfStartedAt)
          val _tmpLastUpdatedAt: Long
          _tmpLastUpdatedAt = _stmt.getLong(_columnIndexOfLastUpdatedAt)
          val _tmpUpdatesCount: Int
          _tmpUpdatesCount = _stmt.getLong(_columnIndexOfUpdatesCount).toInt()
          val _tmpEndedAt: Long?
          if (_stmt.isNull(_columnIndexOfEndedAt)) {
            _tmpEndedAt = null
          } else {
            _tmpEndedAt = _stmt.getLong(_columnIndexOfEndedAt)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpHealthScore: Int
          _tmpHealthScore = _stmt.getLong(_columnIndexOfHealthScore).toInt()
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
              QuarantineRecordEntity(_tmpQuarantineId,_tmpProductId,_tmpFarmerId,_tmpReason,_tmpProtocol,_tmpMedicationScheduleJson,_tmpStatusHistoryJson,_tmpVetNotes,_tmpStartedAt,_tmpLastUpdatedAt,_tmpUpdatesCount,_tmpEndedAt,_tmpStatus,_tmpHealthScore,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDirty(): List<QuarantineRecordEntity> {
    val _sql: String = "SELECT * FROM quarantine_records WHERE dirty = 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfQuarantineId: Int = getColumnIndexOrThrow(_stmt, "quarantineId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfReason: Int = getColumnIndexOrThrow(_stmt, "reason")
        val _columnIndexOfProtocol: Int = getColumnIndexOrThrow(_stmt, "protocol")
        val _columnIndexOfMedicationScheduleJson: Int = getColumnIndexOrThrow(_stmt,
            "medicationScheduleJson")
        val _columnIndexOfStatusHistoryJson: Int = getColumnIndexOrThrow(_stmt, "statusHistoryJson")
        val _columnIndexOfVetNotes: Int = getColumnIndexOrThrow(_stmt, "vetNotes")
        val _columnIndexOfStartedAt: Int = getColumnIndexOrThrow(_stmt, "startedAt")
        val _columnIndexOfLastUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "lastUpdatedAt")
        val _columnIndexOfUpdatesCount: Int = getColumnIndexOrThrow(_stmt, "updatesCount")
        val _columnIndexOfEndedAt: Int = getColumnIndexOrThrow(_stmt, "endedAt")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfHealthScore: Int = getColumnIndexOrThrow(_stmt, "healthScore")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<QuarantineRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: QuarantineRecordEntity
          val _tmpQuarantineId: String
          _tmpQuarantineId = _stmt.getText(_columnIndexOfQuarantineId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpReason: String
          _tmpReason = _stmt.getText(_columnIndexOfReason)
          val _tmpProtocol: String?
          if (_stmt.isNull(_columnIndexOfProtocol)) {
            _tmpProtocol = null
          } else {
            _tmpProtocol = _stmt.getText(_columnIndexOfProtocol)
          }
          val _tmpMedicationScheduleJson: String?
          if (_stmt.isNull(_columnIndexOfMedicationScheduleJson)) {
            _tmpMedicationScheduleJson = null
          } else {
            _tmpMedicationScheduleJson = _stmt.getText(_columnIndexOfMedicationScheduleJson)
          }
          val _tmpStatusHistoryJson: String?
          if (_stmt.isNull(_columnIndexOfStatusHistoryJson)) {
            _tmpStatusHistoryJson = null
          } else {
            _tmpStatusHistoryJson = _stmt.getText(_columnIndexOfStatusHistoryJson)
          }
          val _tmpVetNotes: String?
          if (_stmt.isNull(_columnIndexOfVetNotes)) {
            _tmpVetNotes = null
          } else {
            _tmpVetNotes = _stmt.getText(_columnIndexOfVetNotes)
          }
          val _tmpStartedAt: Long
          _tmpStartedAt = _stmt.getLong(_columnIndexOfStartedAt)
          val _tmpLastUpdatedAt: Long
          _tmpLastUpdatedAt = _stmt.getLong(_columnIndexOfLastUpdatedAt)
          val _tmpUpdatesCount: Int
          _tmpUpdatesCount = _stmt.getLong(_columnIndexOfUpdatesCount).toInt()
          val _tmpEndedAt: Long?
          if (_stmt.isNull(_columnIndexOfEndedAt)) {
            _tmpEndedAt = null
          } else {
            _tmpEndedAt = _stmt.getLong(_columnIndexOfEndedAt)
          }
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpHealthScore: Int
          _tmpHealthScore = _stmt.getLong(_columnIndexOfHealthScore).toInt()
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
              QuarantineRecordEntity(_tmpQuarantineId,_tmpProductId,_tmpFarmerId,_tmpReason,_tmpProtocol,_tmpMedicationScheduleJson,_tmpStatusHistoryJson,_tmpVetNotes,_tmpStartedAt,_tmpLastUpdatedAt,_tmpUpdatesCount,_tmpEndedAt,_tmpStatus,_tmpHealthScore,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearDirty(ids: List<String>, syncedAt: Long) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("UPDATE quarantine_records SET dirty = 0, syncedAt = ")
    _stringBuilder.append("?")
    _stringBuilder.append(" WHERE quarantineId IN (")
    val _inputSize: Int = ids.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, syncedAt)
        _argIndex = 2
        for (_item: String in ids) {
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
