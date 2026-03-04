package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.EntityUpsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.ListingDraftEntity
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
public class ListingDraftDao_Impl(
  __db: RoomDatabase,
) : ListingDraftDao {
  private val __db: RoomDatabase

  private val __upsertAdapterOfListingDraftEntity: EntityUpsertAdapter<ListingDraftEntity>
  init {
    this.__db = __db
    this.__upsertAdapterOfListingDraftEntity = EntityUpsertAdapter<ListingDraftEntity>(object :
        EntityInsertAdapter<ListingDraftEntity>() {
      protected override fun createQuery(): String =
          "INSERT INTO `listing_drafts` (`draftId`,`farmerId`,`step`,`formDataJson`,`createdAt`,`updatedAt`,`expiresAt`) VALUES (?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ListingDraftEntity) {
        statement.bindText(1, entity.draftId)
        statement.bindText(2, entity.farmerId)
        statement.bindText(3, entity.step)
        statement.bindText(4, entity.formDataJson)
        statement.bindLong(5, entity.createdAt)
        statement.bindLong(6, entity.updatedAt)
        val _tmpExpiresAt: Long? = entity.expiresAt
        if (_tmpExpiresAt == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpExpiresAt)
        }
      }
    }, object : EntityDeleteOrUpdateAdapter<ListingDraftEntity>() {
      protected override fun createQuery(): String =
          "UPDATE `listing_drafts` SET `draftId` = ?,`farmerId` = ?,`step` = ?,`formDataJson` = ?,`createdAt` = ?,`updatedAt` = ?,`expiresAt` = ? WHERE `draftId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ListingDraftEntity) {
        statement.bindText(1, entity.draftId)
        statement.bindText(2, entity.farmerId)
        statement.bindText(3, entity.step)
        statement.bindText(4, entity.formDataJson)
        statement.bindLong(5, entity.createdAt)
        statement.bindLong(6, entity.updatedAt)
        val _tmpExpiresAt: Long? = entity.expiresAt
        if (_tmpExpiresAt == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpExpiresAt)
        }
        statement.bindText(8, entity.draftId)
      }
    })
  }

  public override suspend fun upsert(draft: ListingDraftEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __upsertAdapterOfListingDraftEntity.upsert(_connection, draft)
  }

  public override suspend fun getByFarmer(farmerId: String): ListingDraftEntity? {
    val _sql: String =
        "SELECT * FROM listing_drafts WHERE farmerId = ? ORDER BY updatedAt DESC LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfDraftId: Int = getColumnIndexOrThrow(_stmt, "draftId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfStep: Int = getColumnIndexOrThrow(_stmt, "step")
        val _columnIndexOfFormDataJson: Int = getColumnIndexOrThrow(_stmt, "formDataJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfExpiresAt: Int = getColumnIndexOrThrow(_stmt, "expiresAt")
        val _result: ListingDraftEntity?
        if (_stmt.step()) {
          val _tmpDraftId: String
          _tmpDraftId = _stmt.getText(_columnIndexOfDraftId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpStep: String
          _tmpStep = _stmt.getText(_columnIndexOfStep)
          val _tmpFormDataJson: String
          _tmpFormDataJson = _stmt.getText(_columnIndexOfFormDataJson)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpExpiresAt: Long?
          if (_stmt.isNull(_columnIndexOfExpiresAt)) {
            _tmpExpiresAt = null
          } else {
            _tmpExpiresAt = _stmt.getLong(_columnIndexOfExpiresAt)
          }
          _result =
              ListingDraftEntity(_tmpDraftId,_tmpFarmerId,_tmpStep,_tmpFormDataJson,_tmpCreatedAt,_tmpUpdatedAt,_tmpExpiresAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun delete(draftId: String) {
    val _sql: String = "DELETE FROM listing_drafts WHERE draftId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, draftId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteExpired(now: Long) {
    val _sql: String = "DELETE FROM listing_drafts WHERE expiresAt IS NOT NULL AND expiresAt < ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, now)
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
