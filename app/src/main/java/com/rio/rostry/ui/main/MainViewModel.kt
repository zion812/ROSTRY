package com.rio.rostry.ui.main

import com.google.firebase.auth.FirebaseAuth
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.models.User
import com.rio.rostry.data.repo.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import com.rio.rostry.utils.PerformanceLogger
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val auth: FirebaseAuth,
    private val performanceLogger: PerformanceLogger
) : ViewModel() {

    private val _user = MutableStateFlow<User?>(null)
    val user: StateFlow<User?> = _user.asStateFlow()

    
    private val authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser == null) {
            _user.value = null
        } else {
            viewModelScope.launch {
                userRepository.getUser(firebaseUser.uid).collect { _user.value = it }
            }
        }
    }

    init {
        auth.addAuthStateListener(authStateListener)
        // Measure time to first non-null user emission
        val start = System.currentTimeMillis()
        var logged = false
        viewModelScope.launch {
            user.filterNotNull().collect {
                if (!logged) {
                    val duration = System.currentTimeMillis() - start
                    performanceLogger.logNetworkRequest("user_load_from_room", duration, true)
                    logged = true
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        auth.removeAuthStateListener(authStateListener)
    }
}
