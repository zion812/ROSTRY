package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.BreedingRecordEntity
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

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class BreedingRecordDao_Impl(
  __db: RoomDatabase,
) : BreedingRecordDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfBreedingRecordEntity: EntityInsertAdapter<BreedingRecordEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfBreedingRecordEntity = object :
        EntityInsertAdapter<BreedingRecordEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR ABORT INTO `breeding_records` (`recordId`,`parentId`,`partnerId`,`childId`,`success`,`notes`,`timestamp`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: BreedingRecordEntity) {
        statement.bindText(1, entity.recordId)
        statement.bindText(2, entity.parentId)
        statement.bindText(3, entity.partnerId)
        statement.bindText(4, entity.childId)
        val _tmp: Int = if (entity.success) 1 else 0
        statement.bindLong(5, _tmp.toLong())
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpNotes)
        }
        statement.bindLong(7, entity.timestamp)
      }
    }
  }

  public override suspend fun insert(record: BreedingRecordEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfBreedingRecordEntity.insert(_connection, record)
  }

  public override suspend fun recordsByChild(childId: String): List<BreedingRecordEntity> {
    val _sql: String = "SELECT * FROM breeding_records WHERE childId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, childId)
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfParentId: Int = getColumnIndexOrThrow(_stmt, "parentId")
        val _columnIndexOfPartnerId: Int = getColumnIndexOrThrow(_stmt, "partnerId")
        val _columnIndexOfChildId: Int = getColumnIndexOrThrow(_stmt, "childId")
        val _columnIndexOfSuccess: Int = getColumnIndexOrThrow(_stmt, "success")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<BreedingRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BreedingRecordEntity
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpParentId: String
          _tmpParentId = _stmt.getText(_columnIndexOfParentId)
          val _tmpPartnerId: String
          _tmpPartnerId = _stmt.getText(_columnIndexOfPartnerId)
          val _tmpChildId: String
          _tmpChildId = _stmt.getText(_columnIndexOfChildId)
          val _tmpSuccess: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfSuccess).toInt()
          _tmpSuccess = _tmp != 0
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _item =
              BreedingRecordEntity(_tmpRecordId,_tmpParentId,_tmpPartnerId,_tmpChildId,_tmpSuccess,_tmpNotes,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun recordsByParent(parentId: String): List<BreedingRecordEntity> {
    val _sql: String = "SELECT * FROM breeding_records WHERE parentId = ? OR partnerId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, parentId)
        _argIndex = 2
        _stmt.bindText(_argIndex, parentId)
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfParentId: Int = getColumnIndexOrThrow(_stmt, "parentId")
        val _columnIndexOfPartnerId: Int = getColumnIndexOrThrow(_stmt, "partnerId")
        val _columnIndexOfChildId: Int = getColumnIndexOrThrow(_stmt, "childId")
        val _columnIndexOfSuccess: Int = getColumnIndexOrThrow(_stmt, "success")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<BreedingRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BreedingRecordEntity
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpParentId: String
          _tmpParentId = _stmt.getText(_columnIndexOfParentId)
          val _tmpPartnerId: String
          _tmpPartnerId = _stmt.getText(_columnIndexOfPartnerId)
          val _tmpChildId: String
          _tmpChildId = _stmt.getText(_columnIndexOfChildId)
          val _tmpSuccess: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfSuccess).toInt()
          _tmpSuccess = _tmp != 0
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _item =
              BreedingRecordEntity(_tmpRecordId,_tmpParentId,_tmpPartnerId,_tmpChildId,_tmpSuccess,_tmpNotes,_tmpTimestamp)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun successfulBreedings(parent: String, partner: String): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM breeding_records WHERE parentId = ? AND partnerId = ? AND success = 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, parent)
        _argIndex = 2
        _stmt.bindText(_argIndex, partner)
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

  public override suspend fun totalBreedings(parent: String, partner: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM breeding_records WHERE parentId = ? AND partnerId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, parent)
        _argIndex = 2
        _stmt.bindText(_argIndex, partner)
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

  public override suspend fun recordsByChildren(childIds: List<String>):
      List<BreedingRecordEntity> {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT * FROM breeding_records WHERE childId IN (")
    val _inputSize: Int = childIds.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        for (_item: String in childIds) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfParentId: Int = getColumnIndexOrThrow(_stmt, "parentId")
        val _columnIndexOfPartnerId: Int = getColumnIndexOrThrow(_stmt, "partnerId")
        val _columnIndexOfChildId: Int = getColumnIndexOrThrow(_stmt, "childId")
        val _columnIndexOfSuccess: Int = getColumnIndexOrThrow(_stmt, "success")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<BreedingRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item_1: BreedingRecordEntity
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpParentId: String
          _tmpParentId = _stmt.getText(_columnIndexOfParentId)
          val _tmpPartnerId: String
          _tmpPartnerId = _stmt.getText(_columnIndexOfPartnerId)
          val _tmpChildId: String
          _tmpChildId = _stmt.getText(_columnIndexOfChildId)
          val _tmpSuccess: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfSuccess).toInt()
          _tmpSuccess = _tmp != 0
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _item_1 =
              BreedingRecordEntity(_tmpRecordId,_tmpParentId,_tmpPartnerId,_tmpChildId,_tmpSuccess,_tmpNotes,_tmpTimestamp)
          _result.add(_item_1)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun recordsByParents(parentIds: List<String>):
      List<BreedingRecordEntity> {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT * FROM breeding_records WHERE parentId IN (")
    val _inputSize: Int = parentIds.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(") OR partnerId IN (")
    val _inputSize_1: Int = parentIds.size
    appendPlaceholders(_stringBuilder, _inputSize_1)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        for (_item: String in parentIds) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        _argIndex = 1 + _inputSize
        for (_item_1: String in parentIds) {
          _stmt.bindText(_argIndex, _item_1)
          _argIndex++
        }
        val _columnIndexOfRecordId: Int = getColumnIndexOrThrow(_stmt, "recordId")
        val _columnIndexOfParentId: Int = getColumnIndexOrThrow(_stmt, "parentId")
        val _columnIndexOfPartnerId: Int = getColumnIndexOrThrow(_stmt, "partnerId")
        val _columnIndexOfChildId: Int = getColumnIndexOrThrow(_stmt, "childId")
        val _columnIndexOfSuccess: Int = getColumnIndexOrThrow(_stmt, "success")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfTimestamp: Int = getColumnIndexOrThrow(_stmt, "timestamp")
        val _result: MutableList<BreedingRecordEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item_2: BreedingRecordEntity
          val _tmpRecordId: String
          _tmpRecordId = _stmt.getText(_columnIndexOfRecordId)
          val _tmpParentId: String
          _tmpParentId = _stmt.getText(_columnIndexOfParentId)
          val _tmpPartnerId: String
          _tmpPartnerId = _stmt.getText(_columnIndexOfPartnerId)
          val _tmpChildId: String
          _tmpChildId = _stmt.getText(_columnIndexOfChildId)
          val _tmpSuccess: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfSuccess).toInt()
          _tmpSuccess = _tmp != 0
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpTimestamp: Long
          _tmpTimestamp = _stmt.getLong(_columnIndexOfTimestamp)
          _item_2 =
              BreedingRecordEntity(_tmpRecordId,_tmpParentId,_tmpPartnerId,_tmpChildId,_tmpSuccess,_tmpNotes,_tmpTimestamp)
          _result.add(_item_2)
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
