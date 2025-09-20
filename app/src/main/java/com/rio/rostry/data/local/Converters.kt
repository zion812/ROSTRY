package com.rio.rostry.data.local

import androidx.room.TypeConverter
import com.rio.rostry.domain.model.KycStatus
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import java.util.Date

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromUserType(value: String?): UserType? {
        return if (value == null) null else UserType.valueOf(value)
    }

    @TypeConverter
    fun userTypeToString(userType: UserType?): String? {
        return userType?.name
    }

    @TypeConverter
    fun fromVerificationStatus(value: String?): VerificationStatus? {
        return if (value == null) null else VerificationStatus.valueOf(value)
    }

    @TypeConverter
    fun verificationStatusToString(verificationStatus: VerificationStatus?): String? {
        return verificationStatus?.name
    }

    @TypeConverter
    fun fromKycStatus(value: String?): KycStatus? {
        return if (value == null) null else KycStatus.valueOf(value)
    }

    @TypeConverter
    fun kycStatusToString(kycStatus: KycStatus?): String? {
        return kycStatus?.name
    }
}