package com.rio.rostry.ui.fowl

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.rio.rostry.data.models.Fowl
import androidx.work.WorkInfo
import com.rio.rostry.data.models.FowlRecord
import com.rio.rostry.data.repo.FowlRepository
import com.rio.rostry.data.repo.StorageRepository
import com.rio.rostry.data.repo.SyncRepository
import com.rio.rostry.utils.QrCodeGenerator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Date
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class FowlViewModel @Inject constructor(
    private val fowlRepository: FowlRepository,
    private val storageRepository: StorageRepository,
    private val syncRepository: SyncRepository
) : ViewModel() {

    val syncWorkInfo: StateFlow<WorkInfo?> = syncRepository.syncWorkInfo
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val fowls: Flow<PagingData<Fowl>> = fowlRepository.getFowls().cachedIn(viewModelScope)

    private val _selectedFowl = MutableStateFlow<Fowl?>(null)
    val selectedFowl: StateFlow<Fowl?> = _selectedFowl.asStateFlow()

    private val _fowlRecords = MutableStateFlow<List<com.rio.rostry.data.models.FowlRecord>>(emptyList())
    val fowlRecords: StateFlow<List<com.rio.rostry.data.models.FowlRecord>> = _fowlRecords.asStateFlow()

    private val _sire = MutableStateFlow<Fowl?>(null)
    val sire: StateFlow<Fowl?> = _sire.asStateFlow()

    private val _dam = MutableStateFlow<Fowl?>(null)
    val dam: StateFlow<Fowl?> = _dam.asStateFlow()

    private val _offspring = MutableStateFlow<List<Fowl>>(emptyList())
    val offspring: StateFlow<List<Fowl>> = _offspring.asStateFlow()

    fun getFowlRecords(fowlId: String) {
        viewModelScope.launch {
            fowlRepository.getFowlRecords(fowlId).collect {
                _fowlRecords.value = it
            }
        }
    }

    fun addFowlRecord(
        fowlId: String,
        date: Date,
        type: String,
        details: String,
        notes: String?,
    ) {
        viewModelScope.launch {
            val newRecord = FowlRecord(
                id = UUID.randomUUID().toString(),
                fowlId = fowlId,
                date = date,
                type = type,
                details = details,
                notes = notes
            )
            fowlRepository.addFowlRecord(newRecord)
        }
    }

    fun getFowlById(fowlId: String) {
        viewModelScope.launch {
            fowlRepository.getFowlById(fowlId).collect { fowl ->
                _selectedFowl.value = fowl
                if (fowl != null) {
                    // Fetch parents
                    fowl.sireId?.let { getParent(it, isSire = true) } ?: run { _sire.value = null }
                    fowl.damId?.let { getParent(it, isSire = false) } ?: run { _dam.value = null }

                    // Fetch offspring
                    getOffspring(fowl.id)
                } else {
                    // Clear family data if fowl is not found
                    _sire.value = null
                    _dam.value = null
                    _offspring.value = emptyList()
                }
            }
        }
    }

    private fun getParent(parentId: String, isSire: Boolean) {
        viewModelScope.launch {
            fowlRepository.getFowlById(parentId).collect { parent ->
                if (isSire) {
                    _sire.value = parent
                } else {
                    _dam.value = parent
                }
            }
        }
    }

    private fun getOffspring(fowlId: String) {
        viewModelScope.launch {
            fowlRepository.getOffspring(fowlId).collect { offspringList ->
                _offspring.value = offspringList
            }
        }
    }

    fun addFowl(
        name: String,
        group: String?,
        sireId: String?,
        damId: String?,
        birthDate: Date,
        status: String,
        imageUri: Uri?
    ) {
        viewModelScope.launch {
            val fowlId = UUID.randomUUID().toString()
            val imageUrl = imageUri?.let { storageRepository.uploadImage(it) }
            val qrCodeBitmap = QrCodeGenerator.generate(fowlId)
            val qrCodeUrl = qrCodeBitmap?.let { storageRepository.uploadBitmap(it) }

            val newFowl = Fowl(
                id = fowlId,
                name = name,
                group = group,
                sireId = sireId,
                damId = damId,
                birthDate = birthDate,
                status = status,
                imageUrl = imageUrl,
                qrCodeUrl = qrCodeUrl
            )
            fowlRepository.addFowl(newFowl, imageUri)
        }
    }
}
