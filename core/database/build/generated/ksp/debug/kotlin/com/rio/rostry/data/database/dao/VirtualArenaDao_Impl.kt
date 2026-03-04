package com.rio.rostry.`data`.database.dao

import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.entity.CompetitionEntryEntity
import com.rio.rostry.`data`.database.entity.MyVotesEntity
import com.rio.rostry.domain.model.CompetitionStatus
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Double
import kotlin.IllegalArgumentException
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
public class VirtualArenaDao_Impl(
  __db: RoomDatabase,
) : VirtualArenaDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfCompetitionEntryEntity: EntityInsertAdapter<CompetitionEntryEntity>

  private val __insertAdapterOfMyVotesEntity: EntityInsertAdapter<MyVotesEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfCompetitionEntryEntity = object :
        EntityInsertAdapter<CompetitionEntryEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `competitions` (`competitionId`,`title`,`description`,`startTime`,`endTime`,`region`,`status`,`bannerUrl`,`entryFee`,`prizePool`,`participantCount`,`participantsPreviewJson`,`rulesJson`,`bracketsJson`,`leaderboardJson`,`galleryUrlsJson`,`createdAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: CompetitionEntryEntity) {
        statement.bindText(1, entity.competitionId)
        statement.bindText(2, entity.title)
        statement.bindText(3, entity.description)
        statement.bindLong(4, entity.startTime)
        statement.bindLong(5, entity.endTime)
        statement.bindText(6, entity.region)
        statement.bindText(7, __CompetitionStatus_enumToString(entity.status))
        val _tmpBannerUrl: String? = entity.bannerUrl
        if (_tmpBannerUrl == null) {
          statement.bindNull(8)
        } else {
          statement.bindText(8, _tmpBannerUrl)
        }
        val _tmpEntryFee: Double? = entity.entryFee
        if (_tmpEntryFee == null) {
          statement.bindNull(9)
        } else {
          statement.bindDouble(9, _tmpEntryFee)
        }
        val _tmpPrizePool: String? = entity.prizePool
        if (_tmpPrizePool == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpPrizePool)
        }
        statement.bindLong(11, entity.participantCount.toLong())
        val _tmpParticipantsPreviewJson: String? = entity.participantsPreviewJson
        if (_tmpParticipantsPreviewJson == null) {
          statement.bindNull(12)
        } else {
          statement.bindText(12, _tmpParticipantsPreviewJson)
        }
        val _tmpRulesJson: String? = entity.rulesJson
        if (_tmpRulesJson == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpRulesJson)
        }
        val _tmpBracketsJson: String? = entity.bracketsJson
        if (_tmpBracketsJson == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmpBracketsJson)
        }
        val _tmpLeaderboardJson: String? = entity.leaderboardJson
        if (_tmpLeaderboardJson == null) {
          statement.bindNull(15)
        } else {
          statement.bindText(15, _tmpLeaderboardJson)
        }
        val _tmpGalleryUrlsJson: String? = entity.galleryUrlsJson
        if (_tmpGalleryUrlsJson == null) {
          statement.bindNull(16)
        } else {
          statement.bindText(16, _tmpGalleryUrlsJson)
        }
        statement.bindLong(17, entity.createdAt)
      }
    }
    this.__insertAdapterOfMyVotesEntity = object : EntityInsertAdapter<MyVotesEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `my_votes` (`id`,`competitionId`,`participantId`,`votedAt`,`points`,`synced`,`syncError`) VALUES (nullif(?, 0),?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: MyVotesEntity) {
        statement.bindLong(1, entity.id)
        statement.bindText(2, entity.competitionId)
        statement.bindText(3, entity.participantId)
        statement.bindLong(4, entity.votedAt)
        statement.bindLong(5, entity.points.toLong())
        val _tmp: Int = if (entity.synced) 1 else 0
        statement.bindLong(6, _tmp.toLong())
        val _tmpSyncError: String? = entity.syncError
        if (_tmpSyncError == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpSyncError)
        }
      }
    }
  }

  public override suspend fun insertCompetition(competition: CompetitionEntryEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfCompetitionEntryEntity.insert(_connection, competition)
  }

  public override suspend fun insertCompetitions(competitions: List<CompetitionEntryEntity>): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfCompetitionEntryEntity.insert(_connection, competitions)
  }

  public override suspend fun castVote(vote: MyVotesEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __insertAdapterOfMyVotesEntity.insert(_connection, vote)
  }

  public override fun getCompetitionsByStatus(status: CompetitionStatus):
      Flow<List<CompetitionEntryEntity>> {
    val _sql: String = "SELECT * FROM competitions WHERE status = ? ORDER BY endTime ASC"
    return createFlow(__db, false, arrayOf("competitions")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, __CompetitionStatus_enumToString(status))
        val _columnIndexOfCompetitionId: Int = getColumnIndexOrThrow(_stmt, "competitionId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfStartTime: Int = getColumnIndexOrThrow(_stmt, "startTime")
        val _columnIndexOfEndTime: Int = getColumnIndexOrThrow(_stmt, "endTime")
        val _columnIndexOfRegion: Int = getColumnIndexOrThrow(_stmt, "region")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfBannerUrl: Int = getColumnIndexOrThrow(_stmt, "bannerUrl")
        val _columnIndexOfEntryFee: Int = getColumnIndexOrThrow(_stmt, "entryFee")
        val _columnIndexOfPrizePool: Int = getColumnIndexOrThrow(_stmt, "prizePool")
        val _columnIndexOfParticipantCount: Int = getColumnIndexOrThrow(_stmt, "participantCount")
        val _columnIndexOfParticipantsPreviewJson: Int = getColumnIndexOrThrow(_stmt,
            "participantsPreviewJson")
        val _columnIndexOfRulesJson: Int = getColumnIndexOrThrow(_stmt, "rulesJson")
        val _columnIndexOfBracketsJson: Int = getColumnIndexOrThrow(_stmt, "bracketsJson")
        val _columnIndexOfLeaderboardJson: Int = getColumnIndexOrThrow(_stmt, "leaderboardJson")
        val _columnIndexOfGalleryUrlsJson: Int = getColumnIndexOrThrow(_stmt, "galleryUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<CompetitionEntryEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: CompetitionEntryEntity
          val _tmpCompetitionId: String
          _tmpCompetitionId = _stmt.getText(_columnIndexOfCompetitionId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpStartTime: Long
          _tmpStartTime = _stmt.getLong(_columnIndexOfStartTime)
          val _tmpEndTime: Long
          _tmpEndTime = _stmt.getLong(_columnIndexOfEndTime)
          val _tmpRegion: String
          _tmpRegion = _stmt.getText(_columnIndexOfRegion)
          val _tmpStatus: CompetitionStatus
          _tmpStatus = __CompetitionStatus_stringToEnum(_stmt.getText(_columnIndexOfStatus))
          val _tmpBannerUrl: String?
          if (_stmt.isNull(_columnIndexOfBannerUrl)) {
            _tmpBannerUrl = null
          } else {
            _tmpBannerUrl = _stmt.getText(_columnIndexOfBannerUrl)
          }
          val _tmpEntryFee: Double?
          if (_stmt.isNull(_columnIndexOfEntryFee)) {
            _tmpEntryFee = null
          } else {
            _tmpEntryFee = _stmt.getDouble(_columnIndexOfEntryFee)
          }
          val _tmpPrizePool: String?
          if (_stmt.isNull(_columnIndexOfPrizePool)) {
            _tmpPrizePool = null
          } else {
            _tmpPrizePool = _stmt.getText(_columnIndexOfPrizePool)
          }
          val _tmpParticipantCount: Int
          _tmpParticipantCount = _stmt.getLong(_columnIndexOfParticipantCount).toInt()
          val _tmpParticipantsPreviewJson: String?
          if (_stmt.isNull(_columnIndexOfParticipantsPreviewJson)) {
            _tmpParticipantsPreviewJson = null
          } else {
            _tmpParticipantsPreviewJson = _stmt.getText(_columnIndexOfParticipantsPreviewJson)
          }
          val _tmpRulesJson: String?
          if (_stmt.isNull(_columnIndexOfRulesJson)) {
            _tmpRulesJson = null
          } else {
            _tmpRulesJson = _stmt.getText(_columnIndexOfRulesJson)
          }
          val _tmpBracketsJson: String?
          if (_stmt.isNull(_columnIndexOfBracketsJson)) {
            _tmpBracketsJson = null
          } else {
            _tmpBracketsJson = _stmt.getText(_columnIndexOfBracketsJson)
          }
          val _tmpLeaderboardJson: String?
          if (_stmt.isNull(_columnIndexOfLeaderboardJson)) {
            _tmpLeaderboardJson = null
          } else {
            _tmpLeaderboardJson = _stmt.getText(_columnIndexOfLeaderboardJson)
          }
          val _tmpGalleryUrlsJson: String?
          if (_stmt.isNull(_columnIndexOfGalleryUrlsJson)) {
            _tmpGalleryUrlsJson = null
          } else {
            _tmpGalleryUrlsJson = _stmt.getText(_columnIndexOfGalleryUrlsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              CompetitionEntryEntity(_tmpCompetitionId,_tmpTitle,_tmpDescription,_tmpStartTime,_tmpEndTime,_tmpRegion,_tmpStatus,_tmpBannerUrl,_tmpEntryFee,_tmpPrizePool,_tmpParticipantCount,_tmpParticipantsPreviewJson,_tmpRulesJson,_tmpBracketsJson,_tmpLeaderboardJson,_tmpGalleryUrlsJson,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getCompetitionsByStatusOneShot(status: CompetitionStatus):
      List<CompetitionEntryEntity> {
    val _sql: String = "SELECT * FROM competitions WHERE status = ? ORDER BY endTime ASC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, __CompetitionStatus_enumToString(status))
        val _columnIndexOfCompetitionId: Int = getColumnIndexOrThrow(_stmt, "competitionId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfStartTime: Int = getColumnIndexOrThrow(_stmt, "startTime")
        val _columnIndexOfEndTime: Int = getColumnIndexOrThrow(_stmt, "endTime")
        val _columnIndexOfRegion: Int = getColumnIndexOrThrow(_stmt, "region")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfBannerUrl: Int = getColumnIndexOrThrow(_stmt, "bannerUrl")
        val _columnIndexOfEntryFee: Int = getColumnIndexOrThrow(_stmt, "entryFee")
        val _columnIndexOfPrizePool: Int = getColumnIndexOrThrow(_stmt, "prizePool")
        val _columnIndexOfParticipantCount: Int = getColumnIndexOrThrow(_stmt, "participantCount")
        val _columnIndexOfParticipantsPreviewJson: Int = getColumnIndexOrThrow(_stmt,
            "participantsPreviewJson")
        val _columnIndexOfRulesJson: Int = getColumnIndexOrThrow(_stmt, "rulesJson")
        val _columnIndexOfBracketsJson: Int = getColumnIndexOrThrow(_stmt, "bracketsJson")
        val _columnIndexOfLeaderboardJson: Int = getColumnIndexOrThrow(_stmt, "leaderboardJson")
        val _columnIndexOfGalleryUrlsJson: Int = getColumnIndexOrThrow(_stmt, "galleryUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: MutableList<CompetitionEntryEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: CompetitionEntryEntity
          val _tmpCompetitionId: String
          _tmpCompetitionId = _stmt.getText(_columnIndexOfCompetitionId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpStartTime: Long
          _tmpStartTime = _stmt.getLong(_columnIndexOfStartTime)
          val _tmpEndTime: Long
          _tmpEndTime = _stmt.getLong(_columnIndexOfEndTime)
          val _tmpRegion: String
          _tmpRegion = _stmt.getText(_columnIndexOfRegion)
          val _tmpStatus: CompetitionStatus
          _tmpStatus = __CompetitionStatus_stringToEnum(_stmt.getText(_columnIndexOfStatus))
          val _tmpBannerUrl: String?
          if (_stmt.isNull(_columnIndexOfBannerUrl)) {
            _tmpBannerUrl = null
          } else {
            _tmpBannerUrl = _stmt.getText(_columnIndexOfBannerUrl)
          }
          val _tmpEntryFee: Double?
          if (_stmt.isNull(_columnIndexOfEntryFee)) {
            _tmpEntryFee = null
          } else {
            _tmpEntryFee = _stmt.getDouble(_columnIndexOfEntryFee)
          }
          val _tmpPrizePool: String?
          if (_stmt.isNull(_columnIndexOfPrizePool)) {
            _tmpPrizePool = null
          } else {
            _tmpPrizePool = _stmt.getText(_columnIndexOfPrizePool)
          }
          val _tmpParticipantCount: Int
          _tmpParticipantCount = _stmt.getLong(_columnIndexOfParticipantCount).toInt()
          val _tmpParticipantsPreviewJson: String?
          if (_stmt.isNull(_columnIndexOfParticipantsPreviewJson)) {
            _tmpParticipantsPreviewJson = null
          } else {
            _tmpParticipantsPreviewJson = _stmt.getText(_columnIndexOfParticipantsPreviewJson)
          }
          val _tmpRulesJson: String?
          if (_stmt.isNull(_columnIndexOfRulesJson)) {
            _tmpRulesJson = null
          } else {
            _tmpRulesJson = _stmt.getText(_columnIndexOfRulesJson)
          }
          val _tmpBracketsJson: String?
          if (_stmt.isNull(_columnIndexOfBracketsJson)) {
            _tmpBracketsJson = null
          } else {
            _tmpBracketsJson = _stmt.getText(_columnIndexOfBracketsJson)
          }
          val _tmpLeaderboardJson: String?
          if (_stmt.isNull(_columnIndexOfLeaderboardJson)) {
            _tmpLeaderboardJson = null
          } else {
            _tmpLeaderboardJson = _stmt.getText(_columnIndexOfLeaderboardJson)
          }
          val _tmpGalleryUrlsJson: String?
          if (_stmt.isNull(_columnIndexOfGalleryUrlsJson)) {
            _tmpGalleryUrlsJson = null
          } else {
            _tmpGalleryUrlsJson = _stmt.getText(_columnIndexOfGalleryUrlsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _item =
              CompetitionEntryEntity(_tmpCompetitionId,_tmpTitle,_tmpDescription,_tmpStartTime,_tmpEndTime,_tmpRegion,_tmpStatus,_tmpBannerUrl,_tmpEntryFee,_tmpPrizePool,_tmpParticipantCount,_tmpParticipantsPreviewJson,_tmpRulesJson,_tmpBracketsJson,_tmpLeaderboardJson,_tmpGalleryUrlsJson,_tmpCreatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getCompetitionById(id: String): CompetitionEntryEntity? {
    val _sql: String = "SELECT * FROM competitions WHERE competitionId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, id)
        val _columnIndexOfCompetitionId: Int = getColumnIndexOrThrow(_stmt, "competitionId")
        val _columnIndexOfTitle: Int = getColumnIndexOrThrow(_stmt, "title")
        val _columnIndexOfDescription: Int = getColumnIndexOrThrow(_stmt, "description")
        val _columnIndexOfStartTime: Int = getColumnIndexOrThrow(_stmt, "startTime")
        val _columnIndexOfEndTime: Int = getColumnIndexOrThrow(_stmt, "endTime")
        val _columnIndexOfRegion: Int = getColumnIndexOrThrow(_stmt, "region")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfBannerUrl: Int = getColumnIndexOrThrow(_stmt, "bannerUrl")
        val _columnIndexOfEntryFee: Int = getColumnIndexOrThrow(_stmt, "entryFee")
        val _columnIndexOfPrizePool: Int = getColumnIndexOrThrow(_stmt, "prizePool")
        val _columnIndexOfParticipantCount: Int = getColumnIndexOrThrow(_stmt, "participantCount")
        val _columnIndexOfParticipantsPreviewJson: Int = getColumnIndexOrThrow(_stmt,
            "participantsPreviewJson")
        val _columnIndexOfRulesJson: Int = getColumnIndexOrThrow(_stmt, "rulesJson")
        val _columnIndexOfBracketsJson: Int = getColumnIndexOrThrow(_stmt, "bracketsJson")
        val _columnIndexOfLeaderboardJson: Int = getColumnIndexOrThrow(_stmt, "leaderboardJson")
        val _columnIndexOfGalleryUrlsJson: Int = getColumnIndexOrThrow(_stmt, "galleryUrlsJson")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _result: CompetitionEntryEntity?
        if (_stmt.step()) {
          val _tmpCompetitionId: String
          _tmpCompetitionId = _stmt.getText(_columnIndexOfCompetitionId)
          val _tmpTitle: String
          _tmpTitle = _stmt.getText(_columnIndexOfTitle)
          val _tmpDescription: String
          _tmpDescription = _stmt.getText(_columnIndexOfDescription)
          val _tmpStartTime: Long
          _tmpStartTime = _stmt.getLong(_columnIndexOfStartTime)
          val _tmpEndTime: Long
          _tmpEndTime = _stmt.getLong(_columnIndexOfEndTime)
          val _tmpRegion: String
          _tmpRegion = _stmt.getText(_columnIndexOfRegion)
          val _tmpStatus: CompetitionStatus
          _tmpStatus = __CompetitionStatus_stringToEnum(_stmt.getText(_columnIndexOfStatus))
          val _tmpBannerUrl: String?
          if (_stmt.isNull(_columnIndexOfBannerUrl)) {
            _tmpBannerUrl = null
          } else {
            _tmpBannerUrl = _stmt.getText(_columnIndexOfBannerUrl)
          }
          val _tmpEntryFee: Double?
          if (_stmt.isNull(_columnIndexOfEntryFee)) {
            _tmpEntryFee = null
          } else {
            _tmpEntryFee = _stmt.getDouble(_columnIndexOfEntryFee)
          }
          val _tmpPrizePool: String?
          if (_stmt.isNull(_columnIndexOfPrizePool)) {
            _tmpPrizePool = null
          } else {
            _tmpPrizePool = _stmt.getText(_columnIndexOfPrizePool)
          }
          val _tmpParticipantCount: Int
          _tmpParticipantCount = _stmt.getLong(_columnIndexOfParticipantCount).toInt()
          val _tmpParticipantsPreviewJson: String?
          if (_stmt.isNull(_columnIndexOfParticipantsPreviewJson)) {
            _tmpParticipantsPreviewJson = null
          } else {
            _tmpParticipantsPreviewJson = _stmt.getText(_columnIndexOfParticipantsPreviewJson)
          }
          val _tmpRulesJson: String?
          if (_stmt.isNull(_columnIndexOfRulesJson)) {
            _tmpRulesJson = null
          } else {
            _tmpRulesJson = _stmt.getText(_columnIndexOfRulesJson)
          }
          val _tmpBracketsJson: String?
          if (_stmt.isNull(_columnIndexOfBracketsJson)) {
            _tmpBracketsJson = null
          } else {
            _tmpBracketsJson = _stmt.getText(_columnIndexOfBracketsJson)
          }
          val _tmpLeaderboardJson: String?
          if (_stmt.isNull(_columnIndexOfLeaderboardJson)) {
            _tmpLeaderboardJson = null
          } else {
            _tmpLeaderboardJson = _stmt.getText(_columnIndexOfLeaderboardJson)
          }
          val _tmpGalleryUrlsJson: String?
          if (_stmt.isNull(_columnIndexOfGalleryUrlsJson)) {
            _tmpGalleryUrlsJson = null
          } else {
            _tmpGalleryUrlsJson = _stmt.getText(_columnIndexOfGalleryUrlsJson)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          _result =
              CompetitionEntryEntity(_tmpCompetitionId,_tmpTitle,_tmpDescription,_tmpStartTime,_tmpEndTime,_tmpRegion,_tmpStatus,_tmpBannerUrl,_tmpEntryFee,_tmpPrizePool,_tmpParticipantCount,_tmpParticipantsPreviewJson,_tmpRulesJson,_tmpBracketsJson,_tmpLeaderboardJson,_tmpGalleryUrlsJson,_tmpCreatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getMyVotesForCompetition(competitionId: String): Flow<List<MyVotesEntity>> {
    val _sql: String = "SELECT * FROM my_votes WHERE competitionId = ?"
    return createFlow(__db, false, arrayOf("my_votes")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, competitionId)
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCompetitionId: Int = getColumnIndexOrThrow(_stmt, "competitionId")
        val _columnIndexOfParticipantId: Int = getColumnIndexOrThrow(_stmt, "participantId")
        val _columnIndexOfVotedAt: Int = getColumnIndexOrThrow(_stmt, "votedAt")
        val _columnIndexOfPoints: Int = getColumnIndexOrThrow(_stmt, "points")
        val _columnIndexOfSynced: Int = getColumnIndexOrThrow(_stmt, "synced")
        val _columnIndexOfSyncError: Int = getColumnIndexOrThrow(_stmt, "syncError")
        val _result: MutableList<MyVotesEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MyVotesEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpCompetitionId: String
          _tmpCompetitionId = _stmt.getText(_columnIndexOfCompetitionId)
          val _tmpParticipantId: String
          _tmpParticipantId = _stmt.getText(_columnIndexOfParticipantId)
          val _tmpVotedAt: Long
          _tmpVotedAt = _stmt.getLong(_columnIndexOfVotedAt)
          val _tmpPoints: Int
          _tmpPoints = _stmt.getLong(_columnIndexOfPoints).toInt()
          val _tmpSynced: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfSynced).toInt()
          _tmpSynced = _tmp != 0
          val _tmpSyncError: String?
          if (_stmt.isNull(_columnIndexOfSyncError)) {
            _tmpSyncError = null
          } else {
            _tmpSyncError = _stmt.getText(_columnIndexOfSyncError)
          }
          _item =
              MyVotesEntity(_tmpId,_tmpCompetitionId,_tmpParticipantId,_tmpVotedAt,_tmpPoints,_tmpSynced,_tmpSyncError)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getUnsyncedVotes(): List<MyVotesEntity> {
    val _sql: String = "SELECT * FROM my_votes WHERE synced = 0"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfId: Int = getColumnIndexOrThrow(_stmt, "id")
        val _columnIndexOfCompetitionId: Int = getColumnIndexOrThrow(_stmt, "competitionId")
        val _columnIndexOfParticipantId: Int = getColumnIndexOrThrow(_stmt, "participantId")
        val _columnIndexOfVotedAt: Int = getColumnIndexOrThrow(_stmt, "votedAt")
        val _columnIndexOfPoints: Int = getColumnIndexOrThrow(_stmt, "points")
        val _columnIndexOfSynced: Int = getColumnIndexOrThrow(_stmt, "synced")
        val _columnIndexOfSyncError: Int = getColumnIndexOrThrow(_stmt, "syncError")
        val _result: MutableList<MyVotesEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: MyVotesEntity
          val _tmpId: Long
          _tmpId = _stmt.getLong(_columnIndexOfId)
          val _tmpCompetitionId: String
          _tmpCompetitionId = _stmt.getText(_columnIndexOfCompetitionId)
          val _tmpParticipantId: String
          _tmpParticipantId = _stmt.getText(_columnIndexOfParticipantId)
          val _tmpVotedAt: Long
          _tmpVotedAt = _stmt.getLong(_columnIndexOfVotedAt)
          val _tmpPoints: Int
          _tmpPoints = _stmt.getLong(_columnIndexOfPoints).toInt()
          val _tmpSynced: Boolean
          val _tmp: Int
          _tmp = _stmt.getLong(_columnIndexOfSynced).toInt()
          _tmpSynced = _tmp != 0
          val _tmpSyncError: String?
          if (_stmt.isNull(_columnIndexOfSyncError)) {
            _tmpSyncError = null
          } else {
            _tmpSyncError = _stmt.getText(_columnIndexOfSyncError)
          }
          _item =
              MyVotesEntity(_tmpId,_tmpCompetitionId,_tmpParticipantId,_tmpVotedAt,_tmpPoints,_tmpSynced,_tmpSyncError)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getMyTotalPointsForParticipant(competitionId: String, participantId: String):
      Flow<Int?> {
    val _sql: String =
        "SELECT SUM(points) FROM my_votes WHERE competitionId = ? AND participantId = ?"
    return createFlow(__db, false, arrayOf("my_votes")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, competitionId)
        _argIndex = 2
        _stmt.bindText(_argIndex, participantId)
        val _result: Int?
        if (_stmt.step()) {
          val _tmp: Int?
          if (_stmt.isNull(0)) {
            _tmp = null
          } else {
            _tmp = _stmt.getLong(0).toInt()
          }
          _result = _tmp
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun markVotesAsSynced(ids: List<Long>) {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("UPDATE my_votes SET synced = 1, syncError = null WHERE id IN (")
    val _inputSize: Int = ids.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        for (_item: Long in ids) {
          _stmt.bindLong(_argIndex, _item)
          _argIndex++
        }
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  private fun __CompetitionStatus_enumToString(_value: CompetitionStatus): String = when (_value) {
    CompetitionStatus.UPCOMING -> "UPCOMING"
    CompetitionStatus.LIVE -> "LIVE"
    CompetitionStatus.COMPLETED -> "COMPLETED"
    CompetitionStatus.CANCELLED -> "CANCELLED"
  }

  private fun __CompetitionStatus_stringToEnum(_value: String): CompetitionStatus = when (_value) {
    "UPCOMING" -> CompetitionStatus.UPCOMING
    "LIVE" -> CompetitionStatus.LIVE
    "COMPLETED" -> CompetitionStatus.COMPLETED
    "CANCELLED" -> CompetitionStatus.CANCELLED
    else -> throw IllegalArgumentException("Can't convert value to enum, unknown value: " + _value)
  }

  public companion object {
    public fun getRequiredConverters(): List<KClass<*>> = emptyList()
  }
}
