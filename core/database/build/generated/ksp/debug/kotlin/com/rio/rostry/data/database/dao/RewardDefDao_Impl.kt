package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.RewardEntity
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
public class RewardDefDao_Impl(
  __db: RoomDatabase,
) : RewardDefDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfRewardEntity: EntityInsertAdapter<RewardEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfRewardEntity = object : EntityInsertAdapter<RewardEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `rewards_def` (`rewardId`,`name`,`description`,`pointsRequired`) VALUES (?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: RewardEntity) {
        statement.bindText(1, entity.rewardId)
        statement.bindText(2, entity.name)
        val _tmpDescription: String? = entity.description
        if (_tmpDescription == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpDescription)
        }
        statement.bindLong(4, entity.pointsRequired.toLong())
      }
    }
  }

  public override suspend fun upsertAll(list: List<RewardEntity>): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfRewardEntity.insert(_connection, list)
  }

  public override fun all(): Flow<List<RewardEntity>> {
    val _sql: String = "SELECT * FROM rewards_def"
    return createFlow(__db, false, arrayOf("rewards_def")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfRewardId: Int = getColumnIndexOrThrow(_stmt, "rewardId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfPointsRequired: Int = getColumnIndexOrThrow(_stmt, "pointsRequired")
        val _result: MutableList<RewardEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: RewardEntity
          val _tmpRewardId: String
          _tmpRewardId = _stmt.getText(_columnIndexOfRewardId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpPointsRequired: Int
          _tmpPointsRequired = _stmt.getLong(_columnIndexOfPointsRequired).toInt()
          _item = RewardEntity(_tmpRewardId,_tmpName,_tmpDescription,_tmpPointsRequired)
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
