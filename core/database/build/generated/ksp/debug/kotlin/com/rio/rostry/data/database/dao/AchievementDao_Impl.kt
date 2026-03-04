package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.AchievementEntity
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
public class AchievementDao_Impl(
  __db: RoomDatabase,
) : AchievementDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfAchievementEntity: EntityInsertAdapter<AchievementEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfAchievementEntity = object : EntityInsertAdapter<AchievementEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `achievements_def` (`achievementId`,`name`,`description`,`points`,`category`,`icon`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: AchievementEntity) {
        statement.bindText(1, entity.achievementId)
        statement.bindText(2, entity.name)
        val _tmpDescription: String? = entity.description
        if (_tmpDescription == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpDescription)
        }
        statement.bindLong(4, entity.points.toLong())
        val _tmpCategory: String? = entity.category
        if (_tmpCategory == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpCategory)
        }
        val _tmpIcon: String? = entity.icon
        if (_tmpIcon == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpIcon)
        }
      }
    }
  }

  public override suspend fun upsertAll(list: List<AchievementEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfAchievementEntity.insert(_connection, list)
  }

  public override fun all(): Flow<List<AchievementEntity>> {
    val _sql: String = "SELECT * FROM achievements_def"
    return createFlow(__db, false, arrayOf("achievements_def")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfAchievementId: Int = getColumnIndexOrThrow(_stmt, "achievementId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfPoints: Int = getColumnIndexOrThrow(_stmt, "points")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfIcon: Int = getColumnIndexOrThrow(_stmt, "icon")
        val _result: MutableList<AchievementEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: AchievementEntity
          val _tmpAchievementId: String
          _tmpAchievementId = _stmt.getText(_columnIndexOfAchievementId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpPoints: Int
          _tmpPoints = _stmt.getLong(_columnIndexOfPoints).toInt()
          val _tmpCategory: String?
          if (_stmt.isNull(_columnIndexOfCategory)) {
            _tmpCategory = null
          } else {
            _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          }
          val _tmpIcon: String?
          if (_stmt.isNull(_columnIndexOfIcon)) {
            _tmpIcon = null
          } else {
            _tmpIcon = _stmt.getText(_columnIndexOfIcon)
          }
          _item =
              AchievementEntity(_tmpAchievementId,_tmpName,_tmpDescription,_tmpPoints,_tmpCategory,_tmpIcon)
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
