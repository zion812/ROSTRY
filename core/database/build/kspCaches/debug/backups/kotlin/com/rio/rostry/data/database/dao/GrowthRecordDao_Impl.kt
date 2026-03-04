package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.GrowthRecordEntity
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
public class GrowthRecordDao_Impl(
  __db: RoomDatabase,
) : GrowthRecordDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfGrowthRecordEntity: EntityInsertAdapter<GrowthRecordEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfGrowthRecordEntity = object : EntityInsertAdapter<GrowthRecordEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `growth_records` (`recordId`,`productId`,`farmerId`,`week`,`weightGrams`,`heightCm`,`photoUrl`,`mediaItemsJson`,`healthStatus`,`milestone`,`createdAt`,`updatedAt`,`dirty`,`syncedAt`,`correctionOf`,`editCount`,`lastEditedBy`,`isBatchLevel`,`sourceBatchId`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: GrowthRecordEntity) {
        statement.bindText(1, entity.recordId)
        statement.bindText(2, entity.productId)
        statement.bindText(3, entity.farmerId)
        statement.bindLong(4, entity.week.toLong())
        val _tmpWeightGrams: Double? = entity.weightGrams
        if (_tmpWeightGrams == null) {
          statement.bindNull(5)
        } else {
          statement.bindDouble(5, _tmpWeightGrams)
        }
        val _tmpHeightCm: Double? = entity.heightCm
        if (_tmpHeightCm == null) {
          statement.bindNull(6)
        } else {
          statement.bindDouble(6, _tmpHeightCm)
        }
        val _tmpPhotoUrl: String? = entity.photoUrl
        if (_tmpPhotoUrl == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpPhotoUrl)
        }
        val _tmpMediaItemsJson: String? = entity.mediaItemsJson
        if (_tmpMediaItemsJson == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpMediaItemsJson)
        }
        val _tmpHealthStatus: String? = entity.healthStatus
        if (_tmpHealthStatus == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpHealthStatus)
        }
        val _tmpMilestone: String? = entity.milestone
        if (_tmpMilestone == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpMilestone)
        }
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
        val _tmpCorrectionOf: String? = entity.correctionOf
        if (_tmpCorrectionOf == null) {
          statement.bindNull(15)
        } else {
          statement.bindText(15, _tmpCorrectionOf)
        }
        statement.bindLong(16, entity.editCount.toLong())
        val _tmpLastEditedBy: String? = entity.lastEditedBy
        if (_tmpLastEditedBy == null) {
          statement.bindNull(17)
        } else {
          statement.bindText(17, _tmpLastEditedBy)
        }
        val _tmp_1: Int = if (entity.isBatchLevel) 1 else 0
        statement.bindLong(18, _tmp_1.toLong())
        val _tmpSourceBatchId: String? = entity.sourceBatchId
        if (_tmpSourceBatchId == null) {
          statement.bindNull(19)
        } else {
          statement.bindText(19, _tmpSourceBatchId)
        }
      }
    }
  }

  public override suspend fun upsert(record: GrowthRecordEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfGrowthRecordEntity.insert(_connection, record)
  }

  public override fun observeForProduct(productId: String): Flow<List<GrowthRecordEntity>> {
    val _sql: String = "SELECT * FROM growth_records WHERE productId = ? ORDER BY week ASC"
    return createFlow(__db, false, arrayOf("growth_records")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfWeek: Int = getColumnIndexOrThrow(_stmt, "week")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "photoUrl")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfMilestone: Int = getColumnIndexOrThrow(_stmt, "milestone")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfCorrectionOf: Int = getColumnIndexOrThrow(_stmt, "correctionOf")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfIsBatchLevel: Int = getColumnIndexOrThrow(_stmt, "isBatchLevel")
        val _columnIndexOfSourceBatchId: Int = getColumnIndexOrThrow(_stmt, "sourceBatchId")
        val _result: MutableList<GrowthRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: GrowthRecordEntity
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpWeek: Int
          _tmpWeek = _stmt.getLong(_columnIndexOfWeek).toInt()
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
          }
          val _tmpPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfPhotoUrl)) {
            _tmpPhotoUrl = null
          } else {
            _tmpPhotoUrl = _stmt.getText(_columnIndexOfPhotoUrl)
          }
          val _tmpMediaItemsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaItemsJson)) {
            _tmpMediaItemsJson = null
          } else {
            _tmpMediaItemsJson = _stmt.getText(_columnIndexOfMediaItemsJson)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpMilestone: String?
          if (_stmt.isNull(_columnIndexOfMilestone)) {
            _tmpMilestone = null
          } else {
            _tmpMilestone = _stmt.getText(_columnIndexOfMilestone)
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
          val _tmpCorrectionOf: String?
          if (_stmt.isNull(_columnIndexOfCorrectionOf)) {
            _tmpCorrectionOf = null
          } else {
            _tmpCorrectionOf = _stmt.getText(_columnIndexOfCorrectionOf)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpIsBatchLevel: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsBatchLevel).toInt()
          _tmpIsBatchLevel = _tmp_1 != 0
          val _tmpSourceBatchId: String?
          if (_stmt.isNull(_columnIndexOfSourceBatchId)) {
            _tmpSourceBatchId = null
          } else {
            _tmpSourceBatchId = _stmt.getText(_columnIndexOfSourceBatchId)
          }
          _item =
              GrowthRecordEntity(_tmpRecordId,_tmpProductId,_tmpFarmerId,_tmpWeek,_tmpWeightGrams,_tmpHeightCm,_tmpPhotoUrl,_tmpMediaItemsJson,_tmpHealthStatus,_tmpMilestone,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpCorrectionOf,_tmpEditCount,_tmpLastEditedBy,_tmpIsBatchLevel,_tmpSourceBatchId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllByProduct(productId: String): List<GrowthRecordEntity> {
    val _sql: String = "SELECT * FROM growth_records WHERE productId = ? ORDER BY week ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfWeek: Int = getColumnIndexOrThrow(_stmt, "week")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "photoUrl")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfMilestone: Int = getColumnIndexOrThrow(_stmt, "milestone")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfCorrectionOf: Int = getColumnIndexOrThrow(_stmt, "correctionOf")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfIsBatchLevel: Int = getColumnIndexOrThrow(_stmt, "isBatchLevel")
        val _columnIndexOfSourceBatchId: Int = getColumnIndexOrThrow(_stmt, "sourceBatchId")
        val _result: MutableList<GrowthRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: GrowthRecordEntity
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpWeek: Int
          _tmpWeek = _stmt.getLong(_columnIndexOfWeek).toInt()
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
          }
          val _tmpPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfPhotoUrl)) {
            _tmpPhotoUrl = null
          } else {
            _tmpPhotoUrl = _stmt.getText(_columnIndexOfPhotoUrl)
          }
          val _tmpMediaItemsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaItemsJson)) {
            _tmpMediaItemsJson = null
          } else {
            _tmpMediaItemsJson = _stmt.getText(_columnIndexOfMediaItemsJson)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpMilestone: String?
          if (_stmt.isNull(_columnIndexOfMilestone)) {
            _tmpMilestone = null
          } else {
            _tmpMilestone = _stmt.getText(_columnIndexOfMilestone)
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
          val _tmpCorrectionOf: String?
          if (_stmt.isNull(_columnIndexOfCorrectionOf)) {
            _tmpCorrectionOf = null
          } else {
            _tmpCorrectionOf = _stmt.getText(_columnIndexOfCorrectionOf)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpIsBatchLevel: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsBatchLevel).toInt()
          _tmpIsBatchLevel = _tmp_1 != 0
          val _tmpSourceBatchId: String?
          if (_stmt.isNull(_columnIndexOfSourceBatchId)) {
            _tmpSourceBatchId = null
          } else {
            _tmpSourceBatchId = _stmt.getText(_columnIndexOfSourceBatchId)
          }
          _item =
              GrowthRecordEntity(_tmpRecordId,_tmpProductId,_tmpFarmerId,_tmpWeek,_tmpWeightGrams,_tmpHeightCm,_tmpPhotoUrl,_tmpMediaItemsJson,_tmpHealthStatus,_tmpMilestone,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpCorrectionOf,_tmpEditCount,_tmpLastEditedBy,_tmpIsBatchLevel,_tmpSourceBatchId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun findByWeek(productId: String, week: Int): GrowthRecordEntity? {
    val _sql: String = "SELECT * FROM growth_records WHERE productId = ? AND week = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, week.toLong())
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfWeek: Int = getColumnIndexOrThrow(_stmt, "week")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "photoUrl")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfMilestone: Int = getColumnIndexOrThrow(_stmt, "milestone")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfCorrectionOf: Int = getColumnIndexOrThrow(_stmt, "correctionOf")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfIsBatchLevel: Int = getColumnIndexOrThrow(_stmt, "isBatchLevel")
        val _columnIndexOfSourceBatchId: Int = getColumnIndexOrThrow(_stmt, "sourceBatchId")
        val _result: GrowthRecordEntity?
        if (_stmt.step()) {
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpWeek: Int
          _tmpWeek = _stmt.getLong(_columnIndexOfWeek).toInt()
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
          }
          val _tmpPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfPhotoUrl)) {
            _tmpPhotoUrl = null
          } else {
            _tmpPhotoUrl = _stmt.getText(_columnIndexOfPhotoUrl)
          }
          val _tmpMediaItemsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaItemsJson)) {
            _tmpMediaItemsJson = null
          } else {
            _tmpMediaItemsJson = _stmt.getText(_columnIndexOfMediaItemsJson)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpMilestone: String?
          if (_stmt.isNull(_columnIndexOfMilestone)) {
            _tmpMilestone = null
          } else {
            _tmpMilestone = _stmt.getText(_columnIndexOfMilestone)
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
          val _tmpCorrectionOf: String?
          if (_stmt.isNull(_columnIndexOfCorrectionOf)) {
            _tmpCorrectionOf = null
          } else {
            _tmpCorrectionOf = _stmt.getText(_columnIndexOfCorrectionOf)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpIsBatchLevel: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsBatchLevel).toInt()
          _tmpIsBatchLevel = _tmp_1 != 0
          val _tmpSourceBatchId: String?
          if (_stmt.isNull(_columnIndexOfSourceBatchId)) {
            _tmpSourceBatchId = null
          } else {
            _tmpSourceBatchId = _stmt.getText(_columnIndexOfSourceBatchId)
          }
          _result =
              GrowthRecordEntity(_tmpRecordId,_tmpProductId,_tmpFarmerId,_tmpWeek,_tmpWeightGrams,_tmpHeightCm,_tmpPhotoUrl,_tmpMediaItemsJson,_tmpHealthStatus,_tmpMilestone,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpCorrectionOf,_tmpEditCount,_tmpLastEditedBy,_tmpIsBatchLevel,_tmpSourceBatchId)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getRecordsForProduct(productId: String): List<GrowthRecordEntity> {
    val _sql: String = "SELECT * FROM growth_records WHERE productId = ? ORDER BY week ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfWeek: Int = getColumnIndexOrThrow(_stmt, "week")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "photoUrl")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfMilestone: Int = getColumnIndexOrThrow(_stmt, "milestone")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfCorrectionOf: Int = getColumnIndexOrThrow(_stmt, "correctionOf")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfIsBatchLevel: Int = getColumnIndexOrThrow(_stmt, "isBatchLevel")
        val _columnIndexOfSourceBatchId: Int = getColumnIndexOrThrow(_stmt, "sourceBatchId")
        val _result: MutableList<GrowthRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: GrowthRecordEntity
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpWeek: Int
          _tmpWeek = _stmt.getLong(_columnIndexOfWeek).toInt()
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
          }
          val _tmpPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfPhotoUrl)) {
            _tmpPhotoUrl = null
          } else {
            _tmpPhotoUrl = _stmt.getText(_columnIndexOfPhotoUrl)
          }
          val _tmpMediaItemsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaItemsJson)) {
            _tmpMediaItemsJson = null
          } else {
            _tmpMediaItemsJson = _stmt.getText(_columnIndexOfMediaItemsJson)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpMilestone: String?
          if (_stmt.isNull(_columnIndexOfMilestone)) {
            _tmpMilestone = null
          } else {
            _tmpMilestone = _stmt.getText(_columnIndexOfMilestone)
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
          val _tmpCorrectionOf: String?
          if (_stmt.isNull(_columnIndexOfCorrectionOf)) {
            _tmpCorrectionOf = null
          } else {
            _tmpCorrectionOf = _stmt.getText(_columnIndexOfCorrectionOf)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpIsBatchLevel: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsBatchLevel).toInt()
          _tmpIsBatchLevel = _tmp_1 != 0
          val _tmpSourceBatchId: String?
          if (_stmt.isNull(_columnIndexOfSourceBatchId)) {
            _tmpSourceBatchId = null
          } else {
            _tmpSourceBatchId = _stmt.getText(_columnIndexOfSourceBatchId)
          }
          _item =
              GrowthRecordEntity(_tmpRecordId,_tmpProductId,_tmpFarmerId,_tmpWeek,_tmpWeightGrams,_tmpHeightCm,_tmpPhotoUrl,_tmpMediaItemsJson,_tmpHealthStatus,_tmpMilestone,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpCorrectionOf,_tmpEditCount,_tmpLastEditedBy,_tmpIsBatchLevel,_tmpSourceBatchId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countBetween(start: Long, end: Long): Int {
    val _sql: String = "SELECT COUNT(*) FROM growth_records WHERE createdAt BETWEEN ? AND ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, start)
        _argIndex = 2
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

  public override suspend fun countForFarmerBetween(
    farmerId: String,
    start: Long,
    end: Long,
  ): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM growth_records WHERE farmerId = ? AND createdAt BETWEEN ? AND ?"
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

  public override suspend fun getRecordsForFarmerBetween(
    farmerId: String,
    start: Long,
    end: Long,
  ): List<GrowthRecordEntity> {
    val _sql: String =
        "SELECT * FROM growth_records WHERE farmerId = ? AND createdAt BETWEEN ? AND ? ORDER BY createdAt ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, start)
        _argIndex = 3
        _stmt.bindLong(_argIndex, end)
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfWeek: Int = getColumnIndexOrThrow(_stmt, "week")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "photoUrl")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfMilestone: Int = getColumnIndexOrThrow(_stmt, "milestone")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfCorrectionOf: Int = getColumnIndexOrThrow(_stmt, "correctionOf")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfIsBatchLevel: Int = getColumnIndexOrThrow(_stmt, "isBatchLevel")
        val _columnIndexOfSourceBatchId: Int = getColumnIndexOrThrow(_stmt, "sourceBatchId")
        val _result: MutableList<GrowthRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: GrowthRecordEntity
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpWeek: Int
          _tmpWeek = _stmt.getLong(_columnIndexOfWeek).toInt()
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
          }
          val _tmpPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfPhotoUrl)) {
            _tmpPhotoUrl = null
          } else {
            _tmpPhotoUrl = _stmt.getText(_columnIndexOfPhotoUrl)
          }
          val _tmpMediaItemsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaItemsJson)) {
            _tmpMediaItemsJson = null
          } else {
            _tmpMediaItemsJson = _stmt.getText(_columnIndexOfMediaItemsJson)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpMilestone: String?
          if (_stmt.isNull(_columnIndexOfMilestone)) {
            _tmpMilestone = null
          } else {
            _tmpMilestone = _stmt.getText(_columnIndexOfMilestone)
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
          val _tmpCorrectionOf: String?
          if (_stmt.isNull(_columnIndexOfCorrectionOf)) {
            _tmpCorrectionOf = null
          } else {
            _tmpCorrectionOf = _stmt.getText(_columnIndexOfCorrectionOf)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpIsBatchLevel: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsBatchLevel).toInt()
          _tmpIsBatchLevel = _tmp_1 != 0
          val _tmpSourceBatchId: String?
          if (_stmt.isNull(_columnIndexOfSourceBatchId)) {
            _tmpSourceBatchId = null
          } else {
            _tmpSourceBatchId = _stmt.getText(_columnIndexOfSourceBatchId)
          }
          _item =
              GrowthRecordEntity(_tmpRecordId,_tmpProductId,_tmpFarmerId,_tmpWeek,_tmpWeightGrams,_tmpHeightCm,_tmpPhotoUrl,_tmpMediaItemsJson,_tmpHealthStatus,_tmpMilestone,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpCorrectionOf,_tmpEditCount,_tmpLastEditedBy,_tmpIsBatchLevel,_tmpSourceBatchId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeCountForFarmerBetween(
    farmerId: String,
    start: Long,
    end: Long,
  ): Flow<Int> {
    val _sql: String =
        "SELECT COUNT(*) FROM growth_records WHERE farmerId = ? AND createdAt BETWEEN ? AND ?"
    return createFlow(__db, false, arrayOf("growth_records")) { _connection ->
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

  public override suspend fun getAllByFarmer(farmerId: String): List<GrowthRecordEntity> {
    val _sql: String = "SELECT * FROM growth_records WHERE farmerId = ? ORDER BY week ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfWeek: Int = getColumnIndexOrThrow(_stmt, "week")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "photoUrl")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfMilestone: Int = getColumnIndexOrThrow(_stmt, "milestone")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfCorrectionOf: Int = getColumnIndexOrThrow(_stmt, "correctionOf")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfIsBatchLevel: Int = getColumnIndexOrThrow(_stmt, "isBatchLevel")
        val _columnIndexOfSourceBatchId: Int = getColumnIndexOrThrow(_stmt, "sourceBatchId")
        val _result: MutableList<GrowthRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: GrowthRecordEntity
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpWeek: Int
          _tmpWeek = _stmt.getLong(_columnIndexOfWeek).toInt()
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
          }
          val _tmpPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfPhotoUrl)) {
            _tmpPhotoUrl = null
          } else {
            _tmpPhotoUrl = _stmt.getText(_columnIndexOfPhotoUrl)
          }
          val _tmpMediaItemsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaItemsJson)) {
            _tmpMediaItemsJson = null
          } else {
            _tmpMediaItemsJson = _stmt.getText(_columnIndexOfMediaItemsJson)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpMilestone: String?
          if (_stmt.isNull(_columnIndexOfMilestone)) {
            _tmpMilestone = null
          } else {
            _tmpMilestone = _stmt.getText(_columnIndexOfMilestone)
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
          val _tmpCorrectionOf: String?
          if (_stmt.isNull(_columnIndexOfCorrectionOf)) {
            _tmpCorrectionOf = null
          } else {
            _tmpCorrectionOf = _stmt.getText(_columnIndexOfCorrectionOf)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpIsBatchLevel: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsBatchLevel).toInt()
          _tmpIsBatchLevel = _tmp_1 != 0
          val _tmpSourceBatchId: String?
          if (_stmt.isNull(_columnIndexOfSourceBatchId)) {
            _tmpSourceBatchId = null
          } else {
            _tmpSourceBatchId = _stmt.getText(_columnIndexOfSourceBatchId)
          }
          _item =
              GrowthRecordEntity(_tmpRecordId,_tmpProductId,_tmpFarmerId,_tmpWeek,_tmpWeightGrams,_tmpHeightCm,_tmpPhotoUrl,_tmpMediaItemsJson,_tmpHealthStatus,_tmpMilestone,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpCorrectionOf,_tmpEditCount,_tmpLastEditedBy,_tmpIsBatchLevel,_tmpSourceBatchId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeAllByFarmer(farmerId: String): Flow<List<GrowthRecordEntity>> {
    val _sql: String = "SELECT * FROM growth_records WHERE farmerId = ? ORDER BY week ASC"
    return createFlow(__db, false, arrayOf("growth_records")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfWeek: Int = getColumnIndexOrThrow(_stmt, "week")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "photoUrl")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfMilestone: Int = getColumnIndexOrThrow(_stmt, "milestone")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfCorrectionOf: Int = getColumnIndexOrThrow(_stmt, "correctionOf")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfIsBatchLevel: Int = getColumnIndexOrThrow(_stmt, "isBatchLevel")
        val _columnIndexOfSourceBatchId: Int = getColumnIndexOrThrow(_stmt, "sourceBatchId")
        val _result: MutableList<GrowthRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: GrowthRecordEntity
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpWeek: Int
          _tmpWeek = _stmt.getLong(_columnIndexOfWeek).toInt()
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
          }
          val _tmpPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfPhotoUrl)) {
            _tmpPhotoUrl = null
          } else {
            _tmpPhotoUrl = _stmt.getText(_columnIndexOfPhotoUrl)
          }
          val _tmpMediaItemsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaItemsJson)) {
            _tmpMediaItemsJson = null
          } else {
            _tmpMediaItemsJson = _stmt.getText(_columnIndexOfMediaItemsJson)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpMilestone: String?
          if (_stmt.isNull(_columnIndexOfMilestone)) {
            _tmpMilestone = null
          } else {
            _tmpMilestone = _stmt.getText(_columnIndexOfMilestone)
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
          val _tmpCorrectionOf: String?
          if (_stmt.isNull(_columnIndexOfCorrectionOf)) {
            _tmpCorrectionOf = null
          } else {
            _tmpCorrectionOf = _stmt.getText(_columnIndexOfCorrectionOf)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpIsBatchLevel: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsBatchLevel).toInt()
          _tmpIsBatchLevel = _tmp_1 != 0
          val _tmpSourceBatchId: String?
          if (_stmt.isNull(_columnIndexOfSourceBatchId)) {
            _tmpSourceBatchId = null
          } else {
            _tmpSourceBatchId = _stmt.getText(_columnIndexOfSourceBatchId)
          }
          _item =
              GrowthRecordEntity(_tmpRecordId,_tmpProductId,_tmpFarmerId,_tmpWeek,_tmpWeightGrams,_tmpHeightCm,_tmpPhotoUrl,_tmpMediaItemsJson,_tmpHealthStatus,_tmpMilestone,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpCorrectionOf,_tmpEditCount,_tmpLastEditedBy,_tmpIsBatchLevel,_tmpSourceBatchId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDirty(): List<GrowthRecordEntity> {
    val _sql: String = "SELECT * FROM growth_records WHERE dirty = 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfWeek: Int = getColumnIndexOrThrow(_stmt, "week")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "photoUrl")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfMilestone: Int = getColumnIndexOrThrow(_stmt, "milestone")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfCorrectionOf: Int = getColumnIndexOrThrow(_stmt, "correctionOf")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfIsBatchLevel: Int = getColumnIndexOrThrow(_stmt, "isBatchLevel")
        val _columnIndexOfSourceBatchId: Int = getColumnIndexOrThrow(_stmt, "sourceBatchId")
        val _result: MutableList<GrowthRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: GrowthRecordEntity
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpWeek: Int
          _tmpWeek = _stmt.getLong(_columnIndexOfWeek).toInt()
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
          }
          val _tmpPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfPhotoUrl)) {
            _tmpPhotoUrl = null
          } else {
            _tmpPhotoUrl = _stmt.getText(_columnIndexOfPhotoUrl)
          }
          val _tmpMediaItemsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaItemsJson)) {
            _tmpMediaItemsJson = null
          } else {
            _tmpMediaItemsJson = _stmt.getText(_columnIndexOfMediaItemsJson)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpMilestone: String?
          if (_stmt.isNull(_columnIndexOfMilestone)) {
            _tmpMilestone = null
          } else {
            _tmpMilestone = _stmt.getText(_columnIndexOfMilestone)
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
          val _tmpCorrectionOf: String?
          if (_stmt.isNull(_columnIndexOfCorrectionOf)) {
            _tmpCorrectionOf = null
          } else {
            _tmpCorrectionOf = _stmt.getText(_columnIndexOfCorrectionOf)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpIsBatchLevel: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsBatchLevel).toInt()
          _tmpIsBatchLevel = _tmp_1 != 0
          val _tmpSourceBatchId: String?
          if (_stmt.isNull(_columnIndexOfSourceBatchId)) {
            _tmpSourceBatchId = null
          } else {
            _tmpSourceBatchId = _stmt.getText(_columnIndexOfSourceBatchId)
          }
          _item =
              GrowthRecordEntity(_tmpRecordId,_tmpProductId,_tmpFarmerId,_tmpWeek,_tmpWeightGrams,_tmpHeightCm,_tmpPhotoUrl,_tmpMediaItemsJson,_tmpHealthStatus,_tmpMilestone,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpCorrectionOf,_tmpEditCount,_tmpLastEditedBy,_tmpIsBatchLevel,_tmpSourceBatchId)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countByFarmerInRange(
    farmerId: String,
    start: Long,
    end: Long,
  ): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM growth_records WHERE farmerId = ? AND createdAt BETWEEN ? AND ?"
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

  public override suspend fun getRecordCountForFarmer(farmerId: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM growth_records WHERE farmerId = ?"
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

  public override suspend fun getById(recordId: String): GrowthRecordEntity? {
    val _sql: String = "SELECT * FROM growth_records WHERE recordId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, recordId)
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfWeek: Int = getColumnIndexOrThrow(_stmt, "week")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfHeightCm: Int = getColumnIndexOrThrow(_stmt, "heightCm")
        val _columnIndexOfPhotoUrl: Int = getColumnIndexOrThrow(_stmt, "photoUrl")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfHealthStatus: Int = getColumnIndexOrThrow(_stmt, "healthStatus")
        val _columnIndexOfMilestone: Int = getColumnIndexOrThrow(_stmt, "milestone")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfCorrectionOf: Int = getColumnIndexOrThrow(_stmt, "correctionOf")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _columnIndexOfIsBatchLevel: Int = getColumnIndexOrThrow(_stmt, "isBatchLevel")
        val _columnIndexOfSourceBatchId: Int = getColumnIndexOrThrow(_stmt, "sourceBatchId")
        val _result: GrowthRecordEntity?
        if (_stmt.step()) {
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpWeek: Int
          _tmpWeek = _stmt.getLong(_columnIndexOfWeek).toInt()
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpHeightCm: Double?
          if (_stmt.isNull(_columnIndexOfHeightCm)) {
            _tmpHeightCm = null
          } else {
            _tmpHeightCm = _stmt.getDouble(_columnIndexOfHeightCm)
          }
          val _tmpPhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfPhotoUrl)) {
            _tmpPhotoUrl = null
          } else {
            _tmpPhotoUrl = _stmt.getText(_columnIndexOfPhotoUrl)
          }
          val _tmpMediaItemsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaItemsJson)) {
            _tmpMediaItemsJson = null
          } else {
            _tmpMediaItemsJson = _stmt.getText(_columnIndexOfMediaItemsJson)
          }
          val _tmpHealthStatus: String?
          if (_stmt.isNull(_columnIndexOfHealthStatus)) {
            _tmpHealthStatus = null
          } else {
            _tmpHealthStatus = _stmt.getText(_columnIndexOfHealthStatus)
          }
          val _tmpMilestone: String?
          if (_stmt.isNull(_columnIndexOfMilestone)) {
            _tmpMilestone = null
          } else {
            _tmpMilestone = _stmt.getText(_columnIndexOfMilestone)
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
          val _tmpCorrectionOf: String?
          if (_stmt.isNull(_columnIndexOfCorrectionOf)) {
            _tmpCorrectionOf = null
          } else {
            _tmpCorrectionOf = _stmt.getText(_columnIndexOfCorrectionOf)
          }
          val _tmpEditCount: Int
          _tmpEditCount = _stmt.getLong(_columnIndexOfEditCount).toInt()
          val _tmpLastEditedBy: String?
          if (_stmt.isNull(_columnIndexOfLastEditedBy)) {
            _tmpLastEditedBy = null
          } else {
            _tmpLastEditedBy = _stmt.getText(_columnIndexOfLastEditedBy)
          }
          val _tmpIsBatchLevel: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfIsBatchLevel).toInt()
          _tmpIsBatchLevel = _tmp_1 != 0
          val _tmpSourceBatchId: String?
          if (_stmt.isNull(_columnIndexOfSourceBatchId)) {
            _tmpSourceBatchId = null
          } else {
            _tmpSourceBatchId = _stmt.getText(_columnIndexOfSourceBatchId)
          }
          _result =
              GrowthRecordEntity(_tmpRecordId,_tmpProductId,_tmpFarmerId,_tmpWeek,_tmpWeightGrams,_tmpHeightCm,_tmpPhotoUrl,_tmpMediaItemsJson,_tmpHealthStatus,_tmpMilestone,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpCorrectionOf,_tmpEditCount,_tmpLastEditedBy,_tmpIsBatchLevel,_tmpSourceBatchId)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearDirty(recordIds: List<String>, syncedAt: Long) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("UPDATE growth_records SET dirty = 0, syncedAt = ")
    _stringBuilder.append("?")
    _stringBuilder.append(" WHERE recordId IN (")
    val _inputSize: Int = recordIds.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, syncedAt)
        _argIndex = 2
        for (_item: String in recordIds) {
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
