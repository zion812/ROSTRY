package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.GalleryFilterStateEntity
import javax.`annotation`.processing.Generated
import kotlin.Int
import kotlin.Long
import kotlin.String
import kotlin.Suppress
import kotlin.Unit
import kotlin.collections.List
import kotlin.reflect.KClass

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class GalleryFilterStateDao_Impl(
  __db: RoomDatabase,
) : GalleryFilterStateDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfGalleryFilterStateEntity:
      EntityInsertAdapter<GalleryFilterStateEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfGalleryFilterStateEntity = object :
        EntityInsertAdapter<GalleryFilterStateEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `gallery_filter_state` (`id`,`ageGroupsJson`,`sourceTypesJson`,`updatedAt`) VALUES (?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: GalleryFilterStateEntity) {
        statement.bindText(1, entity.id)
        statement.bindText(2, entity.ageGroupsJson)
        statement.bindText(3, entity.sourceTypesJson)
        statement.bindLong(4, entity.updatedAt)
      }
    }
  }

  public override suspend fun saveFilterState(state: GalleryFilterStateEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfGalleryFilterStateEntity.insert(_connection, state)
  }

  public override suspend fun getFilterState(id: String): GalleryFilterStateEntity? {
    val _sql: String = "SELECT * FROM gallery_filter_state WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfAgeGroupsJson: Int = getColumnIndexOrThrow(_stmt, "ageGroupsJson")
        val _columnIndexOfSourceTypesJson: Int = getColumnIndexOrThrow(_stmt, "sourceTypesJson")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: GalleryFilterStateEntity?
        if (_stmt.step()) {
          val _tmpId: String
          _tmpId = _stmt.getText(_columnIndexOfId)
          val _tmpAgeGroupsJson: String
          _tmpAgeGroupsJson = _stmt.getText(_columnIndexOfAgeGroupsJson)
          val _tmpSourceTypesJson: String
          _tmpSourceTypesJson = _stmt.getText(_columnIndexOfSourceTypesJson)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              GalleryFilterStateEntity(_tmpId,_tmpAgeGroupsJson,_tmpSourceTypesJson,_tmpUpdatedAt)
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
