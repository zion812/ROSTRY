package com.rio.rostry.ui.showcase

import androidx.lifecycle.viewModelScope
import com.rio.rostry.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel managing component gallery state: role selection, dark mode, filters, comparison.
 */
class ComponentGalleryViewModel : BaseViewModel() {
    enum class GalleryRole { GENERAL, FARMER, ENTHUSIAST }

    private val _selectedRole = MutableStateFlow(GalleryRole.GENERAL)
    val selectedRole: StateFlow<GalleryRole> = _selectedRole.asStateFlow()

    private val _darkMode = MutableStateFlow(false)
    val darkMode: StateFlow<Boolean> = _darkMode.asStateFlow()

    private val _query = MutableStateFlow("")
    val query: StateFlow<String> = _query.asStateFlow()

    private val _sideBySide = MutableStateFlow(false)
    val sideBySide: StateFlow<Boolean> = _sideBySide.asStateFlow()

    fun setRole(role: GalleryRole) { _selectedRole.value = role }
    fun toggleDarkMode(enabled: Boolean) { _darkMode.value = enabled }
    fun setQuery(text: String) { _query.value = text }
    fun setSideBySide(enabled: Boolean) { _sideBySide.value = enabled }

    fun trackInteraction(event: String) {
        viewModelScope.launch {
            // Hook analytics tracker here if available.
        }
    }
}
