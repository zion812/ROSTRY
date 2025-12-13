package com.rio.rostry.data.database

import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import org.junit.Assert.assertEquals
import org.junit.Test

class ConvertersTest {

    @Test
    fun userType_convertsCorrectly() {
        assertEquals("GENERAL", AppDatabase.Converters.fromUserType(UserType.GENERAL))
        assertEquals("FARMER", AppDatabase.Converters.fromUserType(UserType.FARMER))
        assertEquals("ENTHUSIAST", AppDatabase.Converters.fromUserType(UserType.ENTHUSIAST))

        assertEquals(UserType.GENERAL, AppDatabase.Converters.toUserType("GENERAL"))
        assertEquals(UserType.FARMER, AppDatabase.Converters.toUserType("FARMER"))
        assertEquals(UserType.ENTHUSIAST, AppDatabase.Converters.toUserType("ENTHUSIAST"))
        
        // Test nulls and invalid values
        assertEquals(null, AppDatabase.Converters.fromUserType(null))
        assertEquals(null, AppDatabase.Converters.toUserType(null))
        assertEquals(null, AppDatabase.Converters.toUserType("INVALID_TYPE"))
    }

    @Test
    fun verificationStatus_convertsCorrectly() {
        assertEquals("UNVERIFIED", AppDatabase.Converters.fromVerificationStatus(VerificationStatus.UNVERIFIED))
        assertEquals("PENDING", AppDatabase.Converters.fromVerificationStatus(VerificationStatus.PENDING))
        assertEquals("VERIFIED", AppDatabase.Converters.fromVerificationStatus(VerificationStatus.VERIFIED))
        assertEquals("REJECTED", AppDatabase.Converters.fromVerificationStatus(VerificationStatus.REJECTED))

        assertEquals(VerificationStatus.UNVERIFIED, AppDatabase.Converters.toVerificationStatus("UNVERIFIED"))
        assertEquals(VerificationStatus.PENDING, AppDatabase.Converters.toVerificationStatus("PENDING"))
        assertEquals(VerificationStatus.VERIFIED, AppDatabase.Converters.toVerificationStatus("VERIFIED"))
        assertEquals(VerificationStatus.REJECTED, AppDatabase.Converters.toVerificationStatus("REJECTED"))
        
         // Test nulls and invalid values
        assertEquals(null, AppDatabase.Converters.fromVerificationStatus(null))
        assertEquals(null, AppDatabase.Converters.toVerificationStatus(null))
        assertEquals(null, AppDatabase.Converters.toVerificationStatus("INVALID_STATUS"))
    }
}
