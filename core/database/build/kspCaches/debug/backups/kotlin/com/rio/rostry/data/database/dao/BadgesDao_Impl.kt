package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.BadgeEntity
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
public class BadgesDao_Impl(
  __db: RoomDatabase,
) : BadgesDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfBadgeEntity: EntityInsertAdapter<BadgeEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfBadgeEntity = object : EntityInsertAdapter<BadgeEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `badges` (`badgeId`,`userId`,`name`,`description`,`awardedAt`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: BadgeEntity) {
        statement.bindText(1, entity.badgeId)
        statement.bindText(2, entity.userId)
        statement.bindText(3, entity.name)
        val _tmpDescription: String? = entity.description
        if (_tmpDescription == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpDescription)
        }
        statement.bindLong(5, entity.awardedAt)
      }
    }
  }

  public override suspend fun upsert(badge: BadgeEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfBadgeEntity.insert(_connection, badge)
  }

  public override fun streamUserBadges(userId: String): Flow<List<BadgeEntity>> {
    val _sql: String = "SELECT * FROM badges WHERE userId = ? ORDER BY awardedAt DESC"
    return createFlow(__db, false, arrayOf("badges")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfBadgeId: Int = getColumnIndexOrThrow(_stmt, "badgeId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfAwardedAt: Int = getColumnIndexOrThrow(_stmt, "awardedAt")
        val _result: MutableList<BadgeEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: BadgeEntity
          val _tmpBadgeId: String
          _tmpBadgeId = _stmt.getText(_columnIndexOfBadgeId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpAwardedAt: Long
          _tmpAwardedAt = _stmt.getLong(_columnIndexOfAwardedAt)
          _item = BadgeEntity(_tmpBadgeId,_tmpUserId,_tmpName,_tmpDescription,_tmpAwardedAt)
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
