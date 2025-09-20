package com.rio.rostry.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.rio.rostry.data.local.Converters
import com.rio.rostry.domain.model.KycStatus
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import java.util.Date

@Entity(tableName = "users")
@TypeConverters(Converters::class)
data class User(
    @PrimaryKey
    val id: String,
    val phone: String,
    val email: String? = null,
    val userType: UserType = UserType.GENERAL,
    val verificationStatus: VerificationStatus = VerificationStatus.PENDING,
    val name: String? = null,
    val address: String? = null,
    val location: String? = null, // Required for farmers
    val kycStatus: KycStatus = KycStatus.NOT_SUBMITTED, // Required for enthusiasts
    val coins: Int = 0,
    val createdAt: Date = Date(),
    val updatedAt: Date = Date(),
    val isDeleted: Boolean = false, // Soft delete flag
    val deletedAt: Date? = null // Soft delete timestamp
)