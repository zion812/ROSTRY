package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.BreedingPairEntity
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
public class BreedingPairDao_Impl(
  __db: RoomDatabase,
) : BreedingPairDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfBreedingPairEntity: EntityInsertAdapter<BreedingPairEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfBreedingPairEntity = object : EntityInsertAdapter<BreedingPairEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `breeding_pairs` (`pairId`,`farmerId`,`maleProductId`,`femaleProductId`,`pairedAt`,`status`,`hatchSuccessRate`,`eggsCollected`,`hatchedEggs`,`separatedAt`,`notes`,`createdAt`,`updatedAt`,`dirty`,`syncedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: BreedingPairEntity) {
        statement.bindText(1, entity.pairId)
        statement.bindText(2, entity.farmerId)
        statement.bindText(3, entity.maleProductId)
        statement.bindText(4, entity.femaleProductId)
        statement.bindLong(5, entity.pairedAt)
        statement.bindText(6, entity.status)
        statement.bindDouble(7, entity.hatchSuccessRate)
        statement.bindLong(8, entity.eggsCollected.toLong())
        statement.bindLong(9, entity.hatchedEggs.toLong())
        val _tmpSeparatedAt: Long? = entity.separatedAt
        if (_tmpSeparatedAt == null) {
          statement.bindNull(10)
        } else {
          statement.bindLong(10, _tmpSeparatedAt)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpNotes)
        }
        statement.bindLong(12, entity.createdAt)
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

  public override suspend fun upsert(pair: BreedingPairEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfBreedingPairEntity.insert(_connection, pair)
  }

  public override suspend fun getByProducts(maleId: String, femaleId: String):
      List<BreedingPairEntity> {
    val _sql: String =
        "SELECT * FROM breeding_pairs WHERE (maleProductId = ? AND femaleProductId = ?) OR (maleProductId = ? AND femaleProductId = ?)"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, maleId)
        _argIndex = 2
        _stmt.bindText(_argIndex, femaleId)
        _argIndex = 3
        _stmt.bindText(_argIndex, femaleId)
        _argIndex = 4
        _stmt.bindText(_argIndex, maleId)
        val _columnIndexOfPairId: Int = getColumnIndexOrThrow(_stmt, "pairId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfMaleProductId: Int = getColumnIndexOrThrow(_stmt, "maleProductId")
        val _columnIndexOfFemaleProductId: Int = getColumnIndexOrThrow(_stmt, "femaleProductId")
        val _columnIndexOfPairedAt: Int = getColumnIndexOrThrow(_stmt, "pairedAt")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfHatchSuccessRate: Int = getColumnIndexOrThrow(_stmt, "hatchSuccessRate")
        val _columnIndexOfEggsCollected: Int = getColumnIndexOrThrow(_stmt, "eggsCollected")
        val _columnIndexOfHatchedEggs: Int = getColumnIndexOrThrow(_stmt, "hatchedEggs")
        val _columnIndexOfSeparatedAt: Int = getColumnIndexOrThrow(_stmt, "separatedAt")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<BreedingPairEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BreedingPairEntity
          val _tmpPairId: String
          _tmpPairId = _stmt.getText(_columnIndexOfPairId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpMaleProductId: String
          _tmpMaleProductId = _stmt.getText(_columnIndexOfMaleProductId)
          val _tmpFemaleProductId: String
          _tmpFemaleProductId = _stmt.getText(_columnIndexOfFemaleProductId)
          val _tmpPairedAt: Long
          _tmpPairedAt = _stmt.getLong(_columnIndexOfPairedAt)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpHatchSuccessRate: Double
          _tmpHatchSuccessRate = _stmt.getDouble(_columnIndexOfHatchSuccessRate)
          val _tmpEggsCollected: Int
          _tmpEggsCollected = _stmt.getLong(_columnIndexOfEggsCollected).toInt()
          val _tmpHatchedEggs: Int
          _tmpHatchedEggs = _stmt.getLong(_columnIndexOfHatchedEggs).toInt()
          val _tmpSeparatedAt: Long?
          if (_stmt.isNull(_columnIndexOfSeparatedAt)) {
            _tmpSeparatedAt = null
          } else {
            _tmpSeparatedAt = _stmt.getLong(_columnIndexOfSeparatedAt)
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
              BreedingPairEntity(_tmpPairId,_tmpFarmerId,_tmpMaleProductId,_tmpFemaleProductId,_tmpPairedAt,_tmpStatus,_tmpHatchSuccessRate,_tmpEggsCollected,_tmpHatchedEggs,_tmpSeparatedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getActivePairsForFarmer(farmerId: String): List<BreedingPairEntity> {
    val _sql: String = "SELECT * FROM breeding_pairs WHERE farmerId = ? AND status = 'ACTIVE'"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfPairId: Int = getColumnIndexOrThrow(_stmt, "pairId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfMaleProductId: Int = getColumnIndexOrThrow(_stmt, "maleProductId")
        val _columnIndexOfFemaleProductId: Int = getColumnIndexOrThrow(_stmt, "femaleProductId")
        val _columnIndexOfPairedAt: Int = getColumnIndexOrThrow(_stmt, "pairedAt")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfHatchSuccessRate: Int = getColumnIndexOrThrow(_stmt, "hatchSuccessRate")
        val _columnIndexOfEggsCollected: Int = getColumnIndexOrThrow(_stmt, "eggsCollected")
        val _columnIndexOfHatchedEggs: Int = getColumnIndexOrThrow(_stmt, "hatchedEggs")
        val _columnIndexOfSeparatedAt: Int = getColumnIndexOrThrow(_stmt, "separatedAt")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<BreedingPairEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BreedingPairEntity
          val _tmpPairId: String
          _tmpPairId = _stmt.getText(_columnIndexOfPairId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpMaleProductId: String
          _tmpMaleProductId = _stmt.getText(_columnIndexOfMaleProductId)
          val _tmpFemaleProductId: String
          _tmpFemaleProductId = _stmt.getText(_columnIndexOfFemaleProductId)
          val _tmpPairedAt: Long
          _tmpPairedAt = _stmt.getLong(_columnIndexOfPairedAt)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpHatchSuccessRate: Double
          _tmpHatchSuccessRate = _stmt.getDouble(_columnIndexOfHatchSuccessRate)
          val _tmpEggsCollected: Int
          _tmpEggsCollected = _stmt.getLong(_columnIndexOfEggsCollected).toInt()
          val _tmpHatchedEggs: Int
          _tmpHatchedEggs = _stmt.getLong(_columnIndexOfHatchedEggs).toInt()
          val _tmpSeparatedAt: Long?
          if (_stmt.isNull(_columnIndexOfSeparatedAt)) {
            _tmpSeparatedAt = null
          } else {
            _tmpSeparatedAt = _stmt.getLong(_columnIndexOfSeparatedAt)
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
              BreedingPairEntity(_tmpPairId,_tmpFarmerId,_tmpMaleProductId,_tmpFemaleProductId,_tmpPairedAt,_tmpStatus,_tmpHatchSuccessRate,_tmpEggsCollected,_tmpHatchedEggs,_tmpSeparatedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observePairsForFarmer(farmerId: String): Flow<List<BreedingPairEntity>> {
    val _sql: String = "SELECT * FROM breeding_pairs WHERE farmerId = ?"
    return createFlow(__db, false, arrayOf("breeding_pairs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfPairId: Int = getColumnIndexOrThrow(_stmt, "pairId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfMaleProductId: Int = getColumnIndexOrThrow(_stmt, "maleProductId")
        val _columnIndexOfFemaleProductId: Int = getColumnIndexOrThrow(_stmt, "femaleProductId")
        val _columnIndexOfPairedAt: Int = getColumnIndexOrThrow(_stmt, "pairedAt")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfHatchSuccessRate: Int = getColumnIndexOrThrow(_stmt, "hatchSuccessRate")
        val _columnIndexOfEggsCollected: Int = getColumnIndexOrThrow(_stmt, "eggsCollected")
        val _columnIndexOfHatchedEggs: Int = getColumnIndexOrThrow(_stmt, "hatchedEggs")
        val _columnIndexOfSeparatedAt: Int = getColumnIndexOrThrow(_stmt, "separatedAt")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<BreedingPairEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BreedingPairEntity
          val _tmpPairId: String
          _tmpPairId = _stmt.getText(_columnIndexOfPairId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpMaleProductId: String
          _tmpMaleProductId = _stmt.getText(_columnIndexOfMaleProductId)
          val _tmpFemaleProductId: String
          _tmpFemaleProductId = _stmt.getText(_columnIndexOfFemaleProductId)
          val _tmpPairedAt: Long
          _tmpPairedAt = _stmt.getLong(_columnIndexOfPairedAt)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpHatchSuccessRate: Double
          _tmpHatchSuccessRate = _stmt.getDouble(_columnIndexOfHatchSuccessRate)
          val _tmpEggsCollected: Int
          _tmpEggsCollected = _stmt.getLong(_columnIndexOfEggsCollected).toInt()
          val _tmpHatchedEggs: Int
          _tmpHatchedEggs = _stmt.getLong(_columnIndexOfHatchedEggs).toInt()
          val _tmpSeparatedAt: Long?
          if (_stmt.isNull(_columnIndexOfSeparatedAt)) {
            _tmpSeparatedAt = null
          } else {
            _tmpSeparatedAt = _stmt.getLong(_columnIndexOfSeparatedAt)
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
              BreedingPairEntity(_tmpPairId,_tmpFarmerId,_tmpMaleProductId,_tmpFemaleProductId,_tmpPairedAt,_tmpStatus,_tmpHatchSuccessRate,_tmpEggsCollected,_tmpHatchedEggs,_tmpSeparatedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(pairId: String): BreedingPairEntity? {
    val _sql: String = "SELECT * FROM breeding_pairs WHERE pairId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, pairId)
        val _columnIndexOfPairId: Int = getColumnIndexOrThrow(_stmt, "pairId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfMaleProductId: Int = getColumnIndexOrThrow(_stmt, "maleProductId")
        val _columnIndexOfFemaleProductId: Int = getColumnIndexOrThrow(_stmt, "femaleProductId")
        val _columnIndexOfPairedAt: Int = getColumnIndexOrThrow(_stmt, "pairedAt")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfHatchSuccessRate: Int = getColumnIndexOrThrow(_stmt, "hatchSuccessRate")
        val _columnIndexOfEggsCollected: Int = getColumnIndexOrThrow(_stmt, "eggsCollected")
        val _columnIndexOfHatchedEggs: Int = getColumnIndexOrThrow(_stmt, "hatchedEggs")
        val _columnIndexOfSeparatedAt: Int = getColumnIndexOrThrow(_stmt, "separatedAt")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: BreedingPairEntity?
        if (_stmt.step()) {
          val _tmpPairId: String
          _tmpPairId = _stmt.getText(_columnIndexOfPairId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpMaleProductId: String
          _tmpMaleProductId = _stmt.getText(_columnIndexOfMaleProductId)
          val _tmpFemaleProductId: String
          _tmpFemaleProductId = _stmt.getText(_columnIndexOfFemaleProductId)
          val _tmpPairedAt: Long
          _tmpPairedAt = _stmt.getLong(_columnIndexOfPairedAt)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpHatchSuccessRate: Double
          _tmpHatchSuccessRate = _stmt.getDouble(_columnIndexOfHatchSuccessRate)
          val _tmpEggsCollected: Int
          _tmpEggsCollected = _stmt.getLong(_columnIndexOfEggsCollected).toInt()
          val _tmpHatchedEggs: Int
          _tmpHatchedEggs = _stmt.getLong(_columnIndexOfHatchedEggs).toInt()
          val _tmpSeparatedAt: Long?
          if (_stmt.isNull(_columnIndexOfSeparatedAt)) {
            _tmpSeparatedAt = null
          } else {
            _tmpSeparatedAt = _stmt.getLong(_columnIndexOfSeparatedAt)
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
          _result =
              BreedingPairEntity(_tmpPairId,_tmpFarmerId,_tmpMaleProductId,_tmpFemaleProductId,_tmpPairedAt,_tmpStatus,_tmpHatchSuccessRate,_tmpEggsCollected,_tmpHatchedEggs,_tmpSeparatedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeActive(farmerId: String): Flow<List<BreedingPairEntity>> {
    val _sql: String =
        "SELECT * FROM breeding_pairs WHERE farmerId = ? AND status = 'ACTIVE' ORDER BY pairedAt DESC"
    return createFlow(__db, false, arrayOf("breeding_pairs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfPairId: Int = getColumnIndexOrThrow(_stmt, "pairId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfMaleProductId: Int = getColumnIndexOrThrow(_stmt, "maleProductId")
        val _columnIndexOfFemaleProductId: Int = getColumnIndexOrThrow(_stmt, "femaleProductId")
        val _columnIndexOfPairedAt: Int = getColumnIndexOrThrow(_stmt, "pairedAt")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfHatchSuccessRate: Int = getColumnIndexOrThrow(_stmt, "hatchSuccessRate")
        val _columnIndexOfEggsCollected: Int = getColumnIndexOrThrow(_stmt, "eggsCollected")
        val _columnIndexOfHatchedEggs: Int = getColumnIndexOrThrow(_stmt, "hatchedEggs")
        val _columnIndexOfSeparatedAt: Int = getColumnIndexOrThrow(_stmt, "separatedAt")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<BreedingPairEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BreedingPairEntity
          val _tmpPairId: String
          _tmpPairId = _stmt.getText(_columnIndexOfPairId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpMaleProductId: String
          _tmpMaleProductId = _stmt.getText(_columnIndexOfMaleProductId)
          val _tmpFemaleProductId: String
          _tmpFemaleProductId = _stmt.getText(_columnIndexOfFemaleProductId)
          val _tmpPairedAt: Long
          _tmpPairedAt = _stmt.getLong(_columnIndexOfPairedAt)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpHatchSuccessRate: Double
          _tmpHatchSuccessRate = _stmt.getDouble(_columnIndexOfHatchSuccessRate)
          val _tmpEggsCollected: Int
          _tmpEggsCollected = _stmt.getLong(_columnIndexOfEggsCollected).toInt()
          val _tmpHatchedEggs: Int
          _tmpHatchedEggs = _stmt.getLong(_columnIndexOfHatchedEggs).toInt()
          val _tmpSeparatedAt: Long?
          if (_stmt.isNull(_columnIndexOfSeparatedAt)) {
            _tmpSeparatedAt = null
          } else {
            _tmpSeparatedAt = _stmt.getLong(_columnIndexOfSeparatedAt)
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
              BreedingPairEntity(_tmpPairId,_tmpFarmerId,_tmpMaleProductId,_tmpFemaleProductId,_tmpPairedAt,_tmpStatus,_tmpHatchSuccessRate,_tmpEggsCollected,_tmpHatchedEggs,_tmpSeparatedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countActive(farmerId: String): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM breeding_pairs WHERE farmerId = ? AND status = 'ACTIVE'"
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

  public override suspend fun countActiveByMale(farmerId: String, maleProductId: String): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM breeding_pairs WHERE farmerId = ? AND status = 'ACTIVE' AND maleProductId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindText(_argIndex, maleProductId)
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

  public override suspend fun countActiveByFemale(farmerId: String, femaleProductId: String): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM breeding_pairs WHERE farmerId = ? AND status = 'ACTIVE' AND femaleProductId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindText(_argIndex, femaleProductId)
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

  public override suspend fun getDirty(): List<BreedingPairEntity> {
    val _sql: String = "SELECT * FROM breeding_pairs WHERE dirty = 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfPairId: Int = getColumnIndexOrThrow(_stmt, "pairId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfMaleProductId: Int = getColumnIndexOrThrow(_stmt, "maleProductId")
        val _columnIndexOfFemaleProductId: Int = getColumnIndexOrThrow(_stmt, "femaleProductId")
        val _columnIndexOfPairedAt: Int = getColumnIndexOrThrow(_stmt, "pairedAt")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfHatchSuccessRate: Int = getColumnIndexOrThrow(_stmt, "hatchSuccessRate")
        val _columnIndexOfEggsCollected: Int = getColumnIndexOrThrow(_stmt, "eggsCollected")
        val _columnIndexOfHatchedEggs: Int = getColumnIndexOrThrow(_stmt, "hatchedEggs")
        val _columnIndexOfSeparatedAt: Int = getColumnIndexOrThrow(_stmt, "separatedAt")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<BreedingPairEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BreedingPairEntity
          val _tmpPairId: String
          _tmpPairId = _stmt.getText(_columnIndexOfPairId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpMaleProductId: String
          _tmpMaleProductId = _stmt.getText(_columnIndexOfMaleProductId)
          val _tmpFemaleProductId: String
          _tmpFemaleProductId = _stmt.getText(_columnIndexOfFemaleProductId)
          val _tmpPairedAt: Long
          _tmpPairedAt = _stmt.getLong(_columnIndexOfPairedAt)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpHatchSuccessRate: Double
          _tmpHatchSuccessRate = _stmt.getDouble(_columnIndexOfHatchSuccessRate)
          val _tmpEggsCollected: Int
          _tmpEggsCollected = _stmt.getLong(_columnIndexOfEggsCollected).toInt()
          val _tmpHatchedEggs: Int
          _tmpHatchedEggs = _stmt.getLong(_columnIndexOfHatchedEggs).toInt()
          val _tmpSeparatedAt: Long?
          if (_stmt.isNull(_columnIndexOfSeparatedAt)) {
            _tmpSeparatedAt = null
          } else {
            _tmpSeparatedAt = _stmt.getLong(_columnIndexOfSeparatedAt)
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
              BreedingPairEntity(_tmpPairId,_tmpFarmerId,_tmpMaleProductId,_tmpFemaleProductId,_tmpPairedAt,_tmpStatus,_tmpHatchSuccessRate,_tmpEggsCollected,_tmpHatchedEggs,_tmpSeparatedAt,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getPairCountForFarmer(farmerId: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM breeding_pairs WHERE farmerId = ?"
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

  public override suspend fun clearDirty(pairIds: List<String>, syncedAt: Long) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("UPDATE breeding_pairs SET dirty = 0, syncedAt = ")
    _stringBuilder.append("?")
    _stringBuilder.append(" WHERE pairId IN (")
    val _inputSize: Int = pairIds.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, syncedAt)
        _argIndex = 2
        for (_item: String in pairIds) {
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
