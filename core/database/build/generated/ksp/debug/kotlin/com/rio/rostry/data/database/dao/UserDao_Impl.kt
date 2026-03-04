package com.rio.rostry.`data`.database.dao

import androidx.room.EntityDeleteOrUpdateAdapter
import androidx.room.EntityInsertAdapter
import androidx.room.RoomDatabase
import androidx.room.coroutines.createFlow
import androidx.room.util.appendPlaceholders
import androidx.room.util.getColumnIndexOrThrow
import androidx.room.util.performInTransactionSuspending
import androidx.room.util.performSuspending
import androidx.sqlite.SQLiteStatement
import com.rio.rostry.`data`.database.AppDatabase
import com.rio.rostry.`data`.database.entity.UserEntity
import com.rio.rostry.domain.model.VerificationStatus
import java.util.Date
import javax.`annotation`.processing.Generated
import kotlin.Boolean
import kotlin.Double
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
public class UserDao_Impl(
  __db: RoomDatabase,
) : UserDao {
  private val __db: RoomDatabase

  private val __insertAdapterOfUserEntity: EntityInsertAdapter<UserEntity>

  private val __dateLongConverter: UserEntity.DateLongConverter = UserEntity.DateLongConverter()

  private val __insertAdapterOfUserEntity_1: EntityInsertAdapter<UserEntity>

  private val __updateAdapterOfUserEntity: EntityDeleteOrUpdateAdapter<UserEntity>
  init {
    this.__db = __db
    this.__insertAdapterOfUserEntity = object : EntityInsertAdapter<UserEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR IGNORE INTO `users` (`userId`,`phoneNumber`,`email`,`fullName`,`address`,`bio`,`profilePictureUrl`,`userType`,`verificationStatus`,`farmAddressLine1`,`farmAddressLine2`,`farmCity`,`farmState`,`farmPostalCode`,`farmCountry`,`farmLocationLat`,`farmLocationLng`,`locationVerified`,`kycLevel`,`chickenCount`,`farmerType`,`raisingSince`,`favoriteBreed`,`kycVerifiedAt`,`kycRejectionReason`,`verificationRejectionReason`,`latestVerificationId`,`latestVerificationRef`,`verificationSubmittedAt`,`showcaseCount`,`maxShowcaseSlots`,`createdAt`,`updatedAt`,`customClaimsUpdatedAt`,`isSuspended`,`suspensionReason`,`suspensionEndsAt`,`notificationsEnabled`,`farmAlertsEnabled`,`transferAlertsEnabled`,`socialAlertsEnabled`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: UserEntity) {
        statement.bindText(1, entity.userId)
        val _tmpPhoneNumber: String? = entity.phoneNumber
        if (_tmpPhoneNumber == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpPhoneNumber)
        }
        val _tmpEmail: String? = entity.email
        if (_tmpEmail == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpEmail)
        }
        val _tmpFullName: String? = entity.fullName
        if (_tmpFullName == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpFullName)
        }
        val _tmpAddress: String? = entity.address
        if (_tmpAddress == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpAddress)
        }
        val _tmpBio: String? = entity.bio
        if (_tmpBio == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpBio)
        }
        val _tmpProfilePictureUrl: String? = entity.profilePictureUrl
        if (_tmpProfilePictureUrl == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpProfilePictureUrl)
        }
        statement.bindText(8, entity.userType)
        val _tmp: String? = AppDatabase.Converters.fromVerificationStatus(entity.verificationStatus)
        if (_tmp == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmp)
        }
        val _tmpFarmAddressLine1: String? = entity.farmAddressLine1
        if (_tmpFarmAddressLine1 == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpFarmAddressLine1)
        }
        val _tmpFarmAddressLine2: String? = entity.farmAddressLine2
        if (_tmpFarmAddressLine2 == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpFarmAddressLine2)
        }
        val _tmpFarmCity: String? = entity.farmCity
        if (_tmpFarmCity == null) {
          statement.bindNull(12)
        } else {
          statement.bindText(12, _tmpFarmCity)
        }
        val _tmpFarmState: String? = entity.farmState
        if (_tmpFarmState == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpFarmState)
        }
        val _tmpFarmPostalCode: String? = entity.farmPostalCode
        if (_tmpFarmPostalCode == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmpFarmPostalCode)
        }
        val _tmpFarmCountry: String? = entity.farmCountry
        if (_tmpFarmCountry == null) {
          statement.bindNull(15)
        } else {
          statement.bindText(15, _tmpFarmCountry)
        }
        val _tmpFarmLocationLat: Double? = entity.farmLocationLat
        if (_tmpFarmLocationLat == null) {
          statement.bindNull(16)
        } else {
          statement.bindDouble(16, _tmpFarmLocationLat)
        }
        val _tmpFarmLocationLng: Double? = entity.farmLocationLng
        if (_tmpFarmLocationLng == null) {
          statement.bindNull(17)
        } else {
          statement.bindDouble(17, _tmpFarmLocationLng)
        }
        val _tmpLocationVerified: Boolean? = entity.locationVerified
        val _tmp_1: Int? = _tmpLocationVerified?.let { if (it) 1 else 0 }
        if (_tmp_1 == null) {
          statement.bindNull(18)
        } else {
          statement.bindLong(18, _tmp_1.toLong())
        }
        val _tmpKycLevel: Int? = entity.kycLevel
        if (_tmpKycLevel == null) {
          statement.bindNull(19)
        } else {
          statement.bindLong(19, _tmpKycLevel.toLong())
        }
        val _tmpChickenCount: Int? = entity.chickenCount
        if (_tmpChickenCount == null) {
          statement.bindNull(20)
        } else {
          statement.bindLong(20, _tmpChickenCount.toLong())
        }
        val _tmpFarmerType: String? = entity.farmerType
        if (_tmpFarmerType == null) {
          statement.bindNull(21)
        } else {
          statement.bindText(21, _tmpFarmerType)
        }
        val _tmpRaisingSince: Long? = entity.raisingSince
        if (_tmpRaisingSince == null) {
          statement.bindNull(22)
        } else {
          statement.bindLong(22, _tmpRaisingSince)
        }
        val _tmpFavoriteBreed: String? = entity.favoriteBreed
        if (_tmpFavoriteBreed == null) {
          statement.bindNull(23)
        } else {
          statement.bindText(23, _tmpFavoriteBreed)
        }
        val _tmpKycVerifiedAt: Long? = entity.kycVerifiedAt
        if (_tmpKycVerifiedAt == null) {
          statement.bindNull(24)
        } else {
          statement.bindLong(24, _tmpKycVerifiedAt)
        }
        val _tmpKycRejectionReason: String? = entity.kycRejectionReason
        if (_tmpKycRejectionReason == null) {
          statement.bindNull(25)
        } else {
          statement.bindText(25, _tmpKycRejectionReason)
        }
        val _tmpVerificationRejectionReason: String? = entity.verificationRejectionReason
        if (_tmpVerificationRejectionReason == null) {
          statement.bindNull(26)
        } else {
          statement.bindText(26, _tmpVerificationRejectionReason)
        }
        val _tmpLatestVerificationId: String? = entity.latestVerificationId
        if (_tmpLatestVerificationId == null) {
          statement.bindNull(27)
        } else {
          statement.bindText(27, _tmpLatestVerificationId)
        }
        val _tmpLatestVerificationRef: String? = entity.latestVerificationRef
        if (_tmpLatestVerificationRef == null) {
          statement.bindNull(28)
        } else {
          statement.bindText(28, _tmpLatestVerificationRef)
        }
        val _tmpVerificationSubmittedAt: Date? = entity.verificationSubmittedAt
        val _tmp_2: Long? = __dateLongConverter.dateToTimestamp(_tmpVerificationSubmittedAt)
        if (_tmp_2 == null) {
          statement.bindNull(29)
        } else {
          statement.bindLong(29, _tmp_2)
        }
        statement.bindLong(30, entity.showcaseCount.toLong())
        statement.bindLong(31, entity.maxShowcaseSlots.toLong())
        val _tmpCreatedAt: Date? = entity.createdAt
        val _tmp_3: Long? = __dateLongConverter.dateToTimestamp(_tmpCreatedAt)
        if (_tmp_3 == null) {
          statement.bindNull(32)
        } else {
          statement.bindLong(32, _tmp_3)
        }
        val _tmpUpdatedAt: Date? = entity.updatedAt
        val _tmp_4: Long? = __dateLongConverter.dateToTimestamp(_tmpUpdatedAt)
        if (_tmp_4 == null) {
          statement.bindNull(33)
        } else {
          statement.bindLong(33, _tmp_4)
        }
        val _tmpCustomClaimsUpdatedAt: Date? = entity.customClaimsUpdatedAt
        val _tmp_5: Long? = __dateLongConverter.dateToTimestamp(_tmpCustomClaimsUpdatedAt)
        if (_tmp_5 == null) {
          statement.bindNull(34)
        } else {
          statement.bindLong(34, _tmp_5)
        }
        val _tmp_6: Int = if (entity.isSuspended) 1 else 0
        statement.bindLong(35, _tmp_6.toLong())
        val _tmpSuspensionReason: String? = entity.suspensionReason
        if (_tmpSuspensionReason == null) {
          statement.bindNull(36)
        } else {
          statement.bindText(36, _tmpSuspensionReason)
        }
        val _tmpSuspensionEndsAt: Long? = entity.suspensionEndsAt
        if (_tmpSuspensionEndsAt == null) {
          statement.bindNull(37)
        } else {
          statement.bindLong(37, _tmpSuspensionEndsAt)
        }
        val _tmp_7: Int = if (entity.notificationsEnabled) 1 else 0
        statement.bindLong(38, _tmp_7.toLong())
        val _tmp_8: Int = if (entity.farmAlertsEnabled) 1 else 0
        statement.bindLong(39, _tmp_8.toLong())
        val _tmp_9: Int = if (entity.transferAlertsEnabled) 1 else 0
        statement.bindLong(40, _tmp_9.toLong())
        val _tmp_10: Int = if (entity.socialAlertsEnabled) 1 else 0
        statement.bindLong(41, _tmp_10.toLong())
      }
    }
    this.__insertAdapterOfUserEntity_1 = object : EntityInsertAdapter<UserEntity>() {
      protected override fun createQuery(): String =
          "INSERT OR REPLACE INTO `users` (`userId`,`phoneNumber`,`email`,`fullName`,`address`,`bio`,`profilePictureUrl`,`userType`,`verificationStatus`,`farmAddressLine1`,`farmAddressLine2`,`farmCity`,`farmState`,`farmPostalCode`,`farmCountry`,`farmLocationLat`,`farmLocationLng`,`locationVerified`,`kycLevel`,`chickenCount`,`farmerType`,`raisingSince`,`favoriteBreed`,`kycVerifiedAt`,`kycRejectionReason`,`verificationRejectionReason`,`latestVerificationId`,`latestVerificationRef`,`verificationSubmittedAt`,`showcaseCount`,`maxShowcaseSlots`,`createdAt`,`updatedAt`,`customClaimsUpdatedAt`,`isSuspended`,`suspensionReason`,`suspensionEndsAt`,`notificationsEnabled`,`farmAlertsEnabled`,`transferAlertsEnabled`,`socialAlertsEnabled`) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"

      protected override fun bind(statement: SQLiteStatement, entity: UserEntity) {
        statement.bindText(1, entity.userId)
        val _tmpPhoneNumber: String? = entity.phoneNumber
        if (_tmpPhoneNumber == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpPhoneNumber)
        }
        val _tmpEmail: String? = entity.email
        if (_tmpEmail == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpEmail)
        }
        val _tmpFullName: String? = entity.fullName
        if (_tmpFullName == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpFullName)
        }
        val _tmpAddress: String? = entity.address
        if (_tmpAddress == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpAddress)
        }
        val _tmpBio: String? = entity.bio
        if (_tmpBio == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpBio)
        }
        val _tmpProfilePictureUrl: String? = entity.profilePictureUrl
        if (_tmpProfilePictureUrl == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpProfilePictureUrl)
        }
        statement.bindText(8, entity.userType)
        val _tmp: String? = AppDatabase.Converters.fromVerificationStatus(entity.verificationStatus)
        if (_tmp == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmp)
        }
        val _tmpFarmAddressLine1: String? = entity.farmAddressLine1
        if (_tmpFarmAddressLine1 == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpFarmAddressLine1)
        }
        val _tmpFarmAddressLine2: String? = entity.farmAddressLine2
        if (_tmpFarmAddressLine2 == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpFarmAddressLine2)
        }
        val _tmpFarmCity: String? = entity.farmCity
        if (_tmpFarmCity == null) {
          statement.bindNull(12)
        } else {
          statement.bindText(12, _tmpFarmCity)
        }
        val _tmpFarmState: String? = entity.farmState
        if (_tmpFarmState == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpFarmState)
        }
        val _tmpFarmPostalCode: String? = entity.farmPostalCode
        if (_tmpFarmPostalCode == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmpFarmPostalCode)
        }
        val _tmpFarmCountry: String? = entity.farmCountry
        if (_tmpFarmCountry == null) {
          statement.bindNull(15)
        } else {
          statement.bindText(15, _tmpFarmCountry)
        }
        val _tmpFarmLocationLat: Double? = entity.farmLocationLat
        if (_tmpFarmLocationLat == null) {
          statement.bindNull(16)
        } else {
          statement.bindDouble(16, _tmpFarmLocationLat)
        }
        val _tmpFarmLocationLng: Double? = entity.farmLocationLng
        if (_tmpFarmLocationLng == null) {
          statement.bindNull(17)
        } else {
          statement.bindDouble(17, _tmpFarmLocationLng)
        }
        val _tmpLocationVerified: Boolean? = entity.locationVerified
        val _tmp_1: Int? = _tmpLocationVerified?.let { if (it) 1 else 0 }
        if (_tmp_1 == null) {
          statement.bindNull(18)
        } else {
          statement.bindLong(18, _tmp_1.toLong())
        }
        val _tmpKycLevel: Int? = entity.kycLevel
        if (_tmpKycLevel == null) {
          statement.bindNull(19)
        } else {
          statement.bindLong(19, _tmpKycLevel.toLong())
        }
        val _tmpChickenCount: Int? = entity.chickenCount
        if (_tmpChickenCount == null) {
          statement.bindNull(20)
        } else {
          statement.bindLong(20, _tmpChickenCount.toLong())
        }
        val _tmpFarmerType: String? = entity.farmerType
        if (_tmpFarmerType == null) {
          statement.bindNull(21)
        } else {
          statement.bindText(21, _tmpFarmerType)
        }
        val _tmpRaisingSince: Long? = entity.raisingSince
        if (_tmpRaisingSince == null) {
          statement.bindNull(22)
        } else {
          statement.bindLong(22, _tmpRaisingSince)
        }
        val _tmpFavoriteBreed: String? = entity.favoriteBreed
        if (_tmpFavoriteBreed == null) {
          statement.bindNull(23)
        } else {
          statement.bindText(23, _tmpFavoriteBreed)
        }
        val _tmpKycVerifiedAt: Long? = entity.kycVerifiedAt
        if (_tmpKycVerifiedAt == null) {
          statement.bindNull(24)
        } else {
          statement.bindLong(24, _tmpKycVerifiedAt)
        }
        val _tmpKycRejectionReason: String? = entity.kycRejectionReason
        if (_tmpKycRejectionReason == null) {
          statement.bindNull(25)
        } else {
          statement.bindText(25, _tmpKycRejectionReason)
        }
        val _tmpVerificationRejectionReason: String? = entity.verificationRejectionReason
        if (_tmpVerificationRejectionReason == null) {
          statement.bindNull(26)
        } else {
          statement.bindText(26, _tmpVerificationRejectionReason)
        }
        val _tmpLatestVerificationId: String? = entity.latestVerificationId
        if (_tmpLatestVerificationId == null) {
          statement.bindNull(27)
        } else {
          statement.bindText(27, _tmpLatestVerificationId)
        }
        val _tmpLatestVerificationRef: String? = entity.latestVerificationRef
        if (_tmpLatestVerificationRef == null) {
          statement.bindNull(28)
        } else {
          statement.bindText(28, _tmpLatestVerificationRef)
        }
        val _tmpVerificationSubmittedAt: Date? = entity.verificationSubmittedAt
        val _tmp_2: Long? = __dateLongConverter.dateToTimestamp(_tmpVerificationSubmittedAt)
        if (_tmp_2 == null) {
          statement.bindNull(29)
        } else {
          statement.bindLong(29, _tmp_2)
        }
        statement.bindLong(30, entity.showcaseCount.toLong())
        statement.bindLong(31, entity.maxShowcaseSlots.toLong())
        val _tmpCreatedAt: Date? = entity.createdAt
        val _tmp_3: Long? = __dateLongConverter.dateToTimestamp(_tmpCreatedAt)
        if (_tmp_3 == null) {
          statement.bindNull(32)
        } else {
          statement.bindLong(32, _tmp_3)
        }
        val _tmpUpdatedAt: Date? = entity.updatedAt
        val _tmp_4: Long? = __dateLongConverter.dateToTimestamp(_tmpUpdatedAt)
        if (_tmp_4 == null) {
          statement.bindNull(33)
        } else {
          statement.bindLong(33, _tmp_4)
        }
        val _tmpCustomClaimsUpdatedAt: Date? = entity.customClaimsUpdatedAt
        val _tmp_5: Long? = __dateLongConverter.dateToTimestamp(_tmpCustomClaimsUpdatedAt)
        if (_tmp_5 == null) {
          statement.bindNull(34)
        } else {
          statement.bindLong(34, _tmp_5)
        }
        val _tmp_6: Int = if (entity.isSuspended) 1 else 0
        statement.bindLong(35, _tmp_6.toLong())
        val _tmpSuspensionReason: String? = entity.suspensionReason
        if (_tmpSuspensionReason == null) {
          statement.bindNull(36)
        } else {
          statement.bindText(36, _tmpSuspensionReason)
        }
        val _tmpSuspensionEndsAt: Long? = entity.suspensionEndsAt
        if (_tmpSuspensionEndsAt == null) {
          statement.bindNull(37)
        } else {
          statement.bindLong(37, _tmpSuspensionEndsAt)
        }
        val _tmp_7: Int = if (entity.notificationsEnabled) 1 else 0
        statement.bindLong(38, _tmp_7.toLong())
        val _tmp_8: Int = if (entity.farmAlertsEnabled) 1 else 0
        statement.bindLong(39, _tmp_8.toLong())
        val _tmp_9: Int = if (entity.transferAlertsEnabled) 1 else 0
        statement.bindLong(40, _tmp_9.toLong())
        val _tmp_10: Int = if (entity.socialAlertsEnabled) 1 else 0
        statement.bindLong(41, _tmp_10.toLong())
      }
    }
    this.__updateAdapterOfUserEntity = object : EntityDeleteOrUpdateAdapter<UserEntity>() {
      protected override fun createQuery(): String =
          "UPDATE OR ABORT `users` SET `userId` = ?,`phoneNumber` = ?,`email` = ?,`fullName` = ?,`address` = ?,`bio` = ?,`profilePictureUrl` = ?,`userType` = ?,`verificationStatus` = ?,`farmAddressLine1` = ?,`farmAddressLine2` = ?,`farmCity` = ?,`farmState` = ?,`farmPostalCode` = ?,`farmCountry` = ?,`farmLocationLat` = ?,`farmLocationLng` = ?,`locationVerified` = ?,`kycLevel` = ?,`chickenCount` = ?,`farmerType` = ?,`raisingSince` = ?,`favoriteBreed` = ?,`kycVerifiedAt` = ?,`kycRejectionReason` = ?,`verificationRejectionReason` = ?,`latestVerificationId` = ?,`latestVerificationRef` = ?,`verificationSubmittedAt` = ?,`showcaseCount` = ?,`maxShowcaseSlots` = ?,`createdAt` = ?,`updatedAt` = ?,`customClaimsUpdatedAt` = ?,`isSuspended` = ?,`suspensionReason` = ?,`suspensionEndsAt` = ?,`notificationsEnabled` = ?,`farmAlertsEnabled` = ?,`transferAlertsEnabled` = ?,`socialAlertsEnabled` = ? WHERE `userId` = ?"

      protected override fun bind(statement: SQLiteStatement, entity: UserEntity) {
        statement.bindText(1, entity.userId)
        val _tmpPhoneNumber: String? = entity.phoneNumber
        if (_tmpPhoneNumber == null) {
          statement.bindNull(2)
        } else {
          statement.bindText(2, _tmpPhoneNumber)
        }
        val _tmpEmail: String? = entity.email
        if (_tmpEmail == null) {
          statement.bindNull(3)
        } else {
          statement.bindText(3, _tmpEmail)
        }
        val _tmpFullName: String? = entity.fullName
        if (_tmpFullName == null) {
          statement.bindNull(4)
        } else {
          statement.bindText(4, _tmpFullName)
        }
        val _tmpAddress: String? = entity.address
        if (_tmpAddress == null) {
          statement.bindNull(5)
        } else {
          statement.bindText(5, _tmpAddress)
        }
        val _tmpBio: String? = entity.bio
        if (_tmpBio == null) {
          statement.bindNull(6)
        } else {
          statement.bindText(6, _tmpBio)
        }
        val _tmpProfilePictureUrl: String? = entity.profilePictureUrl
        if (_tmpProfilePictureUrl == null) {
          statement.bindNull(7)
        } else {
          statement.bindText(7, _tmpProfilePictureUrl)
        }
        statement.bindText(8, entity.userType)
        val _tmp: String? = AppDatabase.Converters.fromVerificationStatus(entity.verificationStatus)
        if (_tmp == null) {
          statement.bindNull(9)
        } else {
          statement.bindText(9, _tmp)
        }
        val _tmpFarmAddressLine1: String? = entity.farmAddressLine1
        if (_tmpFarmAddressLine1 == null) {
          statement.bindNull(10)
        } else {
          statement.bindText(10, _tmpFarmAddressLine1)
        }
        val _tmpFarmAddressLine2: String? = entity.farmAddressLine2
        if (_tmpFarmAddressLine2 == null) {
          statement.bindNull(11)
        } else {
          statement.bindText(11, _tmpFarmAddressLine2)
        }
        val _tmpFarmCity: String? = entity.farmCity
        if (_tmpFarmCity == null) {
          statement.bindNull(12)
        } else {
          statement.bindText(12, _tmpFarmCity)
        }
        val _tmpFarmState: String? = entity.farmState
        if (_tmpFarmState == null) {
          statement.bindNull(13)
        } else {
          statement.bindText(13, _tmpFarmState)
        }
        val _tmpFarmPostalCode: String? = entity.farmPostalCode
        if (_tmpFarmPostalCode == null) {
          statement.bindNull(14)
        } else {
          statement.bindText(14, _tmpFarmPostalCode)
        }
        val _tmpFarmCountry: String? = entity.farmCountry
        if (_tmpFarmCountry == null) {
          statement.bindNull(15)
        } else {
          statement.bindText(15, _tmpFarmCountry)
        }
        val _tmpFarmLocationLat: Double? = entity.farmLocationLat
        if (_tmpFarmLocationLat == null) {
          statement.bindNull(16)
        } else {
          statement.bindDouble(16, _tmpFarmLocationLat)
        }
        val _tmpFarmLocationLng: Double? = entity.farmLocationLng
        if (_tmpFarmLocationLng == null) {
          statement.bindNull(17)
        } else {
          statement.bindDouble(17, _tmpFarmLocationLng)
        }
        val _tmpLocationVerified: Boolean? = entity.locationVerified
        val _tmp_1: Int? = _tmpLocationVerified?.let { if (it) 1 else 0 }
        if (_tmp_1 == null) {
          statement.bindNull(18)
        } else {
          statement.bindLong(18, _tmp_1.toLong())
        }
        val _tmpKycLevel: Int? = entity.kycLevel
        if (_tmpKycLevel == null) {
          statement.bindNull(19)
        } else {
          statement.bindLong(19, _tmpKycLevel.toLong())
        }
        val _tmpChickenCount: Int? = entity.chickenCount
        if (_tmpChickenCount == null) {
          statement.bindNull(20)
        } else {
          statement.bindLong(20, _tmpChickenCount.toLong())
        }
        val _tmpFarmerType: String? = entity.farmerType
        if (_tmpFarmerType == null) {
          statement.bindNull(21)
        } else {
          statement.bindText(21, _tmpFarmerType)
        }
        val _tmpRaisingSince: Long? = entity.raisingSince
        if (_tmpRaisingSince == null) {
          statement.bindNull(22)
        } else {
          statement.bindLong(22, _tmpRaisingSince)
        }
        val _tmpFavoriteBreed: String? = entity.favoriteBreed
        if (_tmpFavoriteBreed == null) {
          statement.bindNull(23)
        } else {
          statement.bindText(23, _tmpFavoriteBreed)
        }
        val _tmpKycVerifiedAt: Long? = entity.kycVerifiedAt
        if (_tmpKycVerifiedAt == null) {
          statement.bindNull(24)
        } else {
          statement.bindLong(24, _tmpKycVerifiedAt)
        }
        val _tmpKycRejectionReason: String? = entity.kycRejectionReason
        if (_tmpKycRejectionReason == null) {
          statement.bindNull(25)
        } else {
          statement.bindText(25, _tmpKycRejectionReason)
        }
        val _tmpVerificationRejectionReason: String? = entity.verificationRejectionReason
        if (_tmpVerificationRejectionReason == null) {
          statement.bindNull(26)
        } else {
          statement.bindText(26, _tmpVerificationRejectionReason)
        }
        val _tmpLatestVerificationId: String? = entity.latestVerificationId
        if (_tmpLatestVerificationId == null) {
          statement.bindNull(27)
        } else {
          statement.bindText(27, _tmpLatestVerificationId)
        }
        val _tmpLatestVerificationRef: String? = entity.latestVerificationRef
        if (_tmpLatestVerificationRef == null) {
          statement.bindNull(28)
        } else {
          statement.bindText(28, _tmpLatestVerificationRef)
        }
        val _tmpVerificationSubmittedAt: Date? = entity.verificationSubmittedAt
        val _tmp_2: Long? = __dateLongConverter.dateToTimestamp(_tmpVerificationSubmittedAt)
        if (_tmp_2 == null) {
          statement.bindNull(29)
        } else {
          statement.bindLong(29, _tmp_2)
        }
        statement.bindLong(30, entity.showcaseCount.toLong())
        statement.bindLong(31, entity.maxShowcaseSlots.toLong())
        val _tmpCreatedAt: Date? = entity.createdAt
        val _tmp_3: Long? = __dateLongConverter.dateToTimestamp(_tmpCreatedAt)
        if (_tmp_3 == null) {
          statement.bindNull(32)
        } else {
          statement.bindLong(32, _tmp_3)
        }
        val _tmpUpdatedAt: Date? = entity.updatedAt
        val _tmp_4: Long? = __dateLongConverter.dateToTimestamp(_tmpUpdatedAt)
        if (_tmp_4 == null) {
          statement.bindNull(33)
        } else {
          statement.bindLong(33, _tmp_4)
        }
        val _tmpCustomClaimsUpdatedAt: Date? = entity.customClaimsUpdatedAt
        val _tmp_5: Long? = __dateLongConverter.dateToTimestamp(_tmpCustomClaimsUpdatedAt)
        if (_tmp_5 == null) {
          statement.bindNull(34)
        } else {
          statement.bindLong(34, _tmp_5)
        }
        val _tmp_6: Int = if (entity.isSuspended) 1 else 0
        statement.bindLong(35, _tmp_6.toLong())
        val _tmpSuspensionReason: String? = entity.suspensionReason
        if (_tmpSuspensionReason == null) {
          statement.bindNull(36)
        } else {
          statement.bindText(36, _tmpSuspensionReason)
        }
        val _tmpSuspensionEndsAt: Long? = entity.suspensionEndsAt
        if (_tmpSuspensionEndsAt == null) {
          statement.bindNull(37)
        } else {
          statement.bindLong(37, _tmpSuspensionEndsAt)
        }
        val _tmp_7: Int = if (entity.notificationsEnabled) 1 else 0
        statement.bindLong(38, _tmp_7.toLong())
        val _tmp_8: Int = if (entity.farmAlertsEnabled) 1 else 0
        statement.bindLong(39, _tmp_8.toLong())
        val _tmp_9: Int = if (entity.transferAlertsEnabled) 1 else 0
        statement.bindLong(40, _tmp_9.toLong())
        val _tmp_10: Int = if (entity.socialAlertsEnabled) 1 else 0
        statement.bindLong(41, _tmp_10.toLong())
        statement.bindText(42, entity.userId)
      }
    }
  }

  public override suspend fun insertUserIgnore(user: UserEntity): Long = performSuspending(__db,
      false, true) { _connection ->
    val _result: Long = __insertAdapterOfUserEntity.insertAndReturnId(_connection, user)
    _result
  }

  public override suspend fun insertUsersIgnore(users: List<UserEntity>): List<Long> =
      performSuspending(__db, false, true) { _connection ->
    val _result: List<Long> = __insertAdapterOfUserEntity.insertAndReturnIdsList(_connection, users)
    _result
  }

  public override suspend fun insertUserReplace(user: UserEntity): Unit = performSuspending(__db,
      false, true) { _connection ->
    __insertAdapterOfUserEntity_1.insert(_connection, user)
  }

  public override suspend fun updateUser(user: UserEntity): Unit = performSuspending(__db, false,
      true) { _connection ->
    __updateAdapterOfUserEntity.handle(_connection, user)
  }

  public override suspend fun updateUsers(users: List<UserEntity>): Unit = performSuspending(__db,
      false, true) { _connection ->
    __updateAdapterOfUserEntity.handleMultiple(_connection, users)
  }

  public override suspend fun upsertUser(user: UserEntity): Unit =
      performInTransactionSuspending(__db) {
    super@UserDao_Impl.upsertUser(user)
  }

  public override suspend fun upsertUsers(users: List<UserEntity>): Unit =
      performInTransactionSuspending(__db) {
    super@UserDao_Impl.upsertUsers(users)
  }

  public override fun getUserById(userId: String): Flow<UserEntity?> {
    val _sql: String = "SELECT * FROM users WHERE userId = ?"
    return createFlow(__db, false, arrayOf("users")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfPhoneNumber: Int = getColumnIndexOrThrow(_stmt, "phoneNumber")
        val _columnIndexOfEmail: Int = getColumnIndexOrThrow(_stmt, "email")
        val _columnIndexOfFullName: Int = getColumnIndexOrThrow(_stmt, "fullName")
        val _columnIndexOfAddress: Int = getColumnIndexOrThrow(_stmt, "address")
        val _columnIndexOfBio: Int = getColumnIndexOrThrow(_stmt, "bio")
        val _columnIndexOfProfilePictureUrl: Int = getColumnIndexOrThrow(_stmt, "profilePictureUrl")
        val _columnIndexOfUserType: Int = getColumnIndexOrThrow(_stmt, "userType")
        val _columnIndexOfVerificationStatus: Int = getColumnIndexOrThrow(_stmt,
            "verificationStatus")
        val _columnIndexOfFarmAddressLine1: Int = getColumnIndexOrThrow(_stmt, "farmAddressLine1")
        val _columnIndexOfFarmAddressLine2: Int = getColumnIndexOrThrow(_stmt, "farmAddressLine2")
        val _columnIndexOfFarmCity: Int = getColumnIndexOrThrow(_stmt, "farmCity")
        val _columnIndexOfFarmState: Int = getColumnIndexOrThrow(_stmt, "farmState")
        val _columnIndexOfFarmPostalCode: Int = getColumnIndexOrThrow(_stmt, "farmPostalCode")
        val _columnIndexOfFarmCountry: Int = getColumnIndexOrThrow(_stmt, "farmCountry")
        val _columnIndexOfFarmLocationLat: Int = getColumnIndexOrThrow(_stmt, "farmLocationLat")
        val _columnIndexOfFarmLocationLng: Int = getColumnIndexOrThrow(_stmt, "farmLocationLng")
        val _columnIndexOfLocationVerified: Int = getColumnIndexOrThrow(_stmt, "locationVerified")
        val _columnIndexOfKycLevel: Int = getColumnIndexOrThrow(_stmt, "kycLevel")
        val _columnIndexOfChickenCount: Int = getColumnIndexOrThrow(_stmt, "chickenCount")
        val _columnIndexOfFarmerType: Int = getColumnIndexOrThrow(_stmt, "farmerType")
        val _columnIndexOfRaisingSince: Int = getColumnIndexOrThrow(_stmt, "raisingSince")
        val _columnIndexOfFavoriteBreed: Int = getColumnIndexOrThrow(_stmt, "favoriteBreed")
        val _columnIndexOfKycVerifiedAt: Int = getColumnIndexOrThrow(_stmt, "kycVerifiedAt")
        val _columnIndexOfKycRejectionReason: Int = getColumnIndexOrThrow(_stmt,
            "kycRejectionReason")
        val _columnIndexOfVerificationRejectionReason: Int = getColumnIndexOrThrow(_stmt,
            "verificationRejectionReason")
        val _columnIndexOfLatestVerificationId: Int = getColumnIndexOrThrow(_stmt,
            "latestVerificationId")
        val _columnIndexOfLatestVerificationRef: Int = getColumnIndexOrThrow(_stmt,
            "latestVerificationRef")
        val _columnIndexOfVerificationSubmittedAt: Int = getColumnIndexOrThrow(_stmt,
            "verificationSubmittedAt")
        val _columnIndexOfShowcaseCount: Int = getColumnIndexOrThrow(_stmt, "showcaseCount")
        val _columnIndexOfMaxShowcaseSlots: Int = getColumnIndexOrThrow(_stmt, "maxShowcaseSlots")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfCustomClaimsUpdatedAt: Int = getColumnIndexOrThrow(_stmt,
            "customClaimsUpdatedAt")
        val _columnIndexOfIsSuspended: Int = getColumnIndexOrThrow(_stmt, "isSuspended")
        val _columnIndexOfSuspensionReason: Int = getColumnIndexOrThrow(_stmt, "suspensionReason")
        val _columnIndexOfSuspensionEndsAt: Int = getColumnIndexOrThrow(_stmt, "suspensionEndsAt")
        val _columnIndexOfNotificationsEnabled: Int = getColumnIndexOrThrow(_stmt,
            "notificationsEnabled")
        val _columnIndexOfFarmAlertsEnabled: Int = getColumnIndexOrThrow(_stmt, "farmAlertsEnabled")
        val _columnIndexOfTransferAlertsEnabled: Int = getColumnIndexOrThrow(_stmt,
            "transferAlertsEnabled")
        val _columnIndexOfSocialAlertsEnabled: Int = getColumnIndexOrThrow(_stmt,
            "socialAlertsEnabled")
        val _result: UserEntity?
        if (_stmt.step()) {
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpPhoneNumber: String?
          if (_stmt.isNull(_columnIndexOfPhoneNumber)) {
            _tmpPhoneNumber = null
          } else {
            _tmpPhoneNumber = _stmt.getText(_columnIndexOfPhoneNumber)
          }
          val _tmpEmail: String?
          if (_stmt.isNull(_columnIndexOfEmail)) {
            _tmpEmail = null
          } else {
            _tmpEmail = _stmt.getText(_columnIndexOfEmail)
          }
          val _tmpFullName: String?
          if (_stmt.isNull(_columnIndexOfFullName)) {
            _tmpFullName = null
          } else {
            _tmpFullName = _stmt.getText(_columnIndexOfFullName)
          }
          val _tmpAddress: String?
          if (_stmt.isNull(_columnIndexOfAddress)) {
            _tmpAddress = null
          } else {
            _tmpAddress = _stmt.getText(_columnIndexOfAddress)
          }
          val _tmpBio: String?
          if (_stmt.isNull(_columnIndexOfBio)) {
            _tmpBio = null
          } else {
            _tmpBio = _stmt.getText(_columnIndexOfBio)
          }
          val _tmpProfilePictureUrl: String?
          if (_stmt.isNull(_columnIndexOfProfilePictureUrl)) {
            _tmpProfilePictureUrl = null
          } else {
            _tmpProfilePictureUrl = _stmt.getText(_columnIndexOfProfilePictureUrl)
          }
          val _tmpUserType: String
          _tmpUserType = _stmt.getText(_columnIndexOfUserType)
          val _tmpVerificationStatus: VerificationStatus
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfVerificationStatus)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfVerificationStatus)
          }
          val _tmp_1: VerificationStatus? = AppDatabase.Converters.toVerificationStatus(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'com.rio.rostry.domain.model.VerificationStatus', but it was NULL.")
          } else {
            _tmpVerificationStatus = _tmp_1
          }
          val _tmpFarmAddressLine1: String?
          if (_stmt.isNull(_columnIndexOfFarmAddressLine1)) {
            _tmpFarmAddressLine1 = null
          } else {
            _tmpFarmAddressLine1 = _stmt.getText(_columnIndexOfFarmAddressLine1)
          }
          val _tmpFarmAddressLine2: String?
          if (_stmt.isNull(_columnIndexOfFarmAddressLine2)) {
            _tmpFarmAddressLine2 = null
          } else {
            _tmpFarmAddressLine2 = _stmt.getText(_columnIndexOfFarmAddressLine2)
          }
          val _tmpFarmCity: String?
          if (_stmt.isNull(_columnIndexOfFarmCity)) {
            _tmpFarmCity = null
          } else {
            _tmpFarmCity = _stmt.getText(_columnIndexOfFarmCity)
          }
          val _tmpFarmState: String?
          if (_stmt.isNull(_columnIndexOfFarmState)) {
            _tmpFarmState = null
          } else {
            _tmpFarmState = _stmt.getText(_columnIndexOfFarmState)
          }
          val _tmpFarmPostalCode: String?
          if (_stmt.isNull(_columnIndexOfFarmPostalCode)) {
            _tmpFarmPostalCode = null
          } else {
            _tmpFarmPostalCode = _stmt.getText(_columnIndexOfFarmPostalCode)
          }
          val _tmpFarmCountry: String?
          if (_stmt.isNull(_columnIndexOfFarmCountry)) {
            _tmpFarmCountry = null
          } else {
            _tmpFarmCountry = _stmt.getText(_columnIndexOfFarmCountry)
          }
          val _tmpFarmLocationLat: Double?
          if (_stmt.isNull(_columnIndexOfFarmLocationLat)) {
            _tmpFarmLocationLat = null
          } else {
            _tmpFarmLocationLat = _stmt.getDouble(_columnIndexOfFarmLocationLat)
          }
          val _tmpFarmLocationLng: Double?
          if (_stmt.isNull(_columnIndexOfFarmLocationLng)) {
            _tmpFarmLocationLng = null
          } else {
            _tmpFarmLocationLng = _stmt.getDouble(_columnIndexOfFarmLocationLng)
          }
          val _tmpLocationVerified: Boolean?
          val _tmp_2: Int?
          if (_stmt.isNull(_columnIndexOfLocationVerified)) {
            _tmp_2 = null
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfLocationVerified).toInt()
          }
          _tmpLocationVerified = _tmp_2?.let { it != 0 }
          val _tmpKycLevel: Int?
          if (_stmt.isNull(_columnIndexOfKycLevel)) {
            _tmpKycLevel = null
          } else {
            _tmpKycLevel = _stmt.getLong(_columnIndexOfKycLevel).toInt()
          }
          val _tmpChickenCount: Int?
          if (_stmt.isNull(_columnIndexOfChickenCount)) {
            _tmpChickenCount = null
          } else {
            _tmpChickenCount = _stmt.getLong(_columnIndexOfChickenCount).toInt()
          }
          val _tmpFarmerType: String?
          if (_stmt.isNull(_columnIndexOfFarmerType)) {
            _tmpFarmerType = null
          } else {
            _tmpFarmerType = _stmt.getText(_columnIndexOfFarmerType)
          }
          val _tmpRaisingSince: Long?
          if (_stmt.isNull(_columnIndexOfRaisingSince)) {
            _tmpRaisingSince = null
          } else {
            _tmpRaisingSince = _stmt.getLong(_columnIndexOfRaisingSince)
          }
          val _tmpFavoriteBreed: String?
          if (_stmt.isNull(_columnIndexOfFavoriteBreed)) {
            _tmpFavoriteBreed = null
          } else {
            _tmpFavoriteBreed = _stmt.getText(_columnIndexOfFavoriteBreed)
          }
          val _tmpKycVerifiedAt: Long?
          if (_stmt.isNull(_columnIndexOfKycVerifiedAt)) {
            _tmpKycVerifiedAt = null
          } else {
            _tmpKycVerifiedAt = _stmt.getLong(_columnIndexOfKycVerifiedAt)
          }
          val _tmpKycRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfKycRejectionReason)) {
            _tmpKycRejectionReason = null
          } else {
            _tmpKycRejectionReason = _stmt.getText(_columnIndexOfKycRejectionReason)
          }
          val _tmpVerificationRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfVerificationRejectionReason)) {
            _tmpVerificationRejectionReason = null
          } else {
            _tmpVerificationRejectionReason =
                _stmt.getText(_columnIndexOfVerificationRejectionReason)
          }
          val _tmpLatestVerificationId: String?
          if (_stmt.isNull(_columnIndexOfLatestVerificationId)) {
            _tmpLatestVerificationId = null
          } else {
            _tmpLatestVerificationId = _stmt.getText(_columnIndexOfLatestVerificationId)
          }
          val _tmpLatestVerificationRef: String?
          if (_stmt.isNull(_columnIndexOfLatestVerificationRef)) {
            _tmpLatestVerificationRef = null
          } else {
            _tmpLatestVerificationRef = _stmt.getText(_columnIndexOfLatestVerificationRef)
          }
          val _tmpVerificationSubmittedAt: Date?
          val _tmp_3: Long?
          if (_stmt.isNull(_columnIndexOfVerificationSubmittedAt)) {
            _tmp_3 = null
          } else {
            _tmp_3 = _stmt.getLong(_columnIndexOfVerificationSubmittedAt)
          }
          _tmpVerificationSubmittedAt = __dateLongConverter.fromTimestamp(_tmp_3)
          val _tmpShowcaseCount: Int
          _tmpShowcaseCount = _stmt.getLong(_columnIndexOfShowcaseCount).toInt()
          val _tmpMaxShowcaseSlots: Int
          _tmpMaxShowcaseSlots = _stmt.getLong(_columnIndexOfMaxShowcaseSlots).toInt()
          val _tmpCreatedAt: Date?
          val _tmp_4: Long?
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfCreatedAt)
          }
          _tmpCreatedAt = __dateLongConverter.fromTimestamp(_tmp_4)
          val _tmpUpdatedAt: Date?
          val _tmp_5: Long?
          if (_stmt.isNull(_columnIndexOfUpdatedAt)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfUpdatedAt)
          }
          _tmpUpdatedAt = __dateLongConverter.fromTimestamp(_tmp_5)
          val _tmpCustomClaimsUpdatedAt: Date?
          val _tmp_6: Long?
          if (_stmt.isNull(_columnIndexOfCustomClaimsUpdatedAt)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getLong(_columnIndexOfCustomClaimsUpdatedAt)
          }
          _tmpCustomClaimsUpdatedAt = __dateLongConverter.fromTimestamp(_tmp_6)
          val _tmpIsSuspended: Boolean
          val _tmp_7: Int
          _tmp_7 = _stmt.getLong(_columnIndexOfIsSuspended).toInt()
          _tmpIsSuspended = _tmp_7 != 0
          val _tmpSuspensionReason: String?
          if (_stmt.isNull(_columnIndexOfSuspensionReason)) {
            _tmpSuspensionReason = null
          } else {
            _tmpSuspensionReason = _stmt.getText(_columnIndexOfSuspensionReason)
          }
          val _tmpSuspensionEndsAt: Long?
          if (_stmt.isNull(_columnIndexOfSuspensionEndsAt)) {
            _tmpSuspensionEndsAt = null
          } else {
            _tmpSuspensionEndsAt = _stmt.getLong(_columnIndexOfSuspensionEndsAt)
          }
          val _tmpNotificationsEnabled: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfNotificationsEnabled).toInt()
          _tmpNotificationsEnabled = _tmp_8 != 0
          val _tmpFarmAlertsEnabled: Boolean
          val _tmp_9: Int
          _tmp_9 = _stmt.getLong(_columnIndexOfFarmAlertsEnabled).toInt()
          _tmpFarmAlertsEnabled = _tmp_9 != 0
          val _tmpTransferAlertsEnabled: Boolean
          val _tmp_10: Int
          _tmp_10 = _stmt.getLong(_columnIndexOfTransferAlertsEnabled).toInt()
          _tmpTransferAlertsEnabled = _tmp_10 != 0
          val _tmpSocialAlertsEnabled: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfSocialAlertsEnabled).toInt()
          _tmpSocialAlertsEnabled = _tmp_11 != 0
          _result =
              UserEntity(_tmpUserId,_tmpPhoneNumber,_tmpEmail,_tmpFullName,_tmpAddress,_tmpBio,_tmpProfilePictureUrl,_tmpUserType,_tmpVerificationStatus,_tmpFarmAddressLine1,_tmpFarmAddressLine2,_tmpFarmCity,_tmpFarmState,_tmpFarmPostalCode,_tmpFarmCountry,_tmpFarmLocationLat,_tmpFarmLocationLng,_tmpLocationVerified,_tmpKycLevel,_tmpChickenCount,_tmpFarmerType,_tmpRaisingSince,_tmpFavoriteBreed,_tmpKycVerifiedAt,_tmpKycRejectionReason,_tmpVerificationRejectionReason,_tmpLatestVerificationId,_tmpLatestVerificationRef,_tmpVerificationSubmittedAt,_tmpShowcaseCount,_tmpMaxShowcaseSlots,_tmpCreatedAt,_tmpUpdatedAt,_tmpCustomClaimsUpdatedAt,_tmpIsSuspended,_tmpSuspensionReason,_tmpSuspensionEndsAt,_tmpNotificationsEnabled,_tmpFarmAlertsEnabled,_tmpTransferAlertsEnabled,_tmpSocialAlertsEnabled)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun findById(userId: String): UserEntity? {
    val _sql: String = "SELECT * FROM users WHERE userId = ? LIMIT 1"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfPhoneNumber: Int = getColumnIndexOrThrow(_stmt, "phoneNumber")
        val _columnIndexOfEmail: Int = getColumnIndexOrThrow(_stmt, "email")
        val _columnIndexOfFullName: Int = getColumnIndexOrThrow(_stmt, "fullName")
        val _columnIndexOfAddress: Int = getColumnIndexOrThrow(_stmt, "address")
        val _columnIndexOfBio: Int = getColumnIndexOrThrow(_stmt, "bio")
        val _columnIndexOfProfilePictureUrl: Int = getColumnIndexOrThrow(_stmt, "profilePictureUrl")
        val _columnIndexOfUserType: Int = getColumnIndexOrThrow(_stmt, "userType")
        val _columnIndexOfVerificationStatus: Int = getColumnIndexOrThrow(_stmt,
            "verificationStatus")
        val _columnIndexOfFarmAddressLine1: Int = getColumnIndexOrThrow(_stmt, "farmAddressLine1")
        val _columnIndexOfFarmAddressLine2: Int = getColumnIndexOrThrow(_stmt, "farmAddressLine2")
        val _columnIndexOfFarmCity: Int = getColumnIndexOrThrow(_stmt, "farmCity")
        val _columnIndexOfFarmState: Int = getColumnIndexOrThrow(_stmt, "farmState")
        val _columnIndexOfFarmPostalCode: Int = getColumnIndexOrThrow(_stmt, "farmPostalCode")
        val _columnIndexOfFarmCountry: Int = getColumnIndexOrThrow(_stmt, "farmCountry")
        val _columnIndexOfFarmLocationLat: Int = getColumnIndexOrThrow(_stmt, "farmLocationLat")
        val _columnIndexOfFarmLocationLng: Int = getColumnIndexOrThrow(_stmt, "farmLocationLng")
        val _columnIndexOfLocationVerified: Int = getColumnIndexOrThrow(_stmt, "locationVerified")
        val _columnIndexOfKycLevel: Int = getColumnIndexOrThrow(_stmt, "kycLevel")
        val _columnIndexOfChickenCount: Int = getColumnIndexOrThrow(_stmt, "chickenCount")
        val _columnIndexOfFarmerType: Int = getColumnIndexOrThrow(_stmt, "farmerType")
        val _columnIndexOfRaisingSince: Int = getColumnIndexOrThrow(_stmt, "raisingSince")
        val _columnIndexOfFavoriteBreed: Int = getColumnIndexOrThrow(_stmt, "favoriteBreed")
        val _columnIndexOfKycVerifiedAt: Int = getColumnIndexOrThrow(_stmt, "kycVerifiedAt")
        val _columnIndexOfKycRejectionReason: Int = getColumnIndexOrThrow(_stmt,
            "kycRejectionReason")
        val _columnIndexOfVerificationRejectionReason: Int = getColumnIndexOrThrow(_stmt,
            "verificationRejectionReason")
        val _columnIndexOfLatestVerificationId: Int = getColumnIndexOrThrow(_stmt,
            "latestVerificationId")
        val _columnIndexOfLatestVerificationRef: Int = getColumnIndexOrThrow(_stmt,
            "latestVerificationRef")
        val _columnIndexOfVerificationSubmittedAt: Int = getColumnIndexOrThrow(_stmt,
            "verificationSubmittedAt")
        val _columnIndexOfShowcaseCount: Int = getColumnIndexOrThrow(_stmt, "showcaseCount")
        val _columnIndexOfMaxShowcaseSlots: Int = getColumnIndexOrThrow(_stmt, "maxShowcaseSlots")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfCustomClaimsUpdatedAt: Int = getColumnIndexOrThrow(_stmt,
            "customClaimsUpdatedAt")
        val _columnIndexOfIsSuspended: Int = getColumnIndexOrThrow(_stmt, "isSuspended")
        val _columnIndexOfSuspensionReason: Int = getColumnIndexOrThrow(_stmt, "suspensionReason")
        val _columnIndexOfSuspensionEndsAt: Int = getColumnIndexOrThrow(_stmt, "suspensionEndsAt")
        val _columnIndexOfNotificationsEnabled: Int = getColumnIndexOrThrow(_stmt,
            "notificationsEnabled")
        val _columnIndexOfFarmAlertsEnabled: Int = getColumnIndexOrThrow(_stmt, "farmAlertsEnabled")
        val _columnIndexOfTransferAlertsEnabled: Int = getColumnIndexOrThrow(_stmt,
            "transferAlertsEnabled")
        val _columnIndexOfSocialAlertsEnabled: Int = getColumnIndexOrThrow(_stmt,
            "socialAlertsEnabled")
        val _result: UserEntity?
        if (_stmt.step()) {
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpPhoneNumber: String?
          if (_stmt.isNull(_columnIndexOfPhoneNumber)) {
            _tmpPhoneNumber = null
          } else {
            _tmpPhoneNumber = _stmt.getText(_columnIndexOfPhoneNumber)
          }
          val _tmpEmail: String?
          if (_stmt.isNull(_columnIndexOfEmail)) {
            _tmpEmail = null
          } else {
            _tmpEmail = _stmt.getText(_columnIndexOfEmail)
          }
          val _tmpFullName: String?
          if (_stmt.isNull(_columnIndexOfFullName)) {
            _tmpFullName = null
          } else {
            _tmpFullName = _stmt.getText(_columnIndexOfFullName)
          }
          val _tmpAddress: String?
          if (_stmt.isNull(_columnIndexOfAddress)) {
            _tmpAddress = null
          } else {
            _tmpAddress = _stmt.getText(_columnIndexOfAddress)
          }
          val _tmpBio: String?
          if (_stmt.isNull(_columnIndexOfBio)) {
            _tmpBio = null
          } else {
            _tmpBio = _stmt.getText(_columnIndexOfBio)
          }
          val _tmpProfilePictureUrl: String?
          if (_stmt.isNull(_columnIndexOfProfilePictureUrl)) {
            _tmpProfilePictureUrl = null
          } else {
            _tmpProfilePictureUrl = _stmt.getText(_columnIndexOfProfilePictureUrl)
          }
          val _tmpUserType: String
          _tmpUserType = _stmt.getText(_columnIndexOfUserType)
          val _tmpVerificationStatus: VerificationStatus
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfVerificationStatus)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfVerificationStatus)
          }
          val _tmp_1: VerificationStatus? = AppDatabase.Converters.toVerificationStatus(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'com.rio.rostry.domain.model.VerificationStatus', but it was NULL.")
          } else {
            _tmpVerificationStatus = _tmp_1
          }
          val _tmpFarmAddressLine1: String?
          if (_stmt.isNull(_columnIndexOfFarmAddressLine1)) {
            _tmpFarmAddressLine1 = null
          } else {
            _tmpFarmAddressLine1 = _stmt.getText(_columnIndexOfFarmAddressLine1)
          }
          val _tmpFarmAddressLine2: String?
          if (_stmt.isNull(_columnIndexOfFarmAddressLine2)) {
            _tmpFarmAddressLine2 = null
          } else {
            _tmpFarmAddressLine2 = _stmt.getText(_columnIndexOfFarmAddressLine2)
          }
          val _tmpFarmCity: String?
          if (_stmt.isNull(_columnIndexOfFarmCity)) {
            _tmpFarmCity = null
          } else {
            _tmpFarmCity = _stmt.getText(_columnIndexOfFarmCity)
          }
          val _tmpFarmState: String?
          if (_stmt.isNull(_columnIndexOfFarmState)) {
            _tmpFarmState = null
          } else {
            _tmpFarmState = _stmt.getText(_columnIndexOfFarmState)
          }
          val _tmpFarmPostalCode: String?
          if (_stmt.isNull(_columnIndexOfFarmPostalCode)) {
            _tmpFarmPostalCode = null
          } else {
            _tmpFarmPostalCode = _stmt.getText(_columnIndexOfFarmPostalCode)
          }
          val _tmpFarmCountry: String?
          if (_stmt.isNull(_columnIndexOfFarmCountry)) {
            _tmpFarmCountry = null
          } else {
            _tmpFarmCountry = _stmt.getText(_columnIndexOfFarmCountry)
          }
          val _tmpFarmLocationLat: Double?
          if (_stmt.isNull(_columnIndexOfFarmLocationLat)) {
            _tmpFarmLocationLat = null
          } else {
            _tmpFarmLocationLat = _stmt.getDouble(_columnIndexOfFarmLocationLat)
          }
          val _tmpFarmLocationLng: Double?
          if (_stmt.isNull(_columnIndexOfFarmLocationLng)) {
            _tmpFarmLocationLng = null
          } else {
            _tmpFarmLocationLng = _stmt.getDouble(_columnIndexOfFarmLocationLng)
          }
          val _tmpLocationVerified: Boolean?
          val _tmp_2: Int?
          if (_stmt.isNull(_columnIndexOfLocationVerified)) {
            _tmp_2 = null
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfLocationVerified).toInt()
          }
          _tmpLocationVerified = _tmp_2?.let { it != 0 }
          val _tmpKycLevel: Int?
          if (_stmt.isNull(_columnIndexOfKycLevel)) {
            _tmpKycLevel = null
          } else {
            _tmpKycLevel = _stmt.getLong(_columnIndexOfKycLevel).toInt()
          }
          val _tmpChickenCount: Int?
          if (_stmt.isNull(_columnIndexOfChickenCount)) {
            _tmpChickenCount = null
          } else {
            _tmpChickenCount = _stmt.getLong(_columnIndexOfChickenCount).toInt()
          }
          val _tmpFarmerType: String?
          if (_stmt.isNull(_columnIndexOfFarmerType)) {
            _tmpFarmerType = null
          } else {
            _tmpFarmerType = _stmt.getText(_columnIndexOfFarmerType)
          }
          val _tmpRaisingSince: Long?
          if (_stmt.isNull(_columnIndexOfRaisingSince)) {
            _tmpRaisingSince = null
          } else {
            _tmpRaisingSince = _stmt.getLong(_columnIndexOfRaisingSince)
          }
          val _tmpFavoriteBreed: String?
          if (_stmt.isNull(_columnIndexOfFavoriteBreed)) {
            _tmpFavoriteBreed = null
          } else {
            _tmpFavoriteBreed = _stmt.getText(_columnIndexOfFavoriteBreed)
          }
          val _tmpKycVerifiedAt: Long?
          if (_stmt.isNull(_columnIndexOfKycVerifiedAt)) {
            _tmpKycVerifiedAt = null
          } else {
            _tmpKycVerifiedAt = _stmt.getLong(_columnIndexOfKycVerifiedAt)
          }
          val _tmpKycRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfKycRejectionReason)) {
            _tmpKycRejectionReason = null
          } else {
            _tmpKycRejectionReason = _stmt.getText(_columnIndexOfKycRejectionReason)
          }
          val _tmpVerificationRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfVerificationRejectionReason)) {
            _tmpVerificationRejectionReason = null
          } else {
            _tmpVerificationRejectionReason =
                _stmt.getText(_columnIndexOfVerificationRejectionReason)
          }
          val _tmpLatestVerificationId: String?
          if (_stmt.isNull(_columnIndexOfLatestVerificationId)) {
            _tmpLatestVerificationId = null
          } else {
            _tmpLatestVerificationId = _stmt.getText(_columnIndexOfLatestVerificationId)
          }
          val _tmpLatestVerificationRef: String?
          if (_stmt.isNull(_columnIndexOfLatestVerificationRef)) {
            _tmpLatestVerificationRef = null
          } else {
            _tmpLatestVerificationRef = _stmt.getText(_columnIndexOfLatestVerificationRef)
          }
          val _tmpVerificationSubmittedAt: Date?
          val _tmp_3: Long?
          if (_stmt.isNull(_columnIndexOfVerificationSubmittedAt)) {
            _tmp_3 = null
          } else {
            _tmp_3 = _stmt.getLong(_columnIndexOfVerificationSubmittedAt)
          }
          _tmpVerificationSubmittedAt = __dateLongConverter.fromTimestamp(_tmp_3)
          val _tmpShowcaseCount: Int
          _tmpShowcaseCount = _stmt.getLong(_columnIndexOfShowcaseCount).toInt()
          val _tmpMaxShowcaseSlots: Int
          _tmpMaxShowcaseSlots = _stmt.getLong(_columnIndexOfMaxShowcaseSlots).toInt()
          val _tmpCreatedAt: Date?
          val _tmp_4: Long?
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfCreatedAt)
          }
          _tmpCreatedAt = __dateLongConverter.fromTimestamp(_tmp_4)
          val _tmpUpdatedAt: Date?
          val _tmp_5: Long?
          if (_stmt.isNull(_columnIndexOfUpdatedAt)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfUpdatedAt)
          }
          _tmpUpdatedAt = __dateLongConverter.fromTimestamp(_tmp_5)
          val _tmpCustomClaimsUpdatedAt: Date?
          val _tmp_6: Long?
          if (_stmt.isNull(_columnIndexOfCustomClaimsUpdatedAt)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getLong(_columnIndexOfCustomClaimsUpdatedAt)
          }
          _tmpCustomClaimsUpdatedAt = __dateLongConverter.fromTimestamp(_tmp_6)
          val _tmpIsSuspended: Boolean
          val _tmp_7: Int
          _tmp_7 = _stmt.getLong(_columnIndexOfIsSuspended).toInt()
          _tmpIsSuspended = _tmp_7 != 0
          val _tmpSuspensionReason: String?
          if (_stmt.isNull(_columnIndexOfSuspensionReason)) {
            _tmpSuspensionReason = null
          } else {
            _tmpSuspensionReason = _stmt.getText(_columnIndexOfSuspensionReason)
          }
          val _tmpSuspensionEndsAt: Long?
          if (_stmt.isNull(_columnIndexOfSuspensionEndsAt)) {
            _tmpSuspensionEndsAt = null
          } else {
            _tmpSuspensionEndsAt = _stmt.getLong(_columnIndexOfSuspensionEndsAt)
          }
          val _tmpNotificationsEnabled: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfNotificationsEnabled).toInt()
          _tmpNotificationsEnabled = _tmp_8 != 0
          val _tmpFarmAlertsEnabled: Boolean
          val _tmp_9: Int
          _tmp_9 = _stmt.getLong(_columnIndexOfFarmAlertsEnabled).toInt()
          _tmpFarmAlertsEnabled = _tmp_9 != 0
          val _tmpTransferAlertsEnabled: Boolean
          val _tmp_10: Int
          _tmp_10 = _stmt.getLong(_columnIndexOfTransferAlertsEnabled).toInt()
          _tmpTransferAlertsEnabled = _tmp_10 != 0
          val _tmpSocialAlertsEnabled: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfSocialAlertsEnabled).toInt()
          _tmpSocialAlertsEnabled = _tmp_11 != 0
          _result =
              UserEntity(_tmpUserId,_tmpPhoneNumber,_tmpEmail,_tmpFullName,_tmpAddress,_tmpBio,_tmpProfilePictureUrl,_tmpUserType,_tmpVerificationStatus,_tmpFarmAddressLine1,_tmpFarmAddressLine2,_tmpFarmCity,_tmpFarmState,_tmpFarmPostalCode,_tmpFarmCountry,_tmpFarmLocationLat,_tmpFarmLocationLng,_tmpLocationVerified,_tmpKycLevel,_tmpChickenCount,_tmpFarmerType,_tmpRaisingSince,_tmpFavoriteBreed,_tmpKycVerifiedAt,_tmpKycRejectionReason,_tmpVerificationRejectionReason,_tmpLatestVerificationId,_tmpLatestVerificationRef,_tmpVerificationSubmittedAt,_tmpShowcaseCount,_tmpMaxShowcaseSlots,_tmpCreatedAt,_tmpUpdatedAt,_tmpCustomClaimsUpdatedAt,_tmpIsSuspended,_tmpSuspensionReason,_tmpSuspensionEndsAt,_tmpNotificationsEnabled,_tmpFarmAlertsEnabled,_tmpTransferAlertsEnabled,_tmpSocialAlertsEnabled)
        } else {
          _result = null
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getUsersByIds(ids: List<String>): List<UserEntity> {
    val _stringBuilder: StringBuilder = StringBuilder()
    _stringBuilder.append("SELECT * FROM users WHERE userId IN (")
    val _inputSize: Int = ids.size
    appendPlaceholders(_stringBuilder, _inputSize)
    _stringBuilder.append(")")
    val _sql: String = _stringBuilder.toString()
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        for (_item: String in ids) {
          _stmt.bindText(_argIndex, _item)
          _argIndex++
        }
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfPhoneNumber: Int = getColumnIndexOrThrow(_stmt, "phoneNumber")
        val _columnIndexOfEmail: Int = getColumnIndexOrThrow(_stmt, "email")
        val _columnIndexOfFullName: Int = getColumnIndexOrThrow(_stmt, "fullName")
        val _columnIndexOfAddress: Int = getColumnIndexOrThrow(_stmt, "address")
        val _columnIndexOfBio: Int = getColumnIndexOrThrow(_stmt, "bio")
        val _columnIndexOfProfilePictureUrl: Int = getColumnIndexOrThrow(_stmt, "profilePictureUrl")
        val _columnIndexOfUserType: Int = getColumnIndexOrThrow(_stmt, "userType")
        val _columnIndexOfVerificationStatus: Int = getColumnIndexOrThrow(_stmt,
            "verificationStatus")
        val _columnIndexOfFarmAddressLine1: Int = getColumnIndexOrThrow(_stmt, "farmAddressLine1")
        val _columnIndexOfFarmAddressLine2: Int = getColumnIndexOrThrow(_stmt, "farmAddressLine2")
        val _columnIndexOfFarmCity: Int = getColumnIndexOrThrow(_stmt, "farmCity")
        val _columnIndexOfFarmState: Int = getColumnIndexOrThrow(_stmt, "farmState")
        val _columnIndexOfFarmPostalCode: Int = getColumnIndexOrThrow(_stmt, "farmPostalCode")
        val _columnIndexOfFarmCountry: Int = getColumnIndexOrThrow(_stmt, "farmCountry")
        val _columnIndexOfFarmLocationLat: Int = getColumnIndexOrThrow(_stmt, "farmLocationLat")
        val _columnIndexOfFarmLocationLng: Int = getColumnIndexOrThrow(_stmt, "farmLocationLng")
        val _columnIndexOfLocationVerified: Int = getColumnIndexOrThrow(_stmt, "locationVerified")
        val _columnIndexOfKycLevel: Int = getColumnIndexOrThrow(_stmt, "kycLevel")
        val _columnIndexOfChickenCount: Int = getColumnIndexOrThrow(_stmt, "chickenCount")
        val _columnIndexOfFarmerType: Int = getColumnIndexOrThrow(_stmt, "farmerType")
        val _columnIndexOfRaisingSince: Int = getColumnIndexOrThrow(_stmt, "raisingSince")
        val _columnIndexOfFavoriteBreed: Int = getColumnIndexOrThrow(_stmt, "favoriteBreed")
        val _columnIndexOfKycVerifiedAt: Int = getColumnIndexOrThrow(_stmt, "kycVerifiedAt")
        val _columnIndexOfKycRejectionReason: Int = getColumnIndexOrThrow(_stmt,
            "kycRejectionReason")
        val _columnIndexOfVerificationRejectionReason: Int = getColumnIndexOrThrow(_stmt,
            "verificationRejectionReason")
        val _columnIndexOfLatestVerificationId: Int = getColumnIndexOrThrow(_stmt,
            "latestVerificationId")
        val _columnIndexOfLatestVerificationRef: Int = getColumnIndexOrThrow(_stmt,
            "latestVerificationRef")
        val _columnIndexOfVerificationSubmittedAt: Int = getColumnIndexOrThrow(_stmt,
            "verificationSubmittedAt")
        val _columnIndexOfShowcaseCount: Int = getColumnIndexOrThrow(_stmt, "showcaseCount")
        val _columnIndexOfMaxShowcaseSlots: Int = getColumnIndexOrThrow(_stmt, "maxShowcaseSlots")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfCustomClaimsUpdatedAt: Int = getColumnIndexOrThrow(_stmt,
            "customClaimsUpdatedAt")
        val _columnIndexOfIsSuspended: Int = getColumnIndexOrThrow(_stmt, "isSuspended")
        val _columnIndexOfSuspensionReason: Int = getColumnIndexOrThrow(_stmt, "suspensionReason")
        val _columnIndexOfSuspensionEndsAt: Int = getColumnIndexOrThrow(_stmt, "suspensionEndsAt")
        val _columnIndexOfNotificationsEnabled: Int = getColumnIndexOrThrow(_stmt,
            "notificationsEnabled")
        val _columnIndexOfFarmAlertsEnabled: Int = getColumnIndexOrThrow(_stmt, "farmAlertsEnabled")
        val _columnIndexOfTransferAlertsEnabled: Int = getColumnIndexOrThrow(_stmt,
            "transferAlertsEnabled")
        val _columnIndexOfSocialAlertsEnabled: Int = getColumnIndexOrThrow(_stmt,
            "socialAlertsEnabled")
        val _result: MutableList<UserEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item_1: UserEntity
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpPhoneNumber: String?
          if (_stmt.isNull(_columnIndexOfPhoneNumber)) {
            _tmpPhoneNumber = null
          } else {
            _tmpPhoneNumber = _stmt.getText(_columnIndexOfPhoneNumber)
          }
          val _tmpEmail: String?
          if (_stmt.isNull(_columnIndexOfEmail)) {
            _tmpEmail = null
          } else {
            _tmpEmail = _stmt.getText(_columnIndexOfEmail)
          }
          val _tmpFullName: String?
          if (_stmt.isNull(_columnIndexOfFullName)) {
            _tmpFullName = null
          } else {
            _tmpFullName = _stmt.getText(_columnIndexOfFullName)
          }
          val _tmpAddress: String?
          if (_stmt.isNull(_columnIndexOfAddress)) {
            _tmpAddress = null
          } else {
            _tmpAddress = _stmt.getText(_columnIndexOfAddress)
          }
          val _tmpBio: String?
          if (_stmt.isNull(_columnIndexOfBio)) {
            _tmpBio = null
          } else {
            _tmpBio = _stmt.getText(_columnIndexOfBio)
          }
          val _tmpProfilePictureUrl: String?
          if (_stmt.isNull(_columnIndexOfProfilePictureUrl)) {
            _tmpProfilePictureUrl = null
          } else {
            _tmpProfilePictureUrl = _stmt.getText(_columnIndexOfProfilePictureUrl)
          }
          val _tmpUserType: String
          _tmpUserType = _stmt.getText(_columnIndexOfUserType)
          val _tmpVerificationStatus: VerificationStatus
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfVerificationStatus)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfVerificationStatus)
          }
          val _tmp_1: VerificationStatus? = AppDatabase.Converters.toVerificationStatus(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'com.rio.rostry.domain.model.VerificationStatus', but it was NULL.")
          } else {
            _tmpVerificationStatus = _tmp_1
          }
          val _tmpFarmAddressLine1: String?
          if (_stmt.isNull(_columnIndexOfFarmAddressLine1)) {
            _tmpFarmAddressLine1 = null
          } else {
            _tmpFarmAddressLine1 = _stmt.getText(_columnIndexOfFarmAddressLine1)
          }
          val _tmpFarmAddressLine2: String?
          if (_stmt.isNull(_columnIndexOfFarmAddressLine2)) {
            _tmpFarmAddressLine2 = null
          } else {
            _tmpFarmAddressLine2 = _stmt.getText(_columnIndexOfFarmAddressLine2)
          }
          val _tmpFarmCity: String?
          if (_stmt.isNull(_columnIndexOfFarmCity)) {
            _tmpFarmCity = null
          } else {
            _tmpFarmCity = _stmt.getText(_columnIndexOfFarmCity)
          }
          val _tmpFarmState: String?
          if (_stmt.isNull(_columnIndexOfFarmState)) {
            _tmpFarmState = null
          } else {
            _tmpFarmState = _stmt.getText(_columnIndexOfFarmState)
          }
          val _tmpFarmPostalCode: String?
          if (_stmt.isNull(_columnIndexOfFarmPostalCode)) {
            _tmpFarmPostalCode = null
          } else {
            _tmpFarmPostalCode = _stmt.getText(_columnIndexOfFarmPostalCode)
          }
          val _tmpFarmCountry: String?
          if (_stmt.isNull(_columnIndexOfFarmCountry)) {
            _tmpFarmCountry = null
          } else {
            _tmpFarmCountry = _stmt.getText(_columnIndexOfFarmCountry)
          }
          val _tmpFarmLocationLat: Double?
          if (_stmt.isNull(_columnIndexOfFarmLocationLat)) {
            _tmpFarmLocationLat = null
          } else {
            _tmpFarmLocationLat = _stmt.getDouble(_columnIndexOfFarmLocationLat)
          }
          val _tmpFarmLocationLng: Double?
          if (_stmt.isNull(_columnIndexOfFarmLocationLng)) {
            _tmpFarmLocationLng = null
          } else {
            _tmpFarmLocationLng = _stmt.getDouble(_columnIndexOfFarmLocationLng)
          }
          val _tmpLocationVerified: Boolean?
          val _tmp_2: Int?
          if (_stmt.isNull(_columnIndexOfLocationVerified)) {
            _tmp_2 = null
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfLocationVerified).toInt()
          }
          _tmpLocationVerified = _tmp_2?.let { it != 0 }
          val _tmpKycLevel: Int?
          if (_stmt.isNull(_columnIndexOfKycLevel)) {
            _tmpKycLevel = null
          } else {
            _tmpKycLevel = _stmt.getLong(_columnIndexOfKycLevel).toInt()
          }
          val _tmpChickenCount: Int?
          if (_stmt.isNull(_columnIndexOfChickenCount)) {
            _tmpChickenCount = null
          } else {
            _tmpChickenCount = _stmt.getLong(_columnIndexOfChickenCount).toInt()
          }
          val _tmpFarmerType: String?
          if (_stmt.isNull(_columnIndexOfFarmerType)) {
            _tmpFarmerType = null
          } else {
            _tmpFarmerType = _stmt.getText(_columnIndexOfFarmerType)
          }
          val _tmpRaisingSince: Long?
          if (_stmt.isNull(_columnIndexOfRaisingSince)) {
            _tmpRaisingSince = null
          } else {
            _tmpRaisingSince = _stmt.getLong(_columnIndexOfRaisingSince)
          }
          val _tmpFavoriteBreed: String?
          if (_stmt.isNull(_columnIndexOfFavoriteBreed)) {
            _tmpFavoriteBreed = null
          } else {
            _tmpFavoriteBreed = _stmt.getText(_columnIndexOfFavoriteBreed)
          }
          val _tmpKycVerifiedAt: Long?
          if (_stmt.isNull(_columnIndexOfKycVerifiedAt)) {
            _tmpKycVerifiedAt = null
          } else {
            _tmpKycVerifiedAt = _stmt.getLong(_columnIndexOfKycVerifiedAt)
          }
          val _tmpKycRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfKycRejectionReason)) {
            _tmpKycRejectionReason = null
          } else {
            _tmpKycRejectionReason = _stmt.getText(_columnIndexOfKycRejectionReason)
          }
          val _tmpVerificationRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfVerificationRejectionReason)) {
            _tmpVerificationRejectionReason = null
          } else {
            _tmpVerificationRejectionReason =
                _stmt.getText(_columnIndexOfVerificationRejectionReason)
          }
          val _tmpLatestVerificationId: String?
          if (_stmt.isNull(_columnIndexOfLatestVerificationId)) {
            _tmpLatestVerificationId = null
          } else {
            _tmpLatestVerificationId = _stmt.getText(_columnIndexOfLatestVerificationId)
          }
          val _tmpLatestVerificationRef: String?
          if (_stmt.isNull(_columnIndexOfLatestVerificationRef)) {
            _tmpLatestVerificationRef = null
          } else {
            _tmpLatestVerificationRef = _stmt.getText(_columnIndexOfLatestVerificationRef)
          }
          val _tmpVerificationSubmittedAt: Date?
          val _tmp_3: Long?
          if (_stmt.isNull(_columnIndexOfVerificationSubmittedAt)) {
            _tmp_3 = null
          } else {
            _tmp_3 = _stmt.getLong(_columnIndexOfVerificationSubmittedAt)
          }
          _tmpVerificationSubmittedAt = __dateLongConverter.fromTimestamp(_tmp_3)
          val _tmpShowcaseCount: Int
          _tmpShowcaseCount = _stmt.getLong(_columnIndexOfShowcaseCount).toInt()
          val _tmpMaxShowcaseSlots: Int
          _tmpMaxShowcaseSlots = _stmt.getLong(_columnIndexOfMaxShowcaseSlots).toInt()
          val _tmpCreatedAt: Date?
          val _tmp_4: Long?
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfCreatedAt)
          }
          _tmpCreatedAt = __dateLongConverter.fromTimestamp(_tmp_4)
          val _tmpUpdatedAt: Date?
          val _tmp_5: Long?
          if (_stmt.isNull(_columnIndexOfUpdatedAt)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfUpdatedAt)
          }
          _tmpUpdatedAt = __dateLongConverter.fromTimestamp(_tmp_5)
          val _tmpCustomClaimsUpdatedAt: Date?
          val _tmp_6: Long?
          if (_stmt.isNull(_columnIndexOfCustomClaimsUpdatedAt)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getLong(_columnIndexOfCustomClaimsUpdatedAt)
          }
          _tmpCustomClaimsUpdatedAt = __dateLongConverter.fromTimestamp(_tmp_6)
          val _tmpIsSuspended: Boolean
          val _tmp_7: Int
          _tmp_7 = _stmt.getLong(_columnIndexOfIsSuspended).toInt()
          _tmpIsSuspended = _tmp_7 != 0
          val _tmpSuspensionReason: String?
          if (_stmt.isNull(_columnIndexOfSuspensionReason)) {
            _tmpSuspensionReason = null
          } else {
            _tmpSuspensionReason = _stmt.getText(_columnIndexOfSuspensionReason)
          }
          val _tmpSuspensionEndsAt: Long?
          if (_stmt.isNull(_columnIndexOfSuspensionEndsAt)) {
            _tmpSuspensionEndsAt = null
          } else {
            _tmpSuspensionEndsAt = _stmt.getLong(_columnIndexOfSuspensionEndsAt)
          }
          val _tmpNotificationsEnabled: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfNotificationsEnabled).toInt()
          _tmpNotificationsEnabled = _tmp_8 != 0
          val _tmpFarmAlertsEnabled: Boolean
          val _tmp_9: Int
          _tmp_9 = _stmt.getLong(_columnIndexOfFarmAlertsEnabled).toInt()
          _tmpFarmAlertsEnabled = _tmp_9 != 0
          val _tmpTransferAlertsEnabled: Boolean
          val _tmp_10: Int
          _tmp_10 = _stmt.getLong(_columnIndexOfTransferAlertsEnabled).toInt()
          _tmpTransferAlertsEnabled = _tmp_10 != 0
          val _tmpSocialAlertsEnabled: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfSocialAlertsEnabled).toInt()
          _tmpSocialAlertsEnabled = _tmp_11 != 0
          _item_1 =
              UserEntity(_tmpUserId,_tmpPhoneNumber,_tmpEmail,_tmpFullName,_tmpAddress,_tmpBio,_tmpProfilePictureUrl,_tmpUserType,_tmpVerificationStatus,_tmpFarmAddressLine1,_tmpFarmAddressLine2,_tmpFarmCity,_tmpFarmState,_tmpFarmPostalCode,_tmpFarmCountry,_tmpFarmLocationLat,_tmpFarmLocationLng,_tmpLocationVerified,_tmpKycLevel,_tmpChickenCount,_tmpFarmerType,_tmpRaisingSince,_tmpFavoriteBreed,_tmpKycVerifiedAt,_tmpKycRejectionReason,_tmpVerificationRejectionReason,_tmpLatestVerificationId,_tmpLatestVerificationRef,_tmpVerificationSubmittedAt,_tmpShowcaseCount,_tmpMaxShowcaseSlots,_tmpCreatedAt,_tmpUpdatedAt,_tmpCustomClaimsUpdatedAt,_tmpIsSuspended,_tmpSuspensionReason,_tmpSuspensionEndsAt,_tmpNotificationsEnabled,_tmpFarmAlertsEnabled,_tmpTransferAlertsEnabled,_tmpSocialAlertsEnabled)
          _result.add(_item_1)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun getAllUsers(): Flow<List<UserEntity>> {
    val _sql: String = "SELECT * FROM users ORDER BY fullName ASC"
    return createFlow(__db, false, arrayOf("users")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfPhoneNumber: Int = getColumnIndexOrThrow(_stmt, "phoneNumber")
        val _columnIndexOfEmail: Int = getColumnIndexOrThrow(_stmt, "email")
        val _columnIndexOfFullName: Int = getColumnIndexOrThrow(_stmt, "fullName")
        val _columnIndexOfAddress: Int = getColumnIndexOrThrow(_stmt, "address")
        val _columnIndexOfBio: Int = getColumnIndexOrThrow(_stmt, "bio")
        val _columnIndexOfProfilePictureUrl: Int = getColumnIndexOrThrow(_stmt, "profilePictureUrl")
        val _columnIndexOfUserType: Int = getColumnIndexOrThrow(_stmt, "userType")
        val _columnIndexOfVerificationStatus: Int = getColumnIndexOrThrow(_stmt,
            "verificationStatus")
        val _columnIndexOfFarmAddressLine1: Int = getColumnIndexOrThrow(_stmt, "farmAddressLine1")
        val _columnIndexOfFarmAddressLine2: Int = getColumnIndexOrThrow(_stmt, "farmAddressLine2")
        val _columnIndexOfFarmCity: Int = getColumnIndexOrThrow(_stmt, "farmCity")
        val _columnIndexOfFarmState: Int = getColumnIndexOrThrow(_stmt, "farmState")
        val _columnIndexOfFarmPostalCode: Int = getColumnIndexOrThrow(_stmt, "farmPostalCode")
        val _columnIndexOfFarmCountry: Int = getColumnIndexOrThrow(_stmt, "farmCountry")
        val _columnIndexOfFarmLocationLat: Int = getColumnIndexOrThrow(_stmt, "farmLocationLat")
        val _columnIndexOfFarmLocationLng: Int = getColumnIndexOrThrow(_stmt, "farmLocationLng")
        val _columnIndexOfLocationVerified: Int = getColumnIndexOrThrow(_stmt, "locationVerified")
        val _columnIndexOfKycLevel: Int = getColumnIndexOrThrow(_stmt, "kycLevel")
        val _columnIndexOfChickenCount: Int = getColumnIndexOrThrow(_stmt, "chickenCount")
        val _columnIndexOfFarmerType: Int = getColumnIndexOrThrow(_stmt, "farmerType")
        val _columnIndexOfRaisingSince: Int = getColumnIndexOrThrow(_stmt, "raisingSince")
        val _columnIndexOfFavoriteBreed: Int = getColumnIndexOrThrow(_stmt, "favoriteBreed")
        val _columnIndexOfKycVerifiedAt: Int = getColumnIndexOrThrow(_stmt, "kycVerifiedAt")
        val _columnIndexOfKycRejectionReason: Int = getColumnIndexOrThrow(_stmt,
            "kycRejectionReason")
        val _columnIndexOfVerificationRejectionReason: Int = getColumnIndexOrThrow(_stmt,
            "verificationRejectionReason")
        val _columnIndexOfLatestVerificationId: Int = getColumnIndexOrThrow(_stmt,
            "latestVerificationId")
        val _columnIndexOfLatestVerificationRef: Int = getColumnIndexOrThrow(_stmt,
            "latestVerificationRef")
        val _columnIndexOfVerificationSubmittedAt: Int = getColumnIndexOrThrow(_stmt,
            "verificationSubmittedAt")
        val _columnIndexOfShowcaseCount: Int = getColumnIndexOrThrow(_stmt, "showcaseCount")
        val _columnIndexOfMaxShowcaseSlots: Int = getColumnIndexOrThrow(_stmt, "maxShowcaseSlots")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfCustomClaimsUpdatedAt: Int = getColumnIndexOrThrow(_stmt,
            "customClaimsUpdatedAt")
        val _columnIndexOfIsSuspended: Int = getColumnIndexOrThrow(_stmt, "isSuspended")
        val _columnIndexOfSuspensionReason: Int = getColumnIndexOrThrow(_stmt, "suspensionReason")
        val _columnIndexOfSuspensionEndsAt: Int = getColumnIndexOrThrow(_stmt, "suspensionEndsAt")
        val _columnIndexOfNotificationsEnabled: Int = getColumnIndexOrThrow(_stmt,
            "notificationsEnabled")
        val _columnIndexOfFarmAlertsEnabled: Int = getColumnIndexOrThrow(_stmt, "farmAlertsEnabled")
        val _columnIndexOfTransferAlertsEnabled: Int = getColumnIndexOrThrow(_stmt,
            "transferAlertsEnabled")
        val _columnIndexOfSocialAlertsEnabled: Int = getColumnIndexOrThrow(_stmt,
            "socialAlertsEnabled")
        val _result: MutableList<UserEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: UserEntity
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpPhoneNumber: String?
          if (_stmt.isNull(_columnIndexOfPhoneNumber)) {
            _tmpPhoneNumber = null
          } else {
            _tmpPhoneNumber = _stmt.getText(_columnIndexOfPhoneNumber)
          }
          val _tmpEmail: String?
          if (_stmt.isNull(_columnIndexOfEmail)) {
            _tmpEmail = null
          } else {
            _tmpEmail = _stmt.getText(_columnIndexOfEmail)
          }
          val _tmpFullName: String?
          if (_stmt.isNull(_columnIndexOfFullName)) {
            _tmpFullName = null
          } else {
            _tmpFullName = _stmt.getText(_columnIndexOfFullName)
          }
          val _tmpAddress: String?
          if (_stmt.isNull(_columnIndexOfAddress)) {
            _tmpAddress = null
          } else {
            _tmpAddress = _stmt.getText(_columnIndexOfAddress)
          }
          val _tmpBio: String?
          if (_stmt.isNull(_columnIndexOfBio)) {
            _tmpBio = null
          } else {
            _tmpBio = _stmt.getText(_columnIndexOfBio)
          }
          val _tmpProfilePictureUrl: String?
          if (_stmt.isNull(_columnIndexOfProfilePictureUrl)) {
            _tmpProfilePictureUrl = null
          } else {
            _tmpProfilePictureUrl = _stmt.getText(_columnIndexOfProfilePictureUrl)
          }
          val _tmpUserType: String
          _tmpUserType = _stmt.getText(_columnIndexOfUserType)
          val _tmpVerificationStatus: VerificationStatus
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfVerificationStatus)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfVerificationStatus)
          }
          val _tmp_1: VerificationStatus? = AppDatabase.Converters.toVerificationStatus(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'com.rio.rostry.domain.model.VerificationStatus', but it was NULL.")
          } else {
            _tmpVerificationStatus = _tmp_1
          }
          val _tmpFarmAddressLine1: String?
          if (_stmt.isNull(_columnIndexOfFarmAddressLine1)) {
            _tmpFarmAddressLine1 = null
          } else {
            _tmpFarmAddressLine1 = _stmt.getText(_columnIndexOfFarmAddressLine1)
          }
          val _tmpFarmAddressLine2: String?
          if (_stmt.isNull(_columnIndexOfFarmAddressLine2)) {
            _tmpFarmAddressLine2 = null
          } else {
            _tmpFarmAddressLine2 = _stmt.getText(_columnIndexOfFarmAddressLine2)
          }
          val _tmpFarmCity: String?
          if (_stmt.isNull(_columnIndexOfFarmCity)) {
            _tmpFarmCity = null
          } else {
            _tmpFarmCity = _stmt.getText(_columnIndexOfFarmCity)
          }
          val _tmpFarmState: String?
          if (_stmt.isNull(_columnIndexOfFarmState)) {
            _tmpFarmState = null
          } else {
            _tmpFarmState = _stmt.getText(_columnIndexOfFarmState)
          }
          val _tmpFarmPostalCode: String?
          if (_stmt.isNull(_columnIndexOfFarmPostalCode)) {
            _tmpFarmPostalCode = null
          } else {
            _tmpFarmPostalCode = _stmt.getText(_columnIndexOfFarmPostalCode)
          }
          val _tmpFarmCountry: String?
          if (_stmt.isNull(_columnIndexOfFarmCountry)) {
            _tmpFarmCountry = null
          } else {
            _tmpFarmCountry = _stmt.getText(_columnIndexOfFarmCountry)
          }
          val _tmpFarmLocationLat: Double?
          if (_stmt.isNull(_columnIndexOfFarmLocationLat)) {
            _tmpFarmLocationLat = null
          } else {
            _tmpFarmLocationLat = _stmt.getDouble(_columnIndexOfFarmLocationLat)
          }
          val _tmpFarmLocationLng: Double?
          if (_stmt.isNull(_columnIndexOfFarmLocationLng)) {
            _tmpFarmLocationLng = null
          } else {
            _tmpFarmLocationLng = _stmt.getDouble(_columnIndexOfFarmLocationLng)
          }
          val _tmpLocationVerified: Boolean?
          val _tmp_2: Int?
          if (_stmt.isNull(_columnIndexOfLocationVerified)) {
            _tmp_2 = null
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfLocationVerified).toInt()
          }
          _tmpLocationVerified = _tmp_2?.let { it != 0 }
          val _tmpKycLevel: Int?
          if (_stmt.isNull(_columnIndexOfKycLevel)) {
            _tmpKycLevel = null
          } else {
            _tmpKycLevel = _stmt.getLong(_columnIndexOfKycLevel).toInt()
          }
          val _tmpChickenCount: Int?
          if (_stmt.isNull(_columnIndexOfChickenCount)) {
            _tmpChickenCount = null
          } else {
            _tmpChickenCount = _stmt.getLong(_columnIndexOfChickenCount).toInt()
          }
          val _tmpFarmerType: String?
          if (_stmt.isNull(_columnIndexOfFarmerType)) {
            _tmpFarmerType = null
          } else {
            _tmpFarmerType = _stmt.getText(_columnIndexOfFarmerType)
          }
          val _tmpRaisingSince: Long?
          if (_stmt.isNull(_columnIndexOfRaisingSince)) {
            _tmpRaisingSince = null
          } else {
            _tmpRaisingSince = _stmt.getLong(_columnIndexOfRaisingSince)
          }
          val _tmpFavoriteBreed: String?
          if (_stmt.isNull(_columnIndexOfFavoriteBreed)) {
            _tmpFavoriteBreed = null
          } else {
            _tmpFavoriteBreed = _stmt.getText(_columnIndexOfFavoriteBreed)
          }
          val _tmpKycVerifiedAt: Long?
          if (_stmt.isNull(_columnIndexOfKycVerifiedAt)) {
            _tmpKycVerifiedAt = null
          } else {
            _tmpKycVerifiedAt = _stmt.getLong(_columnIndexOfKycVerifiedAt)
          }
          val _tmpKycRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfKycRejectionReason)) {
            _tmpKycRejectionReason = null
          } else {
            _tmpKycRejectionReason = _stmt.getText(_columnIndexOfKycRejectionReason)
          }
          val _tmpVerificationRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfVerificationRejectionReason)) {
            _tmpVerificationRejectionReason = null
          } else {
            _tmpVerificationRejectionReason =
                _stmt.getText(_columnIndexOfVerificationRejectionReason)
          }
          val _tmpLatestVerificationId: String?
          if (_stmt.isNull(_columnIndexOfLatestVerificationId)) {
            _tmpLatestVerificationId = null
          } else {
            _tmpLatestVerificationId = _stmt.getText(_columnIndexOfLatestVerificationId)
          }
          val _tmpLatestVerificationRef: String?
          if (_stmt.isNull(_columnIndexOfLatestVerificationRef)) {
            _tmpLatestVerificationRef = null
          } else {
            _tmpLatestVerificationRef = _stmt.getText(_columnIndexOfLatestVerificationRef)
          }
          val _tmpVerificationSubmittedAt: Date?
          val _tmp_3: Long?
          if (_stmt.isNull(_columnIndexOfVerificationSubmittedAt)) {
            _tmp_3 = null
          } else {
            _tmp_3 = _stmt.getLong(_columnIndexOfVerificationSubmittedAt)
          }
          _tmpVerificationSubmittedAt = __dateLongConverter.fromTimestamp(_tmp_3)
          val _tmpShowcaseCount: Int
          _tmpShowcaseCount = _stmt.getLong(_columnIndexOfShowcaseCount).toInt()
          val _tmpMaxShowcaseSlots: Int
          _tmpMaxShowcaseSlots = _stmt.getLong(_columnIndexOfMaxShowcaseSlots).toInt()
          val _tmpCreatedAt: Date?
          val _tmp_4: Long?
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfCreatedAt)
          }
          _tmpCreatedAt = __dateLongConverter.fromTimestamp(_tmp_4)
          val _tmpUpdatedAt: Date?
          val _tmp_5: Long?
          if (_stmt.isNull(_columnIndexOfUpdatedAt)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfUpdatedAt)
          }
          _tmpUpdatedAt = __dateLongConverter.fromTimestamp(_tmp_5)
          val _tmpCustomClaimsUpdatedAt: Date?
          val _tmp_6: Long?
          if (_stmt.isNull(_columnIndexOfCustomClaimsUpdatedAt)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getLong(_columnIndexOfCustomClaimsUpdatedAt)
          }
          _tmpCustomClaimsUpdatedAt = __dateLongConverter.fromTimestamp(_tmp_6)
          val _tmpIsSuspended: Boolean
          val _tmp_7: Int
          _tmp_7 = _stmt.getLong(_columnIndexOfIsSuspended).toInt()
          _tmpIsSuspended = _tmp_7 != 0
          val _tmpSuspensionReason: String?
          if (_stmt.isNull(_columnIndexOfSuspensionReason)) {
            _tmpSuspensionReason = null
          } else {
            _tmpSuspensionReason = _stmt.getText(_columnIndexOfSuspensionReason)
          }
          val _tmpSuspensionEndsAt: Long?
          if (_stmt.isNull(_columnIndexOfSuspensionEndsAt)) {
            _tmpSuspensionEndsAt = null
          } else {
            _tmpSuspensionEndsAt = _stmt.getLong(_columnIndexOfSuspensionEndsAt)
          }
          val _tmpNotificationsEnabled: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfNotificationsEnabled).toInt()
          _tmpNotificationsEnabled = _tmp_8 != 0
          val _tmpFarmAlertsEnabled: Boolean
          val _tmp_9: Int
          _tmp_9 = _stmt.getLong(_columnIndexOfFarmAlertsEnabled).toInt()
          _tmpFarmAlertsEnabled = _tmp_9 != 0
          val _tmpTransferAlertsEnabled: Boolean
          val _tmp_10: Int
          _tmp_10 = _stmt.getLong(_columnIndexOfTransferAlertsEnabled).toInt()
          _tmpTransferAlertsEnabled = _tmp_10 != 0
          val _tmpSocialAlertsEnabled: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfSocialAlertsEnabled).toInt()
          _tmpSocialAlertsEnabled = _tmp_11 != 0
          _item =
              UserEntity(_tmpUserId,_tmpPhoneNumber,_tmpEmail,_tmpFullName,_tmpAddress,_tmpBio,_tmpProfilePictureUrl,_tmpUserType,_tmpVerificationStatus,_tmpFarmAddressLine1,_tmpFarmAddressLine2,_tmpFarmCity,_tmpFarmState,_tmpFarmPostalCode,_tmpFarmCountry,_tmpFarmLocationLat,_tmpFarmLocationLng,_tmpLocationVerified,_tmpKycLevel,_tmpChickenCount,_tmpFarmerType,_tmpRaisingSince,_tmpFavoriteBreed,_tmpKycVerifiedAt,_tmpKycRejectionReason,_tmpVerificationRejectionReason,_tmpLatestVerificationId,_tmpLatestVerificationRef,_tmpVerificationSubmittedAt,_tmpShowcaseCount,_tmpMaxShowcaseSlots,_tmpCreatedAt,_tmpUpdatedAt,_tmpCustomClaimsUpdatedAt,_tmpIsSuspended,_tmpSuspensionReason,_tmpSuspensionEndsAt,_tmpNotificationsEnabled,_tmpFarmAlertsEnabled,_tmpTransferAlertsEnabled,_tmpSocialAlertsEnabled)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun searchUsers(query: String): Flow<List<UserEntity>> {
    val _sql: String =
        "SELECT * FROM users WHERE fullName LIKE '%' || ? || '%' OR userId LIKE '%' || ? || '%' OR address LIKE '%' || ? || '%'"
    return createFlow(__db, false, arrayOf("users")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, query)
        _argIndex = 2
        _stmt.bindText(_argIndex, query)
        _argIndex = 3
        _stmt.bindText(_argIndex, query)
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfPhoneNumber: Int = getColumnIndexOrThrow(_stmt, "phoneNumber")
        val _columnIndexOfEmail: Int = getColumnIndexOrThrow(_stmt, "email")
        val _columnIndexOfFullName: Int = getColumnIndexOrThrow(_stmt, "fullName")
        val _columnIndexOfAddress: Int = getColumnIndexOrThrow(_stmt, "address")
        val _columnIndexOfBio: Int = getColumnIndexOrThrow(_stmt, "bio")
        val _columnIndexOfProfilePictureUrl: Int = getColumnIndexOrThrow(_stmt, "profilePictureUrl")
        val _columnIndexOfUserType: Int = getColumnIndexOrThrow(_stmt, "userType")
        val _columnIndexOfVerificationStatus: Int = getColumnIndexOrThrow(_stmt,
            "verificationStatus")
        val _columnIndexOfFarmAddressLine1: Int = getColumnIndexOrThrow(_stmt, "farmAddressLine1")
        val _columnIndexOfFarmAddressLine2: Int = getColumnIndexOrThrow(_stmt, "farmAddressLine2")
        val _columnIndexOfFarmCity: Int = getColumnIndexOrThrow(_stmt, "farmCity")
        val _columnIndexOfFarmState: Int = getColumnIndexOrThrow(_stmt, "farmState")
        val _columnIndexOfFarmPostalCode: Int = getColumnIndexOrThrow(_stmt, "farmPostalCode")
        val _columnIndexOfFarmCountry: Int = getColumnIndexOrThrow(_stmt, "farmCountry")
        val _columnIndexOfFarmLocationLat: Int = getColumnIndexOrThrow(_stmt, "farmLocationLat")
        val _columnIndexOfFarmLocationLng: Int = getColumnIndexOrThrow(_stmt, "farmLocationLng")
        val _columnIndexOfLocationVerified: Int = getColumnIndexOrThrow(_stmt, "locationVerified")
        val _columnIndexOfKycLevel: Int = getColumnIndexOrThrow(_stmt, "kycLevel")
        val _columnIndexOfChickenCount: Int = getColumnIndexOrThrow(_stmt, "chickenCount")
        val _columnIndexOfFarmerType: Int = getColumnIndexOrThrow(_stmt, "farmerType")
        val _columnIndexOfRaisingSince: Int = getColumnIndexOrThrow(_stmt, "raisingSince")
        val _columnIndexOfFavoriteBreed: Int = getColumnIndexOrThrow(_stmt, "favoriteBreed")
        val _columnIndexOfKycVerifiedAt: Int = getColumnIndexOrThrow(_stmt, "kycVerifiedAt")
        val _columnIndexOfKycRejectionReason: Int = getColumnIndexOrThrow(_stmt,
            "kycRejectionReason")
        val _columnIndexOfVerificationRejectionReason: Int = getColumnIndexOrThrow(_stmt,
            "verificationRejectionReason")
        val _columnIndexOfLatestVerificationId: Int = getColumnIndexOrThrow(_stmt,
            "latestVerificationId")
        val _columnIndexOfLatestVerificationRef: Int = getColumnIndexOrThrow(_stmt,
            "latestVerificationRef")
        val _columnIndexOfVerificationSubmittedAt: Int = getColumnIndexOrThrow(_stmt,
            "verificationSubmittedAt")
        val _columnIndexOfShowcaseCount: Int = getColumnIndexOrThrow(_stmt, "showcaseCount")
        val _columnIndexOfMaxShowcaseSlots: Int = getColumnIndexOrThrow(_stmt, "maxShowcaseSlots")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfCustomClaimsUpdatedAt: Int = getColumnIndexOrThrow(_stmt,
            "customClaimsUpdatedAt")
        val _columnIndexOfIsSuspended: Int = getColumnIndexOrThrow(_stmt, "isSuspended")
        val _columnIndexOfSuspensionReason: Int = getColumnIndexOrThrow(_stmt, "suspensionReason")
        val _columnIndexOfSuspensionEndsAt: Int = getColumnIndexOrThrow(_stmt, "suspensionEndsAt")
        val _columnIndexOfNotificationsEnabled: Int = getColumnIndexOrThrow(_stmt,
            "notificationsEnabled")
        val _columnIndexOfFarmAlertsEnabled: Int = getColumnIndexOrThrow(_stmt, "farmAlertsEnabled")
        val _columnIndexOfTransferAlertsEnabled: Int = getColumnIndexOrThrow(_stmt,
            "transferAlertsEnabled")
        val _columnIndexOfSocialAlertsEnabled: Int = getColumnIndexOrThrow(_stmt,
            "socialAlertsEnabled")
        val _result: MutableList<UserEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: UserEntity
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpPhoneNumber: String?
          if (_stmt.isNull(_columnIndexOfPhoneNumber)) {
            _tmpPhoneNumber = null
          } else {
            _tmpPhoneNumber = _stmt.getText(_columnIndexOfPhoneNumber)
          }
          val _tmpEmail: String?
          if (_stmt.isNull(_columnIndexOfEmail)) {
            _tmpEmail = null
          } else {
            _tmpEmail = _stmt.getText(_columnIndexOfEmail)
          }
          val _tmpFullName: String?
          if (_stmt.isNull(_columnIndexOfFullName)) {
            _tmpFullName = null
          } else {
            _tmpFullName = _stmt.getText(_columnIndexOfFullName)
          }
          val _tmpAddress: String?
          if (_stmt.isNull(_columnIndexOfAddress)) {
            _tmpAddress = null
          } else {
            _tmpAddress = _stmt.getText(_columnIndexOfAddress)
          }
          val _tmpBio: String?
          if (_stmt.isNull(_columnIndexOfBio)) {
            _tmpBio = null
          } else {
            _tmpBio = _stmt.getText(_columnIndexOfBio)
          }
          val _tmpProfilePictureUrl: String?
          if (_stmt.isNull(_columnIndexOfProfilePictureUrl)) {
            _tmpProfilePictureUrl = null
          } else {
            _tmpProfilePictureUrl = _stmt.getText(_columnIndexOfProfilePictureUrl)
          }
          val _tmpUserType: String
          _tmpUserType = _stmt.getText(_columnIndexOfUserType)
          val _tmpVerificationStatus: VerificationStatus
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfVerificationStatus)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfVerificationStatus)
          }
          val _tmp_1: VerificationStatus? = AppDatabase.Converters.toVerificationStatus(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'com.rio.rostry.domain.model.VerificationStatus', but it was NULL.")
          } else {
            _tmpVerificationStatus = _tmp_1
          }
          val _tmpFarmAddressLine1: String?
          if (_stmt.isNull(_columnIndexOfFarmAddressLine1)) {
            _tmpFarmAddressLine1 = null
          } else {
            _tmpFarmAddressLine1 = _stmt.getText(_columnIndexOfFarmAddressLine1)
          }
          val _tmpFarmAddressLine2: String?
          if (_stmt.isNull(_columnIndexOfFarmAddressLine2)) {
            _tmpFarmAddressLine2 = null
          } else {
            _tmpFarmAddressLine2 = _stmt.getText(_columnIndexOfFarmAddressLine2)
          }
          val _tmpFarmCity: String?
          if (_stmt.isNull(_columnIndexOfFarmCity)) {
            _tmpFarmCity = null
          } else {
            _tmpFarmCity = _stmt.getText(_columnIndexOfFarmCity)
          }
          val _tmpFarmState: String?
          if (_stmt.isNull(_columnIndexOfFarmState)) {
            _tmpFarmState = null
          } else {
            _tmpFarmState = _stmt.getText(_columnIndexOfFarmState)
          }
          val _tmpFarmPostalCode: String?
          if (_stmt.isNull(_columnIndexOfFarmPostalCode)) {
            _tmpFarmPostalCode = null
          } else {
            _tmpFarmPostalCode = _stmt.getText(_columnIndexOfFarmPostalCode)
          }
          val _tmpFarmCountry: String?
          if (_stmt.isNull(_columnIndexOfFarmCountry)) {
            _tmpFarmCountry = null
          } else {
            _tmpFarmCountry = _stmt.getText(_columnIndexOfFarmCountry)
          }
          val _tmpFarmLocationLat: Double?
          if (_stmt.isNull(_columnIndexOfFarmLocationLat)) {
            _tmpFarmLocationLat = null
          } else {
            _tmpFarmLocationLat = _stmt.getDouble(_columnIndexOfFarmLocationLat)
          }
          val _tmpFarmLocationLng: Double?
          if (_stmt.isNull(_columnIndexOfFarmLocationLng)) {
            _tmpFarmLocationLng = null
          } else {
            _tmpFarmLocationLng = _stmt.getDouble(_columnIndexOfFarmLocationLng)
          }
          val _tmpLocationVerified: Boolean?
          val _tmp_2: Int?
          if (_stmt.isNull(_columnIndexOfLocationVerified)) {
            _tmp_2 = null
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfLocationVerified).toInt()
          }
          _tmpLocationVerified = _tmp_2?.let { it != 0 }
          val _tmpKycLevel: Int?
          if (_stmt.isNull(_columnIndexOfKycLevel)) {
            _tmpKycLevel = null
          } else {
            _tmpKycLevel = _stmt.getLong(_columnIndexOfKycLevel).toInt()
          }
          val _tmpChickenCount: Int?
          if (_stmt.isNull(_columnIndexOfChickenCount)) {
            _tmpChickenCount = null
          } else {
            _tmpChickenCount = _stmt.getLong(_columnIndexOfChickenCount).toInt()
          }
          val _tmpFarmerType: String?
          if (_stmt.isNull(_columnIndexOfFarmerType)) {
            _tmpFarmerType = null
          } else {
            _tmpFarmerType = _stmt.getText(_columnIndexOfFarmerType)
          }
          val _tmpRaisingSince: Long?
          if (_stmt.isNull(_columnIndexOfRaisingSince)) {
            _tmpRaisingSince = null
          } else {
            _tmpRaisingSince = _stmt.getLong(_columnIndexOfRaisingSince)
          }
          val _tmpFavoriteBreed: String?
          if (_stmt.isNull(_columnIndexOfFavoriteBreed)) {
            _tmpFavoriteBreed = null
          } else {
            _tmpFavoriteBreed = _stmt.getText(_columnIndexOfFavoriteBreed)
          }
          val _tmpKycVerifiedAt: Long?
          if (_stmt.isNull(_columnIndexOfKycVerifiedAt)) {
            _tmpKycVerifiedAt = null
          } else {
            _tmpKycVerifiedAt = _stmt.getLong(_columnIndexOfKycVerifiedAt)
          }
          val _tmpKycRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfKycRejectionReason)) {
            _tmpKycRejectionReason = null
          } else {
            _tmpKycRejectionReason = _stmt.getText(_columnIndexOfKycRejectionReason)
          }
          val _tmpVerificationRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfVerificationRejectionReason)) {
            _tmpVerificationRejectionReason = null
          } else {
            _tmpVerificationRejectionReason =
                _stmt.getText(_columnIndexOfVerificationRejectionReason)
          }
          val _tmpLatestVerificationId: String?
          if (_stmt.isNull(_columnIndexOfLatestVerificationId)) {
            _tmpLatestVerificationId = null
          } else {
            _tmpLatestVerificationId = _stmt.getText(_columnIndexOfLatestVerificationId)
          }
          val _tmpLatestVerificationRef: String?
          if (_stmt.isNull(_columnIndexOfLatestVerificationRef)) {
            _tmpLatestVerificationRef = null
          } else {
            _tmpLatestVerificationRef = _stmt.getText(_columnIndexOfLatestVerificationRef)
          }
          val _tmpVerificationSubmittedAt: Date?
          val _tmp_3: Long?
          if (_stmt.isNull(_columnIndexOfVerificationSubmittedAt)) {
            _tmp_3 = null
          } else {
            _tmp_3 = _stmt.getLong(_columnIndexOfVerificationSubmittedAt)
          }
          _tmpVerificationSubmittedAt = __dateLongConverter.fromTimestamp(_tmp_3)
          val _tmpShowcaseCount: Int
          _tmpShowcaseCount = _stmt.getLong(_columnIndexOfShowcaseCount).toInt()
          val _tmpMaxShowcaseSlots: Int
          _tmpMaxShowcaseSlots = _stmt.getLong(_columnIndexOfMaxShowcaseSlots).toInt()
          val _tmpCreatedAt: Date?
          val _tmp_4: Long?
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfCreatedAt)
          }
          _tmpCreatedAt = __dateLongConverter.fromTimestamp(_tmp_4)
          val _tmpUpdatedAt: Date?
          val _tmp_5: Long?
          if (_stmt.isNull(_columnIndexOfUpdatedAt)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfUpdatedAt)
          }
          _tmpUpdatedAt = __dateLongConverter.fromTimestamp(_tmp_5)
          val _tmpCustomClaimsUpdatedAt: Date?
          val _tmp_6: Long?
          if (_stmt.isNull(_columnIndexOfCustomClaimsUpdatedAt)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getLong(_columnIndexOfCustomClaimsUpdatedAt)
          }
          _tmpCustomClaimsUpdatedAt = __dateLongConverter.fromTimestamp(_tmp_6)
          val _tmpIsSuspended: Boolean
          val _tmp_7: Int
          _tmp_7 = _stmt.getLong(_columnIndexOfIsSuspended).toInt()
          _tmpIsSuspended = _tmp_7 != 0
          val _tmpSuspensionReason: String?
          if (_stmt.isNull(_columnIndexOfSuspensionReason)) {
            _tmpSuspensionReason = null
          } else {
            _tmpSuspensionReason = _stmt.getText(_columnIndexOfSuspensionReason)
          }
          val _tmpSuspensionEndsAt: Long?
          if (_stmt.isNull(_columnIndexOfSuspensionEndsAt)) {
            _tmpSuspensionEndsAt = null
          } else {
            _tmpSuspensionEndsAt = _stmt.getLong(_columnIndexOfSuspensionEndsAt)
          }
          val _tmpNotificationsEnabled: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfNotificationsEnabled).toInt()
          _tmpNotificationsEnabled = _tmp_8 != 0
          val _tmpFarmAlertsEnabled: Boolean
          val _tmp_9: Int
          _tmp_9 = _stmt.getLong(_columnIndexOfFarmAlertsEnabled).toInt()
          _tmpFarmAlertsEnabled = _tmp_9 != 0
          val _tmpTransferAlertsEnabled: Boolean
          val _tmp_10: Int
          _tmp_10 = _stmt.getLong(_columnIndexOfTransferAlertsEnabled).toInt()
          _tmpTransferAlertsEnabled = _tmp_10 != 0
          val _tmpSocialAlertsEnabled: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfSocialAlertsEnabled).toInt()
          _tmpSocialAlertsEnabled = _tmp_11 != 0
          _item =
              UserEntity(_tmpUserId,_tmpPhoneNumber,_tmpEmail,_tmpFullName,_tmpAddress,_tmpBio,_tmpProfilePictureUrl,_tmpUserType,_tmpVerificationStatus,_tmpFarmAddressLine1,_tmpFarmAddressLine2,_tmpFarmCity,_tmpFarmState,_tmpFarmPostalCode,_tmpFarmCountry,_tmpFarmLocationLat,_tmpFarmLocationLng,_tmpLocationVerified,_tmpKycLevel,_tmpChickenCount,_tmpFarmerType,_tmpRaisingSince,_tmpFavoriteBreed,_tmpKycVerifiedAt,_tmpKycRejectionReason,_tmpVerificationRejectionReason,_tmpLatestVerificationId,_tmpLatestVerificationRef,_tmpVerificationSubmittedAt,_tmpShowcaseCount,_tmpMaxShowcaseSlots,_tmpCreatedAt,_tmpUpdatedAt,_tmpCustomClaimsUpdatedAt,_tmpIsSuspended,_tmpSuspensionReason,_tmpSuspensionEndsAt,_tmpNotificationsEnabled,_tmpFarmAlertsEnabled,_tmpTransferAlertsEnabled,_tmpSocialAlertsEnabled)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override fun searchUsersForTransfer(query: String, currentUserId: String):
      Flow<List<UserEntity>> {
    val _sql: String = """
        |
        |        SELECT * FROM users 
        |        WHERE userId != ? 
        |        AND (fullName LIKE '%' || ? || '%' OR address LIKE '%' || ? || '%') 
        |        ORDER BY CASE WHEN verificationStatus = 'VERIFIED' THEN 0 ELSE 1 END ASC, fullName ASC
        |    
        """.trimMargin()
    return createFlow(__db, false, arrayOf("users")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, currentUserId)
        _argIndex = 2
        _stmt.bindText(_argIndex, query)
        _argIndex = 3
        _stmt.bindText(_argIndex, query)
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfPhoneNumber: Int = getColumnIndexOrThrow(_stmt, "phoneNumber")
        val _columnIndexOfEmail: Int = getColumnIndexOrThrow(_stmt, "email")
        val _columnIndexOfFullName: Int = getColumnIndexOrThrow(_stmt, "fullName")
        val _columnIndexOfAddress: Int = getColumnIndexOrThrow(_stmt, "address")
        val _columnIndexOfBio: Int = getColumnIndexOrThrow(_stmt, "bio")
        val _columnIndexOfProfilePictureUrl: Int = getColumnIndexOrThrow(_stmt, "profilePictureUrl")
        val _columnIndexOfUserType: Int = getColumnIndexOrThrow(_stmt, "userType")
        val _columnIndexOfVerificationStatus: Int = getColumnIndexOrThrow(_stmt,
            "verificationStatus")
        val _columnIndexOfFarmAddressLine1: Int = getColumnIndexOrThrow(_stmt, "farmAddressLine1")
        val _columnIndexOfFarmAddressLine2: Int = getColumnIndexOrThrow(_stmt, "farmAddressLine2")
        val _columnIndexOfFarmCity: Int = getColumnIndexOrThrow(_stmt, "farmCity")
        val _columnIndexOfFarmState: Int = getColumnIndexOrThrow(_stmt, "farmState")
        val _columnIndexOfFarmPostalCode: Int = getColumnIndexOrThrow(_stmt, "farmPostalCode")
        val _columnIndexOfFarmCountry: Int = getColumnIndexOrThrow(_stmt, "farmCountry")
        val _columnIndexOfFarmLocationLat: Int = getColumnIndexOrThrow(_stmt, "farmLocationLat")
        val _columnIndexOfFarmLocationLng: Int = getColumnIndexOrThrow(_stmt, "farmLocationLng")
        val _columnIndexOfLocationVerified: Int = getColumnIndexOrThrow(_stmt, "locationVerified")
        val _columnIndexOfKycLevel: Int = getColumnIndexOrThrow(_stmt, "kycLevel")
        val _columnIndexOfChickenCount: Int = getColumnIndexOrThrow(_stmt, "chickenCount")
        val _columnIndexOfFarmerType: Int = getColumnIndexOrThrow(_stmt, "farmerType")
        val _columnIndexOfRaisingSince: Int = getColumnIndexOrThrow(_stmt, "raisingSince")
        val _columnIndexOfFavoriteBreed: Int = getColumnIndexOrThrow(_stmt, "favoriteBreed")
        val _columnIndexOfKycVerifiedAt: Int = getColumnIndexOrThrow(_stmt, "kycVerifiedAt")
        val _columnIndexOfKycRejectionReason: Int = getColumnIndexOrThrow(_stmt,
            "kycRejectionReason")
        val _columnIndexOfVerificationRejectionReason: Int = getColumnIndexOrThrow(_stmt,
            "verificationRejectionReason")
        val _columnIndexOfLatestVerificationId: Int = getColumnIndexOrThrow(_stmt,
            "latestVerificationId")
        val _columnIndexOfLatestVerificationRef: Int = getColumnIndexOrThrow(_stmt,
            "latestVerificationRef")
        val _columnIndexOfVerificationSubmittedAt: Int = getColumnIndexOrThrow(_stmt,
            "verificationSubmittedAt")
        val _columnIndexOfShowcaseCount: Int = getColumnIndexOrThrow(_stmt, "showcaseCount")
        val _columnIndexOfMaxShowcaseSlots: Int = getColumnIndexOrThrow(_stmt, "maxShowcaseSlots")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfCustomClaimsUpdatedAt: Int = getColumnIndexOrThrow(_stmt,
            "customClaimsUpdatedAt")
        val _columnIndexOfIsSuspended: Int = getColumnIndexOrThrow(_stmt, "isSuspended")
        val _columnIndexOfSuspensionReason: Int = getColumnIndexOrThrow(_stmt, "suspensionReason")
        val _columnIndexOfSuspensionEndsAt: Int = getColumnIndexOrThrow(_stmt, "suspensionEndsAt")
        val _columnIndexOfNotificationsEnabled: Int = getColumnIndexOrThrow(_stmt,
            "notificationsEnabled")
        val _columnIndexOfFarmAlertsEnabled: Int = getColumnIndexOrThrow(_stmt, "farmAlertsEnabled")
        val _columnIndexOfTransferAlertsEnabled: Int = getColumnIndexOrThrow(_stmt,
            "transferAlertsEnabled")
        val _columnIndexOfSocialAlertsEnabled: Int = getColumnIndexOrThrow(_stmt,
            "socialAlertsEnabled")
        val _result: MutableList<UserEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: UserEntity
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpPhoneNumber: String?
          if (_stmt.isNull(_columnIndexOfPhoneNumber)) {
            _tmpPhoneNumber = null
          } else {
            _tmpPhoneNumber = _stmt.getText(_columnIndexOfPhoneNumber)
          }
          val _tmpEmail: String?
          if (_stmt.isNull(_columnIndexOfEmail)) {
            _tmpEmail = null
          } else {
            _tmpEmail = _stmt.getText(_columnIndexOfEmail)
          }
          val _tmpFullName: String?
          if (_stmt.isNull(_columnIndexOfFullName)) {
            _tmpFullName = null
          } else {
            _tmpFullName = _stmt.getText(_columnIndexOfFullName)
          }
          val _tmpAddress: String?
          if (_stmt.isNull(_columnIndexOfAddress)) {
            _tmpAddress = null
          } else {
            _tmpAddress = _stmt.getText(_columnIndexOfAddress)
          }
          val _tmpBio: String?
          if (_stmt.isNull(_columnIndexOfBio)) {
            _tmpBio = null
          } else {
            _tmpBio = _stmt.getText(_columnIndexOfBio)
          }
          val _tmpProfilePictureUrl: String?
          if (_stmt.isNull(_columnIndexOfProfilePictureUrl)) {
            _tmpProfilePictureUrl = null
          } else {
            _tmpProfilePictureUrl = _stmt.getText(_columnIndexOfProfilePictureUrl)
          }
          val _tmpUserType: String
          _tmpUserType = _stmt.getText(_columnIndexOfUserType)
          val _tmpVerificationStatus: VerificationStatus
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfVerificationStatus)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfVerificationStatus)
          }
          val _tmp_1: VerificationStatus? = AppDatabase.Converters.toVerificationStatus(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'com.rio.rostry.domain.model.VerificationStatus', but it was NULL.")
          } else {
            _tmpVerificationStatus = _tmp_1
          }
          val _tmpFarmAddressLine1: String?
          if (_stmt.isNull(_columnIndexOfFarmAddressLine1)) {
            _tmpFarmAddressLine1 = null
          } else {
            _tmpFarmAddressLine1 = _stmt.getText(_columnIndexOfFarmAddressLine1)
          }
          val _tmpFarmAddressLine2: String?
          if (_stmt.isNull(_columnIndexOfFarmAddressLine2)) {
            _tmpFarmAddressLine2 = null
          } else {
            _tmpFarmAddressLine2 = _stmt.getText(_columnIndexOfFarmAddressLine2)
          }
          val _tmpFarmCity: String?
          if (_stmt.isNull(_columnIndexOfFarmCity)) {
            _tmpFarmCity = null
          } else {
            _tmpFarmCity = _stmt.getText(_columnIndexOfFarmCity)
          }
          val _tmpFarmState: String?
          if (_stmt.isNull(_columnIndexOfFarmState)) {
            _tmpFarmState = null
          } else {
            _tmpFarmState = _stmt.getText(_columnIndexOfFarmState)
          }
          val _tmpFarmPostalCode: String?
          if (_stmt.isNull(_columnIndexOfFarmPostalCode)) {
            _tmpFarmPostalCode = null
          } else {
            _tmpFarmPostalCode = _stmt.getText(_columnIndexOfFarmPostalCode)
          }
          val _tmpFarmCountry: String?
          if (_stmt.isNull(_columnIndexOfFarmCountry)) {
            _tmpFarmCountry = null
          } else {
            _tmpFarmCountry = _stmt.getText(_columnIndexOfFarmCountry)
          }
          val _tmpFarmLocationLat: Double?
          if (_stmt.isNull(_columnIndexOfFarmLocationLat)) {
            _tmpFarmLocationLat = null
          } else {
            _tmpFarmLocationLat = _stmt.getDouble(_columnIndexOfFarmLocationLat)
          }
          val _tmpFarmLocationLng: Double?
          if (_stmt.isNull(_columnIndexOfFarmLocationLng)) {
            _tmpFarmLocationLng = null
          } else {
            _tmpFarmLocationLng = _stmt.getDouble(_columnIndexOfFarmLocationLng)
          }
          val _tmpLocationVerified: Boolean?
          val _tmp_2: Int?
          if (_stmt.isNull(_columnIndexOfLocationVerified)) {
            _tmp_2 = null
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfLocationVerified).toInt()
          }
          _tmpLocationVerified = _tmp_2?.let { it != 0 }
          val _tmpKycLevel: Int?
          if (_stmt.isNull(_columnIndexOfKycLevel)) {
            _tmpKycLevel = null
          } else {
            _tmpKycLevel = _stmt.getLong(_columnIndexOfKycLevel).toInt()
          }
          val _tmpChickenCount: Int?
          if (_stmt.isNull(_columnIndexOfChickenCount)) {
            _tmpChickenCount = null
          } else {
            _tmpChickenCount = _stmt.getLong(_columnIndexOfChickenCount).toInt()
          }
          val _tmpFarmerType: String?
          if (_stmt.isNull(_columnIndexOfFarmerType)) {
            _tmpFarmerType = null
          } else {
            _tmpFarmerType = _stmt.getText(_columnIndexOfFarmerType)
          }
          val _tmpRaisingSince: Long?
          if (_stmt.isNull(_columnIndexOfRaisingSince)) {
            _tmpRaisingSince = null
          } else {
            _tmpRaisingSince = _stmt.getLong(_columnIndexOfRaisingSince)
          }
          val _tmpFavoriteBreed: String?
          if (_stmt.isNull(_columnIndexOfFavoriteBreed)) {
            _tmpFavoriteBreed = null
          } else {
            _tmpFavoriteBreed = _stmt.getText(_columnIndexOfFavoriteBreed)
          }
          val _tmpKycVerifiedAt: Long?
          if (_stmt.isNull(_columnIndexOfKycVerifiedAt)) {
            _tmpKycVerifiedAt = null
          } else {
            _tmpKycVerifiedAt = _stmt.getLong(_columnIndexOfKycVerifiedAt)
          }
          val _tmpKycRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfKycRejectionReason)) {
            _tmpKycRejectionReason = null
          } else {
            _tmpKycRejectionReason = _stmt.getText(_columnIndexOfKycRejectionReason)
          }
          val _tmpVerificationRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfVerificationRejectionReason)) {
            _tmpVerificationRejectionReason = null
          } else {
            _tmpVerificationRejectionReason =
                _stmt.getText(_columnIndexOfVerificationRejectionReason)
          }
          val _tmpLatestVerificationId: String?
          if (_stmt.isNull(_columnIndexOfLatestVerificationId)) {
            _tmpLatestVerificationId = null
          } else {
            _tmpLatestVerificationId = _stmt.getText(_columnIndexOfLatestVerificationId)
          }
          val _tmpLatestVerificationRef: String?
          if (_stmt.isNull(_columnIndexOfLatestVerificationRef)) {
            _tmpLatestVerificationRef = null
          } else {
            _tmpLatestVerificationRef = _stmt.getText(_columnIndexOfLatestVerificationRef)
          }
          val _tmpVerificationSubmittedAt: Date?
          val _tmp_3: Long?
          if (_stmt.isNull(_columnIndexOfVerificationSubmittedAt)) {
            _tmp_3 = null
          } else {
            _tmp_3 = _stmt.getLong(_columnIndexOfVerificationSubmittedAt)
          }
          _tmpVerificationSubmittedAt = __dateLongConverter.fromTimestamp(_tmp_3)
          val _tmpShowcaseCount: Int
          _tmpShowcaseCount = _stmt.getLong(_columnIndexOfShowcaseCount).toInt()
          val _tmpMaxShowcaseSlots: Int
          _tmpMaxShowcaseSlots = _stmt.getLong(_columnIndexOfMaxShowcaseSlots).toInt()
          val _tmpCreatedAt: Date?
          val _tmp_4: Long?
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfCreatedAt)
          }
          _tmpCreatedAt = __dateLongConverter.fromTimestamp(_tmp_4)
          val _tmpUpdatedAt: Date?
          val _tmp_5: Long?
          if (_stmt.isNull(_columnIndexOfUpdatedAt)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfUpdatedAt)
          }
          _tmpUpdatedAt = __dateLongConverter.fromTimestamp(_tmp_5)
          val _tmpCustomClaimsUpdatedAt: Date?
          val _tmp_6: Long?
          if (_stmt.isNull(_columnIndexOfCustomClaimsUpdatedAt)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getLong(_columnIndexOfCustomClaimsUpdatedAt)
          }
          _tmpCustomClaimsUpdatedAt = __dateLongConverter.fromTimestamp(_tmp_6)
          val _tmpIsSuspended: Boolean
          val _tmp_7: Int
          _tmp_7 = _stmt.getLong(_columnIndexOfIsSuspended).toInt()
          _tmpIsSuspended = _tmp_7 != 0
          val _tmpSuspensionReason: String?
          if (_stmt.isNull(_columnIndexOfSuspensionReason)) {
            _tmpSuspensionReason = null
          } else {
            _tmpSuspensionReason = _stmt.getText(_columnIndexOfSuspensionReason)
          }
          val _tmpSuspensionEndsAt: Long?
          if (_stmt.isNull(_columnIndexOfSuspensionEndsAt)) {
            _tmpSuspensionEndsAt = null
          } else {
            _tmpSuspensionEndsAt = _stmt.getLong(_columnIndexOfSuspensionEndsAt)
          }
          val _tmpNotificationsEnabled: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfNotificationsEnabled).toInt()
          _tmpNotificationsEnabled = _tmp_8 != 0
          val _tmpFarmAlertsEnabled: Boolean
          val _tmp_9: Int
          _tmp_9 = _stmt.getLong(_columnIndexOfFarmAlertsEnabled).toInt()
          _tmpFarmAlertsEnabled = _tmp_9 != 0
          val _tmpTransferAlertsEnabled: Boolean
          val _tmp_10: Int
          _tmp_10 = _stmt.getLong(_columnIndexOfTransferAlertsEnabled).toInt()
          _tmpTransferAlertsEnabled = _tmp_10 != 0
          val _tmpSocialAlertsEnabled: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfSocialAlertsEnabled).toInt()
          _tmpSocialAlertsEnabled = _tmp_11 != 0
          _item =
              UserEntity(_tmpUserId,_tmpPhoneNumber,_tmpEmail,_tmpFullName,_tmpAddress,_tmpBio,_tmpProfilePictureUrl,_tmpUserType,_tmpVerificationStatus,_tmpFarmAddressLine1,_tmpFarmAddressLine2,_tmpFarmCity,_tmpFarmState,_tmpFarmPostalCode,_tmpFarmCountry,_tmpFarmLocationLat,_tmpFarmLocationLng,_tmpLocationVerified,_tmpKycLevel,_tmpChickenCount,_tmpFarmerType,_tmpRaisingSince,_tmpFavoriteBreed,_tmpKycVerifiedAt,_tmpKycRejectionReason,_tmpVerificationRejectionReason,_tmpLatestVerificationId,_tmpLatestVerificationRef,_tmpVerificationSubmittedAt,_tmpShowcaseCount,_tmpMaxShowcaseSlots,_tmpCreatedAt,_tmpUpdatedAt,_tmpCustomClaimsUpdatedAt,_tmpIsSuspended,_tmpSuspensionReason,_tmpSuspensionEndsAt,_tmpNotificationsEnabled,_tmpFarmAlertsEnabled,_tmpTransferAlertsEnabled,_tmpSocialAlertsEnabled)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun countAllUsers(): Int {
    val _sql: String = "SELECT COUNT(*) FROM users"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
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

  public override suspend fun countUsersByRole(role: String): Int {
    val _sql: String = "SELECT COUNT(*) FROM users WHERE userType = ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, role)
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

  public override suspend fun countUsersCreatedAfter(timestamp: Long): Int {
    val _sql: String = "SELECT COUNT(*) FROM users WHERE createdAt >= ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, timestamp)
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

  public override suspend fun countUsersUpdatedAfter(timestamp: Long): Int {
    val _sql: String = "SELECT COUNT(*) FROM users WHERE updatedAt >= ?"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindLong(_argIndex, timestamp)
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

  public override fun getUsersByRole(role: String): Flow<List<UserEntity>> {
    val _sql: String = "SELECT * FROM users WHERE userType = ?"
    return createFlow(__db, false, arrayOf("users")) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, role)
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfPhoneNumber: Int = getColumnIndexOrThrow(_stmt, "phoneNumber")
        val _columnIndexOfEmail: Int = getColumnIndexOrThrow(_stmt, "email")
        val _columnIndexOfFullName: Int = getColumnIndexOrThrow(_stmt, "fullName")
        val _columnIndexOfAddress: Int = getColumnIndexOrThrow(_stmt, "address")
        val _columnIndexOfBio: Int = getColumnIndexOrThrow(_stmt, "bio")
        val _columnIndexOfProfilePictureUrl: Int = getColumnIndexOrThrow(_stmt, "profilePictureUrl")
        val _columnIndexOfUserType: Int = getColumnIndexOrThrow(_stmt, "userType")
        val _columnIndexOfVerificationStatus: Int = getColumnIndexOrThrow(_stmt,
            "verificationStatus")
        val _columnIndexOfFarmAddressLine1: Int = getColumnIndexOrThrow(_stmt, "farmAddressLine1")
        val _columnIndexOfFarmAddressLine2: Int = getColumnIndexOrThrow(_stmt, "farmAddressLine2")
        val _columnIndexOfFarmCity: Int = getColumnIndexOrThrow(_stmt, "farmCity")
        val _columnIndexOfFarmState: Int = getColumnIndexOrThrow(_stmt, "farmState")
        val _columnIndexOfFarmPostalCode: Int = getColumnIndexOrThrow(_stmt, "farmPostalCode")
        val _columnIndexOfFarmCountry: Int = getColumnIndexOrThrow(_stmt, "farmCountry")
        val _columnIndexOfFarmLocationLat: Int = getColumnIndexOrThrow(_stmt, "farmLocationLat")
        val _columnIndexOfFarmLocationLng: Int = getColumnIndexOrThrow(_stmt, "farmLocationLng")
        val _columnIndexOfLocationVerified: Int = getColumnIndexOrThrow(_stmt, "locationVerified")
        val _columnIndexOfKycLevel: Int = getColumnIndexOrThrow(_stmt, "kycLevel")
        val _columnIndexOfChickenCount: Int = getColumnIndexOrThrow(_stmt, "chickenCount")
        val _columnIndexOfFarmerType: Int = getColumnIndexOrThrow(_stmt, "farmerType")
        val _columnIndexOfRaisingSince: Int = getColumnIndexOrThrow(_stmt, "raisingSince")
        val _columnIndexOfFavoriteBreed: Int = getColumnIndexOrThrow(_stmt, "favoriteBreed")
        val _columnIndexOfKycVerifiedAt: Int = getColumnIndexOrThrow(_stmt, "kycVerifiedAt")
        val _columnIndexOfKycRejectionReason: Int = getColumnIndexOrThrow(_stmt,
            "kycRejectionReason")
        val _columnIndexOfVerificationRejectionReason: Int = getColumnIndexOrThrow(_stmt,
            "verificationRejectionReason")
        val _columnIndexOfLatestVerificationId: Int = getColumnIndexOrThrow(_stmt,
            "latestVerificationId")
        val _columnIndexOfLatestVerificationRef: Int = getColumnIndexOrThrow(_stmt,
            "latestVerificationRef")
        val _columnIndexOfVerificationSubmittedAt: Int = getColumnIndexOrThrow(_stmt,
            "verificationSubmittedAt")
        val _columnIndexOfShowcaseCount: Int = getColumnIndexOrThrow(_stmt, "showcaseCount")
        val _columnIndexOfMaxShowcaseSlots: Int = getColumnIndexOrThrow(_stmt, "maxShowcaseSlots")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfCustomClaimsUpdatedAt: Int = getColumnIndexOrThrow(_stmt,
            "customClaimsUpdatedAt")
        val _columnIndexOfIsSuspended: Int = getColumnIndexOrThrow(_stmt, "isSuspended")
        val _columnIndexOfSuspensionReason: Int = getColumnIndexOrThrow(_stmt, "suspensionReason")
        val _columnIndexOfSuspensionEndsAt: Int = getColumnIndexOrThrow(_stmt, "suspensionEndsAt")
        val _columnIndexOfNotificationsEnabled: Int = getColumnIndexOrThrow(_stmt,
            "notificationsEnabled")
        val _columnIndexOfFarmAlertsEnabled: Int = getColumnIndexOrThrow(_stmt, "farmAlertsEnabled")
        val _columnIndexOfTransferAlertsEnabled: Int = getColumnIndexOrThrow(_stmt,
            "transferAlertsEnabled")
        val _columnIndexOfSocialAlertsEnabled: Int = getColumnIndexOrThrow(_stmt,
            "socialAlertsEnabled")
        val _result: MutableList<UserEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: UserEntity
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpPhoneNumber: String?
          if (_stmt.isNull(_columnIndexOfPhoneNumber)) {
            _tmpPhoneNumber = null
          } else {
            _tmpPhoneNumber = _stmt.getText(_columnIndexOfPhoneNumber)
          }
          val _tmpEmail: String?
          if (_stmt.isNull(_columnIndexOfEmail)) {
            _tmpEmail = null
          } else {
            _tmpEmail = _stmt.getText(_columnIndexOfEmail)
          }
          val _tmpFullName: String?
          if (_stmt.isNull(_columnIndexOfFullName)) {
            _tmpFullName = null
          } else {
            _tmpFullName = _stmt.getText(_columnIndexOfFullName)
          }
          val _tmpAddress: String?
          if (_stmt.isNull(_columnIndexOfAddress)) {
            _tmpAddress = null
          } else {
            _tmpAddress = _stmt.getText(_columnIndexOfAddress)
          }
          val _tmpBio: String?
          if (_stmt.isNull(_columnIndexOfBio)) {
            _tmpBio = null
          } else {
            _tmpBio = _stmt.getText(_columnIndexOfBio)
          }
          val _tmpProfilePictureUrl: String?
          if (_stmt.isNull(_columnIndexOfProfilePictureUrl)) {
            _tmpProfilePictureUrl = null
          } else {
            _tmpProfilePictureUrl = _stmt.getText(_columnIndexOfProfilePictureUrl)
          }
          val _tmpUserType: String
          _tmpUserType = _stmt.getText(_columnIndexOfUserType)
          val _tmpVerificationStatus: VerificationStatus
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfVerificationStatus)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfVerificationStatus)
          }
          val _tmp_1: VerificationStatus? = AppDatabase.Converters.toVerificationStatus(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'com.rio.rostry.domain.model.VerificationStatus', but it was NULL.")
          } else {
            _tmpVerificationStatus = _tmp_1
          }
          val _tmpFarmAddressLine1: String?
          if (_stmt.isNull(_columnIndexOfFarmAddressLine1)) {
            _tmpFarmAddressLine1 = null
          } else {
            _tmpFarmAddressLine1 = _stmt.getText(_columnIndexOfFarmAddressLine1)
          }
          val _tmpFarmAddressLine2: String?
          if (_stmt.isNull(_columnIndexOfFarmAddressLine2)) {
            _tmpFarmAddressLine2 = null
          } else {
            _tmpFarmAddressLine2 = _stmt.getText(_columnIndexOfFarmAddressLine2)
          }
          val _tmpFarmCity: String?
          if (_stmt.isNull(_columnIndexOfFarmCity)) {
            _tmpFarmCity = null
          } else {
            _tmpFarmCity = _stmt.getText(_columnIndexOfFarmCity)
          }
          val _tmpFarmState: String?
          if (_stmt.isNull(_columnIndexOfFarmState)) {
            _tmpFarmState = null
          } else {
            _tmpFarmState = _stmt.getText(_columnIndexOfFarmState)
          }
          val _tmpFarmPostalCode: String?
          if (_stmt.isNull(_columnIndexOfFarmPostalCode)) {
            _tmpFarmPostalCode = null
          } else {
            _tmpFarmPostalCode = _stmt.getText(_columnIndexOfFarmPostalCode)
          }
          val _tmpFarmCountry: String?
          if (_stmt.isNull(_columnIndexOfFarmCountry)) {
            _tmpFarmCountry = null
          } else {
            _tmpFarmCountry = _stmt.getText(_columnIndexOfFarmCountry)
          }
          val _tmpFarmLocationLat: Double?
          if (_stmt.isNull(_columnIndexOfFarmLocationLat)) {
            _tmpFarmLocationLat = null
          } else {
            _tmpFarmLocationLat = _stmt.getDouble(_columnIndexOfFarmLocationLat)
          }
          val _tmpFarmLocationLng: Double?
          if (_stmt.isNull(_columnIndexOfFarmLocationLng)) {
            _tmpFarmLocationLng = null
          } else {
            _tmpFarmLocationLng = _stmt.getDouble(_columnIndexOfFarmLocationLng)
          }
          val _tmpLocationVerified: Boolean?
          val _tmp_2: Int?
          if (_stmt.isNull(_columnIndexOfLocationVerified)) {
            _tmp_2 = null
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfLocationVerified).toInt()
          }
          _tmpLocationVerified = _tmp_2?.let { it != 0 }
          val _tmpKycLevel: Int?
          if (_stmt.isNull(_columnIndexOfKycLevel)) {
            _tmpKycLevel = null
          } else {
            _tmpKycLevel = _stmt.getLong(_columnIndexOfKycLevel).toInt()
          }
          val _tmpChickenCount: Int?
          if (_stmt.isNull(_columnIndexOfChickenCount)) {
            _tmpChickenCount = null
          } else {
            _tmpChickenCount = _stmt.getLong(_columnIndexOfChickenCount).toInt()
          }
          val _tmpFarmerType: String?
          if (_stmt.isNull(_columnIndexOfFarmerType)) {
            _tmpFarmerType = null
          } else {
            _tmpFarmerType = _stmt.getText(_columnIndexOfFarmerType)
          }
          val _tmpRaisingSince: Long?
          if (_stmt.isNull(_columnIndexOfRaisingSince)) {
            _tmpRaisingSince = null
          } else {
            _tmpRaisingSince = _stmt.getLong(_columnIndexOfRaisingSince)
          }
          val _tmpFavoriteBreed: String?
          if (_stmt.isNull(_columnIndexOfFavoriteBreed)) {
            _tmpFavoriteBreed = null
          } else {
            _tmpFavoriteBreed = _stmt.getText(_columnIndexOfFavoriteBreed)
          }
          val _tmpKycVerifiedAt: Long?
          if (_stmt.isNull(_columnIndexOfKycVerifiedAt)) {
            _tmpKycVerifiedAt = null
          } else {
            _tmpKycVerifiedAt = _stmt.getLong(_columnIndexOfKycVerifiedAt)
          }
          val _tmpKycRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfKycRejectionReason)) {
            _tmpKycRejectionReason = null
          } else {
            _tmpKycRejectionReason = _stmt.getText(_columnIndexOfKycRejectionReason)
          }
          val _tmpVerificationRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfVerificationRejectionReason)) {
            _tmpVerificationRejectionReason = null
          } else {
            _tmpVerificationRejectionReason =
                _stmt.getText(_columnIndexOfVerificationRejectionReason)
          }
          val _tmpLatestVerificationId: String?
          if (_stmt.isNull(_columnIndexOfLatestVerificationId)) {
            _tmpLatestVerificationId = null
          } else {
            _tmpLatestVerificationId = _stmt.getText(_columnIndexOfLatestVerificationId)
          }
          val _tmpLatestVerificationRef: String?
          if (_stmt.isNull(_columnIndexOfLatestVerificationRef)) {
            _tmpLatestVerificationRef = null
          } else {
            _tmpLatestVerificationRef = _stmt.getText(_columnIndexOfLatestVerificationRef)
          }
          val _tmpVerificationSubmittedAt: Date?
          val _tmp_3: Long?
          if (_stmt.isNull(_columnIndexOfVerificationSubmittedAt)) {
            _tmp_3 = null
          } else {
            _tmp_3 = _stmt.getLong(_columnIndexOfVerificationSubmittedAt)
          }
          _tmpVerificationSubmittedAt = __dateLongConverter.fromTimestamp(_tmp_3)
          val _tmpShowcaseCount: Int
          _tmpShowcaseCount = _stmt.getLong(_columnIndexOfShowcaseCount).toInt()
          val _tmpMaxShowcaseSlots: Int
          _tmpMaxShowcaseSlots = _stmt.getLong(_columnIndexOfMaxShowcaseSlots).toInt()
          val _tmpCreatedAt: Date?
          val _tmp_4: Long?
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfCreatedAt)
          }
          _tmpCreatedAt = __dateLongConverter.fromTimestamp(_tmp_4)
          val _tmpUpdatedAt: Date?
          val _tmp_5: Long?
          if (_stmt.isNull(_columnIndexOfUpdatedAt)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfUpdatedAt)
          }
          _tmpUpdatedAt = __dateLongConverter.fromTimestamp(_tmp_5)
          val _tmpCustomClaimsUpdatedAt: Date?
          val _tmp_6: Long?
          if (_stmt.isNull(_columnIndexOfCustomClaimsUpdatedAt)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getLong(_columnIndexOfCustomClaimsUpdatedAt)
          }
          _tmpCustomClaimsUpdatedAt = __dateLongConverter.fromTimestamp(_tmp_6)
          val _tmpIsSuspended: Boolean
          val _tmp_7: Int
          _tmp_7 = _stmt.getLong(_columnIndexOfIsSuspended).toInt()
          _tmpIsSuspended = _tmp_7 != 0
          val _tmpSuspensionReason: String?
          if (_stmt.isNull(_columnIndexOfSuspensionReason)) {
            _tmpSuspensionReason = null
          } else {
            _tmpSuspensionReason = _stmt.getText(_columnIndexOfSuspensionReason)
          }
          val _tmpSuspensionEndsAt: Long?
          if (_stmt.isNull(_columnIndexOfSuspensionEndsAt)) {
            _tmpSuspensionEndsAt = null
          } else {
            _tmpSuspensionEndsAt = _stmt.getLong(_columnIndexOfSuspensionEndsAt)
          }
          val _tmpNotificationsEnabled: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfNotificationsEnabled).toInt()
          _tmpNotificationsEnabled = _tmp_8 != 0
          val _tmpFarmAlertsEnabled: Boolean
          val _tmp_9: Int
          _tmp_9 = _stmt.getLong(_columnIndexOfFarmAlertsEnabled).toInt()
          _tmpFarmAlertsEnabled = _tmp_9 != 0
          val _tmpTransferAlertsEnabled: Boolean
          val _tmp_10: Int
          _tmp_10 = _stmt.getLong(_columnIndexOfTransferAlertsEnabled).toInt()
          _tmpTransferAlertsEnabled = _tmp_10 != 0
          val _tmpSocialAlertsEnabled: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfSocialAlertsEnabled).toInt()
          _tmpSocialAlertsEnabled = _tmp_11 != 0
          _item =
              UserEntity(_tmpUserId,_tmpPhoneNumber,_tmpEmail,_tmpFullName,_tmpAddress,_tmpBio,_tmpProfilePictureUrl,_tmpUserType,_tmpVerificationStatus,_tmpFarmAddressLine1,_tmpFarmAddressLine2,_tmpFarmCity,_tmpFarmState,_tmpFarmPostalCode,_tmpFarmCountry,_tmpFarmLocationLat,_tmpFarmLocationLng,_tmpLocationVerified,_tmpKycLevel,_tmpChickenCount,_tmpFarmerType,_tmpRaisingSince,_tmpFavoriteBreed,_tmpKycVerifiedAt,_tmpKycRejectionReason,_tmpVerificationRejectionReason,_tmpLatestVerificationId,_tmpLatestVerificationRef,_tmpVerificationSubmittedAt,_tmpShowcaseCount,_tmpMaxShowcaseSlots,_tmpCreatedAt,_tmpUpdatedAt,_tmpCustomClaimsUpdatedAt,_tmpIsSuspended,_tmpSuspensionReason,_tmpSuspensionEndsAt,_tmpNotificationsEnabled,_tmpFarmAlertsEnabled,_tmpTransferAlertsEnabled,_tmpSocialAlertsEnabled)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun getAllUsersSnapshot(): List<UserEntity> {
    val _sql: String = "SELECT * FROM users ORDER BY createdAt DESC"
    return performSuspending(__db, true, false) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        val _columnIndexOfUserId: Int = getColumnIndexOrThrow(_stmt, "userId")
        val _columnIndexOfPhoneNumber: Int = getColumnIndexOrThrow(_stmt, "phoneNumber")
        val _columnIndexOfEmail: Int = getColumnIndexOrThrow(_stmt, "email")
        val _columnIndexOfFullName: Int = getColumnIndexOrThrow(_stmt, "fullName")
        val _columnIndexOfAddress: Int = getColumnIndexOrThrow(_stmt, "address")
        val _columnIndexOfBio: Int = getColumnIndexOrThrow(_stmt, "bio")
        val _columnIndexOfProfilePictureUrl: Int = getColumnIndexOrThrow(_stmt, "profilePictureUrl")
        val _columnIndexOfUserType: Int = getColumnIndexOrThrow(_stmt, "userType")
        val _columnIndexOfVerificationStatus: Int = getColumnIndexOrThrow(_stmt,
            "verificationStatus")
        val _columnIndexOfFarmAddressLine1: Int = getColumnIndexOrThrow(_stmt, "farmAddressLine1")
        val _columnIndexOfFarmAddressLine2: Int = getColumnIndexOrThrow(_stmt, "farmAddressLine2")
        val _columnIndexOfFarmCity: Int = getColumnIndexOrThrow(_stmt, "farmCity")
        val _columnIndexOfFarmState: Int = getColumnIndexOrThrow(_stmt, "farmState")
        val _columnIndexOfFarmPostalCode: Int = getColumnIndexOrThrow(_stmt, "farmPostalCode")
        val _columnIndexOfFarmCountry: Int = getColumnIndexOrThrow(_stmt, "farmCountry")
        val _columnIndexOfFarmLocationLat: Int = getColumnIndexOrThrow(_stmt, "farmLocationLat")
        val _columnIndexOfFarmLocationLng: Int = getColumnIndexOrThrow(_stmt, "farmLocationLng")
        val _columnIndexOfLocationVerified: Int = getColumnIndexOrThrow(_stmt, "locationVerified")
        val _columnIndexOfKycLevel: Int = getColumnIndexOrThrow(_stmt, "kycLevel")
        val _columnIndexOfChickenCount: Int = getColumnIndexOrThrow(_stmt, "chickenCount")
        val _columnIndexOfFarmerType: Int = getColumnIndexOrThrow(_stmt, "farmerType")
        val _columnIndexOfRaisingSince: Int = getColumnIndexOrThrow(_stmt, "raisingSince")
        val _columnIndexOfFavoriteBreed: Int = getColumnIndexOrThrow(_stmt, "favoriteBreed")
        val _columnIndexOfKycVerifiedAt: Int = getColumnIndexOrThrow(_stmt, "kycVerifiedAt")
        val _columnIndexOfKycRejectionReason: Int = getColumnIndexOrThrow(_stmt,
            "kycRejectionReason")
        val _columnIndexOfVerificationRejectionReason: Int = getColumnIndexOrThrow(_stmt,
            "verificationRejectionReason")
        val _columnIndexOfLatestVerificationId: Int = getColumnIndexOrThrow(_stmt,
            "latestVerificationId")
        val _columnIndexOfLatestVerificationRef: Int = getColumnIndexOrThrow(_stmt,
            "latestVerificationRef")
        val _columnIndexOfVerificationSubmittedAt: Int = getColumnIndexOrThrow(_stmt,
            "verificationSubmittedAt")
        val _columnIndexOfShowcaseCount: Int = getColumnIndexOrThrow(_stmt, "showcaseCount")
        val _columnIndexOfMaxShowcaseSlots: Int = getColumnIndexOrThrow(_stmt, "maxShowcaseSlots")
        val _columnIndexOfCreatedAt: Int = getColumnIndexOrThrow(_stmt, "createdAt")
        val _columnIndexOfUpdatedAt: Int = getColumnIndexOrThrow(_stmt, "updatedAt")
        val _columnIndexOfCustomClaimsUpdatedAt: Int = getColumnIndexOrThrow(_stmt,
            "customClaimsUpdatedAt")
        val _columnIndexOfIsSuspended: Int = getColumnIndexOrThrow(_stmt, "isSuspended")
        val _columnIndexOfSuspensionReason: Int = getColumnIndexOrThrow(_stmt, "suspensionReason")
        val _columnIndexOfSuspensionEndsAt: Int = getColumnIndexOrThrow(_stmt, "suspensionEndsAt")
        val _columnIndexOfNotificationsEnabled: Int = getColumnIndexOrThrow(_stmt,
            "notificationsEnabled")
        val _columnIndexOfFarmAlertsEnabled: Int = getColumnIndexOrThrow(_stmt, "farmAlertsEnabled")
        val _columnIndexOfTransferAlertsEnabled: Int = getColumnIndexOrThrow(_stmt,
            "transferAlertsEnabled")
        val _columnIndexOfSocialAlertsEnabled: Int = getColumnIndexOrThrow(_stmt,
            "socialAlertsEnabled")
        val _result: MutableList<UserEntity> = mutableListOf()
        while (_stmt.step()) {
          val _item: UserEntity
          val _tmpUserId: String
          _tmpUserId = _stmt.getText(_columnIndexOfUserId)
          val _tmpPhoneNumber: String?
          if (_stmt.isNull(_columnIndexOfPhoneNumber)) {
            _tmpPhoneNumber = null
          } else {
            _tmpPhoneNumber = _stmt.getText(_columnIndexOfPhoneNumber)
          }
          val _tmpEmail: String?
          if (_stmt.isNull(_columnIndexOfEmail)) {
            _tmpEmail = null
          } else {
            _tmpEmail = _stmt.getText(_columnIndexOfEmail)
          }
          val _tmpFullName: String?
          if (_stmt.isNull(_columnIndexOfFullName)) {
            _tmpFullName = null
          } else {
            _tmpFullName = _stmt.getText(_columnIndexOfFullName)
          }
          val _tmpAddress: String?
          if (_stmt.isNull(_columnIndexOfAddress)) {
            _tmpAddress = null
          } else {
            _tmpAddress = _stmt.getText(_columnIndexOfAddress)
          }
          val _tmpBio: String?
          if (_stmt.isNull(_columnIndexOfBio)) {
            _tmpBio = null
          } else {
            _tmpBio = _stmt.getText(_columnIndexOfBio)
          }
          val _tmpProfilePictureUrl: String?
          if (_stmt.isNull(_columnIndexOfProfilePictureUrl)) {
            _tmpProfilePictureUrl = null
          } else {
            _tmpProfilePictureUrl = _stmt.getText(_columnIndexOfProfilePictureUrl)
          }
          val _tmpUserType: String
          _tmpUserType = _stmt.getText(_columnIndexOfUserType)
          val _tmpVerificationStatus: VerificationStatus
          val _tmp: String?
          if (_stmt.isNull(_columnIndexOfVerificationStatus)) {
            _tmp = null
          } else {
            _tmp = _stmt.getText(_columnIndexOfVerificationStatus)
          }
          val _tmp_1: VerificationStatus? = AppDatabase.Converters.toVerificationStatus(_tmp)
          if (_tmp_1 == null) {
            error("Expected NON-NULL 'com.rio.rostry.domain.model.VerificationStatus', but it was NULL.")
          } else {
            _tmpVerificationStatus = _tmp_1
          }
          val _tmpFarmAddressLine1: String?
          if (_stmt.isNull(_columnIndexOfFarmAddressLine1)) {
            _tmpFarmAddressLine1 = null
          } else {
            _tmpFarmAddressLine1 = _stmt.getText(_columnIndexOfFarmAddressLine1)
          }
          val _tmpFarmAddressLine2: String?
          if (_stmt.isNull(_columnIndexOfFarmAddressLine2)) {
            _tmpFarmAddressLine2 = null
          } else {
            _tmpFarmAddressLine2 = _stmt.getText(_columnIndexOfFarmAddressLine2)
          }
          val _tmpFarmCity: String?
          if (_stmt.isNull(_columnIndexOfFarmCity)) {
            _tmpFarmCity = null
          } else {
            _tmpFarmCity = _stmt.getText(_columnIndexOfFarmCity)
          }
          val _tmpFarmState: String?
          if (_stmt.isNull(_columnIndexOfFarmState)) {
            _tmpFarmState = null
          } else {
            _tmpFarmState = _stmt.getText(_columnIndexOfFarmState)
          }
          val _tmpFarmPostalCode: String?
          if (_stmt.isNull(_columnIndexOfFarmPostalCode)) {
            _tmpFarmPostalCode = null
          } else {
            _tmpFarmPostalCode = _stmt.getText(_columnIndexOfFarmPostalCode)
          }
          val _tmpFarmCountry: String?
          if (_stmt.isNull(_columnIndexOfFarmCountry)) {
            _tmpFarmCountry = null
          } else {
            _tmpFarmCountry = _stmt.getText(_columnIndexOfFarmCountry)
          }
          val _tmpFarmLocationLat: Double?
          if (_stmt.isNull(_columnIndexOfFarmLocationLat)) {
            _tmpFarmLocationLat = null
          } else {
            _tmpFarmLocationLat = _stmt.getDouble(_columnIndexOfFarmLocationLat)
          }
          val _tmpFarmLocationLng: Double?
          if (_stmt.isNull(_columnIndexOfFarmLocationLng)) {
            _tmpFarmLocationLng = null
          } else {
            _tmpFarmLocationLng = _stmt.getDouble(_columnIndexOfFarmLocationLng)
          }
          val _tmpLocationVerified: Boolean?
          val _tmp_2: Int?
          if (_stmt.isNull(_columnIndexOfLocationVerified)) {
            _tmp_2 = null
          } else {
            _tmp_2 = _stmt.getLong(_columnIndexOfLocationVerified).toInt()
          }
          _tmpLocationVerified = _tmp_2?.let { it != 0 }
          val _tmpKycLevel: Int?
          if (_stmt.isNull(_columnIndexOfKycLevel)) {
            _tmpKycLevel = null
          } else {
            _tmpKycLevel = _stmt.getLong(_columnIndexOfKycLevel).toInt()
          }
          val _tmpChickenCount: Int?
          if (_stmt.isNull(_columnIndexOfChickenCount)) {
            _tmpChickenCount = null
          } else {
            _tmpChickenCount = _stmt.getLong(_columnIndexOfChickenCount).toInt()
          }
          val _tmpFarmerType: String?
          if (_stmt.isNull(_columnIndexOfFarmerType)) {
            _tmpFarmerType = null
          } else {
            _tmpFarmerType = _stmt.getText(_columnIndexOfFarmerType)
          }
          val _tmpRaisingSince: Long?
          if (_stmt.isNull(_columnIndexOfRaisingSince)) {
            _tmpRaisingSince = null
          } else {
            _tmpRaisingSince = _stmt.getLong(_columnIndexOfRaisingSince)
          }
          val _tmpFavoriteBreed: String?
          if (_stmt.isNull(_columnIndexOfFavoriteBreed)) {
            _tmpFavoriteBreed = null
          } else {
            _tmpFavoriteBreed = _stmt.getText(_columnIndexOfFavoriteBreed)
          }
          val _tmpKycVerifiedAt: Long?
          if (_stmt.isNull(_columnIndexOfKycVerifiedAt)) {
            _tmpKycVerifiedAt = null
          } else {
            _tmpKycVerifiedAt = _stmt.getLong(_columnIndexOfKycVerifiedAt)
          }
          val _tmpKycRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfKycRejectionReason)) {
            _tmpKycRejectionReason = null
          } else {
            _tmpKycRejectionReason = _stmt.getText(_columnIndexOfKycRejectionReason)
          }
          val _tmpVerificationRejectionReason: String?
          if (_stmt.isNull(_columnIndexOfVerificationRejectionReason)) {
            _tmpVerificationRejectionReason = null
          } else {
            _tmpVerificationRejectionReason =
                _stmt.getText(_columnIndexOfVerificationRejectionReason)
          }
          val _tmpLatestVerificationId: String?
          if (_stmt.isNull(_columnIndexOfLatestVerificationId)) {
            _tmpLatestVerificationId = null
          } else {
            _tmpLatestVerificationId = _stmt.getText(_columnIndexOfLatestVerificationId)
          }
          val _tmpLatestVerificationRef: String?
          if (_stmt.isNull(_columnIndexOfLatestVerificationRef)) {
            _tmpLatestVerificationRef = null
          } else {
            _tmpLatestVerificationRef = _stmt.getText(_columnIndexOfLatestVerificationRef)
          }
          val _tmpVerificationSubmittedAt: Date?
          val _tmp_3: Long?
          if (_stmt.isNull(_columnIndexOfVerificationSubmittedAt)) {
            _tmp_3 = null
          } else {
            _tmp_3 = _stmt.getLong(_columnIndexOfVerificationSubmittedAt)
          }
          _tmpVerificationSubmittedAt = __dateLongConverter.fromTimestamp(_tmp_3)
          val _tmpShowcaseCount: Int
          _tmpShowcaseCount = _stmt.getLong(_columnIndexOfShowcaseCount).toInt()
          val _tmpMaxShowcaseSlots: Int
          _tmpMaxShowcaseSlots = _stmt.getLong(_columnIndexOfMaxShowcaseSlots).toInt()
          val _tmpCreatedAt: Date?
          val _tmp_4: Long?
          if (_stmt.isNull(_columnIndexOfCreatedAt)) {
            _tmp_4 = null
          } else {
            _tmp_4 = _stmt.getLong(_columnIndexOfCreatedAt)
          }
          _tmpCreatedAt = __dateLongConverter.fromTimestamp(_tmp_4)
          val _tmpUpdatedAt: Date?
          val _tmp_5: Long?
          if (_stmt.isNull(_columnIndexOfUpdatedAt)) {
            _tmp_5 = null
          } else {
            _tmp_5 = _stmt.getLong(_columnIndexOfUpdatedAt)
          }
          _tmpUpdatedAt = __dateLongConverter.fromTimestamp(_tmp_5)
          val _tmpCustomClaimsUpdatedAt: Date?
          val _tmp_6: Long?
          if (_stmt.isNull(_columnIndexOfCustomClaimsUpdatedAt)) {
            _tmp_6 = null
          } else {
            _tmp_6 = _stmt.getLong(_columnIndexOfCustomClaimsUpdatedAt)
          }
          _tmpCustomClaimsUpdatedAt = __dateLongConverter.fromTimestamp(_tmp_6)
          val _tmpIsSuspended: Boolean
          val _tmp_7: Int
          _tmp_7 = _stmt.getLong(_columnIndexOfIsSuspended).toInt()
          _tmpIsSuspended = _tmp_7 != 0
          val _tmpSuspensionReason: String?
          if (_stmt.isNull(_columnIndexOfSuspensionReason)) {
            _tmpSuspensionReason = null
          } else {
            _tmpSuspensionReason = _stmt.getText(_columnIndexOfSuspensionReason)
          }
          val _tmpSuspensionEndsAt: Long?
          if (_stmt.isNull(_columnIndexOfSuspensionEndsAt)) {
            _tmpSuspensionEndsAt = null
          } else {
            _tmpSuspensionEndsAt = _stmt.getLong(_columnIndexOfSuspensionEndsAt)
          }
          val _tmpNotificationsEnabled: Boolean
          val _tmp_8: Int
          _tmp_8 = _stmt.getLong(_columnIndexOfNotificationsEnabled).toInt()
          _tmpNotificationsEnabled = _tmp_8 != 0
          val _tmpFarmAlertsEnabled: Boolean
          val _tmp_9: Int
          _tmp_9 = _stmt.getLong(_columnIndexOfFarmAlertsEnabled).toInt()
          _tmpFarmAlertsEnabled = _tmp_9 != 0
          val _tmpTransferAlertsEnabled: Boolean
          val _tmp_10: Int
          _tmp_10 = _stmt.getLong(_columnIndexOfTransferAlertsEnabled).toInt()
          _tmpTransferAlertsEnabled = _tmp_10 != 0
          val _tmpSocialAlertsEnabled: Boolean
          val _tmp_11: Int
          _tmp_11 = _stmt.getLong(_columnIndexOfSocialAlertsEnabled).toInt()
          _tmpSocialAlertsEnabled = _tmp_11 != 0
          _item =
              UserEntity(_tmpUserId,_tmpPhoneNumber,_tmpEmail,_tmpFullName,_tmpAddress,_tmpBio,_tmpProfilePictureUrl,_tmpUserType,_tmpVerificationStatus,_tmpFarmAddressLine1,_tmpFarmAddressLine2,_tmpFarmCity,_tmpFarmState,_tmpFarmPostalCode,_tmpFarmCountry,_tmpFarmLocationLat,_tmpFarmLocationLng,_tmpLocationVerified,_tmpKycLevel,_tmpChickenCount,_tmpFarmerType,_tmpRaisingSince,_tmpFavoriteBreed,_tmpKycVerifiedAt,_tmpKycRejectionReason,_tmpVerificationRejectionReason,_tmpLatestVerificationId,_tmpLatestVerificationRef,_tmpVerificationSubmittedAt,_tmpShowcaseCount,_tmpMaxShowcaseSlots,_tmpCreatedAt,_tmpUpdatedAt,_tmpCustomClaimsUpdatedAt,_tmpIsSuspended,_tmpSuspensionReason,_tmpSuspensionEndsAt,_tmpNotificationsEnabled,_tmpFarmAlertsEnabled,_tmpTransferAlertsEnabled,_tmpSocialAlertsEnabled)
          _result.add(_item)
        }
        _result
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteUserById(userId: String) {
    val _sql: String = "DELETE FROM users WHERE userId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, userId)
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun deleteAllUsers() {
    val _sql: String = "DELETE FROM users"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        _stmt.step()
      } finally {
        _stmt.close()
      }
    }
  }

  public override suspend fun updateVerificationStatus(
    userId: String,
    status: String,
    updatedAt: Long,
  ) {
    val _sql: String = "UPDATE users SET verificationStatus = ?, updatedAt = ? WHERE userId = ?"
    return performSuspending(__db, false, true) { _connection ->
      val _stmt: SQLiteStatement = _connection.prepare(_sql)
      try {
        var _argIndex: Int = 1
        _stmt.bindText(_argIndex, status)
        _argIndex = 2
        _stmt.bindLong(_argIndex, updatedAt)
        _argIndex = 3
        _stmt.bindText(_argIndex, userId)
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
