package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.WishlistEntity
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
public class WishlistDao_Impl(
  __db: RoomDatabase,
) : WishlistDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfWishlistEntity: EntityInsertAdapter<WishlistEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfWishlistEntity = object : EntityInsertAdapter<WishlistEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `wishlist` (`userId`,`productId`,`addedAt`) VALUES (?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: WishlistEntity) {
        statement.bindText(1, entity.userId)
        statement.bindText(2, entity.productId)
        statement.bindLong(3, entity.addedAt)
      }
    }
  }

  public override suspend fun upsert(item: WishlistEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfWishlistEntity.insert(_connection, item)
  }

  public override fun observe(userId: String): Flow<List<WishlistEntity>> {
    val _sql: String = "SELECT * FROM wishlist WHERE userId = ? ORDER BY addedAt DESC"
    return createFlow(__db, false, arrayOf("wishlist")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfAddedAt: Int = getColumnIndexOrThrow(_stmt, "addedAt")
        val _result: MutableList<WishlistEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: WishlistEntity
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpAddedAt: Long
          _tmpAddedAt = _stmt.getLong(_columnIndexOfAddedAt)
          _item = WishlistEntity(_tmpUserId,_tmpProductId,_tmpAddedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun remove(userId: String, productId: String) {
    val _sql: String = "DELETE FROM wishlist WHERE userId = ? AND productId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
        _stmt.bindText(_argIndex, productId)
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
