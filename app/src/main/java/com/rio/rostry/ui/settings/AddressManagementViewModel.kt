package com.rio.rostry.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.session.CurrentUserProvider
import com.rio.rostry.utils.Resource
import com.rio.rostry.utils.analytics.FlowAnalyticsTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressManagementViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val currentUserProvider: CurrentUserProvider,
    private val analyticsTracker: FlowAnalyticsTracker
) : ViewModel() {

    data class AddressItem(
        val id: String = "",
        val street: String = "",
        val city: String = "",
        val state: String = "",
        val pincode: String = "",
        val landmark: String = "",
        val lat: Double? = null,
        val lng: Double? = null,
        val isDefault: Boolean = false,
        val usageCount: Int = 0,
        val verificationStatus: String = ""
    )

    // Nullable DTO for safe Gson parsing
    private data class AddressItemDto(
        val id: String? = null,
        val street: String? = null,
        val city: String? = null,
        val state: String? = null,
        val pincode: String? = null,
        val landmark: String? = null,
        val lat: Double? = null,
        val lng: Double? = null,
        val isDefault: Boolean? = null,
        val usageCount: Int? = null,
        val verificationStatus: String? = null
    )

    data class UiState(
        val isLoading: Boolean = false,
        val addresses: List<AddressItem> = emptyList(),
        val successMessage: String? = null,
        val error: String? = null,
        val isOffline: Boolean = false
    )

    private val _ui = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _ui

    private val gson = Gson()
    private val addressListType = object : TypeToken<List<AddressItemDto>>() {}.type

    init {
        loadAddresses()
        analyticsTracker.trackAddressManagementOpened()
    }

    fun loadAddresses() {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true)
            userRepository.getCurrentUser().collectLatest { res ->
                when (res) {
                    is Resource.Success -> {
                        val user = res.data
                        val addresses: List<AddressItem> = if (user?.address.isNullOrBlank()) {
                            emptyList()
                        } else {
                            try {
                                val parsed: List<AddressItemDto> = gson.fromJson(user?.address, addressListType) ?: emptyList()
                                parsed.map { dto ->
                                    AddressItem(
                                        id = dto.id.orEmpty(),
                                        street = dto.street.orEmpty(),
                                        city = dto.city.orEmpty(),
                                        state = dto.state.orEmpty(),
                                        pincode = dto.pincode.orEmpty(),
                                        landmark = dto.landmark.orEmpty(),
                                        lat = dto.lat,
                                        lng = dto.lng,
                                        isDefault = dto.isDefault ?: false,
                                        usageCount = dto.usageCount ?: 0,
                                        verificationStatus = dto.verificationStatus.orEmpty()
                                    )
                                }
                            } catch (e: Exception) {
                                emptyList()
                            }
                        }
                        _ui.value = UiState(
                            isLoading = false,
                            addresses = addresses,
                            isOffline = false
                        )
                    }
                    is Resource.Error -> _ui.value = UiState(
                        isLoading = false,
                        error = res.message ?: "Failed to load addresses",
                        isOffline = true
                    )
                    is Resource.Loading -> _ui.value = _ui.value.copy(isLoading = true)
                }
            }
        }
    }

    fun addAddress(address: AddressItem) {
        if (!validateAddress(address)) return

        val newAddress = address.copy(id = generateId())
        val updatedAddresses = _ui.value.addresses + newAddress
        updateAddresses(updatedAddresses, "Address added successfully")
        analyticsTracker.trackAddressAdded()
    }

    fun updateAddress(updatedAddress: AddressItem) {
        if (!validateAddress(updatedAddress)) return

        val updatedAddresses = _ui.value.addresses.map {
            if (it.id == updatedAddress.id) updatedAddress else it
        }
        updateAddresses(updatedAddresses, "Address updated successfully")
        analyticsTracker.trackAddressUpdated()
    }

    fun deleteAddress(addressId: String) {
        val updatedAddresses = _ui.value.addresses.filter { it.id != addressId }
        updateAddresses(updatedAddresses, "Address deleted successfully")
        analyticsTracker.trackAddressDeleted()
    }

    fun setDefaultAddress(addressId: String) {
        val updatedAddresses = _ui.value.addresses.map {
            it.copy(isDefault = it.id == addressId)
        }
        updateAddresses(updatedAddresses, "Default address updated")
        analyticsTracker.trackDefaultAddressSet()
    }

    private fun updateAddresses(addresses: List<AddressItem>, successMessage: String) {
        viewModelScope.launch {
            _ui.value = _ui.value.copy(isLoading = true)
            val userId = currentUserProvider.userIdOrNull() ?: return@launch
            val userResource = userRepository.getCurrentUser().first { it !is Resource.Loading }
            val user = when (userResource) {
                is Resource.Success -> userResource.data ?: return@launch
                is Resource.Error -> {
                    _ui.value = UiState(
                        addresses = _ui.value.addresses,
                        error = userResource.message ?: "Failed to fetch user",
                        isOffline = true
                    )
                    return@launch
                }
                is Resource.Loading -> return@launch
            }
            val updatedUser = user.copy(address = gson.toJson(addresses))
            val result = userRepository.updateUserProfile(updatedUser)
            _ui.value = when (result) {
                is Resource.Success -> UiState(
                    addresses = addresses,
                    successMessage = successMessage,
                    isOffline = false
                )
                is Resource.Error -> UiState(
                    addresses = _ui.value.addresses,
                    error = result.message ?: "Failed to update addresses",
                    isOffline = true
                )
                is Resource.Loading -> _ui.value.copy(isLoading = true)
            }
        }
    }

    private fun validateAddress(address: AddressItem): Boolean {
        val errors = mutableListOf<String>()

        if (address.street.isBlank()) errors.add("Street is required")
        if (address.city.isBlank()) errors.add("City is required")
        if (address.state.isBlank()) errors.add("State is required")
        if (address.pincode.isBlank()) {
            errors.add("Pincode is required")
        } else if (!isValidPincode(address.pincode)) {
            errors.add("Invalid pincode format")
        }

        if (errors.isNotEmpty()) {
            _ui.value = _ui.value.copy(error = errors.joinToString(", "))
            return false
        }
        return true
    }

    private fun isValidPincode(pincode: String): Boolean {
        return pincode.matches(Regex("\\d{6}"))
    }

    private fun generateId(): String {
        return System.currentTimeMillis().toString()
    }

    fun clearError() { _ui.value = _ui.value.copy(error = null) }
    fun clearSuccessMessage() { _ui.value = _ui.value.copy(successMessage = null) }

    fun retryLoad() {
        loadAddresses()
    }
}
