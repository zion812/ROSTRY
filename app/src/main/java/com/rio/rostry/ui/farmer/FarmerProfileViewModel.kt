package com.rio.rostry.ui.farmer

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.data.database.entity.ReputationEntity
import com.rio.rostry.data.database.entity.UserEntity
import com.rio.rostry.data.repository.UserRepository
import com.rio.rostry.data.repository.social.SocialRepository
import com.rio.rostry.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.rio.rostry.data.repository.OrderRepository
import com.rio.rostry.data.repository.ProductRepository
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class FarmerProfileViewModel @Inject constructor(
    private val userRepository: UserRepository,
    private val socialRepository: SocialRepository,
    private val productRepository: ProductRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {

    data class UiState(
        val user: UserEntity? = null,
        val reputation: ReputationEntity? = null,
        val products: List<com.rio.rostry.data.database.entity.ProductEntity> = emptyList(),
        val salesHistory: List<com.rio.rostry.data.database.entity.OrderEntity> = emptyList(),
        val isLoading: Boolean = true
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    init {
        loadProfile()
    }

    private fun loadProfile() {
        viewModelScope.launch {
            userRepository.getCurrentUser().collect { userRes ->
                if (userRes is Resource.Success && userRes.data != null) {
                    val user = userRes.data
                    
                    // Combine flows to update state
                    combine(
                        socialRepository.getReputation(user.userId),
                        productRepository.getProductsBySeller(user.userId),
                        orderRepository.getOrdersBySeller(user.userId)
                    ) { rep, productsRes, orders ->
                        UiState(
                            user = user,
                            reputation = rep,
                            products = productsRes.data ?: emptyList(),
                            salesHistory = orders,
                            isLoading = false
                        )
                    }.collect { newState ->
                        _uiState.value = newState
                    }
                } else {
                     _uiState.value = _uiState.value.copy(isLoading = false)
                }
            }
        }
    }
}
