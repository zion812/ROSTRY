package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.EntityUpsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.FamilyTreeEntity
import javax.`annotation`.processing.Generated
import kotlin.Boolean
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
public class FamilyTreeDao_Impl(
  __db: RoomDatabase,
) : FamilyTreeDao {
  private val __db: RoomDatabase

  private val __upsertAdapterOfFamilyTreeEntity: EntityUpsertAdapter<FamilyTreeEntity>
  init {
    this.__db = __db
    this.__upsertAdapterOfFamilyTreeEntity = EntityUpsertAdapter<FamilyTreeEntity>(object :
        EntityInsertAdapter<FamilyTreeEntity>() {
      protected override fun createQuery(): String =
          "INSERT INTO `family_tree` (`nodeId`,`productId`,`parentProductId`,`childProductId`,`relationType`,`createdAt`,`updatedAt`,`isDeleted`,`deletedAt`) VALUES (?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: FamilyTreeEntity) {
        statement.bindText(1, entity.nodeId)
        statement.bindText(2, entity.productId)
        val _tmpParentProductId: String? = entity.parentProductId
        if (_tmpParentProductId == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpParentProductId)
        }
        val _tmpChildProductId: String? = entity.childProductId
        if (_tmpChildProductId == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpChildProductId)
        }
        val _tmpRelationType: String? = entity.relationType
        if (_tmpRelationType == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpRelationType)
        }
        statement.bindLong(6, entity.createdAt)
        statement.bindLong(7, entity.updatedAt)
        val _tmp: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(8, _tmp.toLong())
        val _tmpDeletedAt: Long? = entity.deletedAt
        if (_tmpDeletedAt == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpDeletedAt)
        }
      }
    }, object : EntityDeleteOrUpdateAdapter<FamilyTreeEntity>() {
      protected override fun createQuery(): String =
          "UPDATE `family_tree` SET `nodeId` = ?,`productId` = ?,`parentProductId` = ?,`childProductId` = ?,`relationType` = ?,`createdAt` = ?,`updatedAt` = ?,`isDeleted` = ?,`deletedAt` = ? WHERE `nodeId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: FamilyTreeEntity) {
        statement.bindText(1, entity.nodeId)
        statement.bindText(2, entity.productId)
        val _tmpParentProductId: String? = entity.parentProductId
        if (_tmpParentProductId == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpParentProductId)
        }
        val _tmpChildProductId: String? = entity.childProductId
        if (_tmpChildProductId == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpChildProductId)
        }
        val _tmpRelationType: String? = entity.relationType
        if (_tmpRelationType == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpRelationType)
        }
        statement.bindLong(6, entity.createdAt)
        statement.bindLong(7, entity.updatedAt)
        val _tmp: Int = if (entity.isDeleted) 1 else 0
        statement.bindLong(8, _tmp.toLong())
        val _tmpDeletedAt: Long? = entity.deletedAt
        if (_tmpDeletedAt == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpDeletedAt)
        }
        statement.bindText(10, entity.nodeId)
      }
    })
  }

  public override suspend fun upsertAll(items: List<FamilyTreeEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __upsertAdapterOfFamilyTreeEntity.upsert(_connection, items)
  }

  public override suspend fun upsert(item: FamilyTreeEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __upsertAdapterOfFamilyTreeEntity.upsert(_connection, item)
  }

  public override suspend fun findById(nodeId: String): FamilyTreeEntity? {
    val _sql: String = "SELECT * FROM family_tree WHERE nodeId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, nodeId)
        val _columnIndexOfNodeId: Int = getColumnIndexOrThrow(_stmt, "nodeId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfParentProductId: Int = getColumnIndexOrThrow(_stmt, "parentProductId")
        val _columnIndexOfChildProductId: Int = getColumnIndexOrThrow(_stmt, "childProductId")
        val _columnIndexOfRelationType: Int = getColumnIndexOrThrow(_stmt, "relationType")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _result: FamilyTreeEntity?
        if (_stmt.step()) {
          val _tmpNodeId: String
          _tmpNodeId = _stmt.getText(_columnIndexOfNodeId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpParentProductId: String?
          if (_stmt.isNull(_columnIndexOfParentProductId)) {
            _tmpParentProductId = null
          } else {
            _tmpParentProductId = _stmt.getText(_columnIndexOfParentProductId)
          }
          val _tmpChildProductId: String?
          if (_stmt.isNull(_columnIndexOfChildProductId)) {
            _tmpChildProductId = null
          } else {
            _tmpChildProductId = _stmt.getText(_columnIndexOfChildProductId)
          }
          val _tmpRelationType: String?
          if (_stmt.isNull(_columnIndexOfRelationType)) {
            _tmpRelationType = null
          } else {
            _tmpRelationType = _stmt.getText(_columnIndexOfRelationType)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          _result =
              FamilyTreeEntity(_tmpNodeId,_tmpProductId,_tmpParentProductId,_tmpChildProductId,_tmpRelationType,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getForProduct(productId: String): Flow<List<FamilyTreeEntity>> {
    val _sql: String = "SELECT * FROM family_tree WHERE productId = ? AND isDeleted = 0"
    return createFlow(__db, false, arrayOf("family_tree")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, productId)
        val _columnIndexOfNodeId: Int = getColumnIndexOrThrow(_stmt, "nodeId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfParentProductId: Int = getColumnIndexOrThrow(_stmt, "parentProductId")
        val _columnIndexOfChildProductId: Int = getColumnIndexOrThrow(_stmt, "childProductId")
        val _columnIndexOfRelationType: Int = getColumnIndexOrThrow(_stmt, "relationType")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _result: MutableList<FamilyTreeEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FamilyTreeEntity
          val _tmpNodeId: String
          _tmpNodeId = _stmt.getText(_columnIndexOfNodeId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpParentProductId: String?
          if (_stmt.isNull(_columnIndexOfParentProductId)) {
            _tmpParentProductId = null
          } else {
            _tmpParentProductId = _stmt.getText(_columnIndexOfParentProductId)
          }
          val _tmpChildProductId: String?
          if (_stmt.isNull(_columnIndexOfChildProductId)) {
            _tmpChildProductId = null
          } else {
            _tmpChildProductId = _stmt.getText(_columnIndexOfChildProductId)
          }
          val _tmpRelationType: String?
          if (_stmt.isNull(_columnIndexOfRelationType)) {
            _tmpRelationType = null
          } else {
            _tmpRelationType = _stmt.getText(_columnIndexOfRelationType)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          _item =
              FamilyTreeEntity(_tmpNodeId,_tmpProductId,_tmpParentProductId,_tmpChildProductId,_tmpRelationType,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getUpdatedSince(since: Long, limit: Int): List<FamilyTreeEntity> {
    val _sql: String =
        "SELECT * FROM family_tree WHERE updatedAt > ? ORDER BY updatedAt ASC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, since)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfNodeId: Int = getColumnIndexOrThrow(_stmt, "nodeId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfParentProductId: Int = getColumnIndexOrThrow(_stmt, "parentProductId")
        val _columnIndexOfChildProductId: Int = getColumnIndexOrThrow(_stmt, "childProductId")
        val _columnIndexOfRelationType: Int = getColumnIndexOrThrow(_stmt, "relationType")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfIsDeleted: Int = getColumnIndexOrThrow(_stmt, "isDeleted")
        val _columnIndexOfDeletedAt: Int = getColumnIndexOrThrow(_stmt, "deletedAt")
        val _result: MutableList<FamilyTreeEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: FamilyTreeEntity
          val _tmpNodeId: String
          _tmpNodeId = _stmt.getText(_columnIndexOfNodeId)
          val _tmpProductId: String
          _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          val _tmpParentProductId: String?
          if (_stmt.isNull(_columnIndexOfParentProductId)) {
            _tmpParentProductId = null
          } else {
            _tmpParentProductId = _stmt.getText(_columnIndexOfParentProductId)
          }
          val _tmpChildProductId: String?
          if (_stmt.isNull(_columnIndexOfChildProductId)) {
            _tmpChildProductId = null
          } else {
            _tmpChildProductId = _stmt.getText(_columnIndexOfChildProductId)
          }
          val _tmpRelationType: String?
          if (_stmt.isNull(_columnIndexOfRelationType)) {
            _tmpRelationType = null
          } else {
            _tmpRelationType = _stmt.getText(_columnIndexOfRelationType)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpIsDeleted: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsDeleted).toInt()
          _tmpIsDeleted = _tmp != 0
          val _tmpDeletedAt: Long?
          if (_stmt.isNull(_columnIndexOfDeletedAt)) {
            _tmpDeletedAt = null
          } else {
            _tmpDeletedAt = _stmt.getLong(_columnIndexOfDeletedAt)
          }
          _item =
              FamilyTreeEntity(_tmpNodeId,_tmpProductId,_tmpParentProductId,_tmpChildProductId,_tmpRelationType,_tmpCreatedAt,_tmpUpdatedAt,_tmpIsDeleted,_tmpDeletedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun purgeDeleted() {
    val _sql: String = "DELETE FROM family_tree WHERE isDeleted = 1"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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
