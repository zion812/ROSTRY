package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.DailyLogEntity
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
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class DailyLogDao_Impl(
  __db: RoomDatabase,
) : DailyLogDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfDailyLogEntity: EntityInsertAdapter<DailyLogEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfDailyLogEntity = object : EntityInsertAdapter<DailyLogEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `daily_logs` (`logId`,`productId`,`farmerId`,`logDate`,`weightGrams`,`feedKg`,`medicationJson`,`symptomsJson`,`activityLevel`,`photoUrls`,`notes`,`temperature`,`humidity`,`createdAt`,`updatedAt`,`dirty`,`syncedAt`,`deviceTimestamp`,`author`,`mergedAt`,`mergeCount`,`conflictResolved`,`mediaItemsJson`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: DailyLogEntity) {
        statement.bindText(1, entity.logId)
        statement.bindText(2, entity.productId)
        statement.bindText(3, entity.farmerId)
        statement.bindLong(4, entity.logDate)
        val _tmpWeightGrams: Double? = entity.weightGrams
        if (_tmpWeightGrams == null) {
          statement.bindNull(5)
        } else {
          statement.bindDouble(5, _tmpWeightGrams)
        }
        val _tmpFeedKg: Double? = entity.feedKg
        if (_tmpFeedKg == null) {
          statement.bindNull(6)
        } else {
          statement.bindDouble(6, _tmpFeedKg)
        }
        val _tmpMedicationJson: String? = entity.medicationJson
        if (_tmpMedicationJson == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpMedicationJson)
        }
        val _tmpSymptomsJson: String? = entity.symptomsJson
        if (_tmpSymptomsJson == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpSymptomsJson)
        }
        val _tmpActivityLevel: String? = entity.activityLevel
        if (_tmpActivityLevel == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpActivityLevel)
        }
        val _tmpPhotoUrls: String? = entity.photoUrls
        if (_tmpPhotoUrls == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpPhotoUrls)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpNotes)
        }
        val _tmpTemperature: Double? = entity.temperature
        if (_tmpTemperature == null) {
          statement.bindNull(12)
        } else {
          statement.bindDouble(12, _tmpTemperature)
        }
        val _tmpHumidity: Double? = entity.humidity
        if (_tmpHumidity == null) {
          statement.bindNull(13)
        } else {
          statement.bindDouble(13, _tmpHumidity)
        }
        statement.bindLong(14, entity.createdAt)
        statement.bindLong(15, entity.updatedAt)
        val _tmp: Int = if (entity.dirty) 1 else 0
        statement.bindLong(16, _tmp.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(17)
        } else {
          statement.bindLong(17, _tmpSyncedAt)
        }
        statement.bindLong(18, entity.deviceTimestamp)
        val _tmpAuthor: String? = entity.author
        if (_tmpAuthor == null) {
          statement.bindNull(19)
        } else {
          statement.bindText(19, _tmpAuthor)
        }
        val _tmpMergedAt: Long? = entity.mergedAt
        if (_tmpMergedAt == null) {
          statement.bindNull(20)
        } else {
          statement.bindLong(20, _tmpMergedAt)
        }
        statement.bindLong(21, entity.mergeCount.toLong())
        val _tmp_1: Int = if (entity.conflictResolved) 1 else 0
        statement.bindLong(22, _tmp_1.toLong())
        val _tmpMediaItemsJson: String? = entity.mediaItemsJson
        if (_tmpMediaItemsJson == null) {
          statement.bindNull(23)
        } else {
          statement.bindText(23, _tmpMediaItemsJson)
        }
      }
    }
  }

  public override suspend fun upsert(log: DailyLogEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfDailyLogEntity.insert(_connection, log)
  }

  public override fun observeForProduct(productId: String): Flow<List<DailyLogEntity>> {
    val _sql: String = "SELECT * FROM daily_logs WHERE productId = ? ORDER BY logDate DESC"
    return createFlow(__db, false, arrayOf("daily_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfLogId: Int = getColumnIndexOrThrow(_stmt, "logId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfLogDate: Int = getColumnIndexOrThrow(_stmt, "logDate")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfFeedKg: Int = getColumnIndexOrThrow(_stmt, "feedKg")
        val _columnIndexOfMedicationJson: Int = getColumnIndexOrThrow(_stmt, "medicationJson")
        val _columnIndexOfSymptomsJson: Int = getColumnIndexOrThrow(_stmt, "symptomsJson")
        val _columnIndexOfActivityLevel: Int = getColumnIndexOrThrow(_stmt, "activityLevel")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfTemperature: Int = getColumnIndexOrThrow(_stmt, "temperature")
        val _columnIndexOfHumidity: Int = getColumnIndexOrThrow(_stmt, "humidity")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfDeviceTimestamp: Int = getColumnIndexOrThrow(_stmt, "deviceTimestamp")
        val _columnIndexOfAuthor: Int = getColumnIndexOrThrow(_stmt, "author")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _columnIndexOfConflictResolved: Int = getColumnIndexOrThrow(_stmt, "conflictResolved")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _result: MutableList<DailyLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DailyLogEntity
          val _tmpLogId: String
          _tmpLogId = _stmt.getText(_columnIndexOfLogId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpLogDate: Long
          _tmpLogDate = _stmt.getLong(_columnIndexOfLogDate)
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpFeedKg: Double?
          if (_stmt.isNull(_columnIndexOfFeedKg)) {
            _tmpFeedKg = null
          } else {
            _tmpFeedKg = _stmt.getDouble(_columnIndexOfFeedKg)
          }
          val _tmpMedicationJson: String?
          if (_stmt.isNull(_columnIndexOfMedicationJson)) {
            _tmpMedicationJson = null
          } else {
            _tmpMedicationJson = _stmt.getText(_columnIndexOfMedicationJson)
          }
          val _tmpSymptomsJson: String?
          if (_stmt.isNull(_columnIndexOfSymptomsJson)) {
            _tmpSymptomsJson = null
          } else {
            _tmpSymptomsJson = _stmt.getText(_columnIndexOfSymptomsJson)
          }
          val _tmpActivityLevel: String?
          if (_stmt.isNull(_columnIndexOfActivityLevel)) {
            _tmpActivityLevel = null
          } else {
            _tmpActivityLevel = _stmt.getText(_columnIndexOfActivityLevel)
          }
          val _tmpPhotoUrls: String?
          if (_stmt.isNull(_columnIndexOfPhotoUrls)) {
            _tmpPhotoUrls = null
          } else {
            _tmpPhotoUrls = _stmt.getText(_columnIndexOfPhotoUrls)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpTemperature: Double?
          if (_stmt.isNull(_columnIndexOfTemperature)) {
            _tmpTemperature = null
          } else {
            _tmpTemperature = _stmt.getDouble(_columnIndexOfTemperature)
          }
          val _tmpHumidity: Double?
          if (_stmt.isNull(_columnIndexOfHumidity)) {
            _tmpHumidity = null
          } else {
            _tmpHumidity = _stmt.getDouble(_columnIndexOfHumidity)
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
          val _tmpDeviceTimestamp: Long
          _tmpDeviceTimestamp = _stmt.getLong(_columnIndexOfDeviceTimestamp)
          val _tmpAuthor: String?
          if (_stmt.isNull(_columnIndexOfAuthor)) {
            _tmpAuthor = null
          } else {
            _tmpAuthor = _stmt.getText(_columnIndexOfAuthor)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          val _tmpConflictResolved: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfConflictResolved).toInt()
          _tmpConflictResolved = _tmp_1 != 0
          val _tmpMediaItemsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaItemsJson)) {
            _tmpMediaItemsJson = null
          } else {
            _tmpMediaItemsJson = _stmt.getText(_columnIndexOfMediaItemsJson)
          }
          _item =
              DailyLogEntity(_tmpLogId,_tmpProductId,_tmpFarmerId,_tmpLogDate,_tmpWeightGrams,_tmpFeedKg,_tmpMedicationJson,_tmpSymptomsJson,_tmpActivityLevel,_tmpPhotoUrls,_tmpNotes,_tmpTemperature,_tmpHumidity,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpDeviceTimestamp,_tmpAuthor,_tmpMergedAt,_tmpMergeCount,_tmpConflictResolved,_tmpMediaItemsJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeForFarmerBetween(
    farmerId: String,
    start: Long,
    end: Long,
  ): Flow<List<DailyLogEntity>> {
    val _sql: String =
        "SELECT * FROM daily_logs WHERE farmerId = ? AND logDate BETWEEN ? AND ? ORDER BY logDate DESC"
    return createFlow(__db, false, arrayOf("daily_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, start)
        _argIndex = 3
        _stmt.bindLong(_argIndex, end)
        val _columnIndexOfLogId: Int = getColumnIndexOrThrow(_stmt, "logId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfLogDate: Int = getColumnIndexOrThrow(_stmt, "logDate")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfFeedKg: Int = getColumnIndexOrThrow(_stmt, "feedKg")
        val _columnIndexOfMedicationJson: Int = getColumnIndexOrThrow(_stmt, "medicationJson")
        val _columnIndexOfSymptomsJson: Int = getColumnIndexOrThrow(_stmt, "symptomsJson")
        val _columnIndexOfActivityLevel: Int = getColumnIndexOrThrow(_stmt, "activityLevel")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfTemperature: Int = getColumnIndexOrThrow(_stmt, "temperature")
        val _columnIndexOfHumidity: Int = getColumnIndexOrThrow(_stmt, "humidity")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfDeviceTimestamp: Int = getColumnIndexOrThrow(_stmt, "deviceTimestamp")
        val _columnIndexOfAuthor: Int = getColumnIndexOrThrow(_stmt, "author")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _columnIndexOfConflictResolved: Int = getColumnIndexOrThrow(_stmt, "conflictResolved")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _result: MutableList<DailyLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DailyLogEntity
          val _tmpLogId: String
          _tmpLogId = _stmt.getText(_columnIndexOfLogId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpLogDate: Long
          _tmpLogDate = _stmt.getLong(_columnIndexOfLogDate)
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpFeedKg: Double?
          if (_stmt.isNull(_columnIndexOfFeedKg)) {
            _tmpFeedKg = null
          } else {
            _tmpFeedKg = _stmt.getDouble(_columnIndexOfFeedKg)
          }
          val _tmpMedicationJson: String?
          if (_stmt.isNull(_columnIndexOfMedicationJson)) {
            _tmpMedicationJson = null
          } else {
            _tmpMedicationJson = _stmt.getText(_columnIndexOfMedicationJson)
          }
          val _tmpSymptomsJson: String?
          if (_stmt.isNull(_columnIndexOfSymptomsJson)) {
            _tmpSymptomsJson = null
          } else {
            _tmpSymptomsJson = _stmt.getText(_columnIndexOfSymptomsJson)
          }
          val _tmpActivityLevel: String?
          if (_stmt.isNull(_columnIndexOfActivityLevel)) {
            _tmpActivityLevel = null
          } else {
            _tmpActivityLevel = _stmt.getText(_columnIndexOfActivityLevel)
          }
          val _tmpPhotoUrls: String?
          if (_stmt.isNull(_columnIndexOfPhotoUrls)) {
            _tmpPhotoUrls = null
          } else {
            _tmpPhotoUrls = _stmt.getText(_columnIndexOfPhotoUrls)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpTemperature: Double?
          if (_stmt.isNull(_columnIndexOfTemperature)) {
            _tmpTemperature = null
          } else {
            _tmpTemperature = _stmt.getDouble(_columnIndexOfTemperature)
          }
          val _tmpHumidity: Double?
          if (_stmt.isNull(_columnIndexOfHumidity)) {
            _tmpHumidity = null
          } else {
            _tmpHumidity = _stmt.getDouble(_columnIndexOfHumidity)
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
          val _tmpDeviceTimestamp: Long
          _tmpDeviceTimestamp = _stmt.getLong(_columnIndexOfDeviceTimestamp)
          val _tmpAuthor: String?
          if (_stmt.isNull(_columnIndexOfAuthor)) {
            _tmpAuthor = null
          } else {
            _tmpAuthor = _stmt.getText(_columnIndexOfAuthor)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          val _tmpConflictResolved: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfConflictResolved).toInt()
          _tmpConflictResolved = _tmp_1 != 0
          val _tmpMediaItemsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaItemsJson)) {
            _tmpMediaItemsJson = null
          } else {
            _tmpMediaItemsJson = _stmt.getText(_columnIndexOfMediaItemsJson)
          }
          _item =
              DailyLogEntity(_tmpLogId,_tmpProductId,_tmpFarmerId,_tmpLogDate,_tmpWeightGrams,_tmpFeedKg,_tmpMedicationJson,_tmpSymptomsJson,_tmpActivityLevel,_tmpPhotoUrls,_tmpNotes,_tmpTemperature,_tmpHumidity,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpDeviceTimestamp,_tmpAuthor,_tmpMergedAt,_tmpMergeCount,_tmpConflictResolved,_tmpMediaItemsJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByFarmerAndDate(farmerId: String, date: Long):
      List<DailyLogEntity> {
    val _sql: String = "SELECT * FROM daily_logs WHERE farmerId = ? AND logDate = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, date)
        val _columnIndexOfLogId: Int = getColumnIndexOrThrow(_stmt, "logId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfLogDate: Int = getColumnIndexOrThrow(_stmt, "logDate")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfFeedKg: Int = getColumnIndexOrThrow(_stmt, "feedKg")
        val _columnIndexOfMedicationJson: Int = getColumnIndexOrThrow(_stmt, "medicationJson")
        val _columnIndexOfSymptomsJson: Int = getColumnIndexOrThrow(_stmt, "symptomsJson")
        val _columnIndexOfActivityLevel: Int = getColumnIndexOrThrow(_stmt, "activityLevel")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfTemperature: Int = getColumnIndexOrThrow(_stmt, "temperature")
        val _columnIndexOfHumidity: Int = getColumnIndexOrThrow(_stmt, "humidity")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfDeviceTimestamp: Int = getColumnIndexOrThrow(_stmt, "deviceTimestamp")
        val _columnIndexOfAuthor: Int = getColumnIndexOrThrow(_stmt, "author")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _columnIndexOfConflictResolved: Int = getColumnIndexOrThrow(_stmt, "conflictResolved")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _result: MutableList<DailyLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DailyLogEntity
          val _tmpLogId: String
          _tmpLogId = _stmt.getText(_columnIndexOfLogId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpLogDate: Long
          _tmpLogDate = _stmt.getLong(_columnIndexOfLogDate)
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpFeedKg: Double?
          if (_stmt.isNull(_columnIndexOfFeedKg)) {
            _tmpFeedKg = null
          } else {
            _tmpFeedKg = _stmt.getDouble(_columnIndexOfFeedKg)
          }
          val _tmpMedicationJson: String?
          if (_stmt.isNull(_columnIndexOfMedicationJson)) {
            _tmpMedicationJson = null
          } else {
            _tmpMedicationJson = _stmt.getText(_columnIndexOfMedicationJson)
          }
          val _tmpSymptomsJson: String?
          if (_stmt.isNull(_columnIndexOfSymptomsJson)) {
            _tmpSymptomsJson = null
          } else {
            _tmpSymptomsJson = _stmt.getText(_columnIndexOfSymptomsJson)
          }
          val _tmpActivityLevel: String?
          if (_stmt.isNull(_columnIndexOfActivityLevel)) {
            _tmpActivityLevel = null
          } else {
            _tmpActivityLevel = _stmt.getText(_columnIndexOfActivityLevel)
          }
          val _tmpPhotoUrls: String?
          if (_stmt.isNull(_columnIndexOfPhotoUrls)) {
            _tmpPhotoUrls = null
          } else {
            _tmpPhotoUrls = _stmt.getText(_columnIndexOfPhotoUrls)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpTemperature: Double?
          if (_stmt.isNull(_columnIndexOfTemperature)) {
            _tmpTemperature = null
          } else {
            _tmpTemperature = _stmt.getDouble(_columnIndexOfTemperature)
          }
          val _tmpHumidity: Double?
          if (_stmt.isNull(_columnIndexOfHumidity)) {
            _tmpHumidity = null
          } else {
            _tmpHumidity = _stmt.getDouble(_columnIndexOfHumidity)
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
          val _tmpDeviceTimestamp: Long
          _tmpDeviceTimestamp = _stmt.getLong(_columnIndexOfDeviceTimestamp)
          val _tmpAuthor: String?
          if (_stmt.isNull(_columnIndexOfAuthor)) {
            _tmpAuthor = null
          } else {
            _tmpAuthor = _stmt.getText(_columnIndexOfAuthor)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          val _tmpConflictResolved: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfConflictResolved).toInt()
          _tmpConflictResolved = _tmp_1 != 0
          val _tmpMediaItemsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaItemsJson)) {
            _tmpMediaItemsJson = null
          } else {
            _tmpMediaItemsJson = _stmt.getText(_columnIndexOfMediaItemsJson)
          }
          _item =
              DailyLogEntity(_tmpLogId,_tmpProductId,_tmpFarmerId,_tmpLogDate,_tmpWeightGrams,_tmpFeedKg,_tmpMedicationJson,_tmpSymptomsJson,_tmpActivityLevel,_tmpPhotoUrls,_tmpNotes,_tmpTemperature,_tmpHumidity,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpDeviceTimestamp,_tmpAuthor,_tmpMergedAt,_tmpMergeCount,_tmpConflictResolved,_tmpMediaItemsJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByProductAndDate(productId: String, date: Long): DailyLogEntity? {
    val _sql: String = "SELECT * FROM daily_logs WHERE productId = ? AND logDate = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, date)
        val _columnIndexOfLogId: Int = getColumnIndexOrThrow(_stmt, "logId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfLogDate: Int = getColumnIndexOrThrow(_stmt, "logDate")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfFeedKg: Int = getColumnIndexOrThrow(_stmt, "feedKg")
        val _columnIndexOfMedicationJson: Int = getColumnIndexOrThrow(_stmt, "medicationJson")
        val _columnIndexOfSymptomsJson: Int = getColumnIndexOrThrow(_stmt, "symptomsJson")
        val _columnIndexOfActivityLevel: Int = getColumnIndexOrThrow(_stmt, "activityLevel")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfTemperature: Int = getColumnIndexOrThrow(_stmt, "temperature")
        val _columnIndexOfHumidity: Int = getColumnIndexOrThrow(_stmt, "humidity")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfDeviceTimestamp: Int = getColumnIndexOrThrow(_stmt, "deviceTimestamp")
        val _columnIndexOfAuthor: Int = getColumnIndexOrThrow(_stmt, "author")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _columnIndexOfConflictResolved: Int = getColumnIndexOrThrow(_stmt, "conflictResolved")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _result: DailyLogEntity?
        if (_stmt.step()) {
          val _tmpLogId: String
          _tmpLogId = _stmt.getText(_columnIndexOfLogId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpLogDate: Long
          _tmpLogDate = _stmt.getLong(_columnIndexOfLogDate)
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpFeedKg: Double?
          if (_stmt.isNull(_columnIndexOfFeedKg)) {
            _tmpFeedKg = null
          } else {
            _tmpFeedKg = _stmt.getDouble(_columnIndexOfFeedKg)
          }
          val _tmpMedicationJson: String?
          if (_stmt.isNull(_columnIndexOfMedicationJson)) {
            _tmpMedicationJson = null
          } else {
            _tmpMedicationJson = _stmt.getText(_columnIndexOfMedicationJson)
          }
          val _tmpSymptomsJson: String?
          if (_stmt.isNull(_columnIndexOfSymptomsJson)) {
            _tmpSymptomsJson = null
          } else {
            _tmpSymptomsJson = _stmt.getText(_columnIndexOfSymptomsJson)
          }
          val _tmpActivityLevel: String?
          if (_stmt.isNull(_columnIndexOfActivityLevel)) {
            _tmpActivityLevel = null
          } else {
            _tmpActivityLevel = _stmt.getText(_columnIndexOfActivityLevel)
          }
          val _tmpPhotoUrls: String?
          if (_stmt.isNull(_columnIndexOfPhotoUrls)) {
            _tmpPhotoUrls = null
          } else {
            _tmpPhotoUrls = _stmt.getText(_columnIndexOfPhotoUrls)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpTemperature: Double?
          if (_stmt.isNull(_columnIndexOfTemperature)) {
            _tmpTemperature = null
          } else {
            _tmpTemperature = _stmt.getDouble(_columnIndexOfTemperature)
          }
          val _tmpHumidity: Double?
          if (_stmt.isNull(_columnIndexOfHumidity)) {
            _tmpHumidity = null
          } else {
            _tmpHumidity = _stmt.getDouble(_columnIndexOfHumidity)
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
          val _tmpDeviceTimestamp: Long
          _tmpDeviceTimestamp = _stmt.getLong(_columnIndexOfDeviceTimestamp)
          val _tmpAuthor: String?
          if (_stmt.isNull(_columnIndexOfAuthor)) {
            _tmpAuthor = null
          } else {
            _tmpAuthor = _stmt.getText(_columnIndexOfAuthor)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          val _tmpConflictResolved: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfConflictResolved).toInt()
          _tmpConflictResolved = _tmp_1 != 0
          val _tmpMediaItemsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaItemsJson)) {
            _tmpMediaItemsJson = null
          } else {
            _tmpMediaItemsJson = _stmt.getText(_columnIndexOfMediaItemsJson)
          }
          _result =
              DailyLogEntity(_tmpLogId,_tmpProductId,_tmpFarmerId,_tmpLogDate,_tmpWeightGrams,_tmpFeedKg,_tmpMedicationJson,_tmpSymptomsJson,_tmpActivityLevel,_tmpPhotoUrls,_tmpNotes,_tmpTemperature,_tmpHumidity,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpDeviceTimestamp,_tmpAuthor,_tmpMergedAt,_tmpMergeCount,_tmpConflictResolved,_tmpMediaItemsJson)
        } else {
          _result = null
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
        "SELECT COUNT(*) FROM daily_logs WHERE farmerId = ? AND logDate BETWEEN ? AND ?"
    return createFlow(__db, false, arrayOf("daily_logs")) { _connection ->
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

  public override suspend fun getById(logId: String): DailyLogEntity? {
    val _sql: String = "SELECT * FROM daily_logs WHERE logId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, logId)
        val _columnIndexOfLogId: Int = getColumnIndexOrThrow(_stmt, "logId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfLogDate: Int = getColumnIndexOrThrow(_stmt, "logDate")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfFeedKg: Int = getColumnIndexOrThrow(_stmt, "feedKg")
        val _columnIndexOfMedicationJson: Int = getColumnIndexOrThrow(_stmt, "medicationJson")
        val _columnIndexOfSymptomsJson: Int = getColumnIndexOrThrow(_stmt, "symptomsJson")
        val _columnIndexOfActivityLevel: Int = getColumnIndexOrThrow(_stmt, "activityLevel")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfTemperature: Int = getColumnIndexOrThrow(_stmt, "temperature")
        val _columnIndexOfHumidity: Int = getColumnIndexOrThrow(_stmt, "humidity")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfDeviceTimestamp: Int = getColumnIndexOrThrow(_stmt, "deviceTimestamp")
        val _columnIndexOfAuthor: Int = getColumnIndexOrThrow(_stmt, "author")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _columnIndexOfConflictResolved: Int = getColumnIndexOrThrow(_stmt, "conflictResolved")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _result: DailyLogEntity?
        if (_stmt.step()) {
          val _tmpLogId: String
          _tmpLogId = _stmt.getText(_columnIndexOfLogId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpLogDate: Long
          _tmpLogDate = _stmt.getLong(_columnIndexOfLogDate)
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpFeedKg: Double?
          if (_stmt.isNull(_columnIndexOfFeedKg)) {
            _tmpFeedKg = null
          } else {
            _tmpFeedKg = _stmt.getDouble(_columnIndexOfFeedKg)
          }
          val _tmpMedicationJson: String?
          if (_stmt.isNull(_columnIndexOfMedicationJson)) {
            _tmpMedicationJson = null
          } else {
            _tmpMedicationJson = _stmt.getText(_columnIndexOfMedicationJson)
          }
          val _tmpSymptomsJson: String?
          if (_stmt.isNull(_columnIndexOfSymptomsJson)) {
            _tmpSymptomsJson = null
          } else {
            _tmpSymptomsJson = _stmt.getText(_columnIndexOfSymptomsJson)
          }
          val _tmpActivityLevel: String?
          if (_stmt.isNull(_columnIndexOfActivityLevel)) {
            _tmpActivityLevel = null
          } else {
            _tmpActivityLevel = _stmt.getText(_columnIndexOfActivityLevel)
          }
          val _tmpPhotoUrls: String?
          if (_stmt.isNull(_columnIndexOfPhotoUrls)) {
            _tmpPhotoUrls = null
          } else {
            _tmpPhotoUrls = _stmt.getText(_columnIndexOfPhotoUrls)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpTemperature: Double?
          if (_stmt.isNull(_columnIndexOfTemperature)) {
            _tmpTemperature = null
          } else {
            _tmpTemperature = _stmt.getDouble(_columnIndexOfTemperature)
          }
          val _tmpHumidity: Double?
          if (_stmt.isNull(_columnIndexOfHumidity)) {
            _tmpHumidity = null
          } else {
            _tmpHumidity = _stmt.getDouble(_columnIndexOfHumidity)
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
          val _tmpDeviceTimestamp: Long
          _tmpDeviceTimestamp = _stmt.getLong(_columnIndexOfDeviceTimestamp)
          val _tmpAuthor: String?
          if (_stmt.isNull(_columnIndexOfAuthor)) {
            _tmpAuthor = null
          } else {
            _tmpAuthor = _stmt.getText(_columnIndexOfAuthor)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          val _tmpConflictResolved: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfConflictResolved).toInt()
          _tmpConflictResolved = _tmp_1 != 0
          val _tmpMediaItemsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaItemsJson)) {
            _tmpMediaItemsJson = null
          } else {
            _tmpMediaItemsJson = _stmt.getText(_columnIndexOfMediaItemsJson)
          }
          _result =
              DailyLogEntity(_tmpLogId,_tmpProductId,_tmpFarmerId,_tmpLogDate,_tmpWeightGrams,_tmpFeedKg,_tmpMedicationJson,_tmpSymptomsJson,_tmpActivityLevel,_tmpPhotoUrls,_tmpNotes,_tmpTemperature,_tmpHumidity,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpDeviceTimestamp,_tmpAuthor,_tmpMergedAt,_tmpMergeCount,_tmpConflictResolved,_tmpMediaItemsJson)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getLogCountForFarmer(farmerId: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM daily_logs WHERE farmerId = ?"
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

  public override suspend fun getLastFeedAmount(farmerId: String): Double? {
    val _sql: String =
        "SELECT feedKg FROM daily_logs WHERE farmerId = ? AND feedKg IS NOT NULL ORDER BY logDate DESC LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _result: Double?
        if (_stmt.step()) {
          if (_stmt.isNull(0)) {
            _result = null
          } else {
            _result = _stmt.getDouble(0)
          }
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTotalFeedBetween(
    farmerId: String,
    start: Long,
    end: Long,
  ): Double {
    val _sql: String =
        "SELECT COALESCE(SUM(feedKg), 0.0) FROM daily_logs WHERE farmerId = ? AND logDate BETWEEN ? AND ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, start)
        _argIndex = 3
        _stmt.bindLong(_argIndex, end)
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

  public override suspend fun getAverageWeightBetween(
    farmerId: String,
    start: Long,
    end: Long,
  ): Double {
    val _sql: String =
        "SELECT COALESCE(AVG(weightGrams), 0.0) FROM daily_logs WHERE farmerId = ? AND logDate BETWEEN ? AND ? AND weightGrams IS NOT NULL"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, start)
        _argIndex = 3
        _stmt.bindLong(_argIndex, end)
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

  public override suspend fun getActiveProductCountBetween(
    farmerId: String,
    start: Long,
    end: Long,
  ): Int {
    val _sql: String =
        "SELECT COUNT(DISTINCT productId) FROM daily_logs WHERE farmerId = ? AND logDate BETWEEN ? AND ?"
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

  public override suspend fun getTotalFeedForProduct(
    productId: String,
    startDate: Long,
    endDate: Long,
  ): Double {
    val _sql: String =
        "SELECT COALESCE(SUM(feedKg), 0.0) FROM daily_logs WHERE productId = ? AND logDate BETWEEN ? AND ? AND feedKg IS NOT NULL"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, startDate)
        _argIndex = 3
        _stmt.bindLong(_argIndex, endDate)
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

  public override suspend fun getInitialWeightForProduct(productId: String, startDate: Long):
      Double? {
    val _sql: String =
        "SELECT weightGrams FROM daily_logs WHERE productId = ? AND logDate >= ? AND weightGrams IS NOT NULL ORDER BY logDate ASC LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, startDate)
        val _result: Double?
        if (_stmt.step()) {
          if (_stmt.isNull(0)) {
            _result = null
          } else {
            _result = _stmt.getDouble(0)
          }
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getFinalWeightForProduct(productId: String, endDate: Long): Double? {
    val _sql: String =
        "SELECT weightGrams FROM daily_logs WHERE productId = ? AND logDate <= ? AND weightGrams IS NOT NULL ORDER BY logDate DESC LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, endDate)
        val _result: Double?
        if (_stmt.step()) {
          if (_stmt.isNull(0)) {
            _result = null
          } else {
            _result = _stmt.getDouble(0)
          }
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeFeedAndWeightLogs(
    productId: String,
    startDate: Long,
    endDate: Long,
  ): Flow<List<DailyLogEntity>> {
    val _sql: String =
        "SELECT * FROM daily_logs WHERE productId = ? AND logDate BETWEEN ? AND ? AND (feedKg IS NOT NULL OR weightGrams IS NOT NULL) ORDER BY logDate ASC"
    return createFlow(__db, false, arrayOf("daily_logs")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, startDate)
        _argIndex = 3
        _stmt.bindLong(_argIndex, endDate)
        val _columnIndexOfLogId: Int = getColumnIndexOrThrow(_stmt, "logId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfLogDate: Int = getColumnIndexOrThrow(_stmt, "logDate")
        val _columnIndexOfWeightGrams: Int = getColumnIndexOrThrow(_stmt, "weightGrams")
        val _columnIndexOfFeedKg: Int = getColumnIndexOrThrow(_stmt, "feedKg")
        val _columnIndexOfMedicationJson: Int = getColumnIndexOrThrow(_stmt, "medicationJson")
        val _columnIndexOfSymptomsJson: Int = getColumnIndexOrThrow(_stmt, "symptomsJson")
        val _columnIndexOfActivityLevel: Int = getColumnIndexOrThrow(_stmt, "activityLevel")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfTemperature: Int = getColumnIndexOrThrow(_stmt, "temperature")
        val _columnIndexOfHumidity: Int = getColumnIndexOrThrow(_stmt, "humidity")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfDeviceTimestamp: Int = getColumnIndexOrThrow(_stmt, "deviceTimestamp")
        val _columnIndexOfAuthor: Int = getColumnIndexOrThrow(_stmt, "author")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _columnIndexOfConflictResolved: Int = getColumnIndexOrThrow(_stmt, "conflictResolved")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _result: MutableList<DailyLogEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: DailyLogEntity
          val _tmpLogId: String
          _tmpLogId = _stmt.getText(_columnIndexOfLogId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpLogDate: Long
          _tmpLogDate = _stmt.getLong(_columnIndexOfLogDate)
          val _tmpWeightGrams: Double?
          if (_stmt.isNull(_columnIndexOfWeightGrams)) {
            _tmpWeightGrams = null
          } else {
            _tmpWeightGrams = _stmt.getDouble(_columnIndexOfWeightGrams)
          }
          val _tmpFeedKg: Double?
          if (_stmt.isNull(_columnIndexOfFeedKg)) {
            _tmpFeedKg = null
          } else {
            _tmpFeedKg = _stmt.getDouble(_columnIndexOfFeedKg)
          }
          val _tmpMedicationJson: String?
          if (_stmt.isNull(_columnIndexOfMedicationJson)) {
            _tmpMedicationJson = null
          } else {
            _tmpMedicationJson = _stmt.getText(_columnIndexOfMedicationJson)
          }
          val _tmpSymptomsJson: String?
          if (_stmt.isNull(_columnIndexOfSymptomsJson)) {
            _tmpSymptomsJson = null
          } else {
            _tmpSymptomsJson = _stmt.getText(_columnIndexOfSymptomsJson)
          }
          val _tmpActivityLevel: String?
          if (_stmt.isNull(_columnIndexOfActivityLevel)) {
            _tmpActivityLevel = null
          } else {
            _tmpActivityLevel = _stmt.getText(_columnIndexOfActivityLevel)
          }
          val _tmpPhotoUrls: String?
          if (_stmt.isNull(_columnIndexOfPhotoUrls)) {
            _tmpPhotoUrls = null
          } else {
            _tmpPhotoUrls = _stmt.getText(_columnIndexOfPhotoUrls)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpTemperature: Double?
          if (_stmt.isNull(_columnIndexOfTemperature)) {
            _tmpTemperature = null
          } else {
            _tmpTemperature = _stmt.getDouble(_columnIndexOfTemperature)
          }
          val _tmpHumidity: Double?
          if (_stmt.isNull(_columnIndexOfHumidity)) {
            _tmpHumidity = null
          } else {
            _tmpHumidity = _stmt.getDouble(_columnIndexOfHumidity)
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
          val _tmpDeviceTimestamp: Long
          _tmpDeviceTimestamp = _stmt.getLong(_columnIndexOfDeviceTimestamp)
          val _tmpAuthor: String?
          if (_stmt.isNull(_columnIndexOfAuthor)) {
            _tmpAuthor = null
          } else {
            _tmpAuthor = _stmt.getText(_columnIndexOfAuthor)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          val _tmpConflictResolved: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfConflictResolved).toInt()
          _tmpConflictResolved = _tmp_1 != 0
          val _tmpMediaItemsJson: String?
          if (_stmt.isNull(_columnIndexOfMediaItemsJson)) {
            _tmpMediaItemsJson = null
          } else {
            _tmpMediaItemsJson = _stmt.getText(_columnIndexOfMediaItemsJson)
          }
          _item =
              DailyLogEntity(_tmpLogId,_tmpProductId,_tmpFarmerId,_tmpLogDate,_tmpWeightGrams,_tmpFeedKg,_tmpMedicationJson,_tmpSymptomsJson,_tmpActivityLevel,_tmpPhotoUrls,_tmpNotes,_tmpTemperature,_tmpHumidity,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpDeviceTimestamp,_tmpAuthor,_tmpMergedAt,_tmpMergeCount,_tmpConflictResolved,_tmpMediaItemsJson)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAverageDailyFeedForProduct(
    productId: String,
    startDate: Long,
    endDate: Long,
  ): Double {
    val _sql: String =
        "SELECT COALESCE(AVG(feedKg), 0.0) FROM daily_logs WHERE productId = ? AND logDate BETWEEN ? AND ? AND feedKg IS NOT NULL"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, startDate)
        _argIndex = 3
        _stmt.bindLong(_argIndex, endDate)
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

  public override suspend fun delete(logId: String) {
    val _sql: String = "DELETE FROM daily_logs WHERE logId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, logId)
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
