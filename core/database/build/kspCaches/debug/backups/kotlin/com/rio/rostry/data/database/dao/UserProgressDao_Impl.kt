package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.UserProgressEntity
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
public class UserProgressDao_Impl(
  __db: RoomDatabase,
) : UserProgressDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfUserProgressEntity: EntityInsertAdapter<UserProgressEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfUserProgressEntity = object : EntityInsertAdapter<UserProgressEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `user_progress` (`id`,`userId`,`achievementId`,`progress`,`target`,`unlockedAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: UserProgressEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.userId)
        statement.bindText(3, entity.achievementId)
        statement.bindLong(4, entity.progress.toLong())
        statement.bindLong(5, entity.target.toLong())
        val _tmpUnlockedAt: Long? = entity.unlockedAt
        if (_tmpUnlockedAt == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpUnlockedAt)
        }
        statement.bindLong(7, entity.updatedAt)
      }
    }
  }

  public override suspend fun upsert(progress: UserProgressEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfUserProgressEntity.insert(_connection, progress)
  }

  public override fun forUser(userId: String): Flow<List<UserProgressEntity>> {
    val _sql: String = "SELECT * FROM user_progress WHERE userId = ?"
    return createFlow(__db, false, arrayOf("user_progress")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfAchievementId: Int = getColumnIndexOrThrow(_stmt, "achievementId")
        val _columnIndexOfProgress: Int = getColumnIndexOrThrow(_stmt, "progress")
        val _columnIndexOfTarget: Int = getColumnIndexOrThrow(_stmt, "target")
        val _columnIndexOfUnlockedAt: Int = getColumnIndexOrThrow(_stmt, "unlockedAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<UserProgressEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: UserProgressEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpAchievementId: String
          _tmpAchievementId = _stmt.getText(_columnIndexOfAchievementId)
          val _tmpProgress: Int
          _tmpProgress = _stmt.getLong(_columnIndexOfProgress).toInt()
          val _tmpTarget: Int
          _tmpTarget = _stmt.getLong(_columnIndexOfTarget).toInt()
          val _tmpUnlockedAt: Long?
          if (_stmt.isNull(_columnIndexOfUnlockedAt)) {
            _tmpUnlockedAt = null
          } else {
            _tmpUnlockedAt = _stmt.getLong(_columnIndexOfUnlockedAt)
          }
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              UserProgressEntity(_tmpId,_tmpUserId,_tmpAchievementId,_tmpProgress,_tmpTarget,_tmpUnlockedAt,_tmpUpdatedAt)
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
