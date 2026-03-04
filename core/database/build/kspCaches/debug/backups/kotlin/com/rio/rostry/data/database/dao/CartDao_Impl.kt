package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.CartItemEntity
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
public class CartDao_Impl(
  __db: RoomDatabase,
) : CartDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfCartItemEntity: EntityInsertAdapter<CartItemEntity>

  private val __updateAdapterOfCartItemEntity: EntityDeleteOrUpdateAdapter<CartItemEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfCartItemEntity = object : EntityInsertAdapter<CartItemEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `cart_items` (`id`,`userId`,`productId`,`quantity`,`addedAt`) VALUES (?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: CartItemEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.userId)
        statement.bindText(3, entity.productId)
        statement.bindDouble(4, entity.quantity)
        statement.bindLong(5, entity.addedAt)
      }
    }
    this.__updateAdapterOfCartItemEntity = object : EntityDeleteOrUpdateAdapter<CartItemEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `cart_items` SET `id` = ?,`userId` = ?,`productId` = ?,`quantity` = ?,`addedAt` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: CartItemEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.userId)
        statement.bindText(3, entity.productId)
        statement.bindDouble(4, entity.quantity)
        statement.bindLong(5, entity.addedAt)
        statement.bindText(6, entity.id)
      }
    }
  }

  public override suspend fun upsert(item: CartItemEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfCartItemEntity.insert(_connection, item)
  }

  public override suspend fun update(item: CartItemEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __updateAdapterOfCartItemEntity.handle(_connection, item)
  }

  public override fun observeCart(userId: String): Flow<List<CartItemEntity>> {
    val _sql: String = "SELECT * FROM cart_items WHERE userId = ? ORDER BY addedAt DESC"
    return createFlow(__db, false, arrayOf("cart_items")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfAddedAt: Int = getColumnIndexOrThrow(_stmt, "addedAt")
        val _result: MutableList<CartItemEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: CartItemEntity
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpAddedAt: Long
          _tmpAddedAt = _stmt.getLong(_columnIndexOfAddedAt)
          _item = CartItemEntity(_tmpId,_tmpUserId,_tmpProductId,_tmpQuantity,_tmpAddedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun find(userId: String, productId: String): CartItemEntity? {
    val _sql: String = "SELECT * FROM cart_items WHERE userId = ? AND productId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _argIndex = 2
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfQuantity: Int = getColumnIndexOrThrow(_stmt, "quantity")
        val _columnIndexOfAddedAt: Int = getColumnIndexOrThrow(_stmt, "addedAt")
        val _result: CartItemEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpQuantity: Double
          _tmpQuantity = _stmt.getDouble(_columnIndexOfQuantity)
          val _tmpAddedAt: Long
          _tmpAddedAt = _stmt.getLong(_columnIndexOfAddedAt)
          _result = CartItemEntity(_tmpId,_tmpUserId,_tmpProductId,_tmpQuantity,_tmpAddedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun remove(id: String) {
    val _sql: String = "DELETE FROM cart_items WHERE id = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun removeByUserAndProduct(userId: String, productId: String) {
    val _sql: String = "DELETE FROM cart_items WHERE userId = ? AND productId = ?"
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
