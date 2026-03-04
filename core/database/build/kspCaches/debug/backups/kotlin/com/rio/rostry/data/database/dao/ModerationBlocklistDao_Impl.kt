package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.ModerationBlocklistEntity
import javax.`annotation`.processing.Generated
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
public class ModerationBlocklistDao_Impl(
  __db: RoomDatabase,
) : ModerationBlocklistDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfModerationBlocklistEntity:
      EntityInsertAdapter<ModerationBlocklistEntity>

  private val __deleteAdapterOfModerationBlocklistEntity:
      EntityDeleteOrUpdateAdapter<ModerationBlocklistEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfModerationBlocklistEntity = object :
        EntityInsertAdapter<ModerationBlocklistEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `moderation_blocklist` (`term`,`type`,`createdAt`) VALUES (?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ModerationBlocklistEntity) {
        statement.bindText(1, entity.term)
        statement.bindText(2, entity.type)
        statement.bindLong(3, entity.createdAt)
      }
    }
    this.__deleteAdapterOfModerationBlocklistEntity = object :
        EntityDeleteOrUpdateAdapter<ModerationBlocklistEntity>() {
      protected override fun createQuery(): String =
          "DELETE FROM `moderation_blocklist` WHERE `term` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ModerationBlocklistEntity) {
        statement.bindText(1, entity.term)
      }
    }
  }

  public override suspend fun insertTerm(term: ModerationBlocklistEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfModerationBlocklistEntity.insert(_connection, term)
  }

  public override suspend fun deleteTerm(term: ModerationBlocklistEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __deleteAdapterOfModerationBlocklistEntity.handle(_connection, term)
  }

  public override fun getAllTerms(): Flow<List<ModerationBlocklistEntity>> {
    val _sql: String = "SELECT * FROM moderation_blocklist"
    return createFlow(__db, false, arrayOf("moderation_blocklist")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfTerm: Int = getColumnIndexOrThrow(_stmt, "term")
        val _columnIndexOfType: Int = getColumnIndexOrThrow(_stmt, "type")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<ModerationBlocklistEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ModerationBlocklistEntity
          val _tmpTerm: String
          _tmpTerm = _stmt.getText(_columnIndexOfTerm)
          val _tmpType: String
          _tmpType = _stmt.getText(_columnIndexOfType)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item = ModerationBlocklistEntity(_tmpTerm,_tmpType,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getTermsList(): List<String> {
    val _sql: String = "SELECT term FROM moderation_blocklist"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
