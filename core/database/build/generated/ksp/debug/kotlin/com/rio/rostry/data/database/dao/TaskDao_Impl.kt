package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.TaskEntity
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
import kotlin.text.StringBuilder
import kotlinx.coroutines.flow.Flow

@Generated(value = ["androidx.room.RoomProcessor"])
@Suppress(names = ["UNCHECKED_CAST", "DEPRECATION", "REDUNDANT_PROJECTION", "REMOVAL"])
public class TaskDao_Impl(
  __db: RoomDatabase,
) : TaskDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfTaskEntity: EntityInsertAdapter<TaskEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfTaskEntity = object : EntityInsertAdapter<TaskEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `tasks` (`taskId`,`farmerId`,`productId`,`batchId`,`taskType`,`title`,`description`,`dueAt`,`completedAt`,`completedBy`,`priority`,`recurrence`,`notes`,`createdAt`,`updatedAt`,`dirty`,`syncedAt`,`snoozeUntil`,`metadata`,`mergedAt`,`mergeCount`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: TaskEntity) {
        statement.bindText(1, entity.taskId)
        statement.bindText(2, entity.farmerId)
        val _tmpProductId: String? = entity.productId
        if (_tmpProductId == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpProductId)
        }
        val _tmpBatchId: String? = entity.batchId
        if (_tmpBatchId == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpBatchId)
        }
        statement.bindText(5, entity.taskType)
        statement.bindText(6, entity.title)
        val _tmpDescription: String? = entity.description
        if (_tmpDescription == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpDescription)
        }
        statement.bindLong(8, entity.dueAt)
        val _tmpCompletedAt: Long? = entity.completedAt
        if (_tmpCompletedAt == null) {
          statement.bindNull(9)
        } else {
          statement.bindLong(9, _tmpCompletedAt)
        }
        val _tmpCompletedBy: String? = entity.completedBy
        if (_tmpCompletedBy == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpCompletedBy)
        }
        statement.bindText(11, entity.priority)
        val _tmpRecurrence: String? = entity.recurrence
        if (_tmpRecurrence == null) {
          statement.bindNull(12)
        } else {
          statement.bindText(12, _tmpRecurrence)
        }
        val _tmpNotes: String? = entity.notes
        if (_tmpNotes == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpNotes)
        }
        statement.bindLong(14, entity.createdAt)
        statement.bindLong(15, entity.updatedAt)
        val _tmp: Int = if (entity.dirty) 1 else 0
        statement.bindLong(16, _tmp.toLong())
        val _tmpSyncedAt: Long? = entity.syncedAt
        if (_tmpSyncedAt == null) {
          statement.bindNull(17)
        } else {
          statement.bindLong(17, _tmpSyncedAt)
        }
        val _tmpSnoozeUntil: Long? = entity.snoozeUntil
        if (_tmpSnoozeUntil == null) {
          statement.bindNull(18)
        } else {
          statement.bindLong(18, _tmpSnoozeUntil)
        }
        val _tmpMetadata: String? = entity.metadata
        if (_tmpMetadata == null) {
          statement.bindNull(19)
        } else {
          statement.bindText(19, _tmpMetadata)
        }
        val _tmpMergedAt: Long? = entity.mergedAt
        if (_tmpMergedAt == null) {
          statement.bindNull(20)
        } else {
          statement.bindLong(20, _tmpMergedAt)
        }
        statement.bindLong(21, entity.mergeCount.toLong())
      }
    }
  }

  public override suspend fun upsert(task: TaskEntity): Unit = performSuspending(__db, false, true)
      { _connection ->
    __insertAdapterOfTaskEntity.insert(_connection, task)
  }

  public override suspend fun upsert(tasks: List<TaskEntity>): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfTaskEntity.insert(_connection, tasks)
  }

  public override fun observeDueForFarmer(farmerId: String, now: Long): Flow<List<TaskEntity>> {
    val _sql: String =
        "SELECT * FROM tasks WHERE farmerId = ? AND completedAt IS NULL AND (snoozeUntil IS NULL OR snoozeUntil <= ?) AND dueAt <= ? ORDER BY CASE priority WHEN 'URGENT' THEN 3 WHEN 'HIGH' THEN 2 WHEN 'MEDIUM' THEN 1 ELSE 0 END DESC, dueAt ASC"
    return createFlow(__db, false, arrayOf("tasks")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
        _argIndex = 3
        _stmt.bindLong(_argIndex, now)
        val _columnIndexOfTaskId: Int = getColumnIndexOrThrow(_stmt, "taskId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfTaskType: Int = getColumnIndexOrThrow(_stmt, "taskType")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfDueAt: Int = getColumnIndexOrThrow(_stmt, "dueAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfCompletedBy: Int = getColumnIndexOrThrow(_stmt, "completedBy")
        val _columnIndexOfPriority: Int = getColumnIndexOrThrow(_stmt, "priority")
        val _columnIndexOfRecurrence: Int = getColumnIndexOrThrow(_stmt, "recurrence")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfSnoozeUntil: Int = getColumnIndexOrThrow(_stmt, "snoozeUntil")
        val _columnIndexOfMetadata: Int = getColumnIndexOrThrow(_stmt, "metadata")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TaskEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TaskEntity
          val _tmpTaskId: String
          _tmpTaskId = _stmt.getText(_columnIndexOfTaskId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpTaskType: String
          _tmpTaskType = _stmt.getText(_columnIndexOfTaskType)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpDueAt: Long
          _tmpDueAt = _stmt.getLong(_columnIndexOfDueAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpCompletedBy: String?
          if (_stmt.isNull(_columnIndexOfCompletedBy)) {
            _tmpCompletedBy = null
          } else {
            _tmpCompletedBy = _stmt.getText(_columnIndexOfCompletedBy)
          }
          val _tmpPriority: String
          _tmpPriority = _stmt.getText(_columnIndexOfPriority)
          val _tmpRecurrence: String?
          if (_stmt.isNull(_columnIndexOfRecurrence)) {
            _tmpRecurrence = null
          } else {
            _tmpRecurrence = _stmt.getText(_columnIndexOfRecurrence)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpSnoozeUntil: Long?
          if (_stmt.isNull(_columnIndexOfSnoozeUntil)) {
            _tmpSnoozeUntil = null
          } else {
            _tmpSnoozeUntil = _stmt.getLong(_columnIndexOfSnoozeUntil)
          }
          val _tmpMetadata: String?
          if (_stmt.isNull(_columnIndexOfMetadata)) {
            _tmpMetadata = null
          } else {
            _tmpMetadata = _stmt.getText(_columnIndexOfMetadata)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TaskEntity(_tmpTaskId,_tmpFarmerId,_tmpProductId,_tmpBatchId,_tmpTaskType,_tmpTitle,_tmpDescription,_tmpDueAt,_tmpCompletedAt,_tmpCompletedBy,_tmpPriority,_tmpRecurrence,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpSnoozeUntil,_tmpMetadata,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeOverdueForFarmer(farmerId: String, now: Long): Flow<List<TaskEntity>> {
    val _sql: String =
        "SELECT * FROM tasks WHERE farmerId = ? AND completedAt IS NULL AND dueAt < ? ORDER BY dueAt ASC"
    return createFlow(__db, false, arrayOf("tasks")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
        val _columnIndexOfTaskId: Int = getColumnIndexOrThrow(_stmt, "taskId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfTaskType: Int = getColumnIndexOrThrow(_stmt, "taskType")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfDueAt: Int = getColumnIndexOrThrow(_stmt, "dueAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfCompletedBy: Int = getColumnIndexOrThrow(_stmt, "completedBy")
        val _columnIndexOfPriority: Int = getColumnIndexOrThrow(_stmt, "priority")
        val _columnIndexOfRecurrence: Int = getColumnIndexOrThrow(_stmt, "recurrence")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfSnoozeUntil: Int = getColumnIndexOrThrow(_stmt, "snoozeUntil")
        val _columnIndexOfMetadata: Int = getColumnIndexOrThrow(_stmt, "metadata")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TaskEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TaskEntity
          val _tmpTaskId: String
          _tmpTaskId = _stmt.getText(_columnIndexOfTaskId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpTaskType: String
          _tmpTaskType = _stmt.getText(_columnIndexOfTaskType)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpDueAt: Long
          _tmpDueAt = _stmt.getLong(_columnIndexOfDueAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpCompletedBy: String?
          if (_stmt.isNull(_columnIndexOfCompletedBy)) {
            _tmpCompletedBy = null
          } else {
            _tmpCompletedBy = _stmt.getText(_columnIndexOfCompletedBy)
          }
          val _tmpPriority: String
          _tmpPriority = _stmt.getText(_columnIndexOfPriority)
          val _tmpRecurrence: String?
          if (_stmt.isNull(_columnIndexOfRecurrence)) {
            _tmpRecurrence = null
          } else {
            _tmpRecurrence = _stmt.getText(_columnIndexOfRecurrence)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpSnoozeUntil: Long?
          if (_stmt.isNull(_columnIndexOfSnoozeUntil)) {
            _tmpSnoozeUntil = null
          } else {
            _tmpSnoozeUntil = _stmt.getLong(_columnIndexOfSnoozeUntil)
          }
          val _tmpMetadata: String?
          if (_stmt.isNull(_columnIndexOfMetadata)) {
            _tmpMetadata = null
          } else {
            _tmpMetadata = _stmt.getText(_columnIndexOfMetadata)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TaskEntity(_tmpTaskId,_tmpFarmerId,_tmpProductId,_tmpBatchId,_tmpTaskType,_tmpTitle,_tmpDescription,_tmpDueAt,_tmpCompletedAt,_tmpCompletedBy,_tmpPriority,_tmpRecurrence,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpSnoozeUntil,_tmpMetadata,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeDueWindowForFarmer(
    farmerId: String,
    now: Long,
    endOfDay: Long,
  ): Flow<List<TaskEntity>> {
    val _sql: String =
        "SELECT * FROM tasks WHERE farmerId = ? AND completedAt IS NULL AND (snoozeUntil IS NULL OR snoozeUntil <= ?) AND dueAt BETWEEN ? AND ? ORDER BY dueAt ASC"
    return createFlow(__db, false, arrayOf("tasks")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
        _argIndex = 3
        _stmt.bindLong(_argIndex, now)
        _argIndex = 4
        _stmt.bindLong(_argIndex, endOfDay)
        val _columnIndexOfTaskId: Int = getColumnIndexOrThrow(_stmt, "taskId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfTaskType: Int = getColumnIndexOrThrow(_stmt, "taskType")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfDueAt: Int = getColumnIndexOrThrow(_stmt, "dueAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfCompletedBy: Int = getColumnIndexOrThrow(_stmt, "completedBy")
        val _columnIndexOfPriority: Int = getColumnIndexOrThrow(_stmt, "priority")
        val _columnIndexOfRecurrence: Int = getColumnIndexOrThrow(_stmt, "recurrence")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfSnoozeUntil: Int = getColumnIndexOrThrow(_stmt, "snoozeUntil")
        val _columnIndexOfMetadata: Int = getColumnIndexOrThrow(_stmt, "metadata")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TaskEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TaskEntity
          val _tmpTaskId: String
          _tmpTaskId = _stmt.getText(_columnIndexOfTaskId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpTaskType: String
          _tmpTaskType = _stmt.getText(_columnIndexOfTaskType)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpDueAt: Long
          _tmpDueAt = _stmt.getLong(_columnIndexOfDueAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpCompletedBy: String?
          if (_stmt.isNull(_columnIndexOfCompletedBy)) {
            _tmpCompletedBy = null
          } else {
            _tmpCompletedBy = _stmt.getText(_columnIndexOfCompletedBy)
          }
          val _tmpPriority: String
          _tmpPriority = _stmt.getText(_columnIndexOfPriority)
          val _tmpRecurrence: String?
          if (_stmt.isNull(_columnIndexOfRecurrence)) {
            _tmpRecurrence = null
          } else {
            _tmpRecurrence = _stmt.getText(_columnIndexOfRecurrence)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpSnoozeUntil: Long?
          if (_stmt.isNull(_columnIndexOfSnoozeUntil)) {
            _tmpSnoozeUntil = null
          } else {
            _tmpSnoozeUntil = _stmt.getLong(_columnIndexOfSnoozeUntil)
          }
          val _tmpMetadata: String?
          if (_stmt.isNull(_columnIndexOfMetadata)) {
            _tmpMetadata = null
          } else {
            _tmpMetadata = _stmt.getText(_columnIndexOfMetadata)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TaskEntity(_tmpTaskId,_tmpFarmerId,_tmpProductId,_tmpBatchId,_tmpTaskType,_tmpTitle,_tmpDescription,_tmpDueAt,_tmpCompletedAt,_tmpCompletedBy,_tmpPriority,_tmpRecurrence,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpSnoozeUntil,_tmpMetadata,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeNextPendingTask(farmerId: String, now: Long): Flow<TaskEntity?> {
    val _sql: String =
        "SELECT * FROM tasks WHERE farmerId = ? AND completedAt IS NULL AND (snoozeUntil IS NULL OR snoozeUntil <= ?) ORDER BY dueAt ASC LIMIT 1"
    return createFlow(__db, false, arrayOf("tasks")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
        val _columnIndexOfTaskId: Int = getColumnIndexOrThrow(_stmt, "taskId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfTaskType: Int = getColumnIndexOrThrow(_stmt, "taskType")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfDueAt: Int = getColumnIndexOrThrow(_stmt, "dueAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfCompletedBy: Int = getColumnIndexOrThrow(_stmt, "completedBy")
        val _columnIndexOfPriority: Int = getColumnIndexOrThrow(_stmt, "priority")
        val _columnIndexOfRecurrence: Int = getColumnIndexOrThrow(_stmt, "recurrence")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfSnoozeUntil: Int = getColumnIndexOrThrow(_stmt, "snoozeUntil")
        val _columnIndexOfMetadata: Int = getColumnIndexOrThrow(_stmt, "metadata")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: TaskEntity?
        if (_stmt.step()) {
          val _tmpTaskId: String
          _tmpTaskId = _stmt.getText(_columnIndexOfTaskId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpTaskType: String
          _tmpTaskType = _stmt.getText(_columnIndexOfTaskType)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpDueAt: Long
          _tmpDueAt = _stmt.getLong(_columnIndexOfDueAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpCompletedBy: String?
          if (_stmt.isNull(_columnIndexOfCompletedBy)) {
            _tmpCompletedBy = null
          } else {
            _tmpCompletedBy = _stmt.getText(_columnIndexOfCompletedBy)
          }
          val _tmpPriority: String
          _tmpPriority = _stmt.getText(_columnIndexOfPriority)
          val _tmpRecurrence: String?
          if (_stmt.isNull(_columnIndexOfRecurrence)) {
            _tmpRecurrence = null
          } else {
            _tmpRecurrence = _stmt.getText(_columnIndexOfRecurrence)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpSnoozeUntil: Long?
          if (_stmt.isNull(_columnIndexOfSnoozeUntil)) {
            _tmpSnoozeUntil = null
          } else {
            _tmpSnoozeUntil = _stmt.getLong(_columnIndexOfSnoozeUntil)
          }
          val _tmpMetadata: String?
          if (_stmt.isNull(_columnIndexOfMetadata)) {
            _tmpMetadata = null
          } else {
            _tmpMetadata = _stmt.getText(_columnIndexOfMetadata)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _result =
              TaskEntity(_tmpTaskId,_tmpFarmerId,_tmpProductId,_tmpBatchId,_tmpTaskType,_tmpTitle,_tmpDescription,_tmpDueAt,_tmpCompletedAt,_tmpCompletedBy,_tmpPriority,_tmpRecurrence,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpSnoozeUntil,_tmpMetadata,_tmpMergedAt,_tmpMergeCount)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeOverdueCountForFarmer(farmerId: String, now: Long): Flow<Int> {
    val _sql: String =
        "SELECT COUNT(*) FROM tasks WHERE farmerId = ? AND completedAt IS NULL AND dueAt < ?"
    return createFlow(__db, false, arrayOf("tasks")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, now)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeRecentCompletedForFarmer(farmerId: String, limit: Int):
      Flow<List<TaskEntity>> {
    val _sql: String =
        "SELECT * FROM tasks WHERE farmerId = ? AND completedAt IS NOT NULL ORDER BY completedAt DESC LIMIT ?"
    return createFlow(__db, false, arrayOf("tasks")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfTaskId: Int = getColumnIndexOrThrow(_stmt, "taskId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfTaskType: Int = getColumnIndexOrThrow(_stmt, "taskType")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfDueAt: Int = getColumnIndexOrThrow(_stmt, "dueAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfCompletedBy: Int = getColumnIndexOrThrow(_stmt, "completedBy")
        val _columnIndexOfPriority: Int = getColumnIndexOrThrow(_stmt, "priority")
        val _columnIndexOfRecurrence: Int = getColumnIndexOrThrow(_stmt, "recurrence")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfSnoozeUntil: Int = getColumnIndexOrThrow(_stmt, "snoozeUntil")
        val _columnIndexOfMetadata: Int = getColumnIndexOrThrow(_stmt, "metadata")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TaskEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TaskEntity
          val _tmpTaskId: String
          _tmpTaskId = _stmt.getText(_columnIndexOfTaskId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpTaskType: String
          _tmpTaskType = _stmt.getText(_columnIndexOfTaskType)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpDueAt: Long
          _tmpDueAt = _stmt.getLong(_columnIndexOfDueAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpCompletedBy: String?
          if (_stmt.isNull(_columnIndexOfCompletedBy)) {
            _tmpCompletedBy = null
          } else {
            _tmpCompletedBy = _stmt.getText(_columnIndexOfCompletedBy)
          }
          val _tmpPriority: String
          _tmpPriority = _stmt.getText(_columnIndexOfPriority)
          val _tmpRecurrence: String?
          if (_stmt.isNull(_columnIndexOfRecurrence)) {
            _tmpRecurrence = null
          } else {
            _tmpRecurrence = _stmt.getText(_columnIndexOfRecurrence)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpSnoozeUntil: Long?
          if (_stmt.isNull(_columnIndexOfSnoozeUntil)) {
            _tmpSnoozeUntil = null
          } else {
            _tmpSnoozeUntil = _stmt.getLong(_columnIndexOfSnoozeUntil)
          }
          val _tmpMetadata: String?
          if (_stmt.isNull(_columnIndexOfMetadata)) {
            _tmpMetadata = null
          } else {
            _tmpMetadata = _stmt.getText(_columnIndexOfMetadata)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TaskEntity(_tmpTaskId,_tmpFarmerId,_tmpProductId,_tmpBatchId,_tmpTaskType,_tmpTitle,_tmpDescription,_tmpDueAt,_tmpCompletedAt,_tmpCompletedBy,_tmpPriority,_tmpRecurrence,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpSnoozeUntil,_tmpMetadata,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByFarmer(farmerId: String): Flow<List<TaskEntity>> {
    val _sql: String = "SELECT * FROM tasks WHERE farmerId = ? ORDER BY createdAt DESC"
    return createFlow(__db, false, arrayOf("tasks")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        val _columnIndexOfTaskId: Int = getColumnIndexOrThrow(_stmt, "taskId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfTaskType: Int = getColumnIndexOrThrow(_stmt, "taskType")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfDueAt: Int = getColumnIndexOrThrow(_stmt, "dueAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfCompletedBy: Int = getColumnIndexOrThrow(_stmt, "completedBy")
        val _columnIndexOfPriority: Int = getColumnIndexOrThrow(_stmt, "priority")
        val _columnIndexOfRecurrence: Int = getColumnIndexOrThrow(_stmt, "recurrence")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfSnoozeUntil: Int = getColumnIndexOrThrow(_stmt, "snoozeUntil")
        val _columnIndexOfMetadata: Int = getColumnIndexOrThrow(_stmt, "metadata")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TaskEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TaskEntity
          val _tmpTaskId: String
          _tmpTaskId = _stmt.getText(_columnIndexOfTaskId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpTaskType: String
          _tmpTaskType = _stmt.getText(_columnIndexOfTaskType)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpDueAt: Long
          _tmpDueAt = _stmt.getLong(_columnIndexOfDueAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpCompletedBy: String?
          if (_stmt.isNull(_columnIndexOfCompletedBy)) {
            _tmpCompletedBy = null
          } else {
            _tmpCompletedBy = _stmt.getText(_columnIndexOfCompletedBy)
          }
          val _tmpPriority: String
          _tmpPriority = _stmt.getText(_columnIndexOfPriority)
          val _tmpRecurrence: String?
          if (_stmt.isNull(_columnIndexOfRecurrence)) {
            _tmpRecurrence = null
          } else {
            _tmpRecurrence = _stmt.getText(_columnIndexOfRecurrence)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpSnoozeUntil: Long?
          if (_stmt.isNull(_columnIndexOfSnoozeUntil)) {
            _tmpSnoozeUntil = null
          } else {
            _tmpSnoozeUntil = _stmt.getLong(_columnIndexOfSnoozeUntil)
          }
          val _tmpMetadata: String?
          if (_stmt.isNull(_columnIndexOfMetadata)) {
            _tmpMetadata = null
          } else {
            _tmpMetadata = _stmt.getText(_columnIndexOfMetadata)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TaskEntity(_tmpTaskId,_tmpFarmerId,_tmpProductId,_tmpBatchId,_tmpTaskType,_tmpTitle,_tmpDescription,_tmpDueAt,_tmpCompletedAt,_tmpCompletedBy,_tmpPriority,_tmpRecurrence,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpSnoozeUntil,_tmpMetadata,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDirty(): List<TaskEntity> {
    val _sql: String = "SELECT * FROM tasks WHERE dirty = 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfTaskId: Int = getColumnIndexOrThrow(_stmt, "taskId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfTaskType: Int = getColumnIndexOrThrow(_stmt, "taskType")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfDueAt: Int = getColumnIndexOrThrow(_stmt, "dueAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfCompletedBy: Int = getColumnIndexOrThrow(_stmt, "completedBy")
        val _columnIndexOfPriority: Int = getColumnIndexOrThrow(_stmt, "priority")
        val _columnIndexOfRecurrence: Int = getColumnIndexOrThrow(_stmt, "recurrence")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfSnoozeUntil: Int = getColumnIndexOrThrow(_stmt, "snoozeUntil")
        val _columnIndexOfMetadata: Int = getColumnIndexOrThrow(_stmt, "metadata")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TaskEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TaskEntity
          val _tmpTaskId: String
          _tmpTaskId = _stmt.getText(_columnIndexOfTaskId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpTaskType: String
          _tmpTaskType = _stmt.getText(_columnIndexOfTaskType)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpDueAt: Long
          _tmpDueAt = _stmt.getLong(_columnIndexOfDueAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpCompletedBy: String?
          if (_stmt.isNull(_columnIndexOfCompletedBy)) {
            _tmpCompletedBy = null
          } else {
            _tmpCompletedBy = _stmt.getText(_columnIndexOfCompletedBy)
          }
          val _tmpPriority: String
          _tmpPriority = _stmt.getText(_columnIndexOfPriority)
          val _tmpRecurrence: String?
          if (_stmt.isNull(_columnIndexOfRecurrence)) {
            _tmpRecurrence = null
          } else {
            _tmpRecurrence = _stmt.getText(_columnIndexOfRecurrence)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpSnoozeUntil: Long?
          if (_stmt.isNull(_columnIndexOfSnoozeUntil)) {
            _tmpSnoozeUntil = null
          } else {
            _tmpSnoozeUntil = _stmt.getLong(_columnIndexOfSnoozeUntil)
          }
          val _tmpMetadata: String?
          if (_stmt.isNull(_columnIndexOfMetadata)) {
            _tmpMetadata = null
          } else {
            _tmpMetadata = _stmt.getText(_columnIndexOfMetadata)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TaskEntity(_tmpTaskId,_tmpFarmerId,_tmpProductId,_tmpBatchId,_tmpTaskType,_tmpTitle,_tmpDescription,_tmpDueAt,_tmpCompletedAt,_tmpCompletedBy,_tmpPriority,_tmpRecurrence,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpSnoozeUntil,_tmpMetadata,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDueByType(
    farmerId: String,
    taskType: String,
    now: Long,
  ): List<TaskEntity> {
    val _sql: String =
        "SELECT * FROM tasks WHERE farmerId = ? AND taskType = ? AND completedAt IS NULL AND (snoozeUntil IS NULL OR snoozeUntil <= ?) AND dueAt <= ? ORDER BY dueAt ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindText(_argIndex, taskType)
        _argIndex = 3
        _stmt.bindLong(_argIndex, now)
        _argIndex = 4
        _stmt.bindLong(_argIndex, now)
        val _columnIndexOfTaskId: Int = getColumnIndexOrThrow(_stmt, "taskId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfTaskType: Int = getColumnIndexOrThrow(_stmt, "taskType")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfDueAt: Int = getColumnIndexOrThrow(_stmt, "dueAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfCompletedBy: Int = getColumnIndexOrThrow(_stmt, "completedBy")
        val _columnIndexOfPriority: Int = getColumnIndexOrThrow(_stmt, "priority")
        val _columnIndexOfRecurrence: Int = getColumnIndexOrThrow(_stmt, "recurrence")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfSnoozeUntil: Int = getColumnIndexOrThrow(_stmt, "snoozeUntil")
        val _columnIndexOfMetadata: Int = getColumnIndexOrThrow(_stmt, "metadata")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TaskEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TaskEntity
          val _tmpTaskId: String
          _tmpTaskId = _stmt.getText(_columnIndexOfTaskId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpTaskType: String
          _tmpTaskType = _stmt.getText(_columnIndexOfTaskType)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpDueAt: Long
          _tmpDueAt = _stmt.getLong(_columnIndexOfDueAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpCompletedBy: String?
          if (_stmt.isNull(_columnIndexOfCompletedBy)) {
            _tmpCompletedBy = null
          } else {
            _tmpCompletedBy = _stmt.getText(_columnIndexOfCompletedBy)
          }
          val _tmpPriority: String
          _tmpPriority = _stmt.getText(_columnIndexOfPriority)
          val _tmpRecurrence: String?
          if (_stmt.isNull(_columnIndexOfRecurrence)) {
            _tmpRecurrence = null
          } else {
            _tmpRecurrence = _stmt.getText(_columnIndexOfRecurrence)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpSnoozeUntil: Long?
          if (_stmt.isNull(_columnIndexOfSnoozeUntil)) {
            _tmpSnoozeUntil = null
          } else {
            _tmpSnoozeUntil = _stmt.getLong(_columnIndexOfSnoozeUntil)
          }
          val _tmpMetadata: String?
          if (_stmt.isNull(_columnIndexOfMetadata)) {
            _tmpMetadata = null
          } else {
            _tmpMetadata = _stmt.getText(_columnIndexOfMetadata)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TaskEntity(_tmpTaskId,_tmpFarmerId,_tmpProductId,_tmpBatchId,_tmpTaskType,_tmpTitle,_tmpDescription,_tmpDueAt,_tmpCompletedAt,_tmpCompletedBy,_tmpPriority,_tmpRecurrence,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpSnoozeUntil,_tmpMetadata,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeByBatchId(batchId: String): Flow<List<TaskEntity>> {
    val _sql: String = "SELECT * FROM tasks WHERE batchId = ? ORDER BY dueAt ASC"
    return createFlow(__db, false, arrayOf("tasks")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, batchId)
        val _columnIndexOfTaskId: Int = getColumnIndexOrThrow(_stmt, "taskId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfTaskType: Int = getColumnIndexOrThrow(_stmt, "taskType")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfDueAt: Int = getColumnIndexOrThrow(_stmt, "dueAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfCompletedBy: Int = getColumnIndexOrThrow(_stmt, "completedBy")
        val _columnIndexOfPriority: Int = getColumnIndexOrThrow(_stmt, "priority")
        val _columnIndexOfRecurrence: Int = getColumnIndexOrThrow(_stmt, "recurrence")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfSnoozeUntil: Int = getColumnIndexOrThrow(_stmt, "snoozeUntil")
        val _columnIndexOfMetadata: Int = getColumnIndexOrThrow(_stmt, "metadata")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TaskEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TaskEntity
          val _tmpTaskId: String
          _tmpTaskId = _stmt.getText(_columnIndexOfTaskId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpTaskType: String
          _tmpTaskType = _stmt.getText(_columnIndexOfTaskType)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpDueAt: Long
          _tmpDueAt = _stmt.getLong(_columnIndexOfDueAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpCompletedBy: String?
          if (_stmt.isNull(_columnIndexOfCompletedBy)) {
            _tmpCompletedBy = null
          } else {
            _tmpCompletedBy = _stmt.getText(_columnIndexOfCompletedBy)
          }
          val _tmpPriority: String
          _tmpPriority = _stmt.getText(_columnIndexOfPriority)
          val _tmpRecurrence: String?
          if (_stmt.isNull(_columnIndexOfRecurrence)) {
            _tmpRecurrence = null
          } else {
            _tmpRecurrence = _stmt.getText(_columnIndexOfRecurrence)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpSnoozeUntil: Long?
          if (_stmt.isNull(_columnIndexOfSnoozeUntil)) {
            _tmpSnoozeUntil = null
          } else {
            _tmpSnoozeUntil = _stmt.getLong(_columnIndexOfSnoozeUntil)
          }
          val _tmpMetadata: String?
          if (_stmt.isNull(_columnIndexOfMetadata)) {
            _tmpMetadata = null
          } else {
            _tmpMetadata = _stmt.getText(_columnIndexOfMetadata)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TaskEntity(_tmpTaskId,_tmpFarmerId,_tmpProductId,_tmpBatchId,_tmpTaskType,_tmpTitle,_tmpDescription,_tmpDueAt,_tmpCompletedAt,_tmpCompletedBy,_tmpPriority,_tmpRecurrence,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpSnoozeUntil,_tmpMetadata,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getDueByBatch(
    farmerId: String,
    batchId: String,
    now: Long,
  ): List<TaskEntity> {
    val _sql: String =
        "SELECT * FROM tasks WHERE farmerId = ? AND batchId = ? AND completedAt IS NULL AND (snoozeUntil IS NULL OR snoozeUntil <= ?) AND dueAt <= ? ORDER BY dueAt ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindText(_argIndex, batchId)
        _argIndex = 3
        _stmt.bindLong(_argIndex, now)
        _argIndex = 4
        _stmt.bindLong(_argIndex, now)
        val _columnIndexOfTaskId: Int = getColumnIndexOrThrow(_stmt, "taskId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfTaskType: Int = getColumnIndexOrThrow(_stmt, "taskType")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfDueAt: Int = getColumnIndexOrThrow(_stmt, "dueAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfCompletedBy: Int = getColumnIndexOrThrow(_stmt, "completedBy")
        val _columnIndexOfPriority: Int = getColumnIndexOrThrow(_stmt, "priority")
        val _columnIndexOfRecurrence: Int = getColumnIndexOrThrow(_stmt, "recurrence")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfSnoozeUntil: Int = getColumnIndexOrThrow(_stmt, "snoozeUntil")
        val _columnIndexOfMetadata: Int = getColumnIndexOrThrow(_stmt, "metadata")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TaskEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TaskEntity
          val _tmpTaskId: String
          _tmpTaskId = _stmt.getText(_columnIndexOfTaskId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpTaskType: String
          _tmpTaskType = _stmt.getText(_columnIndexOfTaskType)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpDueAt: Long
          _tmpDueAt = _stmt.getLong(_columnIndexOfDueAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpCompletedBy: String?
          if (_stmt.isNull(_columnIndexOfCompletedBy)) {
            _tmpCompletedBy = null
          } else {
            _tmpCompletedBy = _stmt.getText(_columnIndexOfCompletedBy)
          }
          val _tmpPriority: String
          _tmpPriority = _stmt.getText(_columnIndexOfPriority)
          val _tmpRecurrence: String?
          if (_stmt.isNull(_columnIndexOfRecurrence)) {
            _tmpRecurrence = null
          } else {
            _tmpRecurrence = _stmt.getText(_columnIndexOfRecurrence)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpSnoozeUntil: Long?
          if (_stmt.isNull(_columnIndexOfSnoozeUntil)) {
            _tmpSnoozeUntil = null
          } else {
            _tmpSnoozeUntil = _stmt.getLong(_columnIndexOfSnoozeUntil)
          }
          val _tmpMetadata: String?
          if (_stmt.isNull(_columnIndexOfMetadata)) {
            _tmpMetadata = null
          } else {
            _tmpMetadata = _stmt.getText(_columnIndexOfMetadata)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TaskEntity(_tmpTaskId,_tmpFarmerId,_tmpProductId,_tmpBatchId,_tmpTaskType,_tmpTitle,_tmpDescription,_tmpDueAt,_tmpCompletedAt,_tmpCompletedBy,_tmpPriority,_tmpRecurrence,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpSnoozeUntil,_tmpMetadata,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun findPendingByTypeProduct(
    farmerId: String,
    productId: String,
    taskType: String,
  ): List<TaskEntity> {
    val _sql: String =
        "SELECT * FROM tasks WHERE farmerId = ? AND productId = ? AND taskType = ? AND completedAt IS NULL"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindText(_argIndex, productId)
        _argIndex = 3
        _stmt.bindText(_argIndex, taskType)
        val _columnIndexOfTaskId: Int = getColumnIndexOrThrow(_stmt, "taskId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfTaskType: Int = getColumnIndexOrThrow(_stmt, "taskType")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfDueAt: Int = getColumnIndexOrThrow(_stmt, "dueAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfCompletedBy: Int = getColumnIndexOrThrow(_stmt, "completedBy")
        val _columnIndexOfPriority: Int = getColumnIndexOrThrow(_stmt, "priority")
        val _columnIndexOfRecurrence: Int = getColumnIndexOrThrow(_stmt, "recurrence")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfSnoozeUntil: Int = getColumnIndexOrThrow(_stmt, "snoozeUntil")
        val _columnIndexOfMetadata: Int = getColumnIndexOrThrow(_stmt, "metadata")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TaskEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TaskEntity
          val _tmpTaskId: String
          _tmpTaskId = _stmt.getText(_columnIndexOfTaskId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpTaskType: String
          _tmpTaskType = _stmt.getText(_columnIndexOfTaskType)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpDueAt: Long
          _tmpDueAt = _stmt.getLong(_columnIndexOfDueAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpCompletedBy: String?
          if (_stmt.isNull(_columnIndexOfCompletedBy)) {
            _tmpCompletedBy = null
          } else {
            _tmpCompletedBy = _stmt.getText(_columnIndexOfCompletedBy)
          }
          val _tmpPriority: String
          _tmpPriority = _stmt.getText(_columnIndexOfPriority)
          val _tmpRecurrence: String?
          if (_stmt.isNull(_columnIndexOfRecurrence)) {
            _tmpRecurrence = null
          } else {
            _tmpRecurrence = _stmt.getText(_columnIndexOfRecurrence)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpSnoozeUntil: Long?
          if (_stmt.isNull(_columnIndexOfSnoozeUntil)) {
            _tmpSnoozeUntil = null
          } else {
            _tmpSnoozeUntil = _stmt.getLong(_columnIndexOfSnoozeUntil)
          }
          val _tmpMetadata: String?
          if (_stmt.isNull(_columnIndexOfMetadata)) {
            _tmpMetadata = null
          } else {
            _tmpMetadata = _stmt.getText(_columnIndexOfMetadata)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TaskEntity(_tmpTaskId,_tmpFarmerId,_tmpProductId,_tmpBatchId,_tmpTaskType,_tmpTitle,_tmpDescription,_tmpDueAt,_tmpCompletedAt,_tmpCompletedBy,_tmpPriority,_tmpRecurrence,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpSnoozeUntil,_tmpMetadata,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun findPendingByType(farmerId: String, taskType: String):
      List<TaskEntity> {
    val _sql: String =
        "SELECT * FROM tasks WHERE farmerId = ? AND taskType = ? AND completedAt IS NULL"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindText(_argIndex, taskType)
        val _columnIndexOfTaskId: Int = getColumnIndexOrThrow(_stmt, "taskId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfTaskType: Int = getColumnIndexOrThrow(_stmt, "taskType")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfDueAt: Int = getColumnIndexOrThrow(_stmt, "dueAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfCompletedBy: Int = getColumnIndexOrThrow(_stmt, "completedBy")
        val _columnIndexOfPriority: Int = getColumnIndexOrThrow(_stmt, "priority")
        val _columnIndexOfRecurrence: Int = getColumnIndexOrThrow(_stmt, "recurrence")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfSnoozeUntil: Int = getColumnIndexOrThrow(_stmt, "snoozeUntil")
        val _columnIndexOfMetadata: Int = getColumnIndexOrThrow(_stmt, "metadata")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TaskEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TaskEntity
          val _tmpTaskId: String
          _tmpTaskId = _stmt.getText(_columnIndexOfTaskId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpTaskType: String
          _tmpTaskType = _stmt.getText(_columnIndexOfTaskType)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpDueAt: Long
          _tmpDueAt = _stmt.getLong(_columnIndexOfDueAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpCompletedBy: String?
          if (_stmt.isNull(_columnIndexOfCompletedBy)) {
            _tmpCompletedBy = null
          } else {
            _tmpCompletedBy = _stmt.getText(_columnIndexOfCompletedBy)
          }
          val _tmpPriority: String
          _tmpPriority = _stmt.getText(_columnIndexOfPriority)
          val _tmpRecurrence: String?
          if (_stmt.isNull(_columnIndexOfRecurrence)) {
            _tmpRecurrence = null
          } else {
            _tmpRecurrence = _stmt.getText(_columnIndexOfRecurrence)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpSnoozeUntil: Long?
          if (_stmt.isNull(_columnIndexOfSnoozeUntil)) {
            _tmpSnoozeUntil = null
          } else {
            _tmpSnoozeUntil = _stmt.getLong(_columnIndexOfSnoozeUntil)
          }
          val _tmpMetadata: String?
          if (_stmt.isNull(_columnIndexOfMetadata)) {
            _tmpMetadata = null
          } else {
            _tmpMetadata = _stmt.getText(_columnIndexOfMetadata)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TaskEntity(_tmpTaskId,_tmpFarmerId,_tmpProductId,_tmpBatchId,_tmpTaskType,_tmpTitle,_tmpDescription,_tmpDueAt,_tmpCompletedAt,_tmpCompletedBy,_tmpPriority,_tmpRecurrence,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpSnoozeUntil,_tmpMetadata,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun observeCompletedCountForFarmerBetween(
    farmerId: String,
    start: Long,
    end: Long,
  ): Flow<Int> {
    val _sql: String =
        "SELECT COUNT(*) FROM tasks WHERE farmerId = ? AND completedAt BETWEEN ? AND ?"
    return createFlow(__db, false, arrayOf("tasks")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, start)
        _argIndex = 3
        _stmt.bindLong(_argIndex, end)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countCompletedForFarmerBetween(
    farmerId: String,
    start: Long,
    end: Long,
  ): Int {
    val _sql: String =
        "SELECT COUNT(*) FROM tasks WHERE farmerId = ? AND completedAt BETWEEN ? AND ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, farmerId)
        _argIndex = 2
        _stmt.bindLong(_argIndex, start)
        _argIndex = 3
        _stmt.bindLong(_argIndex, end)
        val _result: Int
        if (_stmt.step()) {
          val _tmp: Int
          _tmp = _stmt.getLong(0).toInt()
          _result = _tmp
        } else {
          _result = 0
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getUpdatedSince(since: Long, limit: Int): List<TaskEntity> {
    val _sql: String = "SELECT * FROM tasks WHERE updatedAt > ? ORDER BY updatedAt ASC LIMIT ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, since)
        _argIndex = 2
        _stmt.bindLong(_argIndex, limit.toLong())
        val _columnIndexOfTaskId: Int = getColumnIndexOrThrow(_stmt, "taskId")
        val _columnIndexOfFarmerId: Int = getColumnIndexOrThrow(_stmt, "farmerId")
        val _columnIndexOfProductId: Int = getColumnIndexOrThrow(_stmt, "productId")
        val _columnIndexOfBatchId: Int = getColumnIndexOrThrow(_stmt, "batchId")
        val _columnIndexOfTaskType: Int = getColumnIndexOrThrow(_stmt, "taskType")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfDueAt: Int = getColumnIndexOrThrow(_stmt, "dueAt")
        val _columnIndexOfCompletedAt: Int = getColumnIndexOrThrow(_stmt, "completedAt")
        val _columnIndexOfCompletedBy: Int = getColumnIndexOrThrow(_stmt, "completedBy")
        val _columnIndexOfPriority: Int = getColumnIndexOrThrow(_stmt, "priority")
        val _columnIndexOfRecurrence: Int = getColumnIndexOrThrow(_stmt, "recurrence")
        val _columnIndexOfNotes: Int = getColumnIndexOrThrow(_stmt, "notes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfDirty: Int = getColumnIndexOrThrow(_stmt, "dirty")
        val _columnIndexOfSyncedAt: Int = getColumnIndexOrThrow(_stmt, "syncedAt")
        val _columnIndexOfSnoozeUntil: Int = getColumnIndexOrThrow(_stmt, "snoozeUntil")
        val _columnIndexOfMetadata: Int = getColumnIndexOrThrow(_stmt, "metadata")
        val _columnIndexOfMergedAt: Int = getColumnIndexOrThrow(_stmt, "mergedAt")
        val _columnIndexOfMergeCount: Int = getColumnIndexOrThrow(_stmt, "mergeCount")
        val _result: MutableList<TaskEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: TaskEntity
          val _tmpTaskId: String
          _tmpTaskId = _stmt.getText(_columnIndexOfTaskId)
          val _tmpFarmerId: String
          _tmpFarmerId = _stmt.getText(_columnIndexOfFarmerId)
          val _tmpProductId: String?
          if (_stmt.isNull(_columnIndexOfProductId)) {
            _tmpProductId = null
          } else {
            _tmpProductId = _stmt.getText(_columnIndexOfProductId)
          }
          val _tmpBatchId: String?
          if (_stmt.isNull(_columnIndexOfBatchId)) {
            _tmpBatchId = null
          } else {
            _tmpBatchId = _stmt.getText(_columnIndexOfBatchId)
          }
          val _tmpTaskType: String
          _tmpTaskType = _stmt.getText(_columnIndexOfTaskType)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String?
          if (_stmt.isNull(_columnIndexOfDescription)) {
            _tmpDescription = null
          } else {
            _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          }
          val _tmpDueAt: Long
          _tmpDueAt = _stmt.getLong(_columnIndexOfDueAt)
          val _tmpCompletedAt: Long?
          if (_stmt.isNull(_columnIndexOfCompletedAt)) {
            _tmpCompletedAt = null
          } else {
            _tmpCompletedAt = _stmt.getLong(_columnIndexOfCompletedAt)
          }
          val _tmpCompletedBy: String?
          if (_stmt.isNull(_columnIndexOfCompletedBy)) {
            _tmpCompletedBy = null
          } else {
            _tmpCompletedBy = _stmt.getText(_columnIndexOfCompletedBy)
          }
          val _tmpPriority: String
          _tmpPriority = _stmt.getText(_columnIndexOfPriority)
          val _tmpRecurrence: String?
          if (_stmt.isNull(_columnIndexOfRecurrence)) {
            _tmpRecurrence = null
          } else {
            _tmpRecurrence = _stmt.getText(_columnIndexOfRecurrence)
          }
          val _tmpNotes: String?
          if (_stmt.isNull(_columnIndexOfNotes)) {
            _tmpNotes = null
          } else {
            _tmpNotes = _stmt.getText(_columnIndexOfNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          val _tmpDirty: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfDirty).toInt()
          _tmpDirty = _tmp != 0
          val _tmpSyncedAt: Long?
          if (_stmt.isNull(_columnIndexOfSyncedAt)) {
            _tmpSyncedAt = null
          } else {
            _tmpSyncedAt = _stmt.getLong(_columnIndexOfSyncedAt)
          }
          val _tmpSnoozeUntil: Long?
          if (_stmt.isNull(_columnIndexOfSnoozeUntil)) {
            _tmpSnoozeUntil = null
          } else {
            _tmpSnoozeUntil = _stmt.getLong(_columnIndexOfSnoozeUntil)
          }
          val _tmpMetadata: String?
          if (_stmt.isNull(_columnIndexOfMetadata)) {
            _tmpMetadata = null
          } else {
            _tmpMetadata = _stmt.getText(_columnIndexOfMetadata)
          }
          val _tmpMergedAt: Long?
          if (_stmt.isNull(_columnIndexOfMergedAt)) {
            _tmpMergedAt = null
          } else {
            _tmpMergedAt = _stmt.getLong(_columnIndexOfMergedAt)
          }
          val _tmpMergeCount: Int
          _tmpMergeCount = _stmt.getLong(_columnIndexOfMergeCount).toInt()
          _item =
              TaskEntity(_tmpTaskId,_tmpFarmerId,_tmpProductId,_tmpBatchId,_tmpTaskType,_tmpTitle,_tmpDescription,_tmpDueAt,_tmpCompletedAt,_tmpCompletedBy,_tmpPriority,_tmpRecurrence,_tmpNotes,_tmpCreatedAt,_tmpUpdatedAt,_tmpDirty,_tmpSyncedAt,_tmpSnoozeUntil,_tmpMetadata,_tmpMergedAt,_tmpMergeCount)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markComplete(
    taskId: String,
    completedAt: Long,
    completedBy: String,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE tasks SET completedAt = ?, completedBy = ?, updatedAt = ?, dirty = 1 WHERE taskId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, completedAt)
        _argIndex = 2
        _stmt.bindText(_argIndex, completedBy)
        _argIndex = 3
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 4
        _stmt.bindText(_argIndex, taskId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun clearDirty(taskIds: List<String>, syncedAt: Long) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("UPDATE tasks SET dirty = 0, syncedAt = ")
    _stringBuilder.append("?")
    _stringBuilder.append(" WHERE taskId IN (")
    val _inputSize: Int = taskIds.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, syncedAt)
        _argIndex = 2
        for (_item: String in taskIds) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun delete(taskId: String) {
    val _sql: String = "DELETE FROM tasks WHERE taskId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, taskId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateMetadata(
    taskId: String,
    metadata: String?,
    updatedAt: Long,
  ) {
    val _sql: String = "UPDATE tasks SET metadata = ?, updatedAt = ?, dirty = 1 WHERE taskId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        if (metadata == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, metadata)
        }
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, taskId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateDueAt(
    taskId: String,
    dueAt: Long,
    updatedAt: Long,
  ) {
    val _sql: String = "UPDATE tasks SET dueAt = ?, updatedAt = ?, dirty = 1 WHERE taskId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, dueAt)
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, taskId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateSnoozeUntil(
    taskId: String,
    snoozeUntil: Long?,
    updatedAt: Long,
  ) {
    val _sql: String = "UPDATE tasks SET snoozeUntil = ?, updatedAt = ?, dirty = 1 WHERE taskId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        if (snoozeUntil == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindLong(_argIndex, snoozeUntil)
        }
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, taskId)
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
