package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.ProductTraitCrossRef
import com.rio.rostry.`data`.database.entity.TraitEntity
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.collections.MutableList
import kotlin.collections.mutableListOf
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class ProductTraitDao_Impl(
  __db: RoomDatabase,
) : ProductTraitDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfProductTraitCrossRef: EntityInsertAdapter<ProductTraitCrossRef>
  init {
    this.__db = __db
    this.__insertAdapterOfProductTraitCrossRef = object :
        EntityInsertAdapter<ProductTraitCrossRef>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `product_traits` (`productId`,`traitId`) VALUES (?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ProductTraitCrossRef) {
        statement.bindText(1, entity.productId)
        statement.bindText(2, entity.traitId)
      }
    }
  }

  public override suspend fun addTrait(ref: ProductTraitCrossRef): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfProductTraitCrossRef.insert(_connection, ref)
  }

  public override suspend fun traitsForProduct(productId: String): List<TraitEntity> {
    val _sql: String =
        "SELECT t.* FROM traits t INNER JOIN product_traits pt ON t.traitId = pt.traitId WHERE pt.productId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfTraitId: Int = getColumnIndexOrThrow(_stmt, "traitId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _result: MutableList<TraitEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TraitEntity
          val _tmpTraitId: String
          _tmpTraitId = _stmt.getText(_columnIndexOfTraitId)
          val _tmpName: String
          _tmpName = _stmt.getText(_columnIndexOfName)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          _item = TraitEntity(_tmpTraitId,_tmpName,_tmpDescription)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun removeTrait(productId: String, traitId: String) {
    val _sql: String = "DELETE FROM product_traits WHERE productId = ? AND traitId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        _argIndex = 2
        _stmt.bindText(_argIndex, traitId)
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
