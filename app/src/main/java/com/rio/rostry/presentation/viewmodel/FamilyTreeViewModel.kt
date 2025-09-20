package com.rio.rostry.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rio.rostry.domain.usecase.BuildFamilyTreeUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FamilyTreeViewModel @Inject constructor(
    private val buildFamilyTreeUseCase: BuildFamilyTreeUseCase
) : ViewModel() {
    
    private val _familyTree = MutableStateFlow<com.rio.rostry.domain.model.FamilyTreeNode?>(null)
    val familyTree: StateFlow<com.rio.rostry.domain.model.FamilyTreeNode?> = _familyTree
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    fun loadFamilyTree(rootPoultryId: String, maxDepth: Int = 3) {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            
            try {
                val tree = buildFamilyTreeUseCase(rootPoultryId, maxDepth)
                _familyTree.value = tree
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _isLoading.value = false
            }
        }
    }
}