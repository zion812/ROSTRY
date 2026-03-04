package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.AssetHealthRecordEntity
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

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class AssetHealthRecordDao_Impl(
  __db: RoomDatabase,
) : AssetHealthRecordDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfAssetHealthRecordEntity: EntityInsertAdapter<AssetHealthRecordEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfAssetHealthRecordEntity = object :
        EntityInsertAdapter<AssetHealthRecordEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `asset_health_records` (`recordId`,`assetId`,`farmerId`,`recordType`,`recordData`,`healthScore`,`veterinarianId`,`veterinarianNotes`,`followUpRequired`,`followUpDate`,`costInr`,`mediaItemsJson`,`createdAt`,`updatedAt`,`dirty`,`syncedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: AssetHealthRecordEntity) {
        statement.bindText(1, entity.recordId)
        statement.bindText(2, entity.assetId)
        statement.bindText(3, entity.farmerId)
        statement.bindText(4, entity.recordType)
        statement.bindText(5, entity.recordData)
        statement.bindLong(6, entity.healthScore.toLong())
        val _tmpVeterinarianId: String? = entity.veterinarianId
        if (_tmpVeterinarianId == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpVeterinarianId)
        }
        val _tmpVeterinarianNotes: String? = entity.veterinarianNotes
        if (_tmpVeterinarianNotes == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpVeterinarianNotes)
        }
        val _tmp: Int = if (entity.followUpRequired) 1 else 0
        statement.bindLong(9, _tmp.toLong())
        val _tmpFollowUpDate: Long? = entity.followUpDate
        if (_tmpFollowUpDate == null) {
          statement.bindNull(10)
        } else {
          statement.bindLong(10, _tmpFollowUpDate)
        }
        val _tmpCostInr: Double? = entity.costInr
        if (_tmpCostInr == null) {
          statement.bindNull(11)
        } else {
          statement.bindDouble(11, _tmpCostInr)
        }
        val _tmpMediaItemsJson: String? = entity.mediaItemsJson
        if (_tmpMediaItemsJson == null) {
          statement.bindNull(12)
        } else {
          statement.bindText(12, _tmpMediaItemsJson)
        }
        statement.bindLong(13, entity.createdAt)
        statement.bindLong(14, entity.updatedAt)
        val _tmp_1: Int = if (entity.dirty) 1 else 0
        statement.bindLong(15, _tmp_1.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(16)
        } else {
          statement.bindLong(16, _tmpSyncedAt)
        }
      }
    }
  }

  public override suspend fun insert(record: AssetHealthRecordEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfAssetHealthRecordEntity.insert(_connection, record)
  }

  public override suspend fun insertAll(records: List<AssetHealthRecordEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfAssetHealthRecordEntity.insert(_connection, records)
  }

  public override suspend fun getRecordsForAsset(assetId: String): List<AssetHealthRecordEntity> {
    val _sql: String =
        "SELECT * FROM asset_health_records WHERE assetId = ? ORDER BY createdAt DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, assetId)
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfRecordType: Int = getColumnIndexOrThrow(_stmt, "recordType")
        val _columnIndexOfRecordData: Int = getColumnIndexOrThrow(_stmt, "recordData")
        val _columnIndexOfHealthScore: Int = getColumnIndexOrThrow(_stmt, "healthScore")
        val _columnIndexOfVeterinarianId: Int = getColumnIndexOrThrow(_stmt, "veterinarianId")
        val _columnIndexOfVeterinarianNotes: Int = getColumnIndexOrThrow(_stmt, "veterinarianNotes")
        val _columnIndexOfFollowUpRequired: Int = getColumnIndexOrThrow(_stmt, "followUpRequired")
        val _columnIndexOfFollowUpDate: Int = getColumnIndexOrThrow(_stmt, "followUpDate")
        val _columnIndexOfCostInr: Int = getColumnIndexOrThrow(_stmt, "costInr")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<AssetHealthRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AssetHealthRecordEntity
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpAssetId: String
          _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpRecordType: String
          _tmpRecordType = _stmt.getText(_columnIndexOfRecordType)
          val _tmpRecordData: String
          _tmpRecordData = _stmt.getText(_columnIndexOfRecordData)
          val _tmpHealthScore: Int
          _tmpHealthScore = _stmt.getLong(_columnIndexOfHealthScore).toInt()
          val _tmpVeterinarianId: String?
          if (_stmt.isNull(_columnIndexOfVeterinarianId)) {
            _tmpVeterinarianId = null
          } else {
            _tmpVeterinarianId = _stmt.getText(_columnIndexOfVeterinarianId)
          }
          val _tmpVeterinarianNotes: String?
          if (_stmt.isNull(_columnIndexOfVeterinarianNotes)) {
            _tmpVeterinarianNotes = null
          } else {
            _tmpVeterinarianNotes = _stmt.getText(_columnIndexOfVeterinarianNotes)
          }
          val _tmpFollowUpRequired: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfFollowUpRequired).toInt()
          _tmpFollowUpRequired = _tmp != 0
          val _tmpFollowUpDate: Long?
          if (_stmt.isNull(_columnIndexOfFollowUpDate)) {
            _tmpFollowUpDate = null
          } else {
            _tmpFollowUpDate = _stmt.getLong(_columnIndexOfFollowUpDate)
          }
          val _tmpCostInr: Double?
          if (_stmt.isNull(_columnIndexOfCostInr)) {
            _tmpCostInr = null
          } else {
            _tmpCostInr = _stmt.getDouble(_columnIndexOfCostInr)
          }
          val _tmpMediaItemsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaItemsJson)) {
            _tmpMediaItemsJson = null
          } else {
            _tmpMediaItemsJson = _stmt.getText(_columnIndexOfMediaItemsJson)
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
              AssetHealthRecordEntity(_tmpRecordId,_tmpAssetId,_tmpFarmerId,_tmpRecordType,_tmpRecordData,_tmpHealthScore,_tmpVeterinarianId,_tmpVeterinarianNotes,_tmpFollowUpRequired,_tmpFollowUpDate,_tmpCostInr,_tmpMediaItemsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getRecordById(recordId: String): AssetHealthRecordEntity? {
    val _sql: String = "SELECT * FROM asset_health_records WHERE recordId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, recordId)
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfRecordType: Int = getColumnIndexOrThrow(_stmt, "recordType")
        val _columnIndexOfRecordData: Int = getColumnIndexOrThrow(_stmt, "recordData")
        val _columnIndexOfHealthScore: Int = getColumnIndexOrThrow(_stmt, "healthScore")
        val _columnIndexOfVeterinarianId: Int = getColumnIndexOrThrow(_stmt, "veterinarianId")
        val _columnIndexOfVeterinarianNotes: Int = getColumnIndexOrThrow(_stmt, "veterinarianNotes")
        val _columnIndexOfFollowUpRequired: Int = getColumnIndexOrThrow(_stmt, "followUpRequired")
        val _columnIndexOfFollowUpDate: Int = getColumnIndexOrThrow(_stmt, "followUpDate")
        val _columnIndexOfCostInr: Int = getColumnIndexOrThrow(_stmt, "costInr")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: AssetHealthRecordEntity?
        if (_stmt.step()) {
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpAssetId: String
          _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpRecordType: String
          _tmpRecordType = _stmt.getText(_columnIndexOfRecordType)
          val _tmpRecordData: String
          _tmpRecordData = _stmt.getText(_columnIndexOfRecordData)
          val _tmpHealthScore: Int
          _tmpHealthScore = _stmt.getLong(_columnIndexOfHealthScore).toInt()
          val _tmpVeterinarianId: String?
          if (_stmt.isNull(_columnIndexOfVeterinarianId)) {
            _tmpVeterinarianId = null
          } else {
            _tmpVeterinarianId = _stmt.getText(_columnIndexOfVeterinarianId)
          }
          val _tmpVeterinarianNotes: String?
          if (_stmt.isNull(_columnIndexOfVeterinarianNotes)) {
            _tmpVeterinarianNotes = null
          } else {
            _tmpVeterinarianNotes = _stmt.getText(_columnIndexOfVeterinarianNotes)
          }
          val _tmpFollowUpRequired: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfFollowUpRequired).toInt()
          _tmpFollowUpRequired = _tmp != 0
          val _tmpFollowUpDate: Long?
          if (_stmt.isNull(_columnIndexOfFollowUpDate)) {
            _tmpFollowUpDate = null
          } else {
            _tmpFollowUpDate = _stmt.getLong(_columnIndexOfFollowUpDate)
          }
          val _tmpCostInr: Double?
          if (_stmt.isNull(_columnIndexOfCostInr)) {
            _tmpCostInr = null
          } else {
            _tmpCostInr = _stmt.getDouble(_columnIndexOfCostInr)
          }
          val _tmpMediaItemsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaItemsJson)) {
            _tmpMediaItemsJson = null
          } else {
            _tmpMediaItemsJson = _stmt.getText(_columnIndexOfMediaItemsJson)
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
              AssetHealthRecordEntity(_tmpRecordId,_tmpAssetId,_tmpFarmerId,_tmpRecordType,_tmpRecordData,_tmpHealthScore,_tmpVeterinarianId,_tmpVeterinarianNotes,_tmpFollowUpRequired,_tmpFollowUpDate,_tmpCostInr,_tmpMediaItemsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
        } else {
          _result = null
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
