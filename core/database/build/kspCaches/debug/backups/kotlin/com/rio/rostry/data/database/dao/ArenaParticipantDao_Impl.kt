package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.ArenaParticipantEntity
import javax.`annotation`.processing.Generated
import kotlin.Float
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
public class ArenaParticipantDao_Impl(
  __db: RoomDatabase,
) : ArenaParticipantDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfArenaParticipantEntity: EntityInsertAdapter<ArenaParticipantEntity>

  private val __updateAdapterOfArenaParticipantEntity:
      EntityDeleteOrUpdateAdapter<ArenaParticipantEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfArenaParticipantEntity = object :
        EntityInsertAdapter<ArenaParticipantEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `arena_participants` (`id`,`competitionId`,`birdId`,`ownerId`,`birdName`,`birdImageUrl`,`breed`,`entryTime`,`totalVotes`,`averageScore`,`rank`) VALUES (nullif(?, 0),?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: ArenaParticipantEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.competitionId)
        statement.bindText(3, entity.birdId)
        statement.bindText(4, entity.ownerId)
        statement.bindText(5, entity.birdName)
        val _tmpBirdImageUrl: String? = entity.birdImageUrl
        if (_tmpBirdImageUrl == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpBirdImageUrl)
        }
        statement.bindText(7, entity.breed)
        statement.bindLong(8, entity.entryTime)
        statement.bindLong(9, entity.totalVotes.toLong())
        statement.bindDouble(10, entity.averageScore.toDouble())
        statement.bindLong(11, entity.rank.toLong())
      }
    }
    this.__updateAdapterOfArenaParticipantEntity = object :
        EntityDeleteOrUpdateAdapter<ArenaParticipantEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `arena_participants` SET `id` = ?,`competitionId` = ?,`birdId` = ?,`ownerId` = ?,`birdName` = ?,`birdImageUrl` = ?,`breed` = ?,`entryTime` = ?,`totalVotes` = ?,`averageScore` = ?,`rank` = ? WHERE `id` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: ArenaParticipantEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.competitionId)
        statement.bindText(3, entity.birdId)
        statement.bindText(4, entity.ownerId)
        statement.bindText(5, entity.birdName)
        val _tmpBirdImageUrl: String? = entity.birdImageUrl
        if (_tmpBirdImageUrl == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpBirdImageUrl)
        }
        statement.bindText(7, entity.breed)
        statement.bindLong(8, entity.entryTime)
        statement.bindLong(9, entity.totalVotes.toLong())
        statement.bindDouble(10, entity.averageScore.toDouble())
        statement.bindLong(11, entity.rank.toLong())
        statement.bindLong(12, entity.id)
      }
    }
  }

  public override suspend fun insertParticipant(participant: ArenaParticipantEntity): Long =
      performSuspending(__db, false, true) { _connection ->
    val _result: Long = __insertAdapterOfArenaParticipantEntity.insertAndReturnId(_connection,
        participant)
    _result
  }

  public override suspend fun updateParticipant(participant: ArenaParticipantEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfArenaParticipantEntity.handle(_connection, participant)
  }

  public override fun getParticipantsForCompetition(competitionId: String):
      Flow<List<ArenaParticipantEntity>> {
    val _sql: String =
        "SELECT * FROM arena_participants WHERE competitionId = ? ORDER BY totalVotes DESC"
    return createFlow(__db, false, arrayOf("arena_participants")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, competitionId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCompetitionId: Int = getColumnIndexOrThrow(_stmt, "competitionId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfBirdName: Int = getColumnIndexOrThrow(_stmt, "birdName")
        val _columnIndexOfBirdImageUrl: Int = getColumnIndexOrThrow(_stmt, "birdImageUrl")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfEntryTime: Int = getColumnIndexOrThrow(_stmt, "entryTime")
        val _columnIndexOfTotalVotes: Int = getColumnIndexOrThrow(_stmt, "totalVotes")
        val _columnIndexOfAverageScore: Int = getColumnIndexOrThrow(_stmt, "averageScore")
        val _columnIndexOfRank: Int = getColumnIndexOrThrow(_stmt, "rank")
        val _result: MutableList<ArenaParticipantEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: ArenaParticipantEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpCompetitionId: String
          _tmpCompetitionId = _stmt.getText(_columnIndexOfCompetitionId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpBirdName: String
          _tmpBirdName = _stmt.getText(_columnIndexOfBirdName)
          val _tmpBirdImageUrl: String?
          if (_stmt.isNull(_columnIndexOfBirdImageUrl)) {
            _tmpBirdImageUrl = null
          } else {
            _tmpBirdImageUrl = _stmt.getText(_columnIndexOfBirdImageUrl)
          }
          val _tmpBreed: String
          _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          val _tmpEntryTime: Long
          _tmpEntryTime = _stmt.getLong(_columnIndexOfEntryTime)
          val _tmpTotalVotes: Int
          _tmpTotalVotes = _stmt.getLong(_columnIndexOfTotalVotes).toInt()
          val _tmpAverageScore: Float
          _tmpAverageScore = _stmt.getDouble(_columnIndexOfAverageScore).toFloat()
          val _tmpRank: Int
          _tmpRank = _stmt.getLong(_columnIndexOfRank).toInt()
          _item =
              ArenaParticipantEntity(_tmpId,_tmpCompetitionId,_tmpBirdId,_tmpOwnerId,_tmpBirdName,_tmpBirdImageUrl,_tmpBreed,_tmpEntryTime,_tmpTotalVotes,_tmpAverageScore,_tmpRank)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getParticipantByOwner(competitionId: String, ownerId: String):
      ArenaParticipantEntity? {
    val _sql: String = "SELECT * FROM arena_participants WHERE competitionId = ? AND ownerId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, competitionId)
        _argIndex = 2
        _stmt.bindText(_argIndex, ownerId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCompetitionId: Int = getColumnIndexOrThrow(_stmt, "competitionId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfBirdName: Int = getColumnIndexOrThrow(_stmt, "birdName")
        val _columnIndexOfBirdImageUrl: Int = getColumnIndexOrThrow(_stmt, "birdImageUrl")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfEntryTime: Int = getColumnIndexOrThrow(_stmt, "entryTime")
        val _columnIndexOfTotalVotes: Int = getColumnIndexOrThrow(_stmt, "totalVotes")
        val _columnIndexOfAverageScore: Int = getColumnIndexOrThrow(_stmt, "averageScore")
        val _columnIndexOfRank: Int = getColumnIndexOrThrow(_stmt, "rank")
        val _result: ArenaParticipantEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpCompetitionId: String
          _tmpCompetitionId = _stmt.getText(_columnIndexOfCompetitionId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpBirdName: String
          _tmpBirdName = _stmt.getText(_columnIndexOfBirdName)
          val _tmpBirdImageUrl: String?
          if (_stmt.isNull(_columnIndexOfBirdImageUrl)) {
            _tmpBirdImageUrl = null
          } else {
            _tmpBirdImageUrl = _stmt.getText(_columnIndexOfBirdImageUrl)
          }
          val _tmpBreed: String
          _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          val _tmpEntryTime: Long
          _tmpEntryTime = _stmt.getLong(_columnIndexOfEntryTime)
          val _tmpTotalVotes: Int
          _tmpTotalVotes = _stmt.getLong(_columnIndexOfTotalVotes).toInt()
          val _tmpAverageScore: Float
          _tmpAverageScore = _stmt.getDouble(_columnIndexOfAverageScore).toFloat()
          val _tmpRank: Int
          _tmpRank = _stmt.getLong(_columnIndexOfRank).toInt()
          _result =
              ArenaParticipantEntity(_tmpId,_tmpCompetitionId,_tmpBirdId,_tmpOwnerId,_tmpBirdName,_tmpBirdImageUrl,_tmpBreed,_tmpEntryTime,_tmpTotalVotes,_tmpAverageScore,_tmpRank)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getParticipantCount(competitionId: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM arena_participants WHERE competitionId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, competitionId)
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

  public override suspend fun getParticipantById(id: Long): ArenaParticipantEntity? {
    val _sql: String = "SELECT * FROM arena_participants WHERE id = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, id)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCompetitionId: Int = getColumnIndexOrThrow(_stmt, "competitionId")
        val _columnIndexOfBirdId: Int = getColumnIndexOrThrow(_stmt, "birdId")
        val _columnIndexOfOwnerId: Int = getColumnIndexOrThrow(_stmt, "ownerId")
        val _columnIndexOfBirdName: Int = getColumnIndexOrThrow(_stmt, "birdName")
        val _columnIndexOfBirdImageUrl: Int = getColumnIndexOrThrow(_stmt, "birdImageUrl")
        val _columnIndexOfBreed: Int = getColumnIndexOrThrow(_stmt, "breed")
        val _columnIndexOfEntryTime: Int = getColumnIndexOrThrow(_stmt, "entryTime")
        val _columnIndexOfTotalVotes: Int = getColumnIndexOrThrow(_stmt, "totalVotes")
        val _columnIndexOfAverageScore: Int = getColumnIndexOrThrow(_stmt, "averageScore")
        val _columnIndexOfRank: Int = getColumnIndexOrThrow(_stmt, "rank")
        val _result: ArenaParticipantEntity?
        if (_stmt.step()) {
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpCompetitionId: String
          _tmpCompetitionId = _stmt.getText(_columnIndexOfCompetitionId)
          val _tmpBirdId: String
          _tmpBirdId = _stmt.getText(_columnIndexOfBirdId)
          val _tmpOwnerId: String
          _tmpOwnerId = _stmt.getText(_columnIndexOfOwnerId)
          val _tmpBirdName: String
          _tmpBirdName = _stmt.getText(_columnIndexOfBirdName)
          val _tmpBirdImageUrl: String?
          if (_stmt.isNull(_columnIndexOfBirdImageUrl)) {
            _tmpBirdImageUrl = null
          } else {
            _tmpBirdImageUrl = _stmt.getText(_columnIndexOfBirdImageUrl)
          }
          val _tmpBreed: String
          _tmpBreed = _stmt.getText(_columnIndexOfBreed)
          val _tmpEntryTime: Long
          _tmpEntryTime = _stmt.getLong(_columnIndexOfEntryTime)
          val _tmpTotalVotes: Int
          _tmpTotalVotes = _stmt.getLong(_columnIndexOfTotalVotes).toInt()
          val _tmpAverageScore: Float
          _tmpAverageScore = _stmt.getDouble(_columnIndexOfAverageScore).toFloat()
          val _tmpRank: Int
          _tmpRank = _stmt.getLong(_columnIndexOfRank).toInt()
          _result =
              ArenaParticipantEntity(_tmpId,_tmpCompetitionId,_tmpBirdId,_tmpOwnerId,_tmpBirdName,_tmpBirdImageUrl,_tmpBreed,_tmpEntryTime,_tmpTotalVotes,_tmpAverageScore,_tmpRank)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun removeParticipant(competitionId: String, birdId: String) {
    val _sql: String = "DELETE FROM arena_participants WHERE competitionId = ? AND birdId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, competitionId)
        _argIndex = 2
        _stmt.bindText(_argIndex, birdId)
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
