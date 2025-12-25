package com.rio.rostry.ui.farmer.digital

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.rio.rostry.data.database.dao.ProductDao
import com.rio.rostry.domain.model.LifecycleStage
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DigitalFarmUiState(
    val stageCounts: Map<LifecycleStage, Int> = emptyMap(),
    val totalBirds: Int = 0,
    val hatchingCount: Int = 0,
    val readyToGrowCount: Int = 0,
    val readyToLayCount: Int = 0,
    val isLoading: Boolean = true
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class DigitalFarmViewModel @Inject constructor(
    private val productDao: ProductDao,
    private val hatchingBatchDao: com.rio.rostry.data.database.dao.HatchingBatchDao,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(DigitalFarmUiState())
    val uiState: StateFlow<DigitalFarmUiState> = _uiState.asStateFlow()

    // Reactive user ID from Firebase Auth
    private val currentUserId: Flow<String?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.uid)
        }
        firebaseAuth.addAuthStateListener(listener)
        trySend(firebaseAuth.currentUser?.uid)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }.distinctUntilChanged()

    private val _navigationEvent = MutableSharedFlow<String>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    fun navigateToModule(route: String) {
        viewModelScope.launch {
            _navigationEvent.emit(route)
        }
    }

    init {
        loadData()
    }

    private fun loadData() {
        currentUserId
            .filterNotNull()
            .flatMapLatest { farmerId ->
                val now = System.currentTimeMillis()
                combine(
                    productDao.observeStageCounts(farmerId),
                    productDao.observeReadyToGrowCount(farmerId),
                    productDao.observeReadyToLayCount(farmerId),
                    hatchingBatchDao.observeTotalActiveEggs(farmerId, now)
                ) { stageCountsList, readyToGrow, readyToLay, activeEggs ->
                    DataSnapshot(stageCountsList, readyToGrow, readyToLay, activeEggs ?: 0)
                }
            }
            .onEach { snapshot ->
                val map = snapshot.stageCounts.associate { (it.stage ?: LifecycleStage.CHICK) to it.count }
                val total = snapshot.stageCounts.sumOf { it.count }
                _uiState.update { 
                    it.copy(
                        stageCounts = map,
                        totalBirds = total,
                        hatchingCount = snapshot.hatchingCount,
                        readyToGrowCount = snapshot.readyToGrow,
                        readyToLayCount = snapshot.readyToLay,
                        isLoading = false
                    ) 
                }
            }
            .launchIn(viewModelScope)
    }

    private data class DataSnapshot(
        val stageCounts: List<com.rio.rostry.data.database.dao.StageCount>,
        val readyToGrow: Int,
        val readyToLay: Int,
        val hatchingCount: Int
    )
}
