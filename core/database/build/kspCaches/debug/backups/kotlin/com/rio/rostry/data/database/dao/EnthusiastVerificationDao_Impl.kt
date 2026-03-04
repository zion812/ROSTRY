package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.AppDatabase
import com.rio.rostry.`data`.database.entity.EnthusiastVerificationEntity
import com.rio.rostry.domain.model.VerificationStatus
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
public class EnthusiastVerificationDao_Impl(
  __db: RoomDatabase,
) : EnthusiastVerificationDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfEnthusiastVerificationEntity:
      EntityInsertAdapter<EnthusiastVerificationEntity>

  private val __updateAdapterOfEnthusiastVerificationEntity:
      EntityDeleteOrUpdateAdapter<EnthusiastVerificationEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfEnthusiastVerificationEntity = object :
        EntityInsertAdapter<EnthusiastVerificationEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `enthusiast_verifications` (`verificationId`,`userId`,`experienceYears`,`birdCount`,`specializations`,`achievementsDescription`,`referenceContacts`,`verificationDocumentUrls`,`profilePhotoUrl`,`farmPhotoUrls`,`status`,`submittedAt`,`reviewedAt`,`reviewedBy`,`rejectionReason`,`adminNotes`,`createdAt`,`updatedAt`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement,
          entity: EnthusiastVerificationEntity) {
        statement.bindText(1, entity.verificationId)
        statement.bindText(2, entity.userId)
        val _tmpExperienceYears: Int? = entity.experienceYears
        if (_tmpExperienceYears == null) {
          statement.bindNull(3)
        } else {
          statement.bindLong(3, _tmpExperienceYears.toLong())
        }
        val _tmpBirdCount: Int? = entity.birdCount
        if (_tmpBirdCount == null) {
          statement.bindNull(4)
        } else {
          statement.bindLong(4, _tmpBirdCount.toLong())
        }
        val _tmpSpecializations: String? = entity.specializations
        if (_tmpSpecializations == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpSpecializations)
        }
        val _tmpAchievementsDescription: String? = entity.achievementsDescription
        if (_tmpAchievementsDescription == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpAchievementsDescription)
        }
        val _tmpReferenceContacts: String? = entity.referenceContacts
        if (_tmpReferenceContacts == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpReferenceContacts)
        }
        statement.bindText(8, entity.verificationDocumentUrls)
        val _tmpProfilePhotoUrl: String? = entity.profilePhotoUrl
        if (_tmpProfilePhotoUrl == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpProfilePhotoUrl)
        }
        val _tmpFarmPhotoUrls: String? = entity.farmPhotoUrls
        if (_tmpFarmPhotoUrls == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpFarmPhotoUrls)
        }
        val _tmp: String? = AppDatabase.Converters.fromVerificationStatus(entity.status)
        if (_tmp == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmp)
        }
        val _tmpSubmittedAt: Long? = entity.submittedAt
        if (_tmpSubmittedAt == null) {
          statement.bindNull(12)
        } else {
          statement.bindLong(12, _tmpSubmittedAt)
        }
        val _tmpReviewedAt: Long? = entity.reviewedAt
        if (_tmpReviewedAt == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpReviewedAt)
        }
        val _tmpReviewedBy: String? = entity.reviewedBy
        if (_tmpReviewedBy == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmpReviewedBy)
        }
        val _tmpRejectionReason: String? = entity.rejectionReason
        if (_tmpRejectionReason == null) {
          statement.bindNull(15)
        } else {
          statement.bindText(15, _tmpRejectionReason)
        }
        val _tmpAdminNotes: String? = entity.adminNotes
        if (_tmpAdminNotes == null) {
          statement.bindNull(16)
        } else {
          statement.bindText(16, _tmpAdminNotes)
        }
        statement.bindLong(17, entity.createdAt)
        statement.bindLong(18, entity.updatedAt)
      }
    }
    this.__updateAdapterOfEnthusiastVerificationEntity = object :
        EntityDeleteOrUpdateAdapter<EnthusiastVerificationEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `enthusiast_verifications` SET `verificationId` = ?,`userId` = ?,`experienceYears` = ?,`birdCount` = ?,`specializations` = ?,`achievementsDescription` = ?,`referenceContacts` = ?,`verificationDocumentUrls` = ?,`profilePhotoUrl` = ?,`farmPhotoUrls` = ?,`status` = ?,`submittedAt` = ?,`reviewedAt` = ?,`reviewedBy` = ?,`rejectionReason` = ?,`adminNotes` = ?,`createdAt` = ?,`updatedAt` = ? WHERE `verificationId` = ?"

      protected override fun bind(statement: SQLiteStatement,
          entity: EnthusiastVerificationEntity) {
        statement.bindText(1, entity.verificationId)
        statement.bindText(2, entity.userId)
        val _tmpExperienceYears: Int? = entity.experienceYears
        if (_tmpExperienceYears == null) {
          statement.bindNull(3)
        } else {
          statement.bindLong(3, _tmpExperienceYears.toLong())
        }
        val _tmpBirdCount: Int? = entity.birdCount
        if (_tmpBirdCount == null) {
          statement.bindNull(4)
        } else {
          statement.bindLong(4, _tmpBirdCount.toLong())
        }
        val _tmpSpecializations: String? = entity.specializations
        if (_tmpSpecializations == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpSpecializations)
        }
        val _tmpAchievementsDescription: String? = entity.achievementsDescription
        if (_tmpAchievementsDescription == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpAchievementsDescription)
        }
        val _tmpReferenceContacts: String? = entity.referenceContacts
        if (_tmpReferenceContacts == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpReferenceContacts)
        }
        statement.bindText(8, entity.verificationDocumentUrls)
        val _tmpProfilePhotoUrl: String? = entity.profilePhotoUrl
        if (_tmpProfilePhotoUrl == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmpProfilePhotoUrl)
        }
        val _tmpFarmPhotoUrls: String? = entity.farmPhotoUrls
        if (_tmpFarmPhotoUrls == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpFarmPhotoUrls)
        }
        val _tmp: String? = AppDatabase.Converters.fromVerificationStatus(entity.status)
        if (_tmp == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmp)
        }
        val _tmpSubmittedAt: Long? = entity.submittedAt
        if (_tmpSubmittedAt == null) {
          statement.bindNull(12)
        } else {
          statement.bindLong(12, _tmpSubmittedAt)
        }
        val _tmpReviewedAt: Long? = entity.reviewedAt
        if (_tmpReviewedAt == null) {
          statement.bindNull(13)
        } else {
          statement.bindLong(13, _tmpReviewedAt)
        }
        val _tmpReviewedBy: String? = entity.reviewedBy
        if (_tmpReviewedBy == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmpReviewedBy)
        }
        val _tmpRejectionReason: String? = entity.rejectionReason
        if (_tmpRejectionReason == null) {
          statement.bindNull(15)
        } else {
          statement.bindText(15, _tmpRejectionReason)
        }
        val _tmpAdminNotes: String? = entity.adminNotes
        if (_tmpAdminNotes == null) {
          statement.bindNull(16)
        } else {
          statement.bindText(16, _tmpAdminNotes)
        }
        statement.bindLong(17, entity.createdAt)
        statement.bindLong(18, entity.updatedAt)
        statement.bindText(19, entity.verificationId)
      }
    }
  }

  public override suspend fun insertVerification(verification: EnthusiastVerificationEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __insertAdapterOfEnthusiastVerificationEntity.insert(_connection, verification)
  }

  public override suspend fun updateVerification(verification: EnthusiastVerificationEntity): Unit =
      performSuspending(__db, false, true) { _connection ->
    __updateAdapterOfEnthusiastVerificationEntity.handle(_connection, verification)
  }

  public override fun getLatestVerificationForUser(userId: String):
      Flow<EnthusiastVerificationEntity?> {
    val _sql: String =
        "SELECT * FROM enthusiast_verifications WHERE userId = ? ORDER BY createdAt DESC LIMIT 1"
    return createFlow(__db, false, arrayOf("enthusiast_verifications")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfVerificationId: Int = getColumnIndexOrThrow(_stmt, "verificationId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfExperienceYears: Int = getColumnIndexOrThrow(_stmt, "experienceYears")
        val _columnIndexOfBirdCount: Int = getColumnIndexOrThrow(_stmt, "birdCount")
        val _columnIndexOfSpecializations: Int = getColumnIndexOrThrow(_stmt, "specializations")
        val _columnIndexOfAchievementsDescription: Int = getColumnIndexOrThrow(_stmt,
            "achievementsDescription")
        val _columnIndexOfReferenceContacts: Int = getColumnIndexOrThrow(_stmt, "referenceContacts")
        val _columnIndexOfVerificationDocumentUrls: Int = getColumnIndexOrThrow(_stmt,
            "verificationDocumentUrls")
        val _columnIndexOfProfilePhotoUrl: Int = getColumnIndexOrThrow(_stmt, "profilePhotoUrl")
        val _columnIndexOfFarmPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "farmPhotoUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfSubmittedAt: Int = getColumnIndexOrThrow(_stmt, "submittedAt")
        val _columnIndexOfReviewedAt: Int = getColumnIndexOrThrow(_stmt, "reviewedAt")
        val _columnIndexOfReviewedBy: Int = getColumnIndexOrThrow(_stmt, "reviewedBy")
        val _columnIndexOfRejectionReason: Int = getColumnIndexOrThrow(_stmt, "rejectionReason")
        val _columnIndexOfAdminNotes: Int = getColumnIndexOrThrow(_stmt, "adminNotes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: EnthusiastVerificationEntity?
        if (_stmt.step()) {
          val _tmpVerificationId: String
          _tmpVerificationId = _stmt.getText(_columnIndexOfVerificationId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpExperienceYears: Int?
          if (_stmt.isNull(_columnIndexOfExperienceYears)) {
            _tmpExperienceYears = null
          } else {
            _tmpExperienceYears = _stmt.getLong(_columnIndexOfExperienceYears).toInt()
          }
          val _tmpBirdCount: Int?
          if (_stmt.isNull(_columnIndexOfBirdCount)) {
            _tmpBirdCount = null
          } else {
            _tmpBirdCount = _stmt.getLong(_columnIndexOfBirdCount).toInt()
          }
          val _tmpSpecializations: String?
          if (_stmt.isNull(_columnIndexOfSpecializations)) {
            _tmpSpecializations = null
          } else {
            _tmpSpecializations = _stmt.getText(_columnIndexOfSpecializations)
          }
          val _tmpAchievementsDescription: String?
          if (_stmt.isNull(_columnIndexOfAchievementsDescription)) {
            _tmpAchievementsDescription = null
          } else {
            _tmpAchievementsDescription = _stmt.getText(_columnIndexOfAchievementsDescription)
          }
          val _tmpReferenceContacts: String?
          if (_stmt.isNull(_columnIndexOfReferenceContacts)) {
            _tmpReferenceContacts = null
          } else {
            _tmpReferenceContacts = _stmt.getText(_columnIndexOfReferenceContacts)
          }
          val _tmpVerificationDocumentUrls: String
          _tmpVerificationDocumentUrls = _stmt.getText(_columnIndexOfVerificationDocumentUrls)
          val _tmpProfilePhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfProfilePhotoUrl)) {
            _tmpProfilePhotoUrl = null
          } else {
            _tmpProfilePhotoUrl = _stmt.getText(_columnIndexOfProfilePhotoUrl)
          }
          val _tmpFarmPhotoUrls: String?
          if (_stmt.isNull(_columnIndexOfFarmPhotoUrls)) {
            _tmpFarmPhotoUrls = null
          } else {
            _tmpFarmPhotoUrls = _stmt.getText(_columnIndexOfFarmPhotoUrls)
          }
          val _tmpStatus: VerificationStatus
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfStatus)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfStatus)
          }
          val _tmp_1: VerificationStatus? = AppDatabase.Converters.toVerificationStatus(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'com.rio.rostry.domain.model.VerificationStatus', but it was NULL.")
          } else {
            _tmpStatus = _tmp_1
          }
          val _tmpSubmittedAt: Long?
          if (_stmt.isNull(_columnIndexOfSubmittedAt)) {
            _tmpSubmittedAt = null
          } else {
            _tmpSubmittedAt = _stmt.getLong(_columnIndexOfSubmittedAt)
          }
          val _tmpReviewedAt: Long?
          if (_stmt.isNull(_columnIndexOfReviewedAt)) {
            _tmpReviewedAt = null
          } else {
            _tmpReviewedAt = _stmt.getLong(_columnIndexOfReviewedAt)
          }
          val _tmpReviewedBy: String?
          if (_stmt.isNull(_columnIndexOfReviewedBy)) {
            _tmpReviewedBy = null
          } else {
            _tmpReviewedBy = _stmt.getText(_columnIndexOfReviewedBy)
          }
          val _tmpRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfRejectionReason)) {
            _tmpRejectionReason = null
          } else {
            _tmpRejectionReason = _stmt.getText(_columnIndexOfRejectionReason)
          }
          val _tmpAdminNotes: String?
          if (_stmt.isNull(_columnIndexOfAdminNotes)) {
            _tmpAdminNotes = null
          } else {
            _tmpAdminNotes = _stmt.getText(_columnIndexOfAdminNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              EnthusiastVerificationEntity(_tmpVerificationId,_tmpUserId,_tmpExperienceYears,_tmpBirdCount,_tmpSpecializations,_tmpAchievementsDescription,_tmpReferenceContacts,_tmpVerificationDocumentUrls,_tmpProfilePhotoUrl,_tmpFarmPhotoUrls,_tmpStatus,_tmpSubmittedAt,_tmpReviewedAt,_tmpReviewedBy,_tmpRejectionReason,_tmpAdminNotes,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getVerificationById(verificationId: String):
      EnthusiastVerificationEntity? {
    val _sql: String = "SELECT * FROM enthusiast_verifications WHERE verificationId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, verificationId)
        val _columnIndexOfVerificationId: Int = getColumnIndexOrThrow(_stmt, "verificationId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfExperienceYears: Int = getColumnIndexOrThrow(_stmt, "experienceYears")
        val _columnIndexOfBirdCount: Int = getColumnIndexOrThrow(_stmt, "birdCount")
        val _columnIndexOfSpecializations: Int = getColumnIndexOrThrow(_stmt, "specializations")
        val _columnIndexOfAchievementsDescription: Int = getColumnIndexOrThrow(_stmt,
            "achievementsDescription")
        val _columnIndexOfReferenceContacts: Int = getColumnIndexOrThrow(_stmt, "referenceContacts")
        val _columnIndexOfVerificationDocumentUrls: Int = getColumnIndexOrThrow(_stmt,
            "verificationDocumentUrls")
        val _columnIndexOfProfilePhotoUrl: Int = getColumnIndexOrThrow(_stmt, "profilePhotoUrl")
        val _columnIndexOfFarmPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "farmPhotoUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfSubmittedAt: Int = getColumnIndexOrThrow(_stmt, "submittedAt")
        val _columnIndexOfReviewedAt: Int = getColumnIndexOrThrow(_stmt, "reviewedAt")
        val _columnIndexOfReviewedBy: Int = getColumnIndexOrThrow(_stmt, "reviewedBy")
        val _columnIndexOfRejectionReason: Int = getColumnIndexOrThrow(_stmt, "rejectionReason")
        val _columnIndexOfAdminNotes: Int = getColumnIndexOrThrow(_stmt, "adminNotes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: EnthusiastVerificationEntity?
        if (_stmt.step()) {
          val _tmpVerificationId: String
          _tmpVerificationId = _stmt.getText(_columnIndexOfVerificationId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpExperienceYears: Int?
          if (_stmt.isNull(_columnIndexOfExperienceYears)) {
            _tmpExperienceYears = null
          } else {
            _tmpExperienceYears = _stmt.getLong(_columnIndexOfExperienceYears).toInt()
          }
          val _tmpBirdCount: Int?
          if (_stmt.isNull(_columnIndexOfBirdCount)) {
            _tmpBirdCount = null
          } else {
            _tmpBirdCount = _stmt.getLong(_columnIndexOfBirdCount).toInt()
          }
          val _tmpSpecializations: String?
          if (_stmt.isNull(_columnIndexOfSpecializations)) {
            _tmpSpecializations = null
          } else {
            _tmpSpecializations = _stmt.getText(_columnIndexOfSpecializations)
          }
          val _tmpAchievementsDescription: String?
          if (_stmt.isNull(_columnIndexOfAchievementsDescription)) {
            _tmpAchievementsDescription = null
          } else {
            _tmpAchievementsDescription = _stmt.getText(_columnIndexOfAchievementsDescription)
          }
          val _tmpReferenceContacts: String?
          if (_stmt.isNull(_columnIndexOfReferenceContacts)) {
            _tmpReferenceContacts = null
          } else {
            _tmpReferenceContacts = _stmt.getText(_columnIndexOfReferenceContacts)
          }
          val _tmpVerificationDocumentUrls: String
          _tmpVerificationDocumentUrls = _stmt.getText(_columnIndexOfVerificationDocumentUrls)
          val _tmpProfilePhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfProfilePhotoUrl)) {
            _tmpProfilePhotoUrl = null
          } else {
            _tmpProfilePhotoUrl = _stmt.getText(_columnIndexOfProfilePhotoUrl)
          }
          val _tmpFarmPhotoUrls: String?
          if (_stmt.isNull(_columnIndexOfFarmPhotoUrls)) {
            _tmpFarmPhotoUrls = null
          } else {
            _tmpFarmPhotoUrls = _stmt.getText(_columnIndexOfFarmPhotoUrls)
          }
          val _tmpStatus: VerificationStatus
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfStatus)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfStatus)
          }
          val _tmp_1: VerificationStatus? = AppDatabase.Converters.toVerificationStatus(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'com.rio.rostry.domain.model.VerificationStatus', but it was NULL.")
          } else {
            _tmpStatus = _tmp_1
          }
          val _tmpSubmittedAt: Long?
          if (_stmt.isNull(_columnIndexOfSubmittedAt)) {
            _tmpSubmittedAt = null
          } else {
            _tmpSubmittedAt = _stmt.getLong(_columnIndexOfSubmittedAt)
          }
          val _tmpReviewedAt: Long?
          if (_stmt.isNull(_columnIndexOfReviewedAt)) {
            _tmpReviewedAt = null
          } else {
            _tmpReviewedAt = _stmt.getLong(_columnIndexOfReviewedAt)
          }
          val _tmpReviewedBy: String?
          if (_stmt.isNull(_columnIndexOfReviewedBy)) {
            _tmpReviewedBy = null
          } else {
            _tmpReviewedBy = _stmt.getText(_columnIndexOfReviewedBy)
          }
          val _tmpRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfRejectionReason)) {
            _tmpRejectionReason = null
          } else {
            _tmpRejectionReason = _stmt.getText(_columnIndexOfRejectionReason)
          }
          val _tmpAdminNotes: String?
          if (_stmt.isNull(_columnIndexOfAdminNotes)) {
            _tmpAdminNotes = null
          } else {
            _tmpAdminNotes = _stmt.getText(_columnIndexOfAdminNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _result =
              EnthusiastVerificationEntity(_tmpVerificationId,_tmpUserId,_tmpExperienceYears,_tmpBirdCount,_tmpSpecializations,_tmpAchievementsDescription,_tmpReferenceContacts,_tmpVerificationDocumentUrls,_tmpProfilePhotoUrl,_tmpFarmPhotoUrls,_tmpStatus,_tmpSubmittedAt,_tmpReviewedAt,_tmpReviewedBy,_tmpRejectionReason,_tmpAdminNotes,_tmpCreatedAt,_tmpUpdatedAt)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllVerificationsForUser(userId: String):
      List<EnthusiastVerificationEntity> {
    val _sql: String = "SELECT * FROM enthusiast_verifications WHERE userId = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfVerificationId: Int = getColumnIndexOrThrow(_stmt, "verificationId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfExperienceYears: Int = getColumnIndexOrThrow(_stmt, "experienceYears")
        val _columnIndexOfBirdCount: Int = getColumnIndexOrThrow(_stmt, "birdCount")
        val _columnIndexOfSpecializations: Int = getColumnIndexOrThrow(_stmt, "specializations")
        val _columnIndexOfAchievementsDescription: Int = getColumnIndexOrThrow(_stmt,
            "achievementsDescription")
        val _columnIndexOfReferenceContacts: Int = getColumnIndexOrThrow(_stmt, "referenceContacts")
        val _columnIndexOfVerificationDocumentUrls: Int = getColumnIndexOrThrow(_stmt,
            "verificationDocumentUrls")
        val _columnIndexOfProfilePhotoUrl: Int = getColumnIndexOrThrow(_stmt, "profilePhotoUrl")
        val _columnIndexOfFarmPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "farmPhotoUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfSubmittedAt: Int = getColumnIndexOrThrow(_stmt, "submittedAt")
        val _columnIndexOfReviewedAt: Int = getColumnIndexOrThrow(_stmt, "reviewedAt")
        val _columnIndexOfReviewedBy: Int = getColumnIndexOrThrow(_stmt, "reviewedBy")
        val _columnIndexOfRejectionReason: Int = getColumnIndexOrThrow(_stmt, "rejectionReason")
        val _columnIndexOfAdminNotes: Int = getColumnIndexOrThrow(_stmt, "adminNotes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<EnthusiastVerificationEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: EnthusiastVerificationEntity
          val _tmpVerificationId: String
          _tmpVerificationId = _stmt.getText(_columnIndexOfVerificationId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpExperienceYears: Int?
          if (_stmt.isNull(_columnIndexOfExperienceYears)) {
            _tmpExperienceYears = null
          } else {
            _tmpExperienceYears = _stmt.getLong(_columnIndexOfExperienceYears).toInt()
          }
          val _tmpBirdCount: Int?
          if (_stmt.isNull(_columnIndexOfBirdCount)) {
            _tmpBirdCount = null
          } else {
            _tmpBirdCount = _stmt.getLong(_columnIndexOfBirdCount).toInt()
          }
          val _tmpSpecializations: String?
          if (_stmt.isNull(_columnIndexOfSpecializations)) {
            _tmpSpecializations = null
          } else {
            _tmpSpecializations = _stmt.getText(_columnIndexOfSpecializations)
          }
          val _tmpAchievementsDescription: String?
          if (_stmt.isNull(_columnIndexOfAchievementsDescription)) {
            _tmpAchievementsDescription = null
          } else {
            _tmpAchievementsDescription = _stmt.getText(_columnIndexOfAchievementsDescription)
          }
          val _tmpReferenceContacts: String?
          if (_stmt.isNull(_columnIndexOfReferenceContacts)) {
            _tmpReferenceContacts = null
          } else {
            _tmpReferenceContacts = _stmt.getText(_columnIndexOfReferenceContacts)
          }
          val _tmpVerificationDocumentUrls: String
          _tmpVerificationDocumentUrls = _stmt.getText(_columnIndexOfVerificationDocumentUrls)
          val _tmpProfilePhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfProfilePhotoUrl)) {
            _tmpProfilePhotoUrl = null
          } else {
            _tmpProfilePhotoUrl = _stmt.getText(_columnIndexOfProfilePhotoUrl)
          }
          val _tmpFarmPhotoUrls: String?
          if (_stmt.isNull(_columnIndexOfFarmPhotoUrls)) {
            _tmpFarmPhotoUrls = null
          } else {
            _tmpFarmPhotoUrls = _stmt.getText(_columnIndexOfFarmPhotoUrls)
          }
          val _tmpStatus: VerificationStatus
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfStatus)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfStatus)
          }
          val _tmp_1: VerificationStatus? = AppDatabase.Converters.toVerificationStatus(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'com.rio.rostry.domain.model.VerificationStatus', but it was NULL.")
          } else {
            _tmpStatus = _tmp_1
          }
          val _tmpSubmittedAt: Long?
          if (_stmt.isNull(_columnIndexOfSubmittedAt)) {
            _tmpSubmittedAt = null
          } else {
            _tmpSubmittedAt = _stmt.getLong(_columnIndexOfSubmittedAt)
          }
          val _tmpReviewedAt: Long?
          if (_stmt.isNull(_columnIndexOfReviewedAt)) {
            _tmpReviewedAt = null
          } else {
            _tmpReviewedAt = _stmt.getLong(_columnIndexOfReviewedAt)
          }
          val _tmpReviewedBy: String?
          if (_stmt.isNull(_columnIndexOfReviewedBy)) {
            _tmpReviewedBy = null
          } else {
            _tmpReviewedBy = _stmt.getText(_columnIndexOfReviewedBy)
          }
          val _tmpRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfRejectionReason)) {
            _tmpRejectionReason = null
          } else {
            _tmpRejectionReason = _stmt.getText(_columnIndexOfRejectionReason)
          }
          val _tmpAdminNotes: String?
          if (_stmt.isNull(_columnIndexOfAdminNotes)) {
            _tmpAdminNotes = null
          } else {
            _tmpAdminNotes = _stmt.getText(_columnIndexOfAdminNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              EnthusiastVerificationEntity(_tmpVerificationId,_tmpUserId,_tmpExperienceYears,_tmpBirdCount,_tmpSpecializations,_tmpAchievementsDescription,_tmpReferenceContacts,_tmpVerificationDocumentUrls,_tmpProfilePhotoUrl,_tmpFarmPhotoUrls,_tmpStatus,_tmpSubmittedAt,_tmpReviewedAt,_tmpReviewedBy,_tmpRejectionReason,_tmpAdminNotes,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getVerificationsByStatus(status: VerificationStatus):
      Flow<List<EnthusiastVerificationEntity>> {
    val _sql: String =
        "SELECT * FROM enthusiast_verifications WHERE status = ? ORDER BY submittedAt ASC"
    return createFlow(__db, false, arrayOf("enthusiast_verifications")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: String? = AppDatabase.Converters.fromVerificationStatus(status)
        if (_tmp == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, _tmp)
        }
        val _columnIndexOfVerificationId: Int = getColumnIndexOrThrow(_stmt, "verificationId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfExperienceYears: Int = getColumnIndexOrThrow(_stmt, "experienceYears")
        val _columnIndexOfBirdCount: Int = getColumnIndexOrThrow(_stmt, "birdCount")
        val _columnIndexOfSpecializations: Int = getColumnIndexOrThrow(_stmt, "specializations")
        val _columnIndexOfAchievementsDescription: Int = getColumnIndexOrThrow(_stmt,
            "achievementsDescription")
        val _columnIndexOfReferenceContacts: Int = getColumnIndexOrThrow(_stmt, "referenceContacts")
        val _columnIndexOfVerificationDocumentUrls: Int = getColumnIndexOrThrow(_stmt,
            "verificationDocumentUrls")
        val _columnIndexOfProfilePhotoUrl: Int = getColumnIndexOrThrow(_stmt, "profilePhotoUrl")
        val _columnIndexOfFarmPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "farmPhotoUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfSubmittedAt: Int = getColumnIndexOrThrow(_stmt, "submittedAt")
        val _columnIndexOfReviewedAt: Int = getColumnIndexOrThrow(_stmt, "reviewedAt")
        val _columnIndexOfReviewedBy: Int = getColumnIndexOrThrow(_stmt, "reviewedBy")
        val _columnIndexOfRejectionReason: Int = getColumnIndexOrThrow(_stmt, "rejectionReason")
        val _columnIndexOfAdminNotes: Int = getColumnIndexOrThrow(_stmt, "adminNotes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<EnthusiastVerificationEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: EnthusiastVerificationEntity
          val _tmpVerificationId: String
          _tmpVerificationId = _stmt.getText(_columnIndexOfVerificationId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpExperienceYears: Int?
          if (_stmt.isNull(_columnIndexOfExperienceYears)) {
            _tmpExperienceYears = null
          } else {
            _tmpExperienceYears = _stmt.getLong(_columnIndexOfExperienceYears).toInt()
          }
          val _tmpBirdCount: Int?
          if (_stmt.isNull(_columnIndexOfBirdCount)) {
            _tmpBirdCount = null
          } else {
            _tmpBirdCount = _stmt.getLong(_columnIndexOfBirdCount).toInt()
          }
          val _tmpSpecializations: String?
          if (_stmt.isNull(_columnIndexOfSpecializations)) {
            _tmpSpecializations = null
          } else {
            _tmpSpecializations = _stmt.getText(_columnIndexOfSpecializations)
          }
          val _tmpAchievementsDescription: String?
          if (_stmt.isNull(_columnIndexOfAchievementsDescription)) {
            _tmpAchievementsDescription = null
          } else {
            _tmpAchievementsDescription = _stmt.getText(_columnIndexOfAchievementsDescription)
          }
          val _tmpReferenceContacts: String?
          if (_stmt.isNull(_columnIndexOfReferenceContacts)) {
            _tmpReferenceContacts = null
          } else {
            _tmpReferenceContacts = _stmt.getText(_columnIndexOfReferenceContacts)
          }
          val _tmpVerificationDocumentUrls: String
          _tmpVerificationDocumentUrls = _stmt.getText(_columnIndexOfVerificationDocumentUrls)
          val _tmpProfilePhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfProfilePhotoUrl)) {
            _tmpProfilePhotoUrl = null
          } else {
            _tmpProfilePhotoUrl = _stmt.getText(_columnIndexOfProfilePhotoUrl)
          }
          val _tmpFarmPhotoUrls: String?
          if (_stmt.isNull(_columnIndexOfFarmPhotoUrls)) {
            _tmpFarmPhotoUrls = null
          } else {
            _tmpFarmPhotoUrls = _stmt.getText(_columnIndexOfFarmPhotoUrls)
          }
          val _tmpStatus: VerificationStatus
          val _tmp_1: String?
          if (_stmt.isNull(_columnIndexOfStatus)) {
            _tmp_1 = null
          } else {
            _tmp_1 = _stmt.getText(_columnIndexOfStatus)
          }
          val _tmp_2: VerificationStatus? = AppDatabase.Converters.toVerificationStatus(_tmp_1)
          if (_tmp_2 == null) {
            error("Expected NON-NULL 'com.rio.rostry.domain.model.VerificationStatus', but it was NULL.")
          } else {
            _tmpStatus = _tmp_2
          }
          val _tmpSubmittedAt: Long?
          if (_stmt.isNull(_columnIndexOfSubmittedAt)) {
            _tmpSubmittedAt = null
          } else {
            _tmpSubmittedAt = _stmt.getLong(_columnIndexOfSubmittedAt)
          }
          val _tmpReviewedAt: Long?
          if (_stmt.isNull(_columnIndexOfReviewedAt)) {
            _tmpReviewedAt = null
          } else {
            _tmpReviewedAt = _stmt.getLong(_columnIndexOfReviewedAt)
          }
          val _tmpReviewedBy: String?
          if (_stmt.isNull(_columnIndexOfReviewedBy)) {
            _tmpReviewedBy = null
          } else {
            _tmpReviewedBy = _stmt.getText(_columnIndexOfReviewedBy)
          }
          val _tmpRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfRejectionReason)) {
            _tmpRejectionReason = null
          } else {
            _tmpRejectionReason = _stmt.getText(_columnIndexOfRejectionReason)
          }
          val _tmpAdminNotes: String?
          if (_stmt.isNull(_columnIndexOfAdminNotes)) {
            _tmpAdminNotes = null
          } else {
            _tmpAdminNotes = _stmt.getText(_columnIndexOfAdminNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              EnthusiastVerificationEntity(_tmpVerificationId,_tmpUserId,_tmpExperienceYears,_tmpBirdCount,_tmpSpecializations,_tmpAchievementsDescription,_tmpReferenceContacts,_tmpVerificationDocumentUrls,_tmpProfilePhotoUrl,_tmpFarmPhotoUrls,_tmpStatus,_tmpSubmittedAt,_tmpReviewedAt,_tmpReviewedBy,_tmpRejectionReason,_tmpAdminNotes,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getPendingVerifications(): Flow<List<EnthusiastVerificationEntity>> {
    val _sql: String =
        "SELECT * FROM enthusiast_verifications WHERE status = 'PENDING' ORDER BY submittedAt ASC"
    return createFlow(__db, false, arrayOf("enthusiast_verifications")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfVerificationId: Int = getColumnIndexOrThrow(_stmt, "verificationId")
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfExperienceYears: Int = getColumnIndexOrThrow(_stmt, "experienceYears")
        val _columnIndexOfBirdCount: Int = getColumnIndexOrThrow(_stmt, "birdCount")
        val _columnIndexOfSpecializations: Int = getColumnIndexOrThrow(_stmt, "specializations")
        val _columnIndexOfAchievementsDescription: Int = getColumnIndexOrThrow(_stmt,
            "achievementsDescription")
        val _columnIndexOfReferenceContacts: Int = getColumnIndexOrThrow(_stmt, "referenceContacts")
        val _columnIndexOfVerificationDocumentUrls: Int = getColumnIndexOrThrow(_stmt,
            "verificationDocumentUrls")
        val _columnIndexOfProfilePhotoUrl: Int = getColumnIndexOrThrow(_stmt, "profilePhotoUrl")
        val _columnIndexOfFarmPhotoUrls: Int = getColumnIndexOrThrow(_stmt, "farmPhotoUrls")
        val _columnIndexOfStatus: Int = getColumnIndexOrThrow(_stmt, "status")
        val _columnIndexOfSubmittedAt: Int = getColumnIndexOrThrow(_stmt, "submittedAt")
        val _columnIndexOfReviewedAt: Int = getColumnIndexOrThrow(_stmt, "reviewedAt")
        val _columnIndexOfReviewedBy: Int = getColumnIndexOrThrow(_stmt, "reviewedBy")
        val _columnIndexOfRejectionReason: Int = getColumnIndexOrThrow(_stmt, "rejectionReason")
        val _columnIndexOfAdminNotes: Int = getColumnIndexOrThrow(_stmt, "adminNotes")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _result: MutableList<EnthusiastVerificationEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: EnthusiastVerificationEntity
          val _tmpVerificationId: String
          _tmpVerificationId = _stmt.getText(_columnIndexOfVerificationId)
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpExperienceYears: Int?
          if (_stmt.isNull(_columnIndexOfExperienceYears)) {
            _tmpExperienceYears = null
          } else {
            _tmpExperienceYears = _stmt.getLong(_columnIndexOfExperienceYears).toInt()
          }
          val _tmpBirdCount: Int?
          if (_stmt.isNull(_columnIndexOfBirdCount)) {
            _tmpBirdCount = null
          } else {
            _tmpBirdCount = _stmt.getLong(_columnIndexOfBirdCount).toInt()
          }
          val _tmpSpecializations: String?
          if (_stmt.isNull(_columnIndexOfSpecializations)) {
            _tmpSpecializations = null
          } else {
            _tmpSpecializations = _stmt.getText(_columnIndexOfSpecializations)
          }
          val _tmpAchievementsDescription: String?
          if (_stmt.isNull(_columnIndexOfAchievementsDescription)) {
            _tmpAchievementsDescription = null
          } else {
            _tmpAchievementsDescription = _stmt.getText(_columnIndexOfAchievementsDescription)
          }
          val _tmpReferenceContacts: String?
          if (_stmt.isNull(_columnIndexOfReferenceContacts)) {
            _tmpReferenceContacts = null
          } else {
            _tmpReferenceContacts = _stmt.getText(_columnIndexOfReferenceContacts)
          }
          val _tmpVerificationDocumentUrls: String
          _tmpVerificationDocumentUrls = _stmt.getText(_columnIndexOfVerificationDocumentUrls)
          val _tmpProfilePhotoUrl: String?
          if (_stmt.isNull(_columnIndexOfProfilePhotoUrl)) {
            _tmpProfilePhotoUrl = null
          } else {
            _tmpProfilePhotoUrl = _stmt.getText(_columnIndexOfProfilePhotoUrl)
          }
          val _tmpFarmPhotoUrls: String?
          if (_stmt.isNull(_columnIndexOfFarmPhotoUrls)) {
            _tmpFarmPhotoUrls = null
          } else {
            _tmpFarmPhotoUrls = _stmt.getText(_columnIndexOfFarmPhotoUrls)
          }
          val _tmpStatus: VerificationStatus
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfStatus)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfStatus)
          }
          val _tmp_1: VerificationStatus? = AppDatabase.Converters.toVerificationStatus(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'com.rio.rostry.domain.model.VerificationStatus', but it was NULL.")
          } else {
            _tmpStatus = _tmp_1
          }
          val _tmpSubmittedAt: Long?
          if (_stmt.isNull(_columnIndexOfSubmittedAt)) {
            _tmpSubmittedAt = null
          } else {
            _tmpSubmittedAt = _stmt.getLong(_columnIndexOfSubmittedAt)
          }
          val _tmpReviewedAt: Long?
          if (_stmt.isNull(_columnIndexOfReviewedAt)) {
            _tmpReviewedAt = null
          } else {
            _tmpReviewedAt = _stmt.getLong(_columnIndexOfReviewedAt)
          }
          val _tmpReviewedBy: String?
          if (_stmt.isNull(_columnIndexOfReviewedBy)) {
            _tmpReviewedBy = null
          } else {
            _tmpReviewedBy = _stmt.getText(_columnIndexOfReviewedBy)
          }
          val _tmpRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfRejectionReason)) {
            _tmpRejectionReason = null
          } else {
            _tmpRejectionReason = _stmt.getText(_columnIndexOfRejectionReason)
          }
          val _tmpAdminNotes: String?
          if (_stmt.isNull(_columnIndexOfAdminNotes)) {
            _tmpAdminNotes = null
          } else {
            _tmpAdminNotes = _stmt.getText(_columnIndexOfAdminNotes)
          }
          val _tmpCreatedAt: Long
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt)
          val _tmpUpdatedAt: Long
          _tmpUpdatedAt = _stmt.getLong(_columnIndexOfUpdatedAt)
          _item =
              EnthusiastVerificationEntity(_tmpVerificationId,_tmpUserId,_tmpExperienceYears,_tmpBirdCount,_tmpSpecializations,_tmpAchievementsDescription,_tmpReferenceContacts,_tmpVerificationDocumentUrls,_tmpProfilePhotoUrl,_tmpFarmPhotoUrls,_tmpStatus,_tmpSubmittedAt,_tmpReviewedAt,_tmpReviewedBy,_tmpRejectionReason,_tmpAdminNotes,_tmpCreatedAt,_tmpUpdatedAt)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateVerificationStatus(
    verificationId: String,
    status: VerificationStatus,
    reviewedAt: Long,
    reviewedBy: String,
    reason: String?,
    updatedAt: Long,
  ) {
    val _sql: String =
        "UPDATE enthusiast_verifications SET status = ?, reviewedAt = ?, reviewedBy = ?, rejectionReason = ?, updatedAt = ? WHERE verificationId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        val _tmp: String? = AppDatabase.Converters.fromVerificationStatus(status)
        if (_tmp == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, _tmp)
        }
        _argIndex = 2
        _stmt.bindLong(_argIndex, reviewedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, reviewedBy)
        _argIndex = 4
        if (reason == null) {
          _stmt.bindNull(_argIndex)
        } else {
          _stmt.bindText(_argIndex, reason)
        }
        _argIndex = 5
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 6
        _stmt.bindText(_argIndex, verificationId)
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
