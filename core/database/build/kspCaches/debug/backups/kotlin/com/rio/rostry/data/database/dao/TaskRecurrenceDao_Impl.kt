package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.TaskRecurrenceEntity
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

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class TaskRecurrenceDao_Impl(
  __db: RoomDatabase,
) : TaskRecurrenceDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfTaskRecurrenceEntity: EntityInsertAdapter<TaskRecurrenceEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfTaskRecurrenceEntity = object :
        EntityInsertAdapter<TaskRecurrenceEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `task_recurrences` (`recurrenceId`,`taskId`,`pattern`,`interval`,`daysOfWeek`,`endDate`,`maxOccurrences`,`currentOccurrence`,`lastGenerated`,`nextDue`,`isActive`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: TaskRecurrenceEntity) {
        statement.bindText(1, entity.recurrenceId)
        statement.bindText(2, entity.taskId)
        statement.bindText(3, entity.pattern)
        statement.bindLong(4, entity.interval.toLong())
        val _tmpDaysOfWeek: String? = entity.daysOfWeek
        if (_tmpDaysOfWeek == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpDaysOfWeek)
        }
        val _tmpEndDate: Long? = entity.endDate
        if (_tmpEndDate == null) {
          statement.bindNull(6)
        } else {
          statement.bindLong(6, _tmpEndDate)
        }
        val _tmpMaxOccurrences: Int? = entity.maxOccurrences
        if (_tmpMaxOccurrences == null) {
          statement.bindNull(7)
        } else {
          statement.bindLong(7, _tmpMaxOccurrences.toLong())
        }
        statement.bindLong(8, entity.currentOccurrence.toLong())
        val _tmpLastGenerated: Long? = entity.lastGenerated
        if (_tmpLastGenerated == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpLastGenerated)
        }
        val _tmpNextDue: Long? = entity.nextDue
        if (_tmpNextDue == null) {
          statement.bindNull(10)
        } else {
          statement.bindLong(10, _tmpNextDue)
        }
        val _tmp: Int = if (entity.isActive) 1 else 0
        statement.bindLong(11, _tmp.toLong())
        statement.bindLong(12, entity.createdAt)
        statement.bindLong(13, entity.updatedAt)
      }
    }
  }

  public override suspend fun insert(recurrence: TaskRecurrenceEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfTaskRecurrenceEntity.insert(_connection, recurrence)
  }

  public override suspend fun getRecurrenceForTask(taskId: String): TaskRecurrenceEntity? {
    val _sql: String = "SELECT * FROM task_recurrences WHERE taskId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, taskId)
        val _columnIndexOfRecurrenceId: Int = getColumnIndexOrThrow(_stmt, "recurrenceId")
        val _columnIndexOfTaskId: Int = getColumnIndexOrThrow(_stmt, "taskId")
        val _columnIndexOfPattern: Int = getColumnIndexOrThrow(_stmt, "pattern")
        val _columnIndexOfInterval: Int = getColumnIndexOrThrow(_stmt, "interval")
        val _columnIndexOfDaysOfWeek: Int = getColumnIndexOrThrow(_stmt, "daysOfWeek")
        val _columnIndexOfEndDate: Int = getColumnIndexOrThrow(_stmt, "endDate")
        val _columnIndexOfMaxOccurrences: Int = getColumnIndexOrThrow(_stmt, "maxOccurrences")
        val _columnIndexOfCurrentOccurrence: Int = getColumnIndexOrThrow(_stmt, "currentOccurrence")
        val _columnIndexOfLastGenerated: Int = getColumnIndexOrThrow(_stmt, "lastGenerated")
        val _columnIndexOfNextDue: Int = getColumnIndexOrThrow(_stmt, "nextDue")
        val _columnIndexOfIsActive: Int = getColumnIndexOrThrow(_stmt, "isActive")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: TaskRecurrenceEntity?
        if (_stmt.step()) {
          val _tmpRecurrenceId: String
          _tmpRecurrenceId = _stmt.getText(_columnIndexOfRecurrenceId)
          val _tmpTaskId: String
          _tmpTaskId = _stmt.getText(_columnIndexOfTaskId)
          val _tmpPattern: String
          _tmpPattern = _stmt.getText(_columnIndexOfPattern)
          val _tmpInterval: Int
          _tmpInterval = _stmt.getLong(_columnIndexOfInterval).toInt()
          val _tmpDaysOfWeek: String?
          if (_stmt.isNull(_columnIndexOfDaysOfWeek)) {
            _tmpDaysOfWeek = null
          } else {
            _tmpDaysOfWeek = _stmt.getText(_columnIndexOfDaysOfWeek)
          }
          val _tmpEndDate: Long?
          if (_stmt.isNull(_columnIndexOfEndDate)) {
            _tmpEndDate = null
          } else {
            _tmpEndDate = _stmt.getLong(_columnIndexOfEndDate)
          }
          val _tmpMaxOccurrences: Int?
          if (_stmt.isNull(_columnIndexOfMaxOccurrences)) {
            _tmpMaxOccurrences = null
          } else {
            _tmpMaxOccurrences = _stmt.getLong(_columnIndexOfMaxOccurrences).toInt()
          }
          val _tmpCurrentOccurrence: Int
          _tmpCurrentOccurrence = _stmt.getLong(_columnIndexOfCurrentOccurrence).toInt()
          val _tmpLastGenerated: Long?
          if (_stmt.isNull(_columnIndexOfLastGenerated)) {
            _tmpLastGenerated = null
          } else {
            _tmpLastGenerated = _stmt.getLong(_columnIndexOfLastGenerated)
          }
          val _tmpNextDue: Long?
          if (_stmt.isNull(_columnIndexOfNextDue)) {
            _tmpNextDue = null
          } else {
            _tmpNextDue = _stmt.getLong(_columnIndexOfNextDue)
          }
          val _tmpIsActive: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsActive).toInt()
          _tmpIsActive = _tmp != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              TaskRecurrenceEntity(_tmpRecurrenceId,_tmpTaskId,_tmpPattern,_tmpInterval,_tmpDaysOfWeek,_tmpEndDate,_tmpMaxOccurrences,_tmpCurrentOccurrence,_tmpLastGenerated,_tmpNextDue,_tmpIsActive,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDueRecurrences(currentTime: Long): List<TaskRecurrenceEntity> {
    val _sql: String = "SELECT * FROM task_recurrences WHERE isActive = 1 AND nextDue <= ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, currentTime)
        val _columnIndexOfRecurrenceId: Int = getColumnIndexOrThrow(_stmt, "recurrenceId")
        val _columnIndexOfTaskId: Int = getColumnIndexOrThrow(_stmt, "taskId")
        val _columnIndexOfPattern: Int = getColumnIndexOrThrow(_stmt, "pattern")
        val _columnIndexOfInterval: Int = getColumnIndexOrThrow(_stmt, "interval")
        val _columnIndexOfDaysOfWeek: Int = getColumnIndexOrThrow(_stmt, "daysOfWeek")
        val _columnIndexOfEndDate: Int = getColumnIndexOrThrow(_stmt, "endDate")
        val _columnIndexOfMaxOccurrences: Int = getColumnIndexOrThrow(_stmt, "maxOccurrences")
        val _columnIndexOfCurrentOccurrence: Int = getColumnIndexOrThrow(_stmt, "currentOccurrence")
        val _columnIndexOfLastGenerated: Int = getColumnIndexOrThrow(_stmt, "lastGenerated")
        val _columnIndexOfNextDue: Int = getColumnIndexOrThrow(_stmt, "nextDue")
        val _columnIndexOfIsActive: Int = getColumnIndexOrThrow(_stmt, "isActive")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<TaskRecurrenceEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TaskRecurrenceEntity
          val _tmpRecurrenceId: String
          _tmpRecurrenceId = _stmt.getText(_columnIndexOfRecurrenceId)
          val _tmpTaskId: String
          _tmpTaskId = _stmt.getText(_columnIndexOfTaskId)
          val _tmpPattern: String
          _tmpPattern = _stmt.getText(_columnIndexOfPattern)
          val _tmpInterval: Int
          _tmpInterval = _stmt.getLong(_columnIndexOfInterval).toInt()
          val _tmpDaysOfWeek: String?
          if (_stmt.isNull(_columnIndexOfDaysOfWeek)) {
            _tmpDaysOfWeek = null
          } else {
            _tmpDaysOfWeek = _stmt.getText(_columnIndexOfDaysOfWeek)
          }
          val _tmpEndDate: Long?
          if (_stmt.isNull(_columnIndexOfEndDate)) {
            _tmpEndDate = null
          } else {
            _tmpEndDate = _stmt.getLong(_columnIndexOfEndDate)
          }
          val _tmpMaxOccurrences: Int?
          if (_stmt.isNull(_columnIndexOfMaxOccurrences)) {
            _tmpMaxOccurrences = null
          } else {
            _tmpMaxOccurrences = _stmt.getLong(_columnIndexOfMaxOccurrences).toInt()
          }
          val _tmpCurrentOccurrence: Int
          _tmpCurrentOccurrence = _stmt.getLong(_columnIndexOfCurrentOccurrence).toInt()
          val _tmpLastGenerated: Long?
          if (_stmt.isNull(_columnIndexOfLastGenerated)) {
            _tmpLastGenerated = null
          } else {
            _tmpLastGenerated = _stmt.getLong(_columnIndexOfLastGenerated)
          }
          val _tmpNextDue: Long?
          if (_stmt.isNull(_columnIndexOfNextDue)) {
            _tmpNextDue = null
          } else {
            _tmpNextDue = _stmt.getLong(_columnIndexOfNextDue)
          }
          val _tmpIsActive: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfIsActive).toInt()
          _tmpIsActive = _tmp != 0
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              TaskRecurrenceEntity(_tmpRecurrenceId,_tmpTaskId,_tmpPattern,_tmpInterval,_tmpDaysOfWeek,_tmpEndDate,_tmpMaxOccurrences,_tmpCurrentOccurrence,_tmpLastGenerated,_tmpNextDue,_tmpIsActive,_tmpCreatedAt,_tmpUpdatedAt)
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
