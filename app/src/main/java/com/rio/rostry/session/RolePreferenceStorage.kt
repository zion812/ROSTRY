package com.rio.rostry.session

import android.content.SharedPreferences
import com.rio.rostry.domain.model.UserType
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface RolePreferenceDataSource {
    val role: StateFlow<UserType?>
    fun persist(role: UserType)
    fun clear()
}

@Singleton
class RolePreferenceStorage @Inject constructor(
    @Named(ROLE_PREFS_NAME) private val sharedPreferences: SharedPreferences
) : RolePreferenceDataSource {

    companion object {
        private const val KEY_ROLE = "role_current"
        const val ROLE_PREFS_NAME = "role_prefs"
    }

    private val mutableRole = MutableStateFlow(sharedPreferences.readRole())

    override val role: StateFlow<UserType?> = mutableRole.asStateFlow()

    override fun persist(role: UserType) {
        sharedPreferences.edit().putString(KEY_ROLE, role.name).apply()
        mutableRole.value = role
    }

    override fun clear() {
        sharedPreferences.edit().remove(KEY_ROLE).apply()
        mutableRole.value = null
    }

    private fun SharedPreferences.readRole(): UserType? {
        val stored = getString(KEY_ROLE, null) ?: return null
        return runCatching { UserType.valueOf(stored) }.getOrNull()
    }
}
