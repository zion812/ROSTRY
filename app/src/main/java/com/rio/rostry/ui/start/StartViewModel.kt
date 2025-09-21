package com.rio.rostry.ui.start

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.domain.auth.AuthRepository
import com.rio.rostry.domain.model.UserType
import com.rio.rostry.session.SessionManager
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class StartViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    sealed class Nav {
        object ToAuth : Nav()
        data class ToHome(val role: UserType) : Nav()
    }

    val nav = MutableSharedFlow<Nav>(extraBufferCapacity = 4)

    fun decide(nowMillis: Long) {
        viewModelScope.launch {
            var authed = false
            authRepository.isAuthenticated.collectLatest { isAuthed ->
                authed = isAuthed
                if (!authed) {
                    nav.tryEmit(Nav.ToAuth)
                } else {
                    // Check session validity and user role
                    sessionManager.isSessionValidFlow { nowMillis }.collectLatest { valid ->
                        if (valid) {
                            userRepository.getCurrentUser().collectLatest { res ->
                                if (res is Resource.Success) {
                                    val role = res.data?.userType ?: UserType.GENERAL
                                    nav.tryEmit(Nav.ToHome(role))
                                } else if (res is Resource.Error) {
                                    nav.tryEmit(Nav.ToAuth)
                                }
                            }
                        } else {
                            nav.tryEmit(Nav.ToAuth)
                        }
                    }
                }
            }
        }
    }
}
