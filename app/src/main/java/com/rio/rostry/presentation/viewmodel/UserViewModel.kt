package com.rio.rostry.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.domain.model.User
import com.rio.rostry.domain.repository.UserRepository
import com.rio.rostry.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val userRepository: UserRepository
) : BaseViewModel() {

    private val _users = MutableStateFlow<Resource<List<User>>>(Resource.Loading)
    val users: StateFlow<Resource<List<User>>> = _users

    private val _user = MutableStateFlow<Resource<User?>>(Resource.Loading)
    val user: StateFlow<Resource<User?>> = _user

    init {
        fetchAllUsers()
    }

    fun fetchAllUsers() {
        viewModelScope.launch {
            userRepository.getAllUsers().collectLatest { userList ->
                _users.value = Resource.Success(userList)
            }
        }
    }

    fun fetchUserById(id: String) {
        executeWithLoading(_user) {
            userRepository.getUserById(id)
        }
    }

    fun createUser(user: User) {
        executeWithLoading(_user) {
            userRepository.insertUser(user)
            user
        }
    }

    fun updateUser(user: User) {
        executeWithLoading(_user) {
            userRepository.updateUser(user)
            user
        }
    }

    fun deleteUser(user: User) {
        executeWithLoading(_user) {
            userRepository.deleteUser(user)
            null
        }
    }
}