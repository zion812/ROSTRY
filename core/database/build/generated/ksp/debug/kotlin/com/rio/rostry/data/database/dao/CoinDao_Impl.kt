package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.CoinEntity
import javax.`annotation`.processing.Generated
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
public class CoinDao_Impl(
  __db: RoomDatabase,
) : CoinDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfCoinEntity: EntityInsertAdapter<CoinEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfCoinEntity = object : EntityInsertAdapter<CoinEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `coins` (`coinTransactionId`,`userId`,`amount`,`type`,`description`,`relatedTransferId`,`relatedOrderId`,`transactionDate`) VALUES (?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: CoinEntity) {
        statement.bindText(1, entity.coinTransactionId)
        statement.bindText(2, entity.userId)
        statement.bindDouble(3, entity.amount)
        statement.bindText(4, entity.type)
        val _tmpDescription: String? = entity.description
        if (_tmpDescription == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpDescription)
        }
        val _tmpRelatedTransferId: String? = entity.relatedTransferId
        if (_tmpRelatedTransferId == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpRelatedTransferId)
        }
        val _tmpRelatedOrderId: String? = entity.relatedOrderId
        if (_tmpRelatedOrderId == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpRelatedOrderId)
        }
        statement.bindLong(8, entity.transactionDate)
      }
    }
  }

  public override suspend fun insertCoinTransaction(coinTransaction: CoinEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfCoinEntity.insert(_connection, coinTransaction)
  }

  public override fun getCoinTransactionById(transactionId: String): Flow<CoinEntity?> {
    val _sql: String = "SELECT * FROM coins WHERE coinTransactionId = ?"
    return createFlow(__db, false, arrayOf("coins")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, transactionId)
        val _columnIndexOfCoinTransactionId: Int = getColumnIndexOrThrow(_stmt, "coinTransactionId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfRelatedTransferId: Int = getColumnIndexOrThrow(_stmt, "relatedTransferId")
        val _columnIndexOfRelatedOrderId: Int = getColumnIndexOrThrow(_stmt, "relatedOrderId")
        val _columnIndexOfTransactionDate: Int = getColumnIndexOrThrow(_stmt, "transactionDate")
        val _result: CoinEntity?
        if (_stmt.step()) {
          val _tmpCoinTransactionId: String
          _tmpCoinTransactionId = _stmt.getText(_columnIndexOfCoinTransactionId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpRelatedTransferId: String?
          if (_stmt.isNull(_columnIndexOfRelatedTransferId)) {
            _tmpRelatedTransferId = null
          } else {
            _tmpRelatedTransferId = _stmt.getText(_columnIndexOfRelatedTransferId)
          }
          val _tmpRelatedOrderId: String?
          if (_stmt.isNull(_columnIndexOfRelatedOrderId)) {
            _tmpRelatedOrderId = null
          } else {
            _tmpRelatedOrderId = _stmt.getText(_columnIndexOfRelatedOrderId)
          }
          val _tmpTransactionDate: Long
          _tmpTransactionDate = _stmt.getLong(_columnIndexOfTransactionDate)
          _result =
              CoinEntity(_tmpCoinTransactionId,_tmpUserId,_tmpAmount,_tmpType,_tmpDescription,_tmpRelatedTransferId,_tmpRelatedOrderId,_tmpTransactionDate)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getCoinTransactionsByUserId(userId: String): Flow<List<CoinEntity>> {
    val _sql: String = "SELECT * FROM coins WHERE userId = ? ORDER BY transactionDate DESC"
    return createFlow(__db, false, arrayOf("coins")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfCoinTransactionId: Int = getColumnIndexOrThrow(_stmt, "coinTransactionId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfRelatedTransferId: Int = getColumnIndexOrThrow(_stmt, "relatedTransferId")
        val _columnIndexOfRelatedOrderId: Int = getColumnIndexOrThrow(_stmt, "relatedOrderId")
        val _columnIndexOfTransactionDate: Int = getColumnIndexOrThrow(_stmt, "transactionDate")
        val _result: MutableList<CoinEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: CoinEntity
          val _tmpCoinTransactionId: String
          _tmpCoinTransactionId = _stmt.getText(_columnIndexOfCoinTransactionId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpRelatedTransferId: String?
          if (_stmt.isNull(_columnIndexOfRelatedTransferId)) {
            _tmpRelatedTransferId = null
          } else {
            _tmpRelatedTransferId = _stmt.getText(_columnIndexOfRelatedTransferId)
          }
          val _tmpRelatedOrderId: String?
          if (_stmt.isNull(_columnIndexOfRelatedOrderId)) {
            _tmpRelatedOrderId = null
          } else {
            _tmpRelatedOrderId = _stmt.getText(_columnIndexOfRelatedOrderId)
          }
          val _tmpTransactionDate: Long
          _tmpTransactionDate = _stmt.getLong(_columnIndexOfTransactionDate)
          _item =
              CoinEntity(_tmpCoinTransactionId,_tmpUserId,_tmpAmount,_tmpType,_tmpDescription,_tmpRelatedTransferId,_tmpRelatedOrderId,_tmpTransactionDate)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getUserCoinBalance(userId: String): Flow<Double?> {
    val _sql: String = "SELECT SUM(amount) FROM coins WHERE userId = ?"
    return createFlow(__db, false, arrayOf("coins")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
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

  public override fun getCoinTransactionsByType(type: String): Flow<List<CoinEntity>> {
    val _sql: String = "SELECT * FROM coins WHERE type = ? ORDER BY transactionDate DESC"
    return createFlow(__db, false, arrayOf("coins")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, type)
        val _columnIndexOfCoinTransactionId: Int = getColumnIndexOrThrow(_stmt, "coinTransactionId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfRelatedTransferId: Int = getColumnIndexOrThrow(_stmt, "relatedTransferId")
        val _columnIndexOfRelatedOrderId: Int = getColumnIndexOrThrow(_stmt, "relatedOrderId")
        val _columnIndexOfTransactionDate: Int = getColumnIndexOrThrow(_stmt, "transactionDate")
        val _result: MutableList<CoinEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: CoinEntity
          val _tmpCoinTransactionId: String
          _tmpCoinTransactionId = _stmt.getText(_columnIndexOfCoinTransactionId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpRelatedTransferId: String?
          if (_stmt.isNull(_columnIndexOfRelatedTransferId)) {
            _tmpRelatedTransferId = null
          } else {
            _tmpRelatedTransferId = _stmt.getText(_columnIndexOfRelatedTransferId)
          }
          val _tmpRelatedOrderId: String?
          if (_stmt.isNull(_columnIndexOfRelatedOrderId)) {
            _tmpRelatedOrderId = null
          } else {
            _tmpRelatedOrderId = _stmt.getText(_columnIndexOfRelatedOrderId)
          }
          val _tmpTransactionDate: Long
          _tmpTransactionDate = _stmt.getLong(_columnIndexOfTransactionDate)
          _item =
              CoinEntity(_tmpCoinTransactionId,_tmpUserId,_tmpAmount,_tmpType,_tmpDescription,_tmpRelatedTransferId,_tmpRelatedOrderId,_tmpTransactionDate)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getCoinTransactionsByOrderId(orderId: String): Flow<List<CoinEntity>> {
    val _sql: String = "SELECT * FROM coins WHERE relatedOrderId = ? ORDER BY transactionDate DESC"
    return createFlow(__db, false, arrayOf("coins")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, orderId)
        val _columnIndexOfCoinTransactionId: Int = getColumnIndexOrThrow(_stmt, "coinTransactionId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfAmount: Int = getColumnIndexOrThrow(_stmt, "amount")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfRelatedTransferId: Int = getColumnIndexOrThrow(_stmt, "relatedTransferId")
        val _columnIndexOfRelatedOrderId: Int = getColumnIndexOrThrow(_stmt, "relatedOrderId")
        val _columnIndexOfTransactionDate: Int = getColumnIndexOrThrow(_stmt, "transactionDate")
        val _result: MutableList<CoinEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: CoinEntity
          val _tmpCoinTransactionId: String
          _tmpCoinTransactionId = _stmt.getText(_columnIndexOfCoinTransactionId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpAmount: Double
          _tmpAmount = _stmt.getDouble(_columnIndexOfAmount)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpRelatedTransferId: String?
          if (_stmt.isNull(_columnIndexOfRelatedTransferId)) {
            _tmpRelatedTransferId = null
          } else {
            _tmpRelatedTransferId = _stmt.getText(_columnIndexOfRelatedTransferId)
          }
          val _tmpRelatedOrderId: String?
          if (_stmt.isNull(_columnIndexOfRelatedOrderId)) {
            _tmpRelatedOrderId = null
          } else {
            _tmpRelatedOrderId = _stmt.getText(_columnIndexOfRelatedOrderId)
          }
          val _tmpTransactionDate: Long
          _tmpTransactionDate = _stmt.getLong(_columnIndexOfTransactionDate)
          _item =
              CoinEntity(_tmpCoinTransactionId,_tmpUserId,_tmpAmount,_tmpType,_tmpDescription,_tmpRelatedTransferId,_tmpRelatedOrderId,_tmpTransactionDate)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteCoinTransactionById(transactionId: String) {
    val _sql: String = "DELETE FROM coins WHERE coinTransactionId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, transactionId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAllCoinTransactionsForUser(userId: String) {
    val _sql: String = "DELETE FROM coins WHERE userId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAllCoinTransactions() {
    val _sql: String = "DELETE FROM coins"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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
