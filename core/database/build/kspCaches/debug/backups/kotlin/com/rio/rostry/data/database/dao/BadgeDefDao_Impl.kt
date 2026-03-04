package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.GamificationBadgeEntity
import javax.`annotation`.processing.Generated
import kotlin.Int
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
public class BadgeDefDao_Impl(
  __db: RoomDatabase,
) : BadgeDefDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfGamificationBadgeEntity: EntityInsertAdapter<GamificationBadgeEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfGamificationBadgeEntity = object :
        EntityInsertAdapter<GamificationBadgeEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `badges_def` (`badgeId`,`name`,`description`,`icon`) VALUES (?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: GamificationBadgeEntity) {
        statement.bindText(1, entity.badgeId)
        statement.bindText(2, entity.name)
        val _tmpDescription: String? = entity.description
        if (_tmpDescription == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpDescription)
        }
        val _tmpIcon: String? = entity.icon
        if (_tmpIcon == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpIcon)
        }
      }
    }
  }

  public override suspend fun upsertAll(list: List<GamificationBadgeEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfGamificationBadgeEntity.insert(_connection, list)
  }

  public override fun all(): Flow<List<GamificationBadgeEntity>> {
    val _sql: String = "SELECT * FROM badges_def"
    return createFlow(__db, false, arrayOf("badges_def")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfBadgeId: Int = getColumnIndexOrThrow(_stmt, "badgeId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfIcon: Int = getColumnIndexOrThrow(_stmt, "icon")
        val _result: MutableList<GamificationBadgeEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: GamificationBadgeEntity
          val _tmpBadgeId: String
          _tmpBadgeId = _stmt.getText(_columnIndexOfBadgeId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpIcon: String?
          if (_stmt.isNull(_columnIndexOfIcon)) {
            _tmpIcon = null
          } else {
            _tmpIcon = _stmt.getText(_columnIndexOfIcon)
          }
          _item = GamificationBadgeEntity(_tmpBadgeId,_tmpName,_tmpDescription,_tmpIcon)
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
