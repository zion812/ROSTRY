package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.BirdTraitRecordEntity
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
public class BirdTraitRecordDao_Impl(
  __db: RoomDatabase,
) : BirdTraitRecordDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfBirdTraitRecordEntity: EntityInsertAdapter<BirdTraitRecordEntity>

  private val __updateAdapterOfBirdTraitRecordEntity:
      EntityDeleteOrUpdateAdapter<BirdTraitRecordEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfBirdTraitRecordEntity = object :
        EntityInsertAdapter<BirdTraitRecordEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `bird_trait_records` (`recordId`,`productId`,`ownerId`,`traitCategory`,`traitName`,`traitValue`,`traitUnit`,`numericValue`,`ageWeeks`,`recordedAt`,`measuredBy`,`notes`,`createdAt`,`updatedAt`,`dirty`,`syncedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: BirdTraitRecordEntity) {
        statement.bindText(1, entity.recordId)
        statement.bindText(2, entity.productId)
        statement.bindText(3, entity.ownerId)
        statement.bindText(4, entity.traitCategory)
        statement.bindText(5, entity.traitName)
        statement.bindText(6, entity.traitValue)
        val _tmpTraitUnit: String? = entity.traitUnit
        if (_tmpTraitUnit == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpTraitUnit)
        }
        val _tmpNumericValue: Double? = entity.numericValue
        if (_tmpNumericValue == null) {
          statement.bindNull(8)
        } else {
          statement.bindDouble(8, _tmpNumericValue)
        }
        val _tmpAgeWeeks: Int? = entity.ageWeeks
        if (_tmpAgeWeeks == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpAgeWeeks.toLong())
        }
        statement.bindLong(10, entity.recordedAt)
        val _tmpMeasuredBy: String? = entity.measuredBy
        if (_tmpMeasuredBy == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpMeasuredBy)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(12)
        } else {
          statement.bindText(12, _tmpNotes)
        }
        statement.bindLong(13, entity.createdAt)
        statement.bindLong(14, entity.updatedAt)
        val _tmp: Int = if (entity.dirty) 1 else 0
        statement.bindLong(15, _tmp.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(16)
        } else {
          statement.bindLong(16, _tmpSyncedAt)
        }
      }
    }
    this.__updateAdapterOfBirdTraitRecordEntity = object :
        EntityDeleteOrUpdateAdapter<BirdTraitRecordEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `bird_trait_records` SET `recordId` = ?,`productId` = ?,`ownerId` = ?,`traitCategory` = ?,`traitName` = ?,`traitValue` = ?,`traitUnit` = ?,`numericValue` = ?,`ageWeeks` = ?,`recordedAt` = ?,`measuredBy` = ?,`notes` = ?,`createdAt` = ?,`updatedAt` = ?,`dirty` = ?,`syncedAt` = ? WHERE `recordId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: BirdTraitRecordEntity) {
        statement.bindText(1, entity.recordId)
        statement.bindText(2, entity.productId)
        statement.bindText(3, entity.ownerId)
        statement.bindText(4, entity.traitCategory)
        statement.bindText(5, entity.traitName)
        statement.bindText(6, entity.traitValue)
        val _tmpTraitUnit: String? = entity.traitUnit
        if (_tmpTraitUnit == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpTraitUnit)
        }
        val _tmpNumericValue: Double? = entity.numericValue
        if (_tmpNumericValue == null) {
          statement.bindNull(8)
        } else {
          statement.bindDouble(8, _tmpNumericValue)
        }
        val _tmpAgeWeeks: Int? = entity.ageWeeks
        if (_tmpAgeWeeks == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpAgeWeeks.toLong())
        }
        statement.bindLong(10, entity.recordedAt)
        val _tmpMeasuredBy: String? = entity.measuredBy
        if (_tmpMeasuredBy == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpMeasuredBy)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(12)
        } else {
          statement.bindText(12, _tmpNotes)
        }
        statement.bindLong(13, entity.createdAt)
        statement.bindLong(14, entity.updatedAt)
        val _tmp: Int = if (entity.dirty) 1 else 0
        statement.bindLong(15, _tmp.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(16)
        } else {
          statement.bindLong(16, _tmpSyncedAt)
        }
        statement.bindText(17, entity.recordId)
      }
    }
  }

  public override suspend fun insert(record: BirdTraitRecordEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfBirdTraitRecordEntity.insert(_connection, record)
  }

  public override suspend fun insertAll(records: List<BirdTraitRecordEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfBirdTraitRecordEntity.insert(_connection, records)
  }

  public override suspend fun update(record: BirdTraitRecordEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __updateAdapterOfBirdTraitRecordEntity.handle(_connection, record)
  }

  public override suspend fun findById(recordId: String): BirdTraitRecordEntity? {
    val _sql: String = "SELECT * FROM bird_trait_records WHERE recordId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, recordId)
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfTraitCategory: Int = getColumnIndexOrThrow(_stmt, "traitCategory")
        val _columnIndexOfTraitName: Int = getColumnIndexOrThrow(_stmt, "traitName")
        val _columnIndexOfTraitValue: Int = getColumnIndexOrThrow(_stmt, "traitValue")
        val _columnIndexOfTraitUnit: Int = getColumnIndexOrThrow(_stmt, "traitUnit")
        val _columnIndexOfNumericValue: Int = getColumnIndexOrThrow(_stmt, "numericValue")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfRecordedAt: Int = getColumnIndexOrThrow(_stmt, "recordedAt")
        val _columnIndexOfMeasuredBy: Int = getColumnIndexOrThrow(_stmt, "measuredBy")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: BirdTraitRecordEntity?
        if (_stmt.step()) {
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpTraitCategory: String
          _tmpTraitCategory = _stmt.getText(_columnIndexOfTraitCategory)
          val _tmpTraitName: String
          _tmpTraitName = _stmt.getText(_columnIndexOfTraitName)
          val _tmpTraitValue: String
          _tmpTraitValue = _stmt.getText(_columnIndexOfTraitValue)
          val _tmpTraitUnit: String?
          if (_stmt.isNull(_columnIndexOfTraitUnit)) {
            _tmpTraitUnit = null
          } else {
            _tmpTraitUnit = _stmt.getText(_columnIndexOfTraitUnit)
          }
          val _tmpNumericValue: Double?
          if (_stmt.isNull(_columnIndexOfNumericValue)) {
            _tmpNumericValue = null
          } else {
            _tmpNumericValue = _stmt.getDouble(_columnIndexOfNumericValue)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpRecordedAt: Long
          _tmpRecordedAt = _stmt.getLong(_columnIndexOfRecordedAt)
          val _tmpMeasuredBy: String?
          if (_stmt.isNull(_columnIndexOfMeasuredBy)) {
            _tmpMeasuredBy = null
          } else {
            _tmpMeasuredBy = _stmt.getText(_columnIndexOfMeasuredBy)
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
              BirdTraitRecordEntity(_tmpRecordId,_tmpProductId,_tmpOwnerId,_tmpTraitCategory,_tmpTraitName,_tmpTraitValue,_tmpTraitUnit,_tmpNumericValue,_tmpAgeWeeks,_tmpRecordedAt,_tmpMeasuredBy,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByBird(productId: String): Flow<List<BirdTraitRecordEntity>> {
    val _sql: String =
        "SELECT * FROM bird_trait_records WHERE productId = ? ORDER BY recordedAt DESC"
    return createFlow(__db, false, arrayOf("bird_trait_records")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfTraitCategory: Int = getColumnIndexOrThrow(_stmt, "traitCategory")
        val _columnIndexOfTraitName: Int = getColumnIndexOrThrow(_stmt, "traitName")
        val _columnIndexOfTraitValue: Int = getColumnIndexOrThrow(_stmt, "traitValue")
        val _columnIndexOfTraitUnit: Int = getColumnIndexOrThrow(_stmt, "traitUnit")
        val _columnIndexOfNumericValue: Int = getColumnIndexOrThrow(_stmt, "numericValue")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfRecordedAt: Int = getColumnIndexOrThrow(_stmt, "recordedAt")
        val _columnIndexOfMeasuredBy: Int = getColumnIndexOrThrow(_stmt, "measuredBy")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<BirdTraitRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BirdTraitRecordEntity
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpTraitCategory: String
          _tmpTraitCategory = _stmt.getText(_columnIndexOfTraitCategory)
          val _tmpTraitName: String
          _tmpTraitName = _stmt.getText(_columnIndexOfTraitName)
          val _tmpTraitValue: String
          _tmpTraitValue = _stmt.getText(_columnIndexOfTraitValue)
          val _tmpTraitUnit: String?
          if (_stmt.isNull(_columnIndexOfTraitUnit)) {
            _tmpTraitUnit = null
          } else {
            _tmpTraitUnit = _stmt.getText(_columnIndexOfTraitUnit)
          }
          val _tmpNumericValue: Double?
          if (_stmt.isNull(_columnIndexOfNumericValue)) {
            _tmpNumericValue = null
          } else {
            _tmpNumericValue = _stmt.getDouble(_columnIndexOfNumericValue)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpRecordedAt: Long
          _tmpRecordedAt = _stmt.getLong(_columnIndexOfRecordedAt)
          val _tmpMeasuredBy: String?
          if (_stmt.isNull(_columnIndexOfMeasuredBy)) {
            _tmpMeasuredBy = null
          } else {
            _tmpMeasuredBy = _stmt.getText(_columnIndexOfMeasuredBy)
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
              BirdTraitRecordEntity(_tmpRecordId,_tmpProductId,_tmpOwnerId,_tmpTraitCategory,_tmpTraitName,_tmpTraitValue,_tmpTraitUnit,_tmpNumericValue,_tmpAgeWeeks,_tmpRecordedAt,_tmpMeasuredBy,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getByBird(productId: String): List<BirdTraitRecordEntity> {
    val _sql: String =
        "SELECT * FROM bird_trait_records WHERE productId = ? ORDER BY recordedAt DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfTraitCategory: Int = getColumnIndexOrThrow(_stmt, "traitCategory")
        val _columnIndexOfTraitName: Int = getColumnIndexOrThrow(_stmt, "traitName")
        val _columnIndexOfTraitValue: Int = getColumnIndexOrThrow(_stmt, "traitValue")
        val _columnIndexOfTraitUnit: Int = getColumnIndexOrThrow(_stmt, "traitUnit")
        val _columnIndexOfNumericValue: Int = getColumnIndexOrThrow(_stmt, "numericValue")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfRecordedAt: Int = getColumnIndexOrThrow(_stmt, "recordedAt")
        val _columnIndexOfMeasuredBy: Int = getColumnIndexOrThrow(_stmt, "measuredBy")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<BirdTraitRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BirdTraitRecordEntity
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpTraitCategory: String
          _tmpTraitCategory = _stmt.getText(_columnIndexOfTraitCategory)
          val _tmpTraitName: String
          _tmpTraitName = _stmt.getText(_columnIndexOfTraitName)
          val _tmpTraitValue: String
          _tmpTraitValue = _stmt.getText(_columnIndexOfTraitValue)
          val _tmpTraitUnit: String?
          if (_stmt.isNull(_columnIndexOfTraitUnit)) {
            _tmpTraitUnit = null
          } else {
            _tmpTraitUnit = _stmt.getText(_columnIndexOfTraitUnit)
          }
          val _tmpNumericValue: Double?
          if (_stmt.isNull(_columnIndexOfNumericValue)) {
            _tmpNumericValue = null
          } else {
            _tmpNumericValue = _stmt.getDouble(_columnIndexOfNumericValue)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpRecordedAt: Long
          _tmpRecordedAt = _stmt.getLong(_columnIndexOfRecordedAt)
          val _tmpMeasuredBy: String?
          if (_stmt.isNull(_columnIndexOfMeasuredBy)) {
            _tmpMeasuredBy = null
          } else {
            _tmpMeasuredBy = _stmt.getText(_columnIndexOfMeasuredBy)
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
              BirdTraitRecordEntity(_tmpRecordId,_tmpProductId,_tmpOwnerId,_tmpTraitCategory,_tmpTraitName,_tmpTraitValue,_tmpTraitUnit,_tmpNumericValue,_tmpAgeWeeks,_tmpRecordedAt,_tmpMeasuredBy,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByBirdAndCategory(productId: String, category: String):
      Flow<List<BirdTraitRecordEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM bird_trait_records 
        |        WHERE productId = ? AND traitCategory = ? 
        |        ORDER BY recordedAt DESC
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("bird_trait_records")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        _argIndex = 2
        _stmt.bindText(_argIndex, category)
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfTraitCategory: Int = getColumnIndexOrThrow(_stmt, "traitCategory")
        val _columnIndexOfTraitName: Int = getColumnIndexOrThrow(_stmt, "traitName")
        val _columnIndexOfTraitValue: Int = getColumnIndexOrThrow(_stmt, "traitValue")
        val _columnIndexOfTraitUnit: Int = getColumnIndexOrThrow(_stmt, "traitUnit")
        val _columnIndexOfNumericValue: Int = getColumnIndexOrThrow(_stmt, "numericValue")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfRecordedAt: Int = getColumnIndexOrThrow(_stmt, "recordedAt")
        val _columnIndexOfMeasuredBy: Int = getColumnIndexOrThrow(_stmt, "measuredBy")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<BirdTraitRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BirdTraitRecordEntity
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpTraitCategory: String
          _tmpTraitCategory = _stmt.getText(_columnIndexOfTraitCategory)
          val _tmpTraitName: String
          _tmpTraitName = _stmt.getText(_columnIndexOfTraitName)
          val _tmpTraitValue: String
          _tmpTraitValue = _stmt.getText(_columnIndexOfTraitValue)
          val _tmpTraitUnit: String?
          if (_stmt.isNull(_columnIndexOfTraitUnit)) {
            _tmpTraitUnit = null
          } else {
            _tmpTraitUnit = _stmt.getText(_columnIndexOfTraitUnit)
          }
          val _tmpNumericValue: Double?
          if (_stmt.isNull(_columnIndexOfNumericValue)) {
            _tmpNumericValue = null
          } else {
            _tmpNumericValue = _stmt.getDouble(_columnIndexOfNumericValue)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpRecordedAt: Long
          _tmpRecordedAt = _stmt.getLong(_columnIndexOfRecordedAt)
          val _tmpMeasuredBy: String?
          if (_stmt.isNull(_columnIndexOfMeasuredBy)) {
            _tmpMeasuredBy = null
          } else {
            _tmpMeasuredBy = _stmt.getText(_columnIndexOfMeasuredBy)
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
              BirdTraitRecordEntity(_tmpRecordId,_tmpProductId,_tmpOwnerId,_tmpTraitCategory,_tmpTraitName,_tmpTraitValue,_tmpTraitUnit,_tmpNumericValue,_tmpAgeWeeks,_tmpRecordedAt,_tmpMeasuredBy,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getLatestTrait(productId: String, traitName: String):
      BirdTraitRecordEntity? {
    val _sql: String = """
        |
        |        SELECT * FROM bird_trait_records 
        |        WHERE productId = ? AND traitName = ? 
        |        ORDER BY recordedAt DESC LIMIT 1
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        _argIndex = 2
        _stmt.bindText(_argIndex, traitName)
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfTraitCategory: Int = getColumnIndexOrThrow(_stmt, "traitCategory")
        val _columnIndexOfTraitName: Int = getColumnIndexOrThrow(_stmt, "traitName")
        val _columnIndexOfTraitValue: Int = getColumnIndexOrThrow(_stmt, "traitValue")
        val _columnIndexOfTraitUnit: Int = getColumnIndexOrThrow(_stmt, "traitUnit")
        val _columnIndexOfNumericValue: Int = getColumnIndexOrThrow(_stmt, "numericValue")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfRecordedAt: Int = getColumnIndexOrThrow(_stmt, "recordedAt")
        val _columnIndexOfMeasuredBy: Int = getColumnIndexOrThrow(_stmt, "measuredBy")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: BirdTraitRecordEntity?
        if (_stmt.step()) {
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpTraitCategory: String
          _tmpTraitCategory = _stmt.getText(_columnIndexOfTraitCategory)
          val _tmpTraitName: String
          _tmpTraitName = _stmt.getText(_columnIndexOfTraitName)
          val _tmpTraitValue: String
          _tmpTraitValue = _stmt.getText(_columnIndexOfTraitValue)
          val _tmpTraitUnit: String?
          if (_stmt.isNull(_columnIndexOfTraitUnit)) {
            _tmpTraitUnit = null
          } else {
            _tmpTraitUnit = _stmt.getText(_columnIndexOfTraitUnit)
          }
          val _tmpNumericValue: Double?
          if (_stmt.isNull(_columnIndexOfNumericValue)) {
            _tmpNumericValue = null
          } else {
            _tmpNumericValue = _stmt.getDouble(_columnIndexOfNumericValue)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpRecordedAt: Long
          _tmpRecordedAt = _stmt.getLong(_columnIndexOfRecordedAt)
          val _tmpMeasuredBy: String?
          if (_stmt.isNull(_columnIndexOfMeasuredBy)) {
            _tmpMeasuredBy = null
          } else {
            _tmpMeasuredBy = _stmt.getText(_columnIndexOfMeasuredBy)
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
              BirdTraitRecordEntity(_tmpRecordId,_tmpProductId,_tmpOwnerId,_tmpTraitCategory,_tmpTraitName,_tmpTraitValue,_tmpTraitUnit,_tmpNumericValue,_tmpAgeWeeks,_tmpRecordedAt,_tmpMeasuredBy,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeTraitHistory(productId: String, traitName: String):
      Flow<List<BirdTraitRecordEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM bird_trait_records 
        |        WHERE productId = ? AND traitName = ? 
        |        ORDER BY ageWeeks ASC, recordedAt ASC
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("bird_trait_records")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        _argIndex = 2
        _stmt.bindText(_argIndex, traitName)
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfTraitCategory: Int = getColumnIndexOrThrow(_stmt, "traitCategory")
        val _columnIndexOfTraitName: Int = getColumnIndexOrThrow(_stmt, "traitName")
        val _columnIndexOfTraitValue: Int = getColumnIndexOrThrow(_stmt, "traitValue")
        val _columnIndexOfTraitUnit: Int = getColumnIndexOrThrow(_stmt, "traitUnit")
        val _columnIndexOfNumericValue: Int = getColumnIndexOrThrow(_stmt, "numericValue")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfRecordedAt: Int = getColumnIndexOrThrow(_stmt, "recordedAt")
        val _columnIndexOfMeasuredBy: Int = getColumnIndexOrThrow(_stmt, "measuredBy")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<BirdTraitRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BirdTraitRecordEntity
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpTraitCategory: String
          _tmpTraitCategory = _stmt.getText(_columnIndexOfTraitCategory)
          val _tmpTraitName: String
          _tmpTraitName = _stmt.getText(_columnIndexOfTraitName)
          val _tmpTraitValue: String
          _tmpTraitValue = _stmt.getText(_columnIndexOfTraitValue)
          val _tmpTraitUnit: String?
          if (_stmt.isNull(_columnIndexOfTraitUnit)) {
            _tmpTraitUnit = null
          } else {
            _tmpTraitUnit = _stmt.getText(_columnIndexOfTraitUnit)
          }
          val _tmpNumericValue: Double?
          if (_stmt.isNull(_columnIndexOfNumericValue)) {
            _tmpNumericValue = null
          } else {
            _tmpNumericValue = _stmt.getDouble(_columnIndexOfNumericValue)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpRecordedAt: Long
          _tmpRecordedAt = _stmt.getLong(_columnIndexOfRecordedAt)
          val _tmpMeasuredBy: String?
          if (_stmt.isNull(_columnIndexOfMeasuredBy)) {
            _tmpMeasuredBy = null
          } else {
            _tmpMeasuredBy = _stmt.getText(_columnIndexOfMeasuredBy)
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
              BirdTraitRecordEntity(_tmpRecordId,_tmpProductId,_tmpOwnerId,_tmpTraitCategory,_tmpTraitName,_tmpTraitValue,_tmpTraitUnit,_tmpNumericValue,_tmpAgeWeeks,_tmpRecordedAt,_tmpMeasuredBy,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTraitHistory(productId: String, traitName: String):
      List<BirdTraitRecordEntity> {
    val _sql: String = """
        |
        |        SELECT * FROM bird_trait_records 
        |        WHERE productId = ? AND traitName = ? 
        |        ORDER BY ageWeeks ASC, recordedAt ASC
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        _argIndex = 2
        _stmt.bindText(_argIndex, traitName)
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfTraitCategory: Int = getColumnIndexOrThrow(_stmt, "traitCategory")
        val _columnIndexOfTraitName: Int = getColumnIndexOrThrow(_stmt, "traitName")
        val _columnIndexOfTraitValue: Int = getColumnIndexOrThrow(_stmt, "traitValue")
        val _columnIndexOfTraitUnit: Int = getColumnIndexOrThrow(_stmt, "traitUnit")
        val _columnIndexOfNumericValue: Int = getColumnIndexOrThrow(_stmt, "numericValue")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfRecordedAt: Int = getColumnIndexOrThrow(_stmt, "recordedAt")
        val _columnIndexOfMeasuredBy: Int = getColumnIndexOrThrow(_stmt, "measuredBy")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<BirdTraitRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BirdTraitRecordEntity
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpTraitCategory: String
          _tmpTraitCategory = _stmt.getText(_columnIndexOfTraitCategory)
          val _tmpTraitName: String
          _tmpTraitName = _stmt.getText(_columnIndexOfTraitName)
          val _tmpTraitValue: String
          _tmpTraitValue = _stmt.getText(_columnIndexOfTraitValue)
          val _tmpTraitUnit: String?
          if (_stmt.isNull(_columnIndexOfTraitUnit)) {
            _tmpTraitUnit = null
          } else {
            _tmpTraitUnit = _stmt.getText(_columnIndexOfTraitUnit)
          }
          val _tmpNumericValue: Double?
          if (_stmt.isNull(_columnIndexOfNumericValue)) {
            _tmpNumericValue = null
          } else {
            _tmpNumericValue = _stmt.getDouble(_columnIndexOfNumericValue)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpRecordedAt: Long
          _tmpRecordedAt = _stmt.getLong(_columnIndexOfRecordedAt)
          val _tmpMeasuredBy: String?
          if (_stmt.isNull(_columnIndexOfMeasuredBy)) {
            _tmpMeasuredBy = null
          } else {
            _tmpMeasuredBy = _stmt.getText(_columnIndexOfMeasuredBy)
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
              BirdTraitRecordEntity(_tmpRecordId,_tmpProductId,_tmpOwnerId,_tmpTraitCategory,_tmpTraitName,_tmpTraitValue,_tmpTraitUnit,_tmpNumericValue,_tmpAgeWeeks,_tmpRecordedAt,_tmpMeasuredBy,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByOwner(ownerId: String): Flow<List<BirdTraitRecordEntity>> {
    val _sql: String = "SELECT * FROM bird_trait_records WHERE ownerId = ? ORDER BY recordedAt DESC"
    return createFlow(__db, false, arrayOf("bird_trait_records")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, ownerId)
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfTraitCategory: Int = getColumnIndexOrThrow(_stmt, "traitCategory")
        val _columnIndexOfTraitName: Int = getColumnIndexOrThrow(_stmt, "traitName")
        val _columnIndexOfTraitValue: Int = getColumnIndexOrThrow(_stmt, "traitValue")
        val _columnIndexOfTraitUnit: Int = getColumnIndexOrThrow(_stmt, "traitUnit")
        val _columnIndexOfNumericValue: Int = getColumnIndexOrThrow(_stmt, "numericValue")
        val _columnIndexOfAgeWeeks: Int = getColumnIndexOrThrow(_stmt, "ageWeeks")
        val _columnIndexOfRecordedAt: Int = getColumnIndexOrThrow(_stmt, "recordedAt")
        val _columnIndexOfMeasuredBy: Int = getColumnIndexOrThrow(_stmt, "measuredBy")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<BirdTraitRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BirdTraitRecordEntity
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpTraitCategory: String
          _tmpTraitCategory = _stmt.getText(_columnIndexOfTraitCategory)
          val _tmpTraitName: String
          _tmpTraitName = _stmt.getText(_columnIndexOfTraitName)
          val _tmpTraitValue: String
          _tmpTraitValue = _stmt.getText(_columnIndexOfTraitValue)
          val _tmpTraitUnit: String?
          if (_stmt.isNull(_columnIndexOfTraitUnit)) {
            _tmpTraitUnit = null
          } else {
            _tmpTraitUnit = _stmt.getText(_columnIndexOfTraitUnit)
          }
          val _tmpNumericValue: Double?
          if (_stmt.isNull(_columnIndexOfNumericValue)) {
            _tmpNumericValue = null
          } else {
            _tmpNumericValue = _stmt.getDouble(_columnIndexOfNumericValue)
          }
          val _tmpAgeWeeks: Int?
          if (_stmt.isNull(_columnIndexOfAgeWeeks)) {
            _tmpAgeWeeks = null
          } else {
            _tmpAgeWeeks = _stmt.getLong(_columnIndexOfAgeWeeks).toInt()
          }
          val _tmpRecordedAt: Long
          _tmpRecordedAt = _stmt.getLong(_columnIndexOfRecordedAt)
          val _tmpMeasuredBy: String?
          if (_stmt.isNull(_columnIndexOfMeasuredBy)) {
            _tmpMeasuredBy = null
          } else {
            _tmpMeasuredBy = _stmt.getText(_columnIndexOfMeasuredBy)
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
              BirdTraitRecordEntity(_tmpRecordId,_tmpProductId,_tmpOwnerId,_tmpTraitCategory,_tmpTraitName,_tmpTraitValue,_tmpTraitUnit,_tmpNumericValue,_tmpAgeWeeks,_tmpRecordedAt,_tmpMeasuredBy,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTraitCountPerBird(ownerId: String): List<TraitCountResult> {
    val _sql: String = """
        |
        |        SELECT productId, COUNT(*) as cnt 
        |        FROM bird_trait_records 
        |        WHERE ownerId = ? 
        |        GROUP BY productId
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, ownerId)
        val _columnIndexOfProductId: Int = 0
        val _columnIndexOfCnt: Int = 1
        val _result: MutableList<TraitCountResult> = mutableListOf()
        while (_stmt.step()) {
          val _item: TraitCountResult
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpCnt: Int
          _tmpCnt = _stmt.getLong(_columnIndexOfCnt).toInt()
          _item = TraitCountResult(_tmpProductId,_tmpCnt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAverageTraitValue(productIds: List<String>, traitName: String):
      Double? {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        SELECT AVG(numericValue) FROM bird_trait_records ")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        WHERE productId IN (")
    val _inputSize: Int = productIds.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(") ")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        AND traitName = ")
    _stringBuilder.append("?")
    _stringBuilder.append(" ")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        AND numericValue IS NOT NULL")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("    ")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        for (_item: String in productIds) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        _argIndex = 1 + _inputSize
        _stmt.bindText(_argIndex, traitName)
        val _result: Double?
        if (_stmt.step()) {
          val _tmp: Double?
          if (_stmt.isNull(0)) {
            _tmp = null
          } else {
            _tmp = _stmt.getDouble(0)
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

  public override suspend fun getAverageTraitAtAge(
    productIds: List<String>,
    traitName: String,
    ageWeeks: Int,
  ): Double? {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        SELECT AVG(numericValue) FROM bird_trait_records ")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        WHERE productId IN (")
    val _inputSize: Int = productIds.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(") ")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        AND traitName = ")
    _stringBuilder.append("?")
    _stringBuilder.append(" ")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        AND ageWeeks = ")
    _stringBuilder.append("?")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("        AND numericValue IS NOT NULL")
    _stringBuilder.append("""
        |
        |""".trimMargin())
    _stringBuilder.append("    ")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        for (_item: String in productIds) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        _argIndex = 1 + _inputSize
        _stmt.bindText(_argIndex, traitName)
        _argIndex = 2 + _inputSize
        _stmt.bindLong(_argIndex, ageWeeks.toLong())
        val _result: Double?
        if (_stmt.step()) {
          val _tmp: Double?
          if (_stmt.isNull(0)) {
            _tmp = null
          } else {
            _tmp = _stmt.getDouble(0)
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

  public override suspend fun getRecordedTraitNames(productId: String): List<String> {
    val _sql: String = """
        |
        |        SELECT DISTINCT traitName FROM bird_trait_records 
        |        WHERE productId = ?
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _result: MutableList<String> = mutableListOf()
        while (_stmt.step()) {
          val _item: String
          _item = _stmt.getText(0)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countBirdsWithTraits(ownerId: String): Int {
    val _sql: String = """
        |
        |        SELECT COUNT(DISTINCT productId) FROM bird_trait_records 
        |        WHERE ownerId = ?
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, ownerId)
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

  public override suspend fun getTraitCount(productId: String): Int {
    val _sql: String =
        "SELECT COUNT(DISTINCT traitName) FROM bird_trait_records WHERE productId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
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

  public override suspend fun countTotalRecords(ownerId: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM bird_trait_records WHERE ownerId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, ownerId)
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

  public override suspend fun delete(recordId: String) {
    val _sql: String = "DELETE FROM bird_trait_records WHERE recordId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, recordId)
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
