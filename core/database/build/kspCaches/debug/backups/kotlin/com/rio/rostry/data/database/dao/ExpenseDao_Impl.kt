package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.ExpenseEntity
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
public class ExpenseDao_Impl(
  __db: RoomDatabase,
) : ExpenseDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfExpenseEntity: EntityInsertAdapter<ExpenseEntity>

  private val __deleteAdapterOfExpenseEntity: EntityDeleteOrUpdateAdapter<ExpenseEntity>

  private val __updateAdapterOfExpenseEntity: EntityDeleteOrUpdateAdapter<ExpenseEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfExpenseEntity = object : EntityInsertAdapter<ExpenseEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `expenses` (`expenseId`,`farmerId`,`assetId`,`category`,`amount`,`description`,`expenseDate`,`createdAt`,`updatedAt`,`dirty`,`syncedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ExpenseEntity) {
        statement.bindText(1, entity.expenseId)
        statement.bindText(2, entity.farmerId)
        val _tmpAssetId: String? = entity.assetId
        if (_tmpAssetId == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpAssetId)
        }
        statement.bindText(4, entity.category)
        statement.bindDouble(5, entity.amount)
        val _tmpDescription: String? = entity.description
        if (_tmpDescription == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpDescription)
        }
        statement.bindLong(7, entity.expenseDate)
        statement.bindLong(8, entity.createdAt)
        statement.bindLong(9, entity.updatedAt)
        val _tmp: Int = if (entity.dirty) 1 else 0
        statement.bindLong(10, _tmp.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpSyncedAt)
        }
      }
    }
    this.__deleteAdapterOfExpenseEntity = object : EntityDeleteOrUpdateAdapter<ExpenseEntity>() {
      protected override fun createQuery(): String = "DELETE FROM `expenses` WHERE `expenseId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ExpenseEntity) {
        statement.bindText(1, entity.expenseId)
      }
    }
    this.__updateAdapterOfExpenseEntity = object : EntityDeleteOrUpdateAdapter<ExpenseEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `expenses` SET `expenseId` = ?,`farmerId` = ?,`assetId` = ?,`category` = ?,`amount` = ?,`description` = ?,`expenseDate` = ?,`createdAt` = ?,`updatedAt` = ?,`dirty` = ?,`syncedAt` = ? WHERE `expenseId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ExpenseEntity) {
        statement.bindText(1, entity.expenseId)
        statement.bindText(2, entity.farmerId)
        val _tmpAssetId: String? = entity.assetId
        if (_tmpAssetId == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpAssetId)
        }
        statement.bindText(4, entity.category)
        statement.bindDouble(5, entity.amount)
        val _tmpDescription: String? = entity.description
        if (_tmpDescription == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpDescription)
        }
        statement.bindLong(7, entity.expenseDate)
        statement.bindLong(8, entity.createdAt)
        statement.bindLong(9, entity.updatedAt)
        val _tmp: Int = if (entity.dirty) 1 else 0
        statement.bindLong(10, _tmp.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(11)
        } else {
          statement.bindLong(11, _tmpSyncedAt)
        }
        statement.bindText(12, entity.expenseId)
      }
    }
  }

  public override suspend fun insert(expense: ExpenseEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfExpenseEntity.insert(_connection, expense)
  }

  public override suspend fun insertAll(expenses: List<ExpenseEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfExpenseEntity.insert(_connection, expenses)
  }

  public override suspend fun delete(expense: ExpenseEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __deleteAdapterOfExpenseEntity.handle(_connection, expense)
  }

  public override suspend fun update(expense: ExpenseEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __updateAdapterOfExpenseEntity.handle(_connection, expense)
  }

  public override fun observeForFarmer(farmerId: String): Flow<List<ExpenseEntity>> {
    val _sql: String = "SELECT * FROM expenses WHERE farmerId = ? ORDER BY expenseDate DESC"
    return createFlow(__db, false, arrayOf("expenses")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfExpenseId: Int = getColumnIndexOrThrow(_stmt, "expenseId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfExpenseDate: Int = getColumnIndexOrThrow(_stmt, "expenseDate")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<ExpenseEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ExpenseEntity
          val _tmpExpenseId: String
          _tmpExpenseId = _stmt.getText(_columnIndexOfExpenseId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpAssetId: String?
          if (_stmt.isNull(_columnIndexOfAssetId)) {
            _tmpAssetId = null
          } else {
            _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          }
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpExpenseDate: Long
          _tmpExpenseDate = _stmt.getLong(_columnIndexOfExpenseDate)
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
              ExpenseEntity(_tmpExpenseId,_tmpFarmerId,_tmpAssetId,_tmpCategory,_tmpAmount,_tmpDescription,_tmpExpenseDate,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeForFarmerByCategory(farmerId: String, category: String):
      Flow<List<ExpenseEntity>> {
    val _sql: String =
        "SELECT * FROM expenses WHERE farmerId = ? AND category = ? ORDER BY expenseDate DESC"
    return createFlow(__db, false, arrayOf("expenses")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindText(_argIndex, category)
        val _columnIndexOfExpenseId: Int = getColumnIndexOrThrow(_stmt, "expenseId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfExpenseDate: Int = getColumnIndexOrThrow(_stmt, "expenseDate")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<ExpenseEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ExpenseEntity
          val _tmpExpenseId: String
          _tmpExpenseId = _stmt.getText(_columnIndexOfExpenseId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpAssetId: String?
          if (_stmt.isNull(_columnIndexOfAssetId)) {
            _tmpAssetId = null
          } else {
            _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          }
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpExpenseDate: Long
          _tmpExpenseDate = _stmt.getLong(_columnIndexOfExpenseDate)
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
              ExpenseEntity(_tmpExpenseId,_tmpFarmerId,_tmpAssetId,_tmpCategory,_tmpAmount,_tmpDescription,_tmpExpenseDate,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeForAsset(assetId: String): Flow<List<ExpenseEntity>> {
    val _sql: String = "SELECT * FROM expenses WHERE assetId = ? ORDER BY expenseDate DESC"
    return createFlow(__db, false, arrayOf("expenses")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, assetId)
        val _columnIndexOfExpenseId: Int = getColumnIndexOrThrow(_stmt, "expenseId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfExpenseDate: Int = getColumnIndexOrThrow(_stmt, "expenseDate")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<ExpenseEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ExpenseEntity
          val _tmpExpenseId: String
          _tmpExpenseId = _stmt.getText(_columnIndexOfExpenseId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpAssetId: String?
          if (_stmt.isNull(_columnIndexOfAssetId)) {
            _tmpAssetId = null
          } else {
            _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          }
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpExpenseDate: Long
          _tmpExpenseDate = _stmt.getLong(_columnIndexOfExpenseDate)
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
              ExpenseEntity(_tmpExpenseId,_tmpFarmerId,_tmpAssetId,_tmpCategory,_tmpAmount,_tmpDescription,_tmpExpenseDate,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeForDateRange(
    farmerId: String,
    startDate: Long,
    endDate: Long,
  ): Flow<List<ExpenseEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM expenses 
        |        WHERE farmerId = ? 
        |        AND expenseDate >= ? 
        |        AND expenseDate <= ? 
        |        ORDER BY expenseDate DESC
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("expenses")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, startDate)
        _argIndex = 3
        _stmt.bindLong(_argIndex, endDate)
        val _columnIndexOfExpenseId: Int = getColumnIndexOrThrow(_stmt, "expenseId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfExpenseDate: Int = getColumnIndexOrThrow(_stmt, "expenseDate")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<ExpenseEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ExpenseEntity
          val _tmpExpenseId: String
          _tmpExpenseId = _stmt.getText(_columnIndexOfExpenseId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpAssetId: String?
          if (_stmt.isNull(_columnIndexOfAssetId)) {
            _tmpAssetId = null
          } else {
            _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          }
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpExpenseDate: Long
          _tmpExpenseDate = _stmt.getLong(_columnIndexOfExpenseDate)
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
              ExpenseEntity(_tmpExpenseId,_tmpFarmerId,_tmpAssetId,_tmpCategory,_tmpAmount,_tmpDescription,_tmpExpenseDate,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTotalForFarmer(farmerId: String): Double {
    val _sql: String = "SELECT COALESCE(SUM(amount), 0.0) FROM expenses WHERE farmerId = ?"
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

  public override suspend fun getTotalByCategory(farmerId: String, category: String): Double {
    val _sql: String =
        "SELECT COALESCE(SUM(amount), 0.0) FROM expenses WHERE farmerId = ? AND category = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindText(_argIndex, category)
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

  public override suspend fun getTotalByCategoryInRange(
    farmerId: String,
    category: String,
    startDate: Long,
    endDate: Long,
  ): Double {
    val _sql: String = """
        |
        |        SELECT COALESCE(SUM(amount), 0.0) FROM expenses 
        |        WHERE farmerId = ? 
        |        AND category = ? 
        |        AND expenseDate >= ? 
        |        AND expenseDate <= ?
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindText(_argIndex, category)
        _argIndex = 3
        _stmt.bindLong(_argIndex, startDate)
        _argIndex = 4
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

  public override suspend fun getTotalInRange(
    farmerId: String,
    startDate: Long,
    endDate: Long,
  ): Double {
    val _sql: String = """
        |
        |        SELECT COALESCE(SUM(amount), 0.0) FROM expenses 
        |        WHERE farmerId = ? 
        |        AND expenseDate >= ? 
        |        AND expenseDate <= ?
        |    
        """.trimMargin()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
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

  public override suspend fun getTotalForAsset(assetId: String): Double {
    val _sql: String = "SELECT COALESCE(SUM(amount), 0.0) FROM expenses WHERE assetId = ?"
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

  public override suspend fun getTotalForAssetByCategory(assetId: String, category: String):
      Double {
    val _sql: String =
        "SELECT COALESCE(SUM(amount), 0.0) FROM expenses WHERE assetId = ? AND category = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, assetId)
        _argIndex = 2
        _stmt.bindText(_argIndex, category)
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

  public override suspend fun countForFarmer(farmerId: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM expenses WHERE farmerId = ?"
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

  public override suspend fun getById(expenseId: String): ExpenseEntity? {
    val _sql: String = "SELECT * FROM expenses WHERE expenseId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, expenseId)
        val _columnIndexOfExpenseId: Int = getColumnIndexOrThrow(_stmt, "expenseId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfExpenseDate: Int = getColumnIndexOrThrow(_stmt, "expenseDate")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: ExpenseEntity?
        if (_stmt.step()) {
          val _tmpExpenseId: String
          _tmpExpenseId = _stmt.getText(_columnIndexOfExpenseId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpAssetId: String?
          if (_stmt.isNull(_columnIndexOfAssetId)) {
            _tmpAssetId = null
          } else {
            _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          }
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpExpenseDate: Long
          _tmpExpenseDate = _stmt.getLong(_columnIndexOfExpenseDate)
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
              ExpenseEntity(_tmpExpenseId,_tmpFarmerId,_tmpAssetId,_tmpCategory,_tmpAmount,_tmpDescription,_tmpExpenseDate,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDirty(): List<ExpenseEntity> {
    val _sql: String = "SELECT * FROM expenses WHERE dirty = 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfExpenseId: Int = getColumnIndexOrThrow(_stmt, "expenseId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfAssetId: Int = getColumnIndexOrThrow(_stmt, "assetId")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfExpenseDate: Int = getColumnIndexOrThrow(_stmt, "expenseDate")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _result: MutableList<ExpenseEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ExpenseEntity
          val _tmpExpenseId: String
          _tmpExpenseId = _stmt.getText(_columnIndexOfExpenseId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpAssetId: String?
          if (_stmt.isNull(_columnIndexOfAssetId)) {
            _tmpAssetId = null
          } else {
            _tmpAssetId = _stmt.getText(_columnIndexOfAssetId)
          }
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpExpenseDate: Long
          _tmpExpenseDate = _stmt.getLong(_columnIndexOfExpenseDate)
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
              ExpenseEntity(_tmpExpenseId,_tmpFarmerId,_tmpAssetId,_tmpCategory,_tmpAmount,_tmpDescription,_tmpExpenseDate,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteById(expenseId: String) {
    val _sql: String = "DELETE FROM expenses WHERE expenseId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, expenseId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAllForFarmer(farmerId: String) {
    val _sql: String = "DELETE FROM expenses WHERE farmerId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markSynced(expenseId: String, syncedAt: Long) {
    val _sql: String = "UPDATE expenses SET dirty = 0, syncedAt = ? WHERE expenseId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, syncedAt)
        _argIndex = 2
        _stmt.bindText(_argIndex, expenseId)
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
