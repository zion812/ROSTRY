package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.CoinLedgerEntity
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
public class CoinLedgerDao_Impl(
  __db: RoomDatabase,
) : CoinLedgerDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfCoinLedgerEntity: EntityInsertAdapter<CoinLedgerEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfCoinLedgerEntity = object : EntityInsertAdapter<CoinLedgerEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `coin_ledger` (`entryId`,`userId`,`type`,`coins`,`amountInInr`,`refId`,`notes`,`createdAt`) VALUES (?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: CoinLedgerEntity) {
        statement.bindText(1, entity.entryId)
        statement.bindText(2, entity.userId)
        statement.bindText(3, entity.type)
        statement.bindLong(4, entity.coins.toLong())
        statement.bindDouble(5, entity.amountInInr)
        val _tmpRefId: String? = entity.refId
        if (_tmpRefId == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpRefId)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpNotes)
        }
        statement.bindLong(8, entity.createdAt)
      }
    }
  }

  public override suspend fun insert(entry: CoinLedgerEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfCoinLedgerEntity.insert(_connection, entry)
  }

  public override suspend fun userCoinBalance(userId: String): Int {
    val _sql: String = "SELECT COALESCE(SUM(coins), 0) FROM coin_ledger WHERE userId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
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

  public override fun observeUserLedger(userId: String): Flow<List<CoinLedgerEntity>> {
    val _sql: String = "SELECT * FROM coin_ledger WHERE userId = ? ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("coin_ledger")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfEntryId: Int = getColumnIndexOrThrow(_stmt, "entryId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfCoins: Int = getColumnIndexOrThrow(_stmt, "coins")
        val _columnIndexOfAmountInInr: Int = getColumnIndexOrThrow(_stmt, "amountInInr")
        val _columnIndexOfRefId: Int = getColumnIndexOrThrow(_stmt, "refId")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<CoinLedgerEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: CoinLedgerEntity
          val _tmpEntryId: String
          _tmpEntryId = _stmt.getText(_columnIndexOfEntryId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpCoins: Int
          _tmpCoins = _stmt.getLong(_columnIndexOfCoins).toInt()
          val _tmpAmountInInr: Double
          _tmpAmountInInr = _stmt.getDouble(_columnIndexOfAmountInInr)
          val _tmpRefId: String?
          if (_stmt.isNull(_columnIndexOfRefId)) {
            _tmpRefId = null
          } else {
            _tmpRefId = _stmt.getText(_columnIndexOfRefId)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              CoinLedgerEntity(_tmpEntryId,_tmpUserId,_tmpType,_tmpCoins,_tmpAmountInInr,_tmpRefId,_tmpNotes,_tmpCreatedAt)
          _result.add(_item)
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
