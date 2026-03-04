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
import com.rio.rostry.`data`.database.entity.EggCollectionEntity
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
public class EggCollectionDao_Impl(
  __db: RoomDatabase,
) : EggCollectionDao {
  private val __db: RoomDatabase

  private val __upsertAdapterOfEggCollectionEntity: EntityUpsertAdapter<EggCollectionEntity>
  init {
    this.__db = __db
    this.__upsertAdapterOfEggCollectionEntity = EntityUpsertAdapter<EggCollectionEntity>(object :
        EntityInsertAdapter<EggCollectionEntity>() {
      protected override fun createQuery(): String =
          "INSERT INTO `egg_collections` (`collectionId`,`pairId`,`farmerId`,`eggsCollected`,`collectedAt`,`qualityGrade`,`weight`,`notes`,`goodCount`,`damagedCount`,`brokenCount`,`trayLayoutJson`,`setForHatching`,`linkedBatchId`,`setForHatchingAt`,`createdAt`,`updatedAt`,`dirty`,`syncedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: EggCollectionEntity) {
        statement.bindText(1, entity.collectionId)
        statement.bindText(2, entity.pairId)
        statement.bindText(3, entity.farmerId)
        statement.bindLong(4, entity.eggsCollected.toLong())
        statement.bindLong(5, entity.collectedAt)
        statement.bindText(6, entity.qualityGrade)
        val _tmpWeight: Double? = entity.weight
        if (_tmpWeight == null) {
          statement.bindNull(7)
        } else {
          statement.bindDouble(7, _tmpWeight)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpNotes)
        }
        statement.bindLong(9, entity.goodCount.toLong())
        statement.bindLong(10, entity.damagedCount.toLong())
        statement.bindLong(11, entity.brokenCount.toLong())
        val _tmpTrayLayoutJson: String? = entity.trayLayoutJson
        if (_tmpTrayLayoutJson == null) {
          statement.bindNull(12)
        } else {
          statement.bindText(12, _tmpTrayLayoutJson)
        }
        val _tmp: Int = if (entity.setForHatching) 1 else 0
        statement.bindLong(13, _tmp.toLong())
        val _tmpLinkedBatchId: String? = entity.linkedBatchId
        if (_tmpLinkedBatchId == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmpLinkedBatchId)
        }
        val _tmpSetForHatchingAt: Long? = entity.setForHatchingAt
        if (_tmpSetForHatchingAt == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpSetForHatchingAt)
        }
        statement.bindLong(16, entity.createdAt)
        statement.bindLong(17, entity.updatedAt)
        val _tmp_1: Int = if (entity.dirty) 1 else 0
        statement.bindLong(18, _tmp_1.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(19)
        } else {
          statement.bindLong(19, _tmpSyncedAt)
        }
      }
    }, object : EntityDeleteOrUpdateAdapter<EggCollectionEntity>() {
      protected override fun createQuery(): String =
          "UPDATE `egg_collections` SET `collectionId` = ?,`pairId` = ?,`farmerId` = ?,`eggsCollected` = ?,`collectedAt` = ?,`qualityGrade` = ?,`weight` = ?,`notes` = ?,`goodCount` = ?,`damagedCount` = ?,`brokenCount` = ?,`trayLayoutJson` = ?,`setForHatching` = ?,`linkedBatchId` = ?,`setForHatchingAt` = ?,`createdAt` = ?,`updatedAt` = ?,`dirty` = ?,`syncedAt` = ? WHERE `collectionId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: EggCollectionEntity) {
        statement.bindText(1, entity.collectionId)
        statement.bindText(2, entity.pairId)
        statement.bindText(3, entity.farmerId)
        statement.bindLong(4, entity.eggsCollected.toLong())
        statement.bindLong(5, entity.collectedAt)
        statement.bindText(6, entity.qualityGrade)
        val _tmpWeight: Double? = entity.weight
        if (_tmpWeight == null) {
          statement.bindNull(7)
        } else {
          statement.bindDouble(7, _tmpWeight)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpNotes)
        }
        statement.bindLong(9, entity.goodCount.toLong())
        statement.bindLong(10, entity.damagedCount.toLong())
        statement.bindLong(11, entity.brokenCount.toLong())
        val _tmpTrayLayoutJson: String? = entity.trayLayoutJson
        if (_tmpTrayLayoutJson == null) {
          statement.bindNull(12)
        } else {
          statement.bindText(12, _tmpTrayLayoutJson)
        }
        val _tmp: Int = if (entity.setForHatching) 1 else 0
        statement.bindLong(13, _tmp.toLong())
        val _tmpLinkedBatchId: String? = entity.linkedBatchId
        if (_tmpLinkedBatchId == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmpLinkedBatchId)
        }
        val _tmpSetForHatchingAt: Long? = entity.setForHatchingAt
        if (_tmpSetForHatchingAt == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpSetForHatchingAt)
        }
        statement.bindLong(16, entity.createdAt)
        statement.bindLong(17, entity.updatedAt)
        val _tmp_1: Int = if (entity.dirty) 1 else 0
        statement.bindLong(18, _tmp_1.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(19)
        } else {
          statement.bindLong(19, _tmpSyncedAt)
        }
        statement.bindText(20, entity.collectionId)
      }
    })
  }

  public override suspend fun upsert(collection: EggCollectionEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __upsertAdapterOfEggCollectionEntity.upsert(_connection, collection)
  }

  public override fun observeByPair(pairId: String): Flow<List<EggCollectionEntity>> {
    val _sql: String = "SELECT * FROM egg_collections WHERE pairId = ? ORDER BY collectedAt DESC"
    return createFlow(__db, false, arrayOf("egg_collections")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, pairId)
        val _columnIndexOfCollectionId: Int = getColumnIndexOrThrow(_stmt, "collectionId")
        val _columnIndexOfPairId: Int = getColumnIndexOrThrow(_stmt, "pairId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfEggsCollected: Int = getColumnIndexOrThrow(_stmt, "eggsCollected")
        val _columnIndexOfCollectedAt: Int = getColumnIndexOrThrow(_stmt, "collectedAt")
        val _columnIndexOfQualityGrade: Int = getColumnIndexOrThrow(_stmt, "qualityGrade")
        val _columnIndexOfWeight: Int = getColumnIndexOrThrow(_stmt, "weight")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfGoodCount: Int = getColumnIndexOrThrow(_stmt, "goodCount")
        val _columnIndexOfDamagedCount: Int = getColumnIndexOrThrow(_stmt, "damagedCount")
        val _columnIndexOfBrokenCount: Int = getColumnIndexOrThrow(_stmt, "brokenCount")
        val _columnIndexOfTrayLayoutJson: Int = getColumnIndexOrThrow(_stmt, "trayLayoutJson")
        val _columnIndexOfSetForHatching: Int = getColumnIndexOrThrow(_stmt, "setForHatching")
        val _columnIndexOfLinkedBatchId: Int = getColumnIndexOrThrow(_stmt, "linkedBatchId")
        val _columnIndexOfSetForHatchingAt: Int = getColumnIndexOrThrow(_stmt, "setForHatchingAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<EggCollectionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: EggCollectionEntity
          val _tmpCollectionId: String
          _tmpCollectionId = _stmt.getText(_columnIndexOfCollectionId)
          val _tmpPairId: String
          _tmpPairId = _stmt.getText(_columnIndexOfPairId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpEggsCollected: Int
          _tmpEggsCollected = _stmt.getLong(_columnIndexOfEggsCollected).toInt()
          val _tmpCollectedAt: Long
          _tmpCollectedAt = _stmt.getLong(_columnIndexOfCollectedAt)
          val _tmpQualityGrade: String
          _tmpQualityGrade = _stmt.getText(_columnIndexOfQualityGrade)
          val _tmpWeight: Double?
          if (_stmt.isNull(_columnIndexOfWeight)) {
            _tmpWeight = null
          } else {
            _tmpWeight = _stmt.getDouble(_columnIndexOfWeight)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpGoodCount: Int
          _tmpGoodCount = _stmt.getLong(_columnIndexOfGoodCount).toInt()
          val _tmpDamagedCount: Int
          _tmpDamagedCount = _stmt.getLong(_columnIndexOfDamagedCount).toInt()
          val _tmpBrokenCount: Int
          _tmpBrokenCount = _stmt.getLong(_columnIndexOfBrokenCount).toInt()
          val _tmpTrayLayoutJson: String?
          if (_stmt.isNull(_columnIndexOfTrayLayoutJson)) {
            _tmpTrayLayoutJson = null
          } else {
            _tmpTrayLayoutJson = _stmt.getText(_columnIndexOfTrayLayoutJson)
          }
          val _tmpSetForHatching: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfSetForHatching).toInt()
          _tmpSetForHatching = _tmp != 0
          val _tmpLinkedBatchId: String?
          if (_stmt.isNull(_columnIndexOfLinkedBatchId)) {
            _tmpLinkedBatchId = null
          } else {
            _tmpLinkedBatchId = _stmt.getText(_columnIndexOfLinkedBatchId)
          }
          val _tmpSetForHatchingAt: Long?
          if (_stmt.isNull(_columnIndexOfSetForHatchingAt)) {
            _tmpSetForHatchingAt = null
          } else {
            _tmpSetForHatchingAt = _stmt.getLong(_columnIndexOfSetForHatchingAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              EggCollectionEntity(_tmpCollectionId,_tmpPairId,_tmpFarmerId,_tmpEggsCollected,_tmpCollectedAt,_tmpQualityGrade,_tmpWeight,_tmpNotes,_tmpGoodCount,_tmpDamagedCount,_tmpBrokenCount,_tmpTrayLayoutJson,_tmpSetForHatching,_tmpLinkedBatchId,_tmpSetForHatchingAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTotalEggsByPair(pairId: String): Int {
    val _sql: String = "SELECT IFNULL(SUM(eggsCollected), 0) FROM egg_collections WHERE pairId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, pairId)
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

  public override suspend fun getCollectionsByPair(pairId: String): List<EggCollectionEntity> {
    val _sql: String = "SELECT * FROM egg_collections WHERE pairId = ? ORDER BY collectedAt DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, pairId)
        val _columnIndexOfCollectionId: Int = getColumnIndexOrThrow(_stmt, "collectionId")
        val _columnIndexOfPairId: Int = getColumnIndexOrThrow(_stmt, "pairId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfEggsCollected: Int = getColumnIndexOrThrow(_stmt, "eggsCollected")
        val _columnIndexOfCollectedAt: Int = getColumnIndexOrThrow(_stmt, "collectedAt")
        val _columnIndexOfQualityGrade: Int = getColumnIndexOrThrow(_stmt, "qualityGrade")
        val _columnIndexOfWeight: Int = getColumnIndexOrThrow(_stmt, "weight")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfGoodCount: Int = getColumnIndexOrThrow(_stmt, "goodCount")
        val _columnIndexOfDamagedCount: Int = getColumnIndexOrThrow(_stmt, "damagedCount")
        val _columnIndexOfBrokenCount: Int = getColumnIndexOrThrow(_stmt, "brokenCount")
        val _columnIndexOfTrayLayoutJson: Int = getColumnIndexOrThrow(_stmt, "trayLayoutJson")
        val _columnIndexOfSetForHatching: Int = getColumnIndexOrThrow(_stmt, "setForHatching")
        val _columnIndexOfLinkedBatchId: Int = getColumnIndexOrThrow(_stmt, "linkedBatchId")
        val _columnIndexOfSetForHatchingAt: Int = getColumnIndexOrThrow(_stmt, "setForHatchingAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<EggCollectionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: EggCollectionEntity
          val _tmpCollectionId: String
          _tmpCollectionId = _stmt.getText(_columnIndexOfCollectionId)
          val _tmpPairId: String
          _tmpPairId = _stmt.getText(_columnIndexOfPairId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpEggsCollected: Int
          _tmpEggsCollected = _stmt.getLong(_columnIndexOfEggsCollected).toInt()
          val _tmpCollectedAt: Long
          _tmpCollectedAt = _stmt.getLong(_columnIndexOfCollectedAt)
          val _tmpQualityGrade: String
          _tmpQualityGrade = _stmt.getText(_columnIndexOfQualityGrade)
          val _tmpWeight: Double?
          if (_stmt.isNull(_columnIndexOfWeight)) {
            _tmpWeight = null
          } else {
            _tmpWeight = _stmt.getDouble(_columnIndexOfWeight)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpGoodCount: Int
          _tmpGoodCount = _stmt.getLong(_columnIndexOfGoodCount).toInt()
          val _tmpDamagedCount: Int
          _tmpDamagedCount = _stmt.getLong(_columnIndexOfDamagedCount).toInt()
          val _tmpBrokenCount: Int
          _tmpBrokenCount = _stmt.getLong(_columnIndexOfBrokenCount).toInt()
          val _tmpTrayLayoutJson: String?
          if (_stmt.isNull(_columnIndexOfTrayLayoutJson)) {
            _tmpTrayLayoutJson = null
          } else {
            _tmpTrayLayoutJson = _stmt.getText(_columnIndexOfTrayLayoutJson)
          }
          val _tmpSetForHatching: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfSetForHatching).toInt()
          _tmpSetForHatching = _tmp != 0
          val _tmpLinkedBatchId: String?
          if (_stmt.isNull(_columnIndexOfLinkedBatchId)) {
            _tmpLinkedBatchId = null
          } else {
            _tmpLinkedBatchId = _stmt.getText(_columnIndexOfLinkedBatchId)
          }
          val _tmpSetForHatchingAt: Long?
          if (_stmt.isNull(_columnIndexOfSetForHatchingAt)) {
            _tmpSetForHatchingAt = null
          } else {
            _tmpSetForHatchingAt = _stmt.getLong(_columnIndexOfSetForHatchingAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              EggCollectionEntity(_tmpCollectionId,_tmpPairId,_tmpFarmerId,_tmpEggsCollected,_tmpCollectedAt,_tmpQualityGrade,_tmpWeight,_tmpNotes,_tmpGoodCount,_tmpDamagedCount,_tmpBrokenCount,_tmpTrayLayoutJson,_tmpSetForHatching,_tmpLinkedBatchId,_tmpSetForHatchingAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getCollectionsDueBetween(start: Long, end: Long):
      List<EggCollectionEntity> {
    val _sql: String =
        "SELECT * FROM egg_collections WHERE collectedAt BETWEEN ? AND ? ORDER BY collectedAt DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, start)
        _argIndex = 2
        _stmt.bindLong(_argIndex, end)
        val _columnIndexOfCollectionId: Int = getColumnIndexOrThrow(_stmt, "collectionId")
        val _columnIndexOfPairId: Int = getColumnIndexOrThrow(_stmt, "pairId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfEggsCollected: Int = getColumnIndexOrThrow(_stmt, "eggsCollected")
        val _columnIndexOfCollectedAt: Int = getColumnIndexOrThrow(_stmt, "collectedAt")
        val _columnIndexOfQualityGrade: Int = getColumnIndexOrThrow(_stmt, "qualityGrade")
        val _columnIndexOfWeight: Int = getColumnIndexOrThrow(_stmt, "weight")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfGoodCount: Int = getColumnIndexOrThrow(_stmt, "goodCount")
        val _columnIndexOfDamagedCount: Int = getColumnIndexOrThrow(_stmt, "damagedCount")
        val _columnIndexOfBrokenCount: Int = getColumnIndexOrThrow(_stmt, "brokenCount")
        val _columnIndexOfTrayLayoutJson: Int = getColumnIndexOrThrow(_stmt, "trayLayoutJson")
        val _columnIndexOfSetForHatching: Int = getColumnIndexOrThrow(_stmt, "setForHatching")
        val _columnIndexOfLinkedBatchId: Int = getColumnIndexOrThrow(_stmt, "linkedBatchId")
        val _columnIndexOfSetForHatchingAt: Int = getColumnIndexOrThrow(_stmt, "setForHatchingAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<EggCollectionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: EggCollectionEntity
          val _tmpCollectionId: String
          _tmpCollectionId = _stmt.getText(_columnIndexOfCollectionId)
          val _tmpPairId: String
          _tmpPairId = _stmt.getText(_columnIndexOfPairId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpEggsCollected: Int
          _tmpEggsCollected = _stmt.getLong(_columnIndexOfEggsCollected).toInt()
          val _tmpCollectedAt: Long
          _tmpCollectedAt = _stmt.getLong(_columnIndexOfCollectedAt)
          val _tmpQualityGrade: String
          _tmpQualityGrade = _stmt.getText(_columnIndexOfQualityGrade)
          val _tmpWeight: Double?
          if (_stmt.isNull(_columnIndexOfWeight)) {
            _tmpWeight = null
          } else {
            _tmpWeight = _stmt.getDouble(_columnIndexOfWeight)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpGoodCount: Int
          _tmpGoodCount = _stmt.getLong(_columnIndexOfGoodCount).toInt()
          val _tmpDamagedCount: Int
          _tmpDamagedCount = _stmt.getLong(_columnIndexOfDamagedCount).toInt()
          val _tmpBrokenCount: Int
          _tmpBrokenCount = _stmt.getLong(_columnIndexOfBrokenCount).toInt()
          val _tmpTrayLayoutJson: String?
          if (_stmt.isNull(_columnIndexOfTrayLayoutJson)) {
            _tmpTrayLayoutJson = null
          } else {
            _tmpTrayLayoutJson = _stmt.getText(_columnIndexOfTrayLayoutJson)
          }
          val _tmpSetForHatching: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfSetForHatching).toInt()
          _tmpSetForHatching = _tmp != 0
          val _tmpLinkedBatchId: String?
          if (_stmt.isNull(_columnIndexOfLinkedBatchId)) {
            _tmpLinkedBatchId = null
          } else {
            _tmpLinkedBatchId = _stmt.getText(_columnIndexOfLinkedBatchId)
          }
          val _tmpSetForHatchingAt: Long?
          if (_stmt.isNull(_columnIndexOfSetForHatchingAt)) {
            _tmpSetForHatchingAt = null
          } else {
            _tmpSetForHatchingAt = _stmt.getLong(_columnIndexOfSetForHatchingAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              EggCollectionEntity(_tmpCollectionId,_tmpPairId,_tmpFarmerId,_tmpEggsCollected,_tmpCollectedAt,_tmpQualityGrade,_tmpWeight,_tmpNotes,_tmpGoodCount,_tmpDamagedCount,_tmpBrokenCount,_tmpTrayLayoutJson,_tmpSetForHatching,_tmpLinkedBatchId,_tmpSetForHatchingAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countEggsForFarmerBetween(
    farmerId: String,
    start: Long,
    end: Long,
  ): Int {
    val _sql: String =
        "SELECT IFNULL(SUM(eggsCollected), 0) FROM egg_collections WHERE farmerId = ? AND collectedAt BETWEEN ? AND ?"
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

  public override fun observeRecentByFarmer(farmerId: String, limit: Int):
      Flow<List<EggCollectionEntity>> {
    val _sql: String =
        "SELECT * FROM egg_collections WHERE farmerId = ? ORDER BY collectedAt DESC LIMIT ?"
    return createFlow(__db, false, arrayOf("egg_collections")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfCollectionId: Int = getColumnIndexOrThrow(_stmt, "collectionId")
        val _columnIndexOfPairId: Int = getColumnIndexOrThrow(_stmt, "pairId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfEggsCollected: Int = getColumnIndexOrThrow(_stmt, "eggsCollected")
        val _columnIndexOfCollectedAt: Int = getColumnIndexOrThrow(_stmt, "collectedAt")
        val _columnIndexOfQualityGrade: Int = getColumnIndexOrThrow(_stmt, "qualityGrade")
        val _columnIndexOfWeight: Int = getColumnIndexOrThrow(_stmt, "weight")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfGoodCount: Int = getColumnIndexOrThrow(_stmt, "goodCount")
        val _columnIndexOfDamagedCount: Int = getColumnIndexOrThrow(_stmt, "damagedCount")
        val _columnIndexOfBrokenCount: Int = getColumnIndexOrThrow(_stmt, "brokenCount")
        val _columnIndexOfTrayLayoutJson: Int = getColumnIndexOrThrow(_stmt, "trayLayoutJson")
        val _columnIndexOfSetForHatching: Int = getColumnIndexOrThrow(_stmt, "setForHatching")
        val _columnIndexOfLinkedBatchId: Int = getColumnIndexOrThrow(_stmt, "linkedBatchId")
        val _columnIndexOfSetForHatchingAt: Int = getColumnIndexOrThrow(_stmt, "setForHatchingAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<EggCollectionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: EggCollectionEntity
          val _tmpCollectionId: String
          _tmpCollectionId = _stmt.getText(_columnIndexOfCollectionId)
          val _tmpPairId: String
          _tmpPairId = _stmt.getText(_columnIndexOfPairId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpEggsCollected: Int
          _tmpEggsCollected = _stmt.getLong(_columnIndexOfEggsCollected).toInt()
          val _tmpCollectedAt: Long
          _tmpCollectedAt = _stmt.getLong(_columnIndexOfCollectedAt)
          val _tmpQualityGrade: String
          _tmpQualityGrade = _stmt.getText(_columnIndexOfQualityGrade)
          val _tmpWeight: Double?
          if (_stmt.isNull(_columnIndexOfWeight)) {
            _tmpWeight = null
          } else {
            _tmpWeight = _stmt.getDouble(_columnIndexOfWeight)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpGoodCount: Int
          _tmpGoodCount = _stmt.getLong(_columnIndexOfGoodCount).toInt()
          val _tmpDamagedCount: Int
          _tmpDamagedCount = _stmt.getLong(_columnIndexOfDamagedCount).toInt()
          val _tmpBrokenCount: Int
          _tmpBrokenCount = _stmt.getLong(_columnIndexOfBrokenCount).toInt()
          val _tmpTrayLayoutJson: String?
          if (_stmt.isNull(_columnIndexOfTrayLayoutJson)) {
            _tmpTrayLayoutJson = null
          } else {
            _tmpTrayLayoutJson = _stmt.getText(_columnIndexOfTrayLayoutJson)
          }
          val _tmpSetForHatching: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfSetForHatching).toInt()
          _tmpSetForHatching = _tmp != 0
          val _tmpLinkedBatchId: String?
          if (_stmt.isNull(_columnIndexOfLinkedBatchId)) {
            _tmpLinkedBatchId = null
          } else {
            _tmpLinkedBatchId = _stmt.getText(_columnIndexOfLinkedBatchId)
          }
          val _tmpSetForHatchingAt: Long?
          if (_stmt.isNull(_columnIndexOfSetForHatchingAt)) {
            _tmpSetForHatchingAt = null
          } else {
            _tmpSetForHatchingAt = _stmt.getLong(_columnIndexOfSetForHatchingAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              EggCollectionEntity(_tmpCollectionId,_tmpPairId,_tmpFarmerId,_tmpEggsCollected,_tmpCollectedAt,_tmpQualityGrade,_tmpWeight,_tmpNotes,_tmpGoodCount,_tmpDamagedCount,_tmpBrokenCount,_tmpTrayLayoutJson,_tmpSetForHatching,_tmpLinkedBatchId,_tmpSetForHatchingAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(collectionId: String): EggCollectionEntity? {
    val _sql: String = "SELECT * FROM egg_collections WHERE collectionId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, collectionId)
        val _columnIndexOfCollectionId: Int = getColumnIndexOrThrow(_stmt, "collectionId")
        val _columnIndexOfPairId: Int = getColumnIndexOrThrow(_stmt, "pairId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfEggsCollected: Int = getColumnIndexOrThrow(_stmt, "eggsCollected")
        val _columnIndexOfCollectedAt: Int = getColumnIndexOrThrow(_stmt, "collectedAt")
        val _columnIndexOfQualityGrade: Int = getColumnIndexOrThrow(_stmt, "qualityGrade")
        val _columnIndexOfWeight: Int = getColumnIndexOrThrow(_stmt, "weight")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfGoodCount: Int = getColumnIndexOrThrow(_stmt, "goodCount")
        val _columnIndexOfDamagedCount: Int = getColumnIndexOrThrow(_stmt, "damagedCount")
        val _columnIndexOfBrokenCount: Int = getColumnIndexOrThrow(_stmt, "brokenCount")
        val _columnIndexOfTrayLayoutJson: Int = getColumnIndexOrThrow(_stmt, "trayLayoutJson")
        val _columnIndexOfSetForHatching: Int = getColumnIndexOrThrow(_stmt, "setForHatching")
        val _columnIndexOfLinkedBatchId: Int = getColumnIndexOrThrow(_stmt, "linkedBatchId")
        val _columnIndexOfSetForHatchingAt: Int = getColumnIndexOrThrow(_stmt, "setForHatchingAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: EggCollectionEntity?
        if (_stmt.step()) {
          val _tmpCollectionId: String
          _tmpCollectionId = _stmt.getText(_columnIndexOfCollectionId)
          val _tmpPairId: String
          _tmpPairId = _stmt.getText(_columnIndexOfPairId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpEggsCollected: Int
          _tmpEggsCollected = _stmt.getLong(_columnIndexOfEggsCollected).toInt()
          val _tmpCollectedAt: Long
          _tmpCollectedAt = _stmt.getLong(_columnIndexOfCollectedAt)
          val _tmpQualityGrade: String
          _tmpQualityGrade = _stmt.getText(_columnIndexOfQualityGrade)
          val _tmpWeight: Double?
          if (_stmt.isNull(_columnIndexOfWeight)) {
            _tmpWeight = null
          } else {
            _tmpWeight = _stmt.getDouble(_columnIndexOfWeight)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpGoodCount: Int
          _tmpGoodCount = _stmt.getLong(_columnIndexOfGoodCount).toInt()
          val _tmpDamagedCount: Int
          _tmpDamagedCount = _stmt.getLong(_columnIndexOfDamagedCount).toInt()
          val _tmpBrokenCount: Int
          _tmpBrokenCount = _stmt.getLong(_columnIndexOfBrokenCount).toInt()
          val _tmpTrayLayoutJson: String?
          if (_stmt.isNull(_columnIndexOfTrayLayoutJson)) {
            _tmpTrayLayoutJson = null
          } else {
            _tmpTrayLayoutJson = _stmt.getText(_columnIndexOfTrayLayoutJson)
          }
          val _tmpSetForHatching: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfSetForHatching).toInt()
          _tmpSetForHatching = _tmp != 0
          val _tmpLinkedBatchId: String?
          if (_stmt.isNull(_columnIndexOfLinkedBatchId)) {
            _tmpLinkedBatchId = null
          } else {
            _tmpLinkedBatchId = _stmt.getText(_columnIndexOfLinkedBatchId)
          }
          val _tmpSetForHatchingAt: Long?
          if (_stmt.isNull(_columnIndexOfSetForHatchingAt)) {
            _tmpSetForHatchingAt = null
          } else {
            _tmpSetForHatchingAt = _stmt.getLong(_columnIndexOfSetForHatchingAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _result =
              EggCollectionEntity(_tmpCollectionId,_tmpPairId,_tmpFarmerId,_tmpEggsCollected,_tmpCollectedAt,_tmpQualityGrade,_tmpWeight,_tmpNotes,_tmpGoodCount,_tmpDamagedCount,_tmpBrokenCount,_tmpTrayLayoutJson,_tmpSetForHatching,_tmpLinkedBatchId,_tmpSetForHatchingAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDirty(): List<EggCollectionEntity> {
    val _sql: String = "SELECT * FROM egg_collections WHERE dirty = 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfCollectionId: Int = getColumnIndexOrThrow(_stmt, "collectionId")
        val _columnIndexOfPairId: Int = getColumnIndexOrThrow(_stmt, "pairId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfEggsCollected: Int = getColumnIndexOrThrow(_stmt, "eggsCollected")
        val _columnIndexOfCollectedAt: Int = getColumnIndexOrThrow(_stmt, "collectedAt")
        val _columnIndexOfQualityGrade: Int = getColumnIndexOrThrow(_stmt, "qualityGrade")
        val _columnIndexOfWeight: Int = getColumnIndexOrThrow(_stmt, "weight")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfGoodCount: Int = getColumnIndexOrThrow(_stmt, "goodCount")
        val _columnIndexOfDamagedCount: Int = getColumnIndexOrThrow(_stmt, "damagedCount")
        val _columnIndexOfBrokenCount: Int = getColumnIndexOrThrow(_stmt, "brokenCount")
        val _columnIndexOfTrayLayoutJson: Int = getColumnIndexOrThrow(_stmt, "trayLayoutJson")
        val _columnIndexOfSetForHatching: Int = getColumnIndexOrThrow(_stmt, "setForHatching")
        val _columnIndexOfLinkedBatchId: Int = getColumnIndexOrThrow(_stmt, "linkedBatchId")
        val _columnIndexOfSetForHatchingAt: Int = getColumnIndexOrThrow(_stmt, "setForHatchingAt")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<EggCollectionEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: EggCollectionEntity
          val _tmpCollectionId: String
          _tmpCollectionId = _stmt.getText(_columnIndexOfCollectionId)
          val _tmpPairId: String
          _tmpPairId = _stmt.getText(_columnIndexOfPairId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpEggsCollected: Int
          _tmpEggsCollected = _stmt.getLong(_columnIndexOfEggsCollected).toInt()
          val _tmpCollectedAt: Long
          _tmpCollectedAt = _stmt.getLong(_columnIndexOfCollectedAt)
          val _tmpQualityGrade: String
          _tmpQualityGrade = _stmt.getText(_columnIndexOfQualityGrade)
          val _tmpWeight: Double?
          if (_stmt.isNull(_columnIndexOfWeight)) {
            _tmpWeight = null
          } else {
            _tmpWeight = _stmt.getDouble(_columnIndexOfWeight)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpGoodCount: Int
          _tmpGoodCount = _stmt.getLong(_columnIndexOfGoodCount).toInt()
          val _tmpDamagedCount: Int
          _tmpDamagedCount = _stmt.getLong(_columnIndexOfDamagedCount).toInt()
          val _tmpBrokenCount: Int
          _tmpBrokenCount = _stmt.getLong(_columnIndexOfBrokenCount).toInt()
          val _tmpTrayLayoutJson: String?
          if (_stmt.isNull(_columnIndexOfTrayLayoutJson)) {
            _tmpTrayLayoutJson = null
          } else {
            _tmpTrayLayoutJson = _stmt.getText(_columnIndexOfTrayLayoutJson)
          }
          val _tmpSetForHatching: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfSetForHatching).toInt()
          _tmpSetForHatching = _tmp != 0
          val _tmpLinkedBatchId: String?
          if (_stmt.isNull(_columnIndexOfLinkedBatchId)) {
            _tmpLinkedBatchId = null
          } else {
            _tmpLinkedBatchId = _stmt.getText(_columnIndexOfLinkedBatchId)
          }
          val _tmpSetForHatchingAt: Long?
          if (_stmt.isNull(_columnIndexOfSetForHatchingAt)) {
            _tmpSetForHatchingAt = null
          } else {
            _tmpSetForHatchingAt = _stmt.getLong(_columnIndexOfSetForHatchingAt)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp_1 != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          _item =
              EggCollectionEntity(_tmpCollectionId,_tmpPairId,_tmpFarmerId,_tmpEggsCollected,_tmpCollectedAt,_tmpQualityGrade,_tmpWeight,_tmpNotes,_tmpGoodCount,_tmpDamagedCount,_tmpBrokenCount,_tmpTrayLayoutJson,_tmpSetForHatching,_tmpLinkedBatchId,_tmpSetForHatchingAt,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearDirty(collectionIds: List<String>, syncedAt: Long) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("UPDATE egg_collections SET dirty = 0, syncedAt = ")
    _stringBuilder.append("?")
    _stringBuilder.append(" WHERE collectionId IN (")
    val _inputSize: Int = collectionIds.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, syncedAt)
        _argIndex = 2
        for (_item: String in collectionIds) {
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
