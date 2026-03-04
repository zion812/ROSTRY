package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.MortalityRecordEntity
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
public class MortalityRecordDao_Impl(
  __db: RoomDatabase,
) : MortalityRecordDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfMortalityRecordEntity: EntityInsertAdapter<MortalityRecordEntity>

  private val __insertAdapterOfMortalityRecordEntity_1: EntityInsertAdapter<MortalityRecordEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfMortalityRecordEntity = object :
        EntityInsertAdapter<MortalityRecordEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `mortality_records` (`deathId`,`productId`,`farmerId`,`causeCategory`,`circumstances`,`ageWeeks`,`disposalMethod`,`quantity`,`financialImpactInr`,`photoUrls`,`mediaItemsJson`,`occurredAt`,`updatedAt`,`dirty`,`syncedAt`,`affectedProductIds`,`affectsAllChildren`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: MortalityRecordEntity) {
        statement.bindText(1, entity.deathId)
        val _tmpProductId: String? = entity.productId
        if (_tmpProductId == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpProductId)
        }
        statement.bindText(3, entity.farmerId)
        statement.bindText(4, entity.causeCategory)
        val _tmpCircumstances: String? = entity.circumstances
        if (_tmpCircumstances == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpCircumstances)
        }
        val _tmpAgeWeeks: Int? = entity.ageWeeks
        if (_tmpAgeWeeks == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpAgeWeeks.toLong())
        }
        val _tmpDisposalMethod: String? = entity.disposalMethod
        if (_tmpDisposalMethod == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpDisposalMethod)
        }
        statement.bindLong(8, entity.quantity.toLong())
        val _tmpFinancialImpactInr: Double? = entity.financialImpactInr
        if (_tmpFinancialImpactInr == null) {
          statement.bindNull(9)
        } else {
          statement.bindDouble(9, _tmpFinancialImpactInr)
        }
        val _tmpPhotoUrls: String? = entity.photoUrls
        if (_tmpPhotoUrls == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpPhotoUrls)
        }
        val _tmpMediaItemsJson: String? = entity.mediaItemsJson
        if (_tmpMediaItemsJson == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpMediaItemsJson)
        }
        statement.bindLong(12, entity.occurredAt)
        statement.bindLong(13, entity.updatedAt)
        val _tmp: Int = if (entity.dirty) 1 else 0
        statement.bindLong(14, _tmp.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpSyncedAt)
        }
        val _tmpAffectedProductIds: String? = entity.affectedProductIds
        if (_tmpAffectedProductIds == null) {
          statement.bindNull(16)
        } else {
          statement.bindText(16, _tmpAffectedProductIds)
        }
        val _tmp_1: Int = if (entity.affectsAllChildren) 1 else 0
        statement.bindLong(17, _tmp_1.toLong())
      }
    }
    this.__insertAdapterOfMortalityRecordEntity_1 = object :
        EntityInsertAdapter<MortalityRecordEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `mortality_records` (`deathId`,`productId`,`farmerId`,`causeCategory`,`circumstances`,`ageWeeks`,`disposalMethod`,`quantity`,`financialImpactInr`,`photoUrls`,`mediaItemsJson`,`occurredAt`,`updatedAt`,`dirty`,`syncedAt`,`affectedProductIds`,`affectsAllChildren`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: MortalityRecordEntity) {
        statement.bindText(1, entity.deathId)
        val _tmpProductId: String? = entity.productId
        if (_tmpProductId == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpProductId)
        }
        statement.bindText(3, entity.farmerId)
        statement.bindText(4, entity.causeCategory)
        val _tmpCircumstances: String? = entity.circumstances
        if (_tmpCircumstances == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpCircumstances)
        }
        val _tmpAgeWeeks: Int? = entity.ageWeeks
        if (_tmpAgeWeeks == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpAgeWeeks.toLong())
        }
        val _tmpDisposalMethod: String? = entity.disposalMethod
        if (_tmpDisposalMethod == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpDisposalMethod)
        }
        statement.bindLong(8, entity.quantity.toLong())
        val _tmpFinancialImpactInr: Double? = entity.financialImpactInr
        if (_tmpFinancialImpactInr == null) {
          statement.bindNull(9)
        } else {
          statement.bindDouble(9, _tmpFinancialImpactInr)
        }
        val _tmpPhotoUrls: String? = entity.photoUrls
        if (_tmpPhotoUrls == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpPhotoUrls)
        }
        val _tmpMediaItemsJson: String? = entity.mediaItemsJson
        if (_tmpMediaItemsJson == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpMediaItemsJson)
        }
        statement.bindLong(12, entity.occurredAt)
        statement.bindLong(13, entity.updatedAt)
        val _tmp: Int = if (entity.dirty) 1 else 0
        statement.bindLong(14, _tmp.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(15)
        } else {
          statement.bindLong(15, _tmpSyncedAt)
        }
        val _tmpAffectedProductIds: String? = entity.affectedProductIds
        if (_tmpAffectedProductIds == null) {
          statement.bindNull(16)
        } else {
          statement.bindText(16, _tmpAffectedProductIds)
        }
        val _tmp_1: Int = if (entity.affectsAllChildren) 1 else 0
        statement.bindLong(17, _tmp_1.toLong())
      }
    }
  }

  public override suspend fun insert(record: MortalityRecordEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfMortalityRecordEntity.insert(_connection, record)
  }

  public override suspend fun upsert(record: MortalityRecordEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfMortalityRecordEntity_1.insert(_connection, record)
  }

  public override fun observeAll(): Flow<List<MortalityRecordEntity>> {
    val _sql: String = "SELECT * FROM mortality_records ORDER BY occurredAt DESC"
    return createFlow(__db, false, arrayOf("mortality_records")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfDeathId: Int = getColumnIndexOrThrow(_stmt, "deathId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfCauseCategory: Int = getColumnIndexOrThrow(_stmt, "causeCategory")
        val _columnIndexOfCircumstances: Int = getColumnIndexOrThrow(_stmt, "circumstances")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfDisposalMethod: Int = getColumnIndexOrThrow(_stmt, "disposalMethod")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfFinancialImpactInr: Int = getColumnIndexOrThrow(_stmt,
            "financialImpactInr")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfOccurredAt: Int = getColumnIndexOrThrow(_stmt, "occurredAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfAffectedProductIds: Int = getColumnIndexOrThrow(_stmt,
            "affectedProductIds")
        val _columnIndexOfAffectsAllChildren: Int = getColumnIndexOrThrow(_stmt,
            "affectsAllChildren")
        val _result: MutableList<MortalityRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MortalityRecordEntity
          val _tmpDeathId: String
          _tmpDeathId = _stmt.getText(_columnIndexOfDeathId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpCauseCategory: String
          _tmpCauseCategory = _stmt.getText(_columnIndexOfCauseCategory)
          val _tmpCircumstances: String?
          if (_stmt.isNull(_columnIndexOfCircumstances)) {
            _tmpCircumstances = null
          } else {
            _tmpCircumstances = _stmt.getText(_columnIndexOfCircumstances)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpDisposalMethod: String?
          if (_stmt.isNull(_columnIndexOfDisposalMethod)) {
            _tmpDisposalMethod = null
          } else {
            _tmpDisposalMethod = _stmt.getText(_columnIndexOfDisposalMethod)
          }
          val _tmpQuantity: Int
          _tmpQuantity = _stmt.getLong(_columnIndexOfQuantity).toInt()
          val _tmpFinancialImpactInr: Double?
          if (_stmt.isNull(_columnIndexOfFinancialImpactInr)) {
            _tmpFinancialImpactInr = null
          } else {
            _tmpFinancialImpactInr = _stmt.getDouble(_columnIndexOfFinancialImpactInr)
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
          val _tmpOccurredAt: Long
          _tmpOccurredAt = _stmt.getLong(_columnIndexOfOccurredAt)
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
          val _tmpAffectedProductIds: String?
          if (_stmt.isNull(_columnIndexOfAffectedProductIds)) {
            _tmpAffectedProductIds = null
          } else {
            _tmpAffectedProductIds = _stmt.getText(_columnIndexOfAffectedProductIds)
          }
          val _tmpAffectsAllChildren: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfAffectsAllChildren).toInt()
          _tmpAffectsAllChildren = _tmp_1 != 0
          _item =
              MortalityRecordEntity(_tmpDeathId,_tmpProductId,_tmpFarmerId,_tmpCauseCategory,_tmpCircumstances,_tmpAgeWeeks,_tmpDisposalMethod,_tmpQuantity,_tmpFinancialImpactInr,_tmpPhotoUrls,_tmpMediaItemsJson,_tmpOccurredAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpAffectedProductIds,_tmpAffectsAllChildren)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun byCategory(category: String): List<MortalityRecordEntity> {
    val _sql: String =
        "SELECT * FROM mortality_records WHERE causeCategory = ? ORDER BY occurredAt DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, category)
        val _columnIndexOfDeathId: Int = getColumnIndexOrThrow(_stmt, "deathId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfCauseCategory: Int = getColumnIndexOrThrow(_stmt, "causeCategory")
        val _columnIndexOfCircumstances: Int = getColumnIndexOrThrow(_stmt, "circumstances")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfDisposalMethod: Int = getColumnIndexOrThrow(_stmt, "disposalMethod")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfFinancialImpactInr: Int = getColumnIndexOrThrow(_stmt,
            "financialImpactInr")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfOccurredAt: Int = getColumnIndexOrThrow(_stmt, "occurredAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfAffectedProductIds: Int = getColumnIndexOrThrow(_stmt,
            "affectedProductIds")
        val _columnIndexOfAffectsAllChildren: Int = getColumnIndexOrThrow(_stmt,
            "affectsAllChildren")
        val _result: MutableList<MortalityRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MortalityRecordEntity
          val _tmpDeathId: String
          _tmpDeathId = _stmt.getText(_columnIndexOfDeathId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpCauseCategory: String
          _tmpCauseCategory = _stmt.getText(_columnIndexOfCauseCategory)
          val _tmpCircumstances: String?
          if (_stmt.isNull(_columnIndexOfCircumstances)) {
            _tmpCircumstances = null
          } else {
            _tmpCircumstances = _stmt.getText(_columnIndexOfCircumstances)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpDisposalMethod: String?
          if (_stmt.isNull(_columnIndexOfDisposalMethod)) {
            _tmpDisposalMethod = null
          } else {
            _tmpDisposalMethod = _stmt.getText(_columnIndexOfDisposalMethod)
          }
          val _tmpQuantity: Int
          _tmpQuantity = _stmt.getLong(_columnIndexOfQuantity).toInt()
          val _tmpFinancialImpactInr: Double?
          if (_stmt.isNull(_columnIndexOfFinancialImpactInr)) {
            _tmpFinancialImpactInr = null
          } else {
            _tmpFinancialImpactInr = _stmt.getDouble(_columnIndexOfFinancialImpactInr)
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
          val _tmpOccurredAt: Long
          _tmpOccurredAt = _stmt.getLong(_columnIndexOfOccurredAt)
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
          val _tmpAffectedProductIds: String?
          if (_stmt.isNull(_columnIndexOfAffectedProductIds)) {
            _tmpAffectedProductIds = null
          } else {
            _tmpAffectedProductIds = _stmt.getText(_columnIndexOfAffectedProductIds)
          }
          val _tmpAffectsAllChildren: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfAffectsAllChildren).toInt()
          _tmpAffectsAllChildren = _tmp_1 != 0
          _item =
              MortalityRecordEntity(_tmpDeathId,_tmpProductId,_tmpFarmerId,_tmpCauseCategory,_tmpCircumstances,_tmpAgeWeeks,_tmpDisposalMethod,_tmpQuantity,_tmpFinancialImpactInr,_tmpPhotoUrls,_tmpMediaItemsJson,_tmpOccurredAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpAffectedProductIds,_tmpAffectsAllChildren)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countBetween(start: Long, end: Long): Int {
    val _sql: String = "SELECT COUNT(*) FROM mortality_records WHERE occurredAt BETWEEN ? AND ?"
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
        "SELECT COUNT(*) FROM mortality_records WHERE farmerId = ? AND occurredAt BETWEEN ? AND ?"
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
  ): List<MortalityRecordEntity> {
    val _sql: String =
        "SELECT * FROM mortality_records WHERE farmerId = ? AND occurredAt BETWEEN ? AND ? ORDER BY occurredAt ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, start)
        _argIndex = 3
        _stmt.bindLong(_argIndex, end)
        val _columnIndexOfDeathId: Int = getColumnIndexOrThrow(_stmt, "deathId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfCauseCategory: Int = getColumnIndexOrThrow(_stmt, "causeCategory")
        val _columnIndexOfCircumstances: Int = getColumnIndexOrThrow(_stmt, "circumstances")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfDisposalMethod: Int = getColumnIndexOrThrow(_stmt, "disposalMethod")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfFinancialImpactInr: Int = getColumnIndexOrThrow(_stmt,
            "financialImpactInr")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfOccurredAt: Int = getColumnIndexOrThrow(_stmt, "occurredAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfAffectedProductIds: Int = getColumnIndexOrThrow(_stmt,
            "affectedProductIds")
        val _columnIndexOfAffectsAllChildren: Int = getColumnIndexOrThrow(_stmt,
            "affectsAllChildren")
        val _result: MutableList<MortalityRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MortalityRecordEntity
          val _tmpDeathId: String
          _tmpDeathId = _stmt.getText(_columnIndexOfDeathId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpCauseCategory: String
          _tmpCauseCategory = _stmt.getText(_columnIndexOfCauseCategory)
          val _tmpCircumstances: String?
          if (_stmt.isNull(_columnIndexOfCircumstances)) {
            _tmpCircumstances = null
          } else {
            _tmpCircumstances = _stmt.getText(_columnIndexOfCircumstances)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpDisposalMethod: String?
          if (_stmt.isNull(_columnIndexOfDisposalMethod)) {
            _tmpDisposalMethod = null
          } else {
            _tmpDisposalMethod = _stmt.getText(_columnIndexOfDisposalMethod)
          }
          val _tmpQuantity: Int
          _tmpQuantity = _stmt.getLong(_columnIndexOfQuantity).toInt()
          val _tmpFinancialImpactInr: Double?
          if (_stmt.isNull(_columnIndexOfFinancialImpactInr)) {
            _tmpFinancialImpactInr = null
          } else {
            _tmpFinancialImpactInr = _stmt.getDouble(_columnIndexOfFinancialImpactInr)
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
          val _tmpOccurredAt: Long
          _tmpOccurredAt = _stmt.getLong(_columnIndexOfOccurredAt)
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
          val _tmpAffectedProductIds: String?
          if (_stmt.isNull(_columnIndexOfAffectedProductIds)) {
            _tmpAffectedProductIds = null
          } else {
            _tmpAffectedProductIds = _stmt.getText(_columnIndexOfAffectedProductIds)
          }
          val _tmpAffectsAllChildren: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfAffectsAllChildren).toInt()
          _tmpAffectsAllChildren = _tmp_1 != 0
          _item =
              MortalityRecordEntity(_tmpDeathId,_tmpProductId,_tmpFarmerId,_tmpCauseCategory,_tmpCircumstances,_tmpAgeWeeks,_tmpDisposalMethod,_tmpQuantity,_tmpFinancialImpactInr,_tmpPhotoUrls,_tmpMediaItemsJson,_tmpOccurredAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpAffectedProductIds,_tmpAffectsAllChildren)
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
        "SELECT COUNT(*) FROM mortality_records WHERE farmerId = ? AND occurredAt BETWEEN ? AND ?"
    return createFlow(__db, false, arrayOf("mortality_records")) { _connection ->
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

  public override suspend fun getDirty(): List<MortalityRecordEntity> {
    val _sql: String = "SELECT * FROM mortality_records WHERE dirty = 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfDeathId: Int = getColumnIndexOrThrow(_stmt, "deathId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfCauseCategory: Int = getColumnIndexOrThrow(_stmt, "causeCategory")
        val _columnIndexOfCircumstances: Int = getColumnIndexOrThrow(_stmt, "circumstances")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfDisposalMethod: Int = getColumnIndexOrThrow(_stmt, "disposalMethod")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfFinancialImpactInr: Int = getColumnIndexOrThrow(_stmt,
            "financialImpactInr")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfOccurredAt: Int = getColumnIndexOrThrow(_stmt, "occurredAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfAffectedProductIds: Int = getColumnIndexOrThrow(_stmt,
            "affectedProductIds")
        val _columnIndexOfAffectsAllChildren: Int = getColumnIndexOrThrow(_stmt,
            "affectsAllChildren")
        val _result: MutableList<MortalityRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MortalityRecordEntity
          val _tmpDeathId: String
          _tmpDeathId = _stmt.getText(_columnIndexOfDeathId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpCauseCategory: String
          _tmpCauseCategory = _stmt.getText(_columnIndexOfCauseCategory)
          val _tmpCircumstances: String?
          if (_stmt.isNull(_columnIndexOfCircumstances)) {
            _tmpCircumstances = null
          } else {
            _tmpCircumstances = _stmt.getText(_columnIndexOfCircumstances)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpDisposalMethod: String?
          if (_stmt.isNull(_columnIndexOfDisposalMethod)) {
            _tmpDisposalMethod = null
          } else {
            _tmpDisposalMethod = _stmt.getText(_columnIndexOfDisposalMethod)
          }
          val _tmpQuantity: Int
          _tmpQuantity = _stmt.getLong(_columnIndexOfQuantity).toInt()
          val _tmpFinancialImpactInr: Double?
          if (_stmt.isNull(_columnIndexOfFinancialImpactInr)) {
            _tmpFinancialImpactInr = null
          } else {
            _tmpFinancialImpactInr = _stmt.getDouble(_columnIndexOfFinancialImpactInr)
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
          val _tmpOccurredAt: Long
          _tmpOccurredAt = _stmt.getLong(_columnIndexOfOccurredAt)
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
          val _tmpAffectedProductIds: String?
          if (_stmt.isNull(_columnIndexOfAffectedProductIds)) {
            _tmpAffectedProductIds = null
          } else {
            _tmpAffectedProductIds = _stmt.getText(_columnIndexOfAffectedProductIds)
          }
          val _tmpAffectsAllChildren: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfAffectsAllChildren).toInt()
          _tmpAffectsAllChildren = _tmp_1 != 0
          _item =
              MortalityRecordEntity(_tmpDeathId,_tmpProductId,_tmpFarmerId,_tmpCauseCategory,_tmpCircumstances,_tmpAgeWeeks,_tmpDisposalMethod,_tmpQuantity,_tmpFinancialImpactInr,_tmpPhotoUrls,_tmpMediaItemsJson,_tmpOccurredAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpAffectedProductIds,_tmpAffectsAllChildren)
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
        "SELECT COUNT(*) FROM mortality_records WHERE farmerId = ? AND occurredAt BETWEEN ? AND ?"
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
    val _sql: String = "SELECT COUNT(*) FROM mortality_records WHERE farmerId = ?"
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

  public override suspend fun getById(deathId: String): MortalityRecordEntity? {
    val _sql: String = "SELECT * FROM mortality_records WHERE deathId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, deathId)
        val _columnIndexOfDeathId: Int = getColumnIndexOrThrow(_stmt, "deathId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfCauseCategory: Int = getColumnIndexOrThrow(_stmt, "causeCategory")
        val _columnIndexOfCircumstances: Int = getColumnIndexOrThrow(_stmt, "circumstances")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfDisposalMethod: Int = getColumnIndexOrThrow(_stmt, "disposalMethod")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfFinancialImpactInr: Int = getColumnIndexOrThrow(_stmt,
            "financialImpactInr")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfOccurredAt: Int = getColumnIndexOrThrow(_stmt, "occurredAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfAffectedProductIds: Int = getColumnIndexOrThrow(_stmt,
            "affectedProductIds")
        val _columnIndexOfAffectsAllChildren: Int = getColumnIndexOrThrow(_stmt,
            "affectsAllChildren")
        val _result: MortalityRecordEntity?
        if (_stmt.step()) {
          val _tmpDeathId: String
          _tmpDeathId = _stmt.getText(_columnIndexOfDeathId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpCauseCategory: String
          _tmpCauseCategory = _stmt.getText(_columnIndexOfCauseCategory)
          val _tmpCircumstances: String?
          if (_stmt.isNull(_columnIndexOfCircumstances)) {
            _tmpCircumstances = null
          } else {
            _tmpCircumstances = _stmt.getText(_columnIndexOfCircumstances)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpDisposalMethod: String?
          if (_stmt.isNull(_columnIndexOfDisposalMethod)) {
            _tmpDisposalMethod = null
          } else {
            _tmpDisposalMethod = _stmt.getText(_columnIndexOfDisposalMethod)
          }
          val _tmpQuantity: Int
          _tmpQuantity = _stmt.getLong(_columnIndexOfQuantity).toInt()
          val _tmpFinancialImpactInr: Double?
          if (_stmt.isNull(_columnIndexOfFinancialImpactInr)) {
            _tmpFinancialImpactInr = null
          } else {
            _tmpFinancialImpactInr = _stmt.getDouble(_columnIndexOfFinancialImpactInr)
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
          val _tmpOccurredAt: Long
          _tmpOccurredAt = _stmt.getLong(_columnIndexOfOccurredAt)
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
          val _tmpAffectedProductIds: String?
          if (_stmt.isNull(_columnIndexOfAffectedProductIds)) {
            _tmpAffectedProductIds = null
          } else {
            _tmpAffectedProductIds = _stmt.getText(_columnIndexOfAffectedProductIds)
          }
          val _tmpAffectsAllChildren: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfAffectsAllChildren).toInt()
          _tmpAffectsAllChildren = _tmp_1 != 0
          _result =
              MortalityRecordEntity(_tmpDeathId,_tmpProductId,_tmpFarmerId,_tmpCauseCategory,_tmpCircumstances,_tmpAgeWeeks,_tmpDisposalMethod,_tmpQuantity,_tmpFinancialImpactInr,_tmpPhotoUrls,_tmpMediaItemsJson,_tmpOccurredAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpAffectedProductIds,_tmpAffectsAllChildren)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByProduct(productId: String): List<MortalityRecordEntity> {
    val _sql: String =
        "SELECT * FROM mortality_records WHERE productId = ? ORDER BY occurredAt DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfDeathId: Int = getColumnIndexOrThrow(_stmt, "deathId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfCauseCategory: Int = getColumnIndexOrThrow(_stmt, "causeCategory")
        val _columnIndexOfCircumstances: Int = getColumnIndexOrThrow(_stmt, "circumstances")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfDisposalMethod: Int = getColumnIndexOrThrow(_stmt, "disposalMethod")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfFinancialImpactInr: Int = getColumnIndexOrThrow(_stmt,
            "financialImpactInr")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfOccurredAt: Int = getColumnIndexOrThrow(_stmt, "occurredAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfAffectedProductIds: Int = getColumnIndexOrThrow(_stmt,
            "affectedProductIds")
        val _columnIndexOfAffectsAllChildren: Int = getColumnIndexOrThrow(_stmt,
            "affectsAllChildren")
        val _result: MutableList<MortalityRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MortalityRecordEntity
          val _tmpDeathId: String
          _tmpDeathId = _stmt.getText(_columnIndexOfDeathId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpCauseCategory: String
          _tmpCauseCategory = _stmt.getText(_columnIndexOfCauseCategory)
          val _tmpCircumstances: String?
          if (_stmt.isNull(_columnIndexOfCircumstances)) {
            _tmpCircumstances = null
          } else {
            _tmpCircumstances = _stmt.getText(_columnIndexOfCircumstances)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpDisposalMethod: String?
          if (_stmt.isNull(_columnIndexOfDisposalMethod)) {
            _tmpDisposalMethod = null
          } else {
            _tmpDisposalMethod = _stmt.getText(_columnIndexOfDisposalMethod)
          }
          val _tmpQuantity: Int
          _tmpQuantity = _stmt.getLong(_columnIndexOfQuantity).toInt()
          val _tmpFinancialImpactInr: Double?
          if (_stmt.isNull(_columnIndexOfFinancialImpactInr)) {
            _tmpFinancialImpactInr = null
          } else {
            _tmpFinancialImpactInr = _stmt.getDouble(_columnIndexOfFinancialImpactInr)
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
          val _tmpOccurredAt: Long
          _tmpOccurredAt = _stmt.getLong(_columnIndexOfOccurredAt)
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
          val _tmpAffectedProductIds: String?
          if (_stmt.isNull(_columnIndexOfAffectedProductIds)) {
            _tmpAffectedProductIds = null
          } else {
            _tmpAffectedProductIds = _stmt.getText(_columnIndexOfAffectedProductIds)
          }
          val _tmpAffectsAllChildren: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfAffectsAllChildren).toInt()
          _tmpAffectsAllChildren = _tmp_1 != 0
          _item =
              MortalityRecordEntity(_tmpDeathId,_tmpProductId,_tmpFarmerId,_tmpCauseCategory,_tmpCircumstances,_tmpAgeWeeks,_tmpDisposalMethod,_tmpQuantity,_tmpFinancialImpactInr,_tmpPhotoUrls,_tmpMediaItemsJson,_tmpOccurredAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpAffectedProductIds,_tmpAffectsAllChildren)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getRecordsForProduct(productId: String): List<MortalityRecordEntity> {
    val _sql: String =
        "SELECT * FROM mortality_records WHERE productId = ? ORDER BY occurredAt DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfDeathId: Int = getColumnIndexOrThrow(_stmt, "deathId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfCauseCategory: Int = getColumnIndexOrThrow(_stmt, "causeCategory")
        val _columnIndexOfCircumstances: Int = getColumnIndexOrThrow(_stmt, "circumstances")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfDisposalMethod: Int = getColumnIndexOrThrow(_stmt, "disposalMethod")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfFinancialImpactInr: Int = getColumnIndexOrThrow(_stmt,
            "financialImpactInr")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfOccurredAt: Int = getColumnIndexOrThrow(_stmt, "occurredAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfAffectedProductIds: Int = getColumnIndexOrThrow(_stmt,
            "affectedProductIds")
        val _columnIndexOfAffectsAllChildren: Int = getColumnIndexOrThrow(_stmt,
            "affectsAllChildren")
        val _result: MutableList<MortalityRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MortalityRecordEntity
          val _tmpDeathId: String
          _tmpDeathId = _stmt.getText(_columnIndexOfDeathId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpCauseCategory: String
          _tmpCauseCategory = _stmt.getText(_columnIndexOfCauseCategory)
          val _tmpCircumstances: String?
          if (_stmt.isNull(_columnIndexOfCircumstances)) {
            _tmpCircumstances = null
          } else {
            _tmpCircumstances = _stmt.getText(_columnIndexOfCircumstances)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpDisposalMethod: String?
          if (_stmt.isNull(_columnIndexOfDisposalMethod)) {
            _tmpDisposalMethod = null
          } else {
            _tmpDisposalMethod = _stmt.getText(_columnIndexOfDisposalMethod)
          }
          val _tmpQuantity: Int
          _tmpQuantity = _stmt.getLong(_columnIndexOfQuantity).toInt()
          val _tmpFinancialImpactInr: Double?
          if (_stmt.isNull(_columnIndexOfFinancialImpactInr)) {
            _tmpFinancialImpactInr = null
          } else {
            _tmpFinancialImpactInr = _stmt.getDouble(_columnIndexOfFinancialImpactInr)
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
          val _tmpOccurredAt: Long
          _tmpOccurredAt = _stmt.getLong(_columnIndexOfOccurredAt)
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
          val _tmpAffectedProductIds: String?
          if (_stmt.isNull(_columnIndexOfAffectedProductIds)) {
            _tmpAffectedProductIds = null
          } else {
            _tmpAffectedProductIds = _stmt.getText(_columnIndexOfAffectedProductIds)
          }
          val _tmpAffectsAllChildren: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfAffectsAllChildren).toInt()
          _tmpAffectsAllChildren = _tmp_1 != 0
          _item =
              MortalityRecordEntity(_tmpDeathId,_tmpProductId,_tmpFarmerId,_tmpCauseCategory,_tmpCircumstances,_tmpAgeWeeks,_tmpDisposalMethod,_tmpQuantity,_tmpFinancialImpactInr,_tmpPhotoUrls,_tmpMediaItemsJson,_tmpOccurredAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpAffectedProductIds,_tmpAffectsAllChildren)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTotalMortalityImpactForAsset(assetId: String): Double {
    val _sql: String =
        "SELECT COALESCE(SUM(financialImpactInr), 0.0) FROM mortality_records WHERE productId = ?"
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

  public override suspend fun getTotalMortalityCountForAsset(assetId: String): Int {
    val _sql: String =
        "SELECT COALESCE(SUM(quantity), 0) FROM mortality_records WHERE productId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, assetId)
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

  public override suspend fun getTotalMortalityImpactByFarmer(farmerId: String): Double {
    val _sql: String =
        "SELECT COALESCE(SUM(financialImpactInr), 0.0) FROM mortality_records WHERE farmerId = ?"
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

  public override suspend fun getTotalMortalityImpactByFarmerBetween(
    farmerId: String,
    start: Long,
    end: Long,
  ): Double {
    val _sql: String =
        "SELECT COALESCE(SUM(financialImpactInr), 0.0) FROM mortality_records WHERE farmerId = ? AND occurredAt BETWEEN ? AND ?"
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

  public override suspend fun getRecentRecords(since: Long): List<MortalityRecordEntity> {
    val _sql: String =
        "SELECT * FROM mortality_records WHERE occurredAt > ? ORDER BY occurredAt DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, since)
        val _columnIndexOfDeathId: Int = getColumnIndexOrThrow(_stmt, "deathId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfCauseCategory: Int = getColumnIndexOrThrow(_stmt, "causeCategory")
        val _columnIndexOfCircumstances: Int = getColumnIndexOrThrow(_stmt, "circumstances")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfDisposalMethod: Int = getColumnIndexOrThrow(_stmt, "disposalMethod")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfFinancialImpactInr: Int = getColumnIndexOrThrow(_stmt,
            "financialImpactInr")
        val _columnIndexOfPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "photoUrls")
        val _columnIndexOfMediaItemsJson: Int = getColumnIndexOrThrow(_stmt, "mediaItemsJson")
        val _columnIndexOfOccurredAt: Int = getColumnIndexOrThrow(_stmt, "occurredAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfAffectedProductIds: Int = getColumnIndexOrThrow(_stmt,
            "affectedProductIds")
        val _columnIndexOfAffectsAllChildren: Int = getColumnIndexOrThrow(_stmt,
            "affectsAllChildren")
        val _result: MutableList<MortalityRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MortalityRecordEntity
          val _tmpDeathId: String
          _tmpDeathId = _stmt.getText(_columnIndexOfDeathId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpCauseCategory: String
          _tmpCauseCategory = _stmt.getText(_columnIndexOfCauseCategory)
          val _tmpCircumstances: String?
          if (_stmt.isNull(_columnIndexOfCircumstances)) {
            _tmpCircumstances = null
          } else {
            _tmpCircumstances = _stmt.getText(_columnIndexOfCircumstances)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpDisposalMethod: String?
          if (_stmt.isNull(_columnIndexOfDisposalMethod)) {
            _tmpDisposalMethod = null
          } else {
            _tmpDisposalMethod = _stmt.getText(_columnIndexOfDisposalMethod)
          }
          val _tmpQuantity: Int
          _tmpQuantity = _stmt.getLong(_columnIndexOfQuantity).toInt()
          val _tmpFinancialImpactInr: Double?
          if (_stmt.isNull(_columnIndexOfFinancialImpactInr)) {
            _tmpFinancialImpactInr = null
          } else {
            _tmpFinancialImpactInr = _stmt.getDouble(_columnIndexOfFinancialImpactInr)
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
          val _tmpOccurredAt: Long
          _tmpOccurredAt = _stmt.getLong(_columnIndexOfOccurredAt)
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
          val _tmpAffectedProductIds: String?
          if (_stmt.isNull(_columnIndexOfAffectedProductIds)) {
            _tmpAffectedProductIds = null
          } else {
            _tmpAffectedProductIds = _stmt.getText(_columnIndexOfAffectedProductIds)
          }
          val _tmpAffectsAllChildren: Boolean
          val _tmp_1: Int
          _tmp_1 = _stmt.getLong(_columnIndexOfAffectsAllChildren).toInt()
          _tmpAffectsAllChildren = _tmp_1 != 0
          _item =
              MortalityRecordEntity(_tmpDeathId,_tmpProductId,_tmpFarmerId,_tmpCauseCategory,_tmpCircumstances,_tmpAgeWeeks,_tmpDisposalMethod,_tmpQuantity,_tmpFinancialImpactInr,_tmpPhotoUrls,_tmpMediaItemsJson,_tmpOccurredAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpAffectedProductIds,_tmpAffectsAllChildren)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearDirty(deathIds: List<String>, syncedAt: Long) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("UPDATE mortality_records SET dirty = 0, syncedAt = ")
    _stringBuilder.append("?")
    _stringBuilder.append(" WHERE deathId IN (")
    val _inputSize: Int = deathIds.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, syncedAt)
        _argIndex = 2
        for (_item: String in deathIds) {
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
