package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.ModerationReportEntity
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
public class ModerationReportsDao_Impl(
  __db: RoomDatabase,
) : ModerationReportsDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfModerationReportEntity: EntityInsertAdapter<ModerationReportEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfModerationReportEntity = object :
        EntityInsertAdapter<ModerationReportEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `moderation_reports` (`reportId`,`targetType`,`targetId`,`reporterId`,`reason`,`status`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ModerationReportEntity) {
        statement.bindText(1, entity.reportId)
        statement.bindText(2, entity.targetType)
        statement.bindText(3, entity.targetId)
        statement.bindText(4, entity.reporterId)
        statement.bindText(5, entity.reason)
        statement.bindText(6, entity.status)
        statement.bindLong(7, entity.createdAt)
        statement.bindLong(8, entity.updatedAt)
      }
    }
  }

  public override suspend fun upsert(report: ModerationReportEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfModerationReportEntity.insert(_connection, report)
  }

  public override fun streamByStatus(status: String): Flow<List<ModerationReportEntity>> {
    val _sql: String = "SELECT * FROM moderation_reports WHERE status = ? ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("moderation_reports")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        val _columnIndexOfReportId: Int = getColumnIndexOrThrow(_stmt, "reportId")
        val _columnIndexOfTargetType: Int = getColumnIndexOrThrow(_stmt, "targetType")
        val _columnIndexOfTargetId: Int = getColumnIndexOrThrow(_stmt, "targetId")
        val _columnIndexOfReporterId: Int = getColumnIndexOrThrow(_stmt, "reporterId")
        val _columnIndexOfReason: Int = getColumnIndexOrThrow(_stmt, "reason")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<ModerationReportEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ModerationReportEntity
          val _tmpReportId: String
          _tmpReportId = _stmt.getText(_columnIndexOfReportId)
          val _tmpTargetType: String
          _tmpTargetType = _stmt.getText(_columnIndexOfTargetType)
          val _tmpTargetId: String
          _tmpTargetId = _stmt.getText(_columnIndexOfTargetId)
          val _tmpReporterId: String
          _tmpReporterId = _stmt.getText(_columnIndexOfReporterId)
          val _tmpReason: String
          _tmpReason = _stmt.getText(_columnIndexOfReason)
          val _tmpStatus: String
          _tmpStatus = _stmt.getText(_columnIndexOfStatus)
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              ModerationReportEntity(_tmpReportId,_tmpTargetType,_tmpTargetId,_tmpReporterId,_tmpReason,_tmpStatus,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateStatus(
    reportId: String,
    status: String,
    updatedAt: Long,
  ) {
    val _sql: String = "UPDATE moderation_reports SET status = ?, updatedAt = ? WHERE reportId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, reportId)
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
