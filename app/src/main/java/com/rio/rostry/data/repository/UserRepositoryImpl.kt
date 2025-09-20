package com.rio.rostry.data.repository

import com.rio.rostry.data.local.UserDao
import com.rio.rostry.data.model.User as DataUser
import com.rio.rostry.domain.model.User as DomainUser
import com.rio.rostry.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
) : UserRepository {

    override fun getAllUsers(): Flow<List<DomainUser>> {
        return userDao.getAllUsers().map { users ->
            users.map { it.toDomainModel() }
        }
    }

    override suspend fun getUserById(id: String): DomainUser? {
        return userDao.getUserById(id)?.toDomainModel()
    }

    override suspend fun insertUser(user: DomainUser) {
        userDao.insertUser(user.toDataModel())
    }

    override suspend fun updateUser(user: DomainUser) {
        userDao.updateUser(user.toDataModel())
    }

    override suspend fun deleteUser(user: DomainUser) {
        userDao.deleteUser(user.toDataModel())
    }

    private fun DataUser.toDomainModel(): DomainUser {
        return DomainUser(
            id = id,
            name = name,
            email = email,
            phone = phone,
            address = address,
            role = role,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun DomainUser.toDataModel(): DataUser {
        return DataUser(
            id = id,
            name = name,
            email = email,
            phone = phone,
            address = address,
            role = role,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}