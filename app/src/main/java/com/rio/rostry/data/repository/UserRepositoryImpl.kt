package com.rio.rostry.data.repository

import com.rio.rostry.data.local.UserDao
import com.rio.rostry.data.model.User as DataUser
import com.rio.rostry.domain.model.User as DomainUser
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.domain.model.VerificationStatus
import com.rio.rostry.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Date
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override fun getAllUsers(): Flow<List<DomainUser>> {
        return userDao.getAllUsers().map { users ->
            users.map { it.toDomainModel() }
        }
    }

    override fun getUsersByType(userType: UserType): Flow<List<DomainUser>> {
        return userDao.getUsersByType(userType).map { users ->
            users.map { it.toDomainModel() }
        }
    }

    override suspend fun getUserById(id: String): DomainUser? {
        return userDao.getUserById(id)?.toDomainModel()
    }

    override suspend fun getUserByPhone(phone: String): DomainUser? {
        return userDao.getUserByPhone(phone)?.toDomainModel()
    }

    override suspend fun insertUser(user: DomainUser) {
        userDao.insertUser(user.toDataModel())
    }

    override suspend fun updateUser(user: DomainUser) {
        val updatedUser = user.copy(updatedAt = Date())
        userDao.updateUser(updatedUser.toDataModel())
    }

    override suspend fun deleteUser(user: DomainUser) {
        userDao.deleteUser(user.id)
    }

    override suspend fun updateVerificationStatus(userId: String, status: VerificationStatus) {
        val user = getUserById(userId)
        user?.let {
            val updatedUser = it.copy(verificationStatus = status, updatedAt = Date())
            userDao.updateUser(updatedUser.toDataModel())
        }
    }

    private fun DataUser.toDomainModel(): DomainUser {
        return DomainUser(
            id = id,
            phone = phone,
            email = email,
            userType = userType,
            verificationStatus = verificationStatus,
            name = name,
            address = address,
            location = location,
            kycStatus = kycStatus,
            coins = coins,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun DomainUser.toDataModel(): DataUser {
        return DataUser(
            id = id,
            phone = phone,
            email = email,
            userType = userType,
            verificationStatus = verificationStatus,
            name = name,
            address = address,
            location = location,
            kycStatus = kycStatus,
            coins = coins,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}