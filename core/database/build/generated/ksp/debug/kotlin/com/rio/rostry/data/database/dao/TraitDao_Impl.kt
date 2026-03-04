package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.TraitEntity
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class TraitDao_Impl(
  __db: RoomDatabase,
) : TraitDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfTraitEntity: EntityInsertAdapter<TraitEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfTraitEntity = object : EntityInsertAdapter<TraitEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `traits` (`traitId`,`name`,`description`) VALUES (?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: TraitEntity) {
        statement.bindText(1, entity.traitId)
        statement.bindText(2, entity.name)
        val _tmpDescription: String? = entity.description
        if (_tmpDescription == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpDescription)
        }
      }
    }
  }

  public override suspend fun upsert(trait: TraitEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfTraitEntity.insert(_connection, trait)
  }

  public override suspend fun findById(traitId: String): TraitEntity? {
    val _sql: String = "SELECT * FROM traits WHERE traitId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, traitId)
        val _columnIndexOfTraitId: Int = getColumnIndexOrThrow(_stmt, "traitId")
        val _columnIndexOfName: Int = getColumnIndexOrThrow(_stmt, "name")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _result: TraitEntity?
        if (_stmt.step()) {
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
          _result = TraitEntity(_tmpTraitId,_tmpName,_tmpDescription)
        } else {
          _result = null
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
