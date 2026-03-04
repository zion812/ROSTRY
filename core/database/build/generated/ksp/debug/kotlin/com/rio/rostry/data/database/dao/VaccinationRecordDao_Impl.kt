package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.VaccinationRecordEntity
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
public class VaccinationRecordDao_Impl(
  __db: RoomDatabase,
) : VaccinationRecordDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfVaccinationRecordEntity: EntityInsertAdapter<VaccinationRecordEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfVaccinationRecordEntity = object :
        EntityInsertAdapter<VaccinationRecordEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `vaccination_records` (`vaccinationId`,`productId`,`farmerId`,`vaccineType`,`supplier`,`batchCode`,`doseMl`,`scheduledAt`,`administeredAt`,`efficacyNotes`,`costInr`,`photoUrls`,`mediaItemsJson`,`createdAt`,`updatedAt`,`dirty`,`syncedAt`,`correctionOf`,`editCount`,`lastEditedBy`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: VaccinationRecordEntity) {
        statement.bindText(1, entity.vaccinationId)
        statement.bindText(2, entity.productId)
        statement.bindText(3, entity.farmerId)
        statement.bindText(4, entity.vaccineType)
        val _tmpSupplier: String? = entity.supplier
        if (_tmpSupplier == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpSupplier)
        }
        val _tmpBatchCode: String? = entity.batchCode
        if (_tmpBatchCode == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpBatchCode)
        }
        val _tmpDoseMl: Double? = entity.doseMl
        if (_tmpDoseMl == null) {
          statement.bindNull(7)
        } else {
          statement.bindDouble(7, _tmpDoseMl)
        }
        statement.bindLong(8, entity.scheduledAt)
        val _tmpAdministeredAt: Long? = entity.administeredAt
        if (_tmpAdministeredAt == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpAdministeredAt)
        }
        val _tmpEfficacyNotes: String? = entity.efficacyNotes
        if (_tmpEfficacyNotes == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpEfficacyNotes)
        }
        val _tmpCostInr: Double? = entity.costInr
        if (_tmpCostInr == null) {
          statement.bindNull(11)
        } else {
          statement.bindDouble(11, _tmpCostInr)
        }
        val _tmpPhotoUrls: String? = entity.photoUrls
        if (_tmpPhotoUrls == null) {
          statement.bindNull(12)
        } else {
          statement.bindText(12, _tmpPhotoUrls)
        }
        val _tmpMediaItemsJson: String? = entity.mediaItemsJson
        if (_tmpMediaItemsJson == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpMediaItemsJson)
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
        val _tmpCorrectionOf: String? = entity.correctionOf
        if (_tmpCorrectionOf == null) {
          statement.bindNull(18)
        } else {
          statement.bindText(18, _tmpCorrectionOf)
        }
        statement.bindLong(19, entity.editCount.toLong())
        val _tmpLastEditedBy: String? = entity.lastEditedBy
        if (_tmpLastEditedBy == null) {
          statement.bindNull(20)
        } else {
          statement.bindText(20, _tmpLastEditedBy)
        }
      }
    }
  }

  public override suspend fun upsert(record: VaccinationRecordEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfVaccinationRecordEntity.insert(_connection, record)
  }

  public override fun observeForProduct(productId: String): Flow<List<VaccinationRecordEntity>> {
    val _sql: String =
        "SELECT * FROM vaccination_records WHERE productId = ? ORDER BY scheduledAt ASC"
    return createFlow(__db, false, arrayOf("vaccination_records")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfVaccinationId: Int = getColumnIndexOrThrow(_stmt, "vaccinationId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfVaccineType: Int = getColumnIndexOrThrow(_stmt, "vaccineType")
        val _columnIndexOfSupplier: Int = getColumnIndexOrThrow(_stmt, "supplier")
        val _columnIndexOfBatchCode: Int = getColumnIndexOrThrow(_stmt, "batchCode")
        val _columnIndexOfDoseMl: Int = getColumnIndexOrThrow(_stmt, "doseMl")
        val _columnIndexOfScheduledAt: Int = getColumnIndexOrThrow(_stmt, "scheduledAt")
        val _columnIndexOfAdministeredAt: Int = getColumnIndexOrThrow(_stmt, "administeredAt")
        val _columnIndexOfEfficacyNotes: Int = getColumnIndexOrThrow(_stmt, "efficacyNotes")
        val _columnIndexOfCostInr: Int = getColumnIndexOrThrow(_stmt, "costInr")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfCorrectionOf: Int = getColumnIndexOrThrow(_stmt, "correctionOf")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _result: MutableList<VaccinationRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: VaccinationRecordEntity
          val _tmpVaccinationId: String
          _tmpVaccinationId = _stmt.getText(_columnIndexOfVaccinationId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpVaccineType: String
          _tmpVaccineType = _stmt.getText(_columnIndexOfVaccineType)
          val _tmpSupplier: String?
          if (_stmt.isNull(_columnIndexOfSupplier)) {
            _tmpSupplier = null
          } else {
            _tmpSupplier = _stmt.getText(_columnIndexOfSupplier)
          }
          val _tmpBatchCode: String?
          if (_stmt.isNull(_columnIndexOfBatchCode)) {
            _tmpBatchCode = null
          } else {
            _tmpBatchCode = _stmt.getText(_columnIndexOfBatchCode)
          }
          val _tmpDoseMl: Double?
          if (_stmt.isNull(_columnIndexOfDoseMl)) {
            _tmpDoseMl = null
          } else {
            _tmpDoseMl = _stmt.getDouble(_columnIndexOfDoseMl)
          }
          val _tmpScheduledAt: Long
          _tmpScheduledAt = _stmt.getLong(_columnIndexOfScheduledAt)
          val _tmpAdministeredAt: Long?
          if (_stmt.isNull(_columnIndexOfAdministeredAt)) {
            _tmpAdministeredAt = null
          } else {
            _tmpAdministeredAt = _stmt.getLong(_columnIndexOfAdministeredAt)
          }
          val _tmpEfficacyNotes: String?
          if (_stmt.isNull(_columnIndexOfEfficacyNotes)) {
            _tmpEfficacyNotes = null
          } else {
            _tmpEfficacyNotes = _stmt.getText(_columnIndexOfEfficacyNotes)
          }
          val _tmpCostInr: Double?
          if (_stmt.isNull(_columnIndexOfCostInr)) {
            _tmpCostInr = null
          } else {
            _tmpCostInr = _stmt.getDouble(_columnIndexOfCostInr)
          }
          val _tmpPhotoUrls: String?
          if (_stmt.isNull(_columnIndexOfPhotoUrls)) {
            _tmpPhotoUrls = null
          } else {
            _tmpPhotoUrls = _stmt.getText(_columnIndexOfPhotoUrls)
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
          _item =
              VaccinationRecordEntity(_tmpVaccinationId,_tmpProductId,_tmpFarmerId,_tmpVaccineType,_tmpSupplier,_tmpBatchCode,_tmpDoseMl,_tmpScheduledAt,_tmpAdministeredAt,_tmpEfficacyNotes,_tmpCostInr,_tmpPhotoUrls,_tmpMediaItemsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpCorrectionOf,_tmpEditCount,_tmpLastEditedBy)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun dueReminders(byTime: Long): List<VaccinationRecordEntity> {
    val _sql: String =
        "SELECT * FROM vaccination_records WHERE administeredAt IS NULL AND scheduledAt <= ? ORDER BY scheduledAt ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, byTime)
        val _columnIndexOfVaccinationId: Int = getColumnIndexOrThrow(_stmt, "vaccinationId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfVaccineType: Int = getColumnIndexOrThrow(_stmt, "vaccineType")
        val _columnIndexOfSupplier: Int = getColumnIndexOrThrow(_stmt, "supplier")
        val _columnIndexOfBatchCode: Int = getColumnIndexOrThrow(_stmt, "batchCode")
        val _columnIndexOfDoseMl: Int = getColumnIndexOrThrow(_stmt, "doseMl")
        val _columnIndexOfScheduledAt: Int = getColumnIndexOrThrow(_stmt, "scheduledAt")
        val _columnIndexOfAdministeredAt: Int = getColumnIndexOrThrow(_stmt, "administeredAt")
        val _columnIndexOfEfficacyNotes: Int = getColumnIndexOrThrow(_stmt, "efficacyNotes")
        val _columnIndexOfCostInr: Int = getColumnIndexOrThrow(_stmt, "costInr")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfCorrectionOf: Int = getColumnIndexOrThrow(_stmt, "correctionOf")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _result: MutableList<VaccinationRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: VaccinationRecordEntity
          val _tmpVaccinationId: String
          _tmpVaccinationId = _stmt.getText(_columnIndexOfVaccinationId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpVaccineType: String
          _tmpVaccineType = _stmt.getText(_columnIndexOfVaccineType)
          val _tmpSupplier: String?
          if (_stmt.isNull(_columnIndexOfSupplier)) {
            _tmpSupplier = null
          } else {
            _tmpSupplier = _stmt.getText(_columnIndexOfSupplier)
          }
          val _tmpBatchCode: String?
          if (_stmt.isNull(_columnIndexOfBatchCode)) {
            _tmpBatchCode = null
          } else {
            _tmpBatchCode = _stmt.getText(_columnIndexOfBatchCode)
          }
          val _tmpDoseMl: Double?
          if (_stmt.isNull(_columnIndexOfDoseMl)) {
            _tmpDoseMl = null
          } else {
            _tmpDoseMl = _stmt.getDouble(_columnIndexOfDoseMl)
          }
          val _tmpScheduledAt: Long
          _tmpScheduledAt = _stmt.getLong(_columnIndexOfScheduledAt)
          val _tmpAdministeredAt: Long?
          if (_stmt.isNull(_columnIndexOfAdministeredAt)) {
            _tmpAdministeredAt = null
          } else {
            _tmpAdministeredAt = _stmt.getLong(_columnIndexOfAdministeredAt)
          }
          val _tmpEfficacyNotes: String?
          if (_stmt.isNull(_columnIndexOfEfficacyNotes)) {
            _tmpEfficacyNotes = null
          } else {
            _tmpEfficacyNotes = _stmt.getText(_columnIndexOfEfficacyNotes)
          }
          val _tmpCostInr: Double?
          if (_stmt.isNull(_columnIndexOfCostInr)) {
            _tmpCostInr = null
          } else {
            _tmpCostInr = _stmt.getDouble(_columnIndexOfCostInr)
          }
          val _tmpPhotoUrls: String?
          if (_stmt.isNull(_columnIndexOfPhotoUrls)) {
            _tmpPhotoUrls = null
          } else {
            _tmpPhotoUrls = _stmt.getText(_columnIndexOfPhotoUrls)
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
          _item =
              VaccinationRecordEntity(_tmpVaccinationId,_tmpProductId,_tmpFarmerId,_tmpVaccineType,_tmpSupplier,_tmpBatchCode,_tmpDoseMl,_tmpScheduledAt,_tmpAdministeredAt,_tmpEfficacyNotes,_tmpCostInr,_tmpPhotoUrls,_tmpMediaItemsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpCorrectionOf,_tmpEditCount,_tmpLastEditedBy)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countAdministeredBetween(start: Long, end: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM vaccination_records WHERE administeredAt IS NOT NULL AND administeredAt BETWEEN ? AND ?"
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

  public override suspend fun countDueForFarmer(
    farmerId: String,
    start: Long,
    end: Long,
  ): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM vaccination_records WHERE farmerId = ? AND administeredAt IS NULL AND scheduledAt BETWEEN ? AND ?"
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

  public override suspend fun countOverdueForFarmer(farmerId: String, now: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM vaccination_records WHERE farmerId = ? AND administeredAt IS NULL AND scheduledAt < ?"
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

  public override suspend fun getOverdueForFarmer(farmerId: String, now: Long):
      List<VaccinationRecordEntity> {
    val _sql: String =
        "SELECT * FROM vaccination_records WHERE farmerId = ? AND administeredAt IS NULL AND scheduledAt < ? ORDER BY scheduledAt ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
        val _columnIndexOfVaccinationId: Int = getColumnIndexOrThrow(_stmt, "vaccinationId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfVaccineType: Int = getColumnIndexOrThrow(_stmt, "vaccineType")
        val _columnIndexOfSupplier: Int = getColumnIndexOrThrow(_stmt, "supplier")
        val _columnIndexOfBatchCode: Int = getColumnIndexOrThrow(_stmt, "batchCode")
        val _columnIndexOfDoseMl: Int = getColumnIndexOrThrow(_stmt, "doseMl")
        val _columnIndexOfScheduledAt: Int = getColumnIndexOrThrow(_stmt, "scheduledAt")
        val _columnIndexOfAdministeredAt: Int = getColumnIndexOrThrow(_stmt, "administeredAt")
        val _columnIndexOfEfficacyNotes: Int = getColumnIndexOrThrow(_stmt, "efficacyNotes")
        val _columnIndexOfCostInr: Int = getColumnIndexOrThrow(_stmt, "costInr")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfCorrectionOf: Int = getColumnIndexOrThrow(_stmt, "correctionOf")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _result: MutableList<VaccinationRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: VaccinationRecordEntity
          val _tmpVaccinationId: String
          _tmpVaccinationId = _stmt.getText(_columnIndexOfVaccinationId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpVaccineType: String
          _tmpVaccineType = _stmt.getText(_columnIndexOfVaccineType)
          val _tmpSupplier: String?
          if (_stmt.isNull(_columnIndexOfSupplier)) {
            _tmpSupplier = null
          } else {
            _tmpSupplier = _stmt.getText(_columnIndexOfSupplier)
          }
          val _tmpBatchCode: String?
          if (_stmt.isNull(_columnIndexOfBatchCode)) {
            _tmpBatchCode = null
          } else {
            _tmpBatchCode = _stmt.getText(_columnIndexOfBatchCode)
          }
          val _tmpDoseMl: Double?
          if (_stmt.isNull(_columnIndexOfDoseMl)) {
            _tmpDoseMl = null
          } else {
            _tmpDoseMl = _stmt.getDouble(_columnIndexOfDoseMl)
          }
          val _tmpScheduledAt: Long
          _tmpScheduledAt = _stmt.getLong(_columnIndexOfScheduledAt)
          val _tmpAdministeredAt: Long?
          if (_stmt.isNull(_columnIndexOfAdministeredAt)) {
            _tmpAdministeredAt = null
          } else {
            _tmpAdministeredAt = _stmt.getLong(_columnIndexOfAdministeredAt)
          }
          val _tmpEfficacyNotes: String?
          if (_stmt.isNull(_columnIndexOfEfficacyNotes)) {
            _tmpEfficacyNotes = null
          } else {
            _tmpEfficacyNotes = _stmt.getText(_columnIndexOfEfficacyNotes)
          }
          val _tmpCostInr: Double?
          if (_stmt.isNull(_columnIndexOfCostInr)) {
            _tmpCostInr = null
          } else {
            _tmpCostInr = _stmt.getDouble(_columnIndexOfCostInr)
          }
          val _tmpPhotoUrls: String?
          if (_stmt.isNull(_columnIndexOfPhotoUrls)) {
            _tmpPhotoUrls = null
          } else {
            _tmpPhotoUrls = _stmt.getText(_columnIndexOfPhotoUrls)
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
          _item =
              VaccinationRecordEntity(_tmpVaccinationId,_tmpProductId,_tmpFarmerId,_tmpVaccineType,_tmpSupplier,_tmpBatchCode,_tmpDoseMl,_tmpScheduledAt,_tmpAdministeredAt,_tmpEfficacyNotes,_tmpCostInr,_tmpPhotoUrls,_tmpMediaItemsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpCorrectionOf,_tmpEditCount,_tmpLastEditedBy)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countScheduledBetweenForFarmer(
    farmerId: String,
    start: Long,
    end: Long,
  ): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM vaccination_records WHERE farmerId = ? AND scheduledAt BETWEEN ? AND ?"
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

  public override suspend fun countAdministeredBetweenForFarmer(
    farmerId: String,
    start: Long,
    end: Long,
  ): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM vaccination_records WHERE farmerId = ? AND administeredAt IS NOT NULL AND administeredAt BETWEEN ? AND ?"
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

  public override fun observeAdministeredCountForFarmerBetween(
    farmerId: String,
    start: Long,
    end: Long,
  ): Flow<Int> {
    val _sql: String =
        "SELECT COUNT(*) FROM vaccination_records WHERE farmerId = ? AND administeredAt IS NOT NULL AND administeredAt BETWEEN ? AND ?"
    return createFlow(__db, false, arrayOf("vaccination_records")) { _connection ->
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

  public override fun observeDueForFarmer(
    farmerId: String,
    start: Long,
    end: Long,
  ): Flow<Int> {
    val _sql: String =
        "SELECT COUNT(*) FROM vaccination_records WHERE farmerId = ? AND administeredAt IS NULL AND scheduledAt BETWEEN ? AND ?"
    return createFlow(__db, false, arrayOf("vaccination_records")) { _connection ->
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

  public override fun observeOverdueForFarmer(farmerId: String, now: Long): Flow<Int> {
    val _sql: String =
        "SELECT COUNT(*) FROM vaccination_records WHERE farmerId = ? AND administeredAt IS NULL AND scheduledAt < ?"
    return createFlow(__db, false, arrayOf("vaccination_records")) { _connection ->
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

  public override fun observeByFarmer(farmerId: String): Flow<List<VaccinationRecordEntity>> {
    val _sql: String =
        "SELECT * FROM vaccination_records WHERE farmerId = ? ORDER BY scheduledAt DESC"
    return createFlow(__db, false, arrayOf("vaccination_records")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfVaccinationId: Int = getColumnIndexOrThrow(_stmt, "vaccinationId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfVaccineType: Int = getColumnIndexOrThrow(_stmt, "vaccineType")
        val _columnIndexOfSupplier: Int = getColumnIndexOrThrow(_stmt, "supplier")
        val _columnIndexOfBatchCode: Int = getColumnIndexOrThrow(_stmt, "batchCode")
        val _columnIndexOfDoseMl: Int = getColumnIndexOrThrow(_stmt, "doseMl")
        val _columnIndexOfScheduledAt: Int = getColumnIndexOrThrow(_stmt, "scheduledAt")
        val _columnIndexOfAdministeredAt: Int = getColumnIndexOrThrow(_stmt, "administeredAt")
        val _columnIndexOfEfficacyNotes: Int = getColumnIndexOrThrow(_stmt, "efficacyNotes")
        val _columnIndexOfCostInr: Int = getColumnIndexOrThrow(_stmt, "costInr")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfCorrectionOf: Int = getColumnIndexOrThrow(_stmt, "correctionOf")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _result: MutableList<VaccinationRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: VaccinationRecordEntity
          val _tmpVaccinationId: String
          _tmpVaccinationId = _stmt.getText(_columnIndexOfVaccinationId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpVaccineType: String
          _tmpVaccineType = _stmt.getText(_columnIndexOfVaccineType)
          val _tmpSupplier: String?
          if (_stmt.isNull(_columnIndexOfSupplier)) {
            _tmpSupplier = null
          } else {
            _tmpSupplier = _stmt.getText(_columnIndexOfSupplier)
          }
          val _tmpBatchCode: String?
          if (_stmt.isNull(_columnIndexOfBatchCode)) {
            _tmpBatchCode = null
          } else {
            _tmpBatchCode = _stmt.getText(_columnIndexOfBatchCode)
          }
          val _tmpDoseMl: Double?
          if (_stmt.isNull(_columnIndexOfDoseMl)) {
            _tmpDoseMl = null
          } else {
            _tmpDoseMl = _stmt.getDouble(_columnIndexOfDoseMl)
          }
          val _tmpScheduledAt: Long
          _tmpScheduledAt = _stmt.getLong(_columnIndexOfScheduledAt)
          val _tmpAdministeredAt: Long?
          if (_stmt.isNull(_columnIndexOfAdministeredAt)) {
            _tmpAdministeredAt = null
          } else {
            _tmpAdministeredAt = _stmt.getLong(_columnIndexOfAdministeredAt)
          }
          val _tmpEfficacyNotes: String?
          if (_stmt.isNull(_columnIndexOfEfficacyNotes)) {
            _tmpEfficacyNotes = null
          } else {
            _tmpEfficacyNotes = _stmt.getText(_columnIndexOfEfficacyNotes)
          }
          val _tmpCostInr: Double?
          if (_stmt.isNull(_columnIndexOfCostInr)) {
            _tmpCostInr = null
          } else {
            _tmpCostInr = _stmt.getDouble(_columnIndexOfCostInr)
          }
          val _tmpPhotoUrls: String?
          if (_stmt.isNull(_columnIndexOfPhotoUrls)) {
            _tmpPhotoUrls = null
          } else {
            _tmpPhotoUrls = _stmt.getText(_columnIndexOfPhotoUrls)
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
          _item =
              VaccinationRecordEntity(_tmpVaccinationId,_tmpProductId,_tmpFarmerId,_tmpVaccineType,_tmpSupplier,_tmpBatchCode,_tmpDoseMl,_tmpScheduledAt,_tmpAdministeredAt,_tmpEfficacyNotes,_tmpCostInr,_tmpPhotoUrls,_tmpMediaItemsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpCorrectionOf,_tmpEditCount,_tmpLastEditedBy)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDirty(): List<VaccinationRecordEntity> {
    val _sql: String = "SELECT * FROM vaccination_records WHERE dirty = 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfVaccinationId: Int = getColumnIndexOrThrow(_stmt, "vaccinationId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfVaccineType: Int = getColumnIndexOrThrow(_stmt, "vaccineType")
        val _columnIndexOfSupplier: Int = getColumnIndexOrThrow(_stmt, "supplier")
        val _columnIndexOfBatchCode: Int = getColumnIndexOrThrow(_stmt, "batchCode")
        val _columnIndexOfDoseMl: Int = getColumnIndexOrThrow(_stmt, "doseMl")
        val _columnIndexOfScheduledAt: Int = getColumnIndexOrThrow(_stmt, "scheduledAt")
        val _columnIndexOfAdministeredAt: Int = getColumnIndexOrThrow(_stmt, "administeredAt")
        val _columnIndexOfEfficacyNotes: Int = getColumnIndexOrThrow(_stmt, "efficacyNotes")
        val _columnIndexOfCostInr: Int = getColumnIndexOrThrow(_stmt, "costInr")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfCorrectionOf: Int = getColumnIndexOrThrow(_stmt, "correctionOf")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _result: MutableList<VaccinationRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: VaccinationRecordEntity
          val _tmpVaccinationId: String
          _tmpVaccinationId = _stmt.getText(_columnIndexOfVaccinationId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpVaccineType: String
          _tmpVaccineType = _stmt.getText(_columnIndexOfVaccineType)
          val _tmpSupplier: String?
          if (_stmt.isNull(_columnIndexOfSupplier)) {
            _tmpSupplier = null
          } else {
            _tmpSupplier = _stmt.getText(_columnIndexOfSupplier)
          }
          val _tmpBatchCode: String?
          if (_stmt.isNull(_columnIndexOfBatchCode)) {
            _tmpBatchCode = null
          } else {
            _tmpBatchCode = _stmt.getText(_columnIndexOfBatchCode)
          }
          val _tmpDoseMl: Double?
          if (_stmt.isNull(_columnIndexOfDoseMl)) {
            _tmpDoseMl = null
          } else {
            _tmpDoseMl = _stmt.getDouble(_columnIndexOfDoseMl)
          }
          val _tmpScheduledAt: Long
          _tmpScheduledAt = _stmt.getLong(_columnIndexOfScheduledAt)
          val _tmpAdministeredAt: Long?
          if (_stmt.isNull(_columnIndexOfAdministeredAt)) {
            _tmpAdministeredAt = null
          } else {
            _tmpAdministeredAt = _stmt.getLong(_columnIndexOfAdministeredAt)
          }
          val _tmpEfficacyNotes: String?
          if (_stmt.isNull(_columnIndexOfEfficacyNotes)) {
            _tmpEfficacyNotes = null
          } else {
            _tmpEfficacyNotes = _stmt.getText(_columnIndexOfEfficacyNotes)
          }
          val _tmpCostInr: Double?
          if (_stmt.isNull(_columnIndexOfCostInr)) {
            _tmpCostInr = null
          } else {
            _tmpCostInr = _stmt.getDouble(_columnIndexOfCostInr)
          }
          val _tmpPhotoUrls: String?
          if (_stmt.isNull(_columnIndexOfPhotoUrls)) {
            _tmpPhotoUrls = null
          } else {
            _tmpPhotoUrls = _stmt.getText(_columnIndexOfPhotoUrls)
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
          _item =
              VaccinationRecordEntity(_tmpVaccinationId,_tmpProductId,_tmpFarmerId,_tmpVaccineType,_tmpSupplier,_tmpBatchCode,_tmpDoseMl,_tmpScheduledAt,_tmpAdministeredAt,_tmpEfficacyNotes,_tmpCostInr,_tmpPhotoUrls,_tmpMediaItemsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpCorrectionOf,_tmpEditCount,_tmpLastEditedBy)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countByFarmer(farmerId: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM vaccination_records WHERE farmerId = ?"
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

  public override suspend fun countCompletedByFarmer(farmerId: String): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM vaccination_records WHERE farmerId = ? AND administeredAt IS NOT NULL"
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

  public override suspend fun getRecordCountForFarmer(farmerId: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM vaccination_records WHERE farmerId = ?"
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

  public override suspend fun countPendingByFarmer(farmerId: String, now: Long): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM vaccination_records WHERE farmerId = ? AND administeredAt IS NULL AND scheduledAt >= ?"
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

  public override suspend fun getRecordsByProduct(productId: String):
      List<VaccinationRecordEntity> {
    val _sql: String =
        "SELECT * FROM vaccination_records WHERE productId = ? ORDER BY scheduledAt DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfVaccinationId: Int = getColumnIndexOrThrow(_stmt, "vaccinationId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfVaccineType: Int = getColumnIndexOrThrow(_stmt, "vaccineType")
        val _columnIndexOfSupplier: Int = getColumnIndexOrThrow(_stmt, "supplier")
        val _columnIndexOfBatchCode: Int = getColumnIndexOrThrow(_stmt, "batchCode")
        val _columnIndexOfDoseMl: Int = getColumnIndexOrThrow(_stmt, "doseMl")
        val _columnIndexOfScheduledAt: Int = getColumnIndexOrThrow(_stmt, "scheduledAt")
        val _columnIndexOfAdministeredAt: Int = getColumnIndexOrThrow(_stmt, "administeredAt")
        val _columnIndexOfEfficacyNotes: Int = getColumnIndexOrThrow(_stmt, "efficacyNotes")
        val _columnIndexOfCostInr: Int = getColumnIndexOrThrow(_stmt, "costInr")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfCorrectionOf: Int = getColumnIndexOrThrow(_stmt, "correctionOf")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _result: MutableList<VaccinationRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: VaccinationRecordEntity
          val _tmpVaccinationId: String
          _tmpVaccinationId = _stmt.getText(_columnIndexOfVaccinationId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpVaccineType: String
          _tmpVaccineType = _stmt.getText(_columnIndexOfVaccineType)
          val _tmpSupplier: String?
          if (_stmt.isNull(_columnIndexOfSupplier)) {
            _tmpSupplier = null
          } else {
            _tmpSupplier = _stmt.getText(_columnIndexOfSupplier)
          }
          val _tmpBatchCode: String?
          if (_stmt.isNull(_columnIndexOfBatchCode)) {
            _tmpBatchCode = null
          } else {
            _tmpBatchCode = _stmt.getText(_columnIndexOfBatchCode)
          }
          val _tmpDoseMl: Double?
          if (_stmt.isNull(_columnIndexOfDoseMl)) {
            _tmpDoseMl = null
          } else {
            _tmpDoseMl = _stmt.getDouble(_columnIndexOfDoseMl)
          }
          val _tmpScheduledAt: Long
          _tmpScheduledAt = _stmt.getLong(_columnIndexOfScheduledAt)
          val _tmpAdministeredAt: Long?
          if (_stmt.isNull(_columnIndexOfAdministeredAt)) {
            _tmpAdministeredAt = null
          } else {
            _tmpAdministeredAt = _stmt.getLong(_columnIndexOfAdministeredAt)
          }
          val _tmpEfficacyNotes: String?
          if (_stmt.isNull(_columnIndexOfEfficacyNotes)) {
            _tmpEfficacyNotes = null
          } else {
            _tmpEfficacyNotes = _stmt.getText(_columnIndexOfEfficacyNotes)
          }
          val _tmpCostInr: Double?
          if (_stmt.isNull(_columnIndexOfCostInr)) {
            _tmpCostInr = null
          } else {
            _tmpCostInr = _stmt.getDouble(_columnIndexOfCostInr)
          }
          val _tmpPhotoUrls: String?
          if (_stmt.isNull(_columnIndexOfPhotoUrls)) {
            _tmpPhotoUrls = null
          } else {
            _tmpPhotoUrls = _stmt.getText(_columnIndexOfPhotoUrls)
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
          _item =
              VaccinationRecordEntity(_tmpVaccinationId,_tmpProductId,_tmpFarmerId,_tmpVaccineType,_tmpSupplier,_tmpBatchCode,_tmpDoseMl,_tmpScheduledAt,_tmpAdministeredAt,_tmpEfficacyNotes,_tmpCostInr,_tmpPhotoUrls,_tmpMediaItemsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpCorrectionOf,_tmpEditCount,_tmpLastEditedBy)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getById(vaccinationId: String): VaccinationRecordEntity? {
    val _sql: String = "SELECT * FROM vaccination_records WHERE vaccinationId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, vaccinationId)
        val _columnIndexOfVaccinationId: Int = getColumnIndexOrThrow(_stmt, "vaccinationId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfVaccineType: Int = getColumnIndexOrThrow(_stmt, "vaccineType")
        val _columnIndexOfSupplier: Int = getColumnIndexOrThrow(_stmt, "supplier")
        val _columnIndexOfBatchCode: Int = getColumnIndexOrThrow(_stmt, "batchCode")
        val _columnIndexOfDoseMl: Int = getColumnIndexOrThrow(_stmt, "doseMl")
        val _columnIndexOfScheduledAt: Int = getColumnIndexOrThrow(_stmt, "scheduledAt")
        val _columnIndexOfAdministeredAt: Int = getColumnIndexOrThrow(_stmt, "administeredAt")
        val _columnIndexOfEfficacyNotes: Int = getColumnIndexOrThrow(_stmt, "efficacyNotes")
        val _columnIndexOfCostInr: Int = getColumnIndexOrThrow(_stmt, "costInr")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfCorrectionOf: Int = getColumnIndexOrThrow(_stmt, "correctionOf")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _result: VaccinationRecordEntity?
        if (_stmt.step()) {
          val _tmpVaccinationId: String
          _tmpVaccinationId = _stmt.getText(_columnIndexOfVaccinationId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpVaccineType: String
          _tmpVaccineType = _stmt.getText(_columnIndexOfVaccineType)
          val _tmpSupplier: String?
          if (_stmt.isNull(_columnIndexOfSupplier)) {
            _tmpSupplier = null
          } else {
            _tmpSupplier = _stmt.getText(_columnIndexOfSupplier)
          }
          val _tmpBatchCode: String?
          if (_stmt.isNull(_columnIndexOfBatchCode)) {
            _tmpBatchCode = null
          } else {
            _tmpBatchCode = _stmt.getText(_columnIndexOfBatchCode)
          }
          val _tmpDoseMl: Double?
          if (_stmt.isNull(_columnIndexOfDoseMl)) {
            _tmpDoseMl = null
          } else {
            _tmpDoseMl = _stmt.getDouble(_columnIndexOfDoseMl)
          }
          val _tmpScheduledAt: Long
          _tmpScheduledAt = _stmt.getLong(_columnIndexOfScheduledAt)
          val _tmpAdministeredAt: Long?
          if (_stmt.isNull(_columnIndexOfAdministeredAt)) {
            _tmpAdministeredAt = null
          } else {
            _tmpAdministeredAt = _stmt.getLong(_columnIndexOfAdministeredAt)
          }
          val _tmpEfficacyNotes: String?
          if (_stmt.isNull(_columnIndexOfEfficacyNotes)) {
            _tmpEfficacyNotes = null
          } else {
            _tmpEfficacyNotes = _stmt.getText(_columnIndexOfEfficacyNotes)
          }
          val _tmpCostInr: Double?
          if (_stmt.isNull(_columnIndexOfCostInr)) {
            _tmpCostInr = null
          } else {
            _tmpCostInr = _stmt.getDouble(_columnIndexOfCostInr)
          }
          val _tmpPhotoUrls: String?
          if (_stmt.isNull(_columnIndexOfPhotoUrls)) {
            _tmpPhotoUrls = null
          } else {
            _tmpPhotoUrls = _stmt.getText(_columnIndexOfPhotoUrls)
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
          _result =
              VaccinationRecordEntity(_tmpVaccinationId,_tmpProductId,_tmpFarmerId,_tmpVaccineType,_tmpSupplier,_tmpBatchCode,_tmpDoseMl,_tmpScheduledAt,_tmpAdministeredAt,_tmpEfficacyNotes,_tmpCostInr,_tmpPhotoUrls,_tmpMediaItemsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpCorrectionOf,_tmpEditCount,_tmpLastEditedBy)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTotalVaccinationCostsForAsset(assetId: String): Double {
    val _sql: String =
        "SELECT COALESCE(SUM(costInr), 0.0) FROM vaccination_records WHERE productId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, assetId)
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

  public override suspend fun getTotalVaccinationCostsForAssetInRange(
    assetId: String,
    startDate: Long,
    endDate: Long,
  ): Double {
    val _sql: String =
        "SELECT COALESCE(SUM(costInr), 0.0) FROM vaccination_records WHERE productId = ? AND administeredAt BETWEEN ? AND ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, assetId)
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

  public override suspend fun getTotalVaccinationCostsByFarmer(farmerId: String): Double {
    val _sql: String =
        "SELECT COALESCE(SUM(costInr), 0.0) FROM vaccination_records WHERE farmerId = ?"
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

  public override suspend fun getTotalVaccinationCostsByFarmerBetween(
    farmerId: String,
    start: Long,
    end: Long,
  ): Double {
    val _sql: String =
        "SELECT COALESCE(SUM(costInr), 0.0) FROM vaccination_records WHERE farmerId = ? AND administeredAt BETWEEN ? AND ?"
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

  public override suspend fun getAllByFarmer(farmerId: String): List<VaccinationRecordEntity> {
    val _sql: String = "SELECT * FROM vaccination_records WHERE farmerId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfVaccinationId: Int = getColumnIndexOrThrow(_stmt, "vaccinationId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfVaccineType: Int = getColumnIndexOrThrow(_stmt, "vaccineType")
        val _columnIndexOfSupplier: Int = getColumnIndexOrThrow(_stmt, "supplier")
        val _columnIndexOfBatchCode: Int = getColumnIndexOrThrow(_stmt, "batchCode")
        val _columnIndexOfDoseMl: Int = getColumnIndexOrThrow(_stmt, "doseMl")
        val _columnIndexOfScheduledAt: Int = getColumnIndexOrThrow(_stmt, "scheduledAt")
        val _columnIndexOfAdministeredAt: Int = getColumnIndexOrThrow(_stmt, "administeredAt")
        val _columnIndexOfEfficacyNotes: Int = getColumnIndexOrThrow(_stmt, "efficacyNotes")
        val _columnIndexOfCostInr: Int = getColumnIndexOrThrow(_stmt, "costInr")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfCorrectionOf: Int = getColumnIndexOrThrow(_stmt, "correctionOf")
        val _columnIndexOfEditCount: Int = getColumnIndexOrThrow(_stmt, "editCount")
        val _columnIndexOfLastEditedBy: Int = getColumnIndexOrThrow(_stmt, "lastEditedBy")
        val _result: MutableList<VaccinationRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: VaccinationRecordEntity
          val _tmpVaccinationId: String
          _tmpVaccinationId = _stmt.getText(_columnIndexOfVaccinationId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpVaccineType: String
          _tmpVaccineType = _stmt.getText(_columnIndexOfVaccineType)
          val _tmpSupplier: String?
          if (_stmt.isNull(_columnIndexOfSupplier)) {
            _tmpSupplier = null
          } else {
            _tmpSupplier = _stmt.getText(_columnIndexOfSupplier)
          }
          val _tmpBatchCode: String?
          if (_stmt.isNull(_columnIndexOfBatchCode)) {
            _tmpBatchCode = null
          } else {
            _tmpBatchCode = _stmt.getText(_columnIndexOfBatchCode)
          }
          val _tmpDoseMl: Double?
          if (_stmt.isNull(_columnIndexOfDoseMl)) {
            _tmpDoseMl = null
          } else {
            _tmpDoseMl = _stmt.getDouble(_columnIndexOfDoseMl)
          }
          val _tmpScheduledAt: Long
          _tmpScheduledAt = _stmt.getLong(_columnIndexOfScheduledAt)
          val _tmpAdministeredAt: Long?
          if (_stmt.isNull(_columnIndexOfAdministeredAt)) {
            _tmpAdministeredAt = null
          } else {
            _tmpAdministeredAt = _stmt.getLong(_columnIndexOfAdministeredAt)
          }
          val _tmpEfficacyNotes: String?
          if (_stmt.isNull(_columnIndexOfEfficacyNotes)) {
            _tmpEfficacyNotes = null
          } else {
            _tmpEfficacyNotes = _stmt.getText(_columnIndexOfEfficacyNotes)
          }
          val _tmpCostInr: Double?
          if (_stmt.isNull(_columnIndexOfCostInr)) {
            _tmpCostInr = null
          } else {
            _tmpCostInr = _stmt.getDouble(_columnIndexOfCostInr)
          }
          val _tmpPhotoUrls: String?
          if (_stmt.isNull(_columnIndexOfPhotoUrls)) {
            _tmpPhotoUrls = null
          } else {
            _tmpPhotoUrls = _stmt.getText(_columnIndexOfPhotoUrls)
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
          _item =
              VaccinationRecordEntity(_tmpVaccinationId,_tmpProductId,_tmpFarmerId,_tmpVaccineType,_tmpSupplier,_tmpBatchCode,_tmpDoseMl,_tmpScheduledAt,_tmpAdministeredAt,_tmpEfficacyNotes,_tmpCostInr,_tmpPhotoUrls,_tmpMediaItemsJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpCorrectionOf,_tmpEditCount,_tmpLastEditedBy)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearDirty(vaccinationIds: List<String>, syncedAt: Long) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("UPDATE vaccination_records SET dirty = 0, syncedAt = ")
    _stringBuilder.append("?")
    _stringBuilder.append(" WHERE vaccinationId IN (")
    val _inputSize: Int = vaccinationIds.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, syncedAt)
        _argIndex = 2
        for (_item: String in vaccinationIds) {
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
