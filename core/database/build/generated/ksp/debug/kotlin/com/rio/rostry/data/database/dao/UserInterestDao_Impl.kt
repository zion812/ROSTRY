package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.EntityUpsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.UserInterestEntity
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
public class UserInterestDao_Impl(
  __db: RoomDatabase,
) : UserInterestDao {
  private val __db: RoomDatabase

  private val __upsertAdapterOfUserInterestEntity: EntityUpsertAdapter<UserInterestEntity>
  init {
    this.__db = __db
    this.__upsertAdapterOfUserInterestEntity = EntityUpsertAdapter<UserInterestEntity>(object :
        EntityInsertAdapter<UserInterestEntity>() {
      protected override fun createQuery(): String =
          "INSERT INTO `user_interests` (`interestId`,`userId`,`category`,`value`,`weight`,`updatedAt`) VALUES (?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: UserInterestEntity) {
        statement.bindText(1, entity.interestId)
        statement.bindText(2, entity.userId)
        statement.bindText(3, entity.category)
        statement.bindText(4, entity.value)
        statement.bindDouble(5, entity.weight)
        statement.bindLong(6, entity.updatedAt)
      }
    }, object : EntityDeleteOrUpdateAdapter<UserInterestEntity>() {
      protected override fun createQuery(): String =
          "UPDATE `user_interests` SET `interestId` = ?,`userId` = ?,`category` = ?,`value` = ?,`weight` = ?,`updatedAt` = ? WHERE `interestId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: UserInterestEntity) {
        statement.bindText(1, entity.interestId)
        statement.bindText(2, entity.userId)
        statement.bindText(3, entity.category)
        statement.bindText(4, entity.value)
        statement.bindDouble(5, entity.weight)
        statement.bindLong(6, entity.updatedAt)
        statement.bindText(7, entity.interestId)
      }
    })
  }

  public override suspend fun upsert(interest: UserInterestEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __upsertAdapterOfUserInterestEntity.upsert(_connection, interest)
  }

  public override fun streamUserInterests(userId: String): Flow<List<UserInterestEntity>> {
    val _sql: String = "SELECT * FROM user_interests WHERE userId = ? ORDER BY weight DESC"
    return createFlow(__db, false, arrayOf("user_interests")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfInterestId: Int = getColumnIndexOrThrow(_stmt, "interestId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfValue: Int = getColumnIndexOrThrow(_stmt, "value")
        val _columnIndexOfWeight: Int = getColumnIndexOrThrow(_stmt, "weight")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<UserInterestEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: UserInterestEntity
          val _tmpInterestId: String
          _tmpInterestId = _stmt.getText(_columnIndexOfInterestId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpValue: String
          _tmpValue = _stmt.getText(_columnIndexOfValue)
          val _tmpWeight: Double
          _tmpWeight = _stmt.getDouble(_columnIndexOfWeight)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              UserInterestEntity(_tmpInterestId,_tmpUserId,_tmpCategory,_tmpValue,_tmpWeight,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun streamByCategory(userId: String, category: String):
      Flow<List<UserInterestEntity>> {
    val _sql: String =
        "SELECT * FROM user_interests WHERE userId = ? AND category = ? ORDER BY weight DESC"
    return createFlow(__db, false, arrayOf("user_interests")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
        _stmt.bindText(_argIndex, category)
        val _columnIndexOfInterestId: Int = getColumnIndexOrThrow(_stmt, "interestId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfCategory: Int = getColumnIndexOrThrow(_stmt, "category")
        val _columnIndexOfValue: Int = getColumnIndexOrThrow(_stmt, "value")
        val _columnIndexOfWeight: Int = getColumnIndexOrThrow(_stmt, "weight")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<UserInterestEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: UserInterestEntity
          val _tmpInterestId: String
          _tmpInterestId = _stmt.getText(_columnIndexOfInterestId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpCategory: String
          _tmpCategory = _stmt.getText(_columnIndexOfCategory)
          val _tmpValue: String
          _tmpValue = _stmt.getText(_columnIndexOfValue)
          val _tmpWeight: Double
          _tmpWeight = _stmt.getDouble(_columnIndexOfWeight)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              UserInterestEntity(_tmpInterestId,_tmpUserId,_tmpCategory,_tmpValue,_tmpWeight,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateWeight(
    userId: String,
    category: String,
    `value`: String,
    weight: Double,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE user_interests SET weight = ?, updatedAt = ? WHERE userId = ? AND category = ? AND value = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindDouble(_argIndex, weight)
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, userId)
        _argIndex = 4
        _stmt.bindText(_argIndex, category)
        _argIndex = 5
        _stmt.bindText(_argIndex, value)
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
